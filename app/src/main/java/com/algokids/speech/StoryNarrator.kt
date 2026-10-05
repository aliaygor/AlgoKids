package com.algokids.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.algokids.ui.screens.AppLanguage
import org.json.JSONObject
import java.security.MessageDigest

/** Bundled recordings first; highest-quality device voice otherwise. No API credential in the app. */
class StoryNarrator(private val context:Context,private val tts:TextToSpeech,private val status:(String)->Unit) {
    private val handler=Handler(Looper.getMainLooper())
    private var player:MediaPlayer?=null
    private var generation=0
    private val pack=runCatching { context.assets.open("narration/index.json").bufferedReader().use { JSONObject(it.readText()) } }.getOrNull()
    fun play(storyId:String,page:Int,language:AppLanguage,text:String,ttsReady:Boolean) {
        stop()
        val token=generation
        val key="${language.name.lowercase()}/${storyId}_${page+1}"
        val entry=pack?.optJSONObject(key)
        val hash=MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
        fun fallback() {
            if(token!=generation)return
            if(!ttsReady) {status("unavailable");return}
            val manager=context.getSystemService(ConnectivityManager::class.java)
            val online=manager.getNetworkCapabilities(manager.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)==true
            var retried=false
            fun speak(allowNetwork:Boolean) {
                if(token!=generation)return
                if(!ChildNarration.configure(tts,language,true,allowNetwork)) {status("unavailable");return}
                val result=tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"story_recording_$token")
                if(result==TextToSpeech.ERROR) {
                    if(allowNetwork && !retried) {retried=true;speak(false)} else status("unavailable")
                } else status(if(tts.voice?.isNetworkConnectionRequired==true) "online" else "device")
            }
            tts.setOnUtteranceProgressListener(object:UtteranceProgressListener() {
                override fun onStart(id:String?) {}
                override fun onDone(id:String?) {}
                override fun onError(id:String?) { handler.post {
                    if(token==generation && id=="story_recording_$token") {
                        if(!retried && online) {retried=true;speak(false)} else status("unavailable")
                    }
                } }
            })
            speak(online)
        }
        if(entry==null || entry.optString("text_sha256")!=hash) {fallback();return}
        val asset=entry.optString("asset")
        if(!asset.startsWith("narration/${language.name.lowercase()}/") || ".." in asset) {fallback();return}
        try {
            val recording=MediaPlayer()
            player=recording
            recording.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build())
            context.assets.openFd(asset).use { recording.setDataSource(it.fileDescriptor,it.startOffset,it.length) }
            recording.setOnPreparedListener { if(token==generation) {it.start();status("recording")} }
            recording.setOnErrorListener { _,_,_ -> if(token==generation) {player?.release();player=null;fallback()};true }
            recording.prepareAsync()
        } catch(_:Exception) {player?.release();player=null;fallback()}
    }
    fun stop() {generation++;player?.release();player=null;tts.stop();tts.setOnUtteranceProgressListener(null)}
}
