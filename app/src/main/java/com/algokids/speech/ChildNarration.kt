package com.algokids.speech
import android.speech.tts.TextToSpeech
import com.algokids.ui.screens.AppLanguage
object ChildNarration {
 private val selectedFamilies=mutableMapOf<AppLanguage,String>()
 private fun family(name:String)=name.removeSuffix("-local").removeSuffix("-network")
 fun loadPreferences(p:android.content.SharedPreferences) {AppLanguage.entries.forEach { language ->
  val selected=p.getString("narrator_voice_${language.name}",null)
  if(selected==null)selectedFamilies.remove(language) else selectedFamilies[language]=selected
 } }
 fun choose(language:AppLanguage,name:String,p:android.content.SharedPreferences) {
  val selected=family(name);selectedFamilies[language]=selected;p.edit().putString("narrator_voice_${language.name}",selected).apply()
 }
 fun choices(tts:TextToSpeech,language:AppLanguage)=tts.voices.orEmpty().filter {
  it.locale.language==language.locale.language && !it.isNetworkConnectionRequired && "notInstalled" !in it.features.orEmpty() && "legacySetLanguageVoice" !in it.features.orEmpty()
 }.sortedBy {it.name}
 fun configure(tts:TextToSpeech,language:AppLanguage,story:Boolean=false,allowNetwork:Boolean=false):Boolean {
  val preferred=selectedFamilies[language] ?: if(language==AppLanguage.TR) "tr-tr-x-cfs" else "en-us-x-tpf"
  val voice=tts.voices?.filter { it.locale.language==language.locale.language && (!it.isNetworkConnectionRequired || allowNetwork) && "notInstalled" !in it.features.orEmpty() }
   ?.sortedWith(compareByDescending<android.speech.tts.Voice> {family(it.name)==preferred}
    .thenByDescending { it.locale.country==language.locale.country }
    .thenBy {"legacySetLanguageVoice" in it.features.orEmpty()}
    .thenByDescending {it.quality}
    .thenByDescending {allowNetwork && it.isNetworkConnectionRequired}
    .thenBy {it.latency}.thenBy {it.name})?.firstOrNull() ?: return false
  if(tts.setVoice(voice)!=TextToSpeech.SUCCESS)return false
  tts.setSpeechRate(if(story) 1f else .94f);tts.setPitch(1f)
  return tts.voice?.locale?.language==language.locale.language && (allowNetwork || tts.voice?.isNetworkConnectionRequired==false)
 }
 fun text(raw:String,en:Boolean):String {
  var result=raw.replace(Regex("(?<=\\d)\\s*→\\s*(?=\\d)"),if(en) " then " else " sonra ")
  val words=mapOf("☀️" to (if(en) "sun" else "güneş"),"⭐" to (if(en) "star" else "yıldız"),"🌙" to (if(en) "moon" else "ay"),"☁️" to (if(en) "cloud" else "bulut"),"🧱" to (if(en) "wall" else "engel"),"🔑" to (if(en) "key" else "anahtar"),"🔋" to (if(en) "charge" else "şarj"),"🚪" to (if(en) "door" else "kapı"),"⏸" to (if(en) "wait" else "bekle"),"🔴" to (if(en) "red" else "kırmızı"),"🟢" to (if(en) "green" else "yeşil"),"○" to (if(en) "clear" else "boş"))
  words.forEach { (symbol,word) -> result=result.replace(symbol," $word ") }
  result=result.replace("→",if(en) " right " else " sağ ").replace("↑",if(en) " up " else " yukarı ").replace("↓",if(en) " down " else " aşağı ").replace("←",if(en) " left " else " sol ").replace("×",if(en) " times " else " çarpı ")
  return result.replace(Regex("\\s+")," ").trim()
 }
}
