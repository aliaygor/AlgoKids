package com.algokids
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.algokids.ui.screens.AppLanguage
import com.algokids.speech.ChildNarration
import com.algokids.data.StoryCatalog
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
@RunWith(AndroidJUnit4::class)
class NarrationIntegrationTest {
 @Test fun actualEngineLanguageAndSynthesisOrExplicitUnavailableStatus() {
  val c=InstrumentationRegistry.getInstrumentation().targetContext
  val init=CountDownLatch(1)
  var initialized=false
  val tts=TextToSpeech(c) { initialized=it==TextToSpeech.SUCCESS;init.countDown() }
  val dir=File(c.filesDir,"narration-audit").apply { mkdirs() }
  val report=StringBuilder()
  try {
   assertTrue("TTS initialization callback",init.await(30,TimeUnit.SECONDS))
   tts.voices.orEmpty().filter { it.locale.language in setOf("tr","en") }.sortedBy {it.name}.forEach {
    report.append("AVAILABLE ${it.name} locale=${it.locale} quality=${it.quality} network=${it.isNetworkConnectionRequired} features=${it.features}\n")
   }
   for(lang in AppLanguage.entries) {
    val configured=initialized && ChildNarration.configure(tts,lang)
    report.append("${lang.name}: ready=$configured voice=${if(configured) tts.voice.name else "unavailable"}\n")
    if(!configured)continue
    assertEquals(lang.locale.language,tts.voice.locale.language)
    assertFalse(tts.voice.isNetworkConnectionRequired)
    if(ChildNarration.choices(tts,lang).isNotEmpty())assertFalse(tts.voice.features.orEmpty().contains("legacySetLanguageVoice"))
    val samples=if(lang==AppLanguage.TR)listOf("Ce, çe, yumuşak ge, ı, i.","Sıfır, kırk, altmış, yetmiş, altmış dört, yüz.",StoryCatalog.all.first().pages.first()) else listOf("A, B, C, D, E.","Zero, forty, sixty, seventy, sixty four, one hundred.",StoryCatalog.all.first().pagesEn.first())
    samples.forEachIndexed { index,text ->
     assertTrue(ChildNarration.configure(tts,lang,story=index==2))
     val done=CountDownLatch(1);var success=false
     val id="${lang.name}_$index"
     tts.setOnUtteranceProgressListener(object:UtteranceProgressListener() {
      override fun onStart(u:String?)=Unit
      override fun onDone(u:String?) { if(u==id) { success=true;done.countDown() } }
      override fun onError(u:String?) { if(u==id)done.countDown() }
     })
     val file=File(dir,"$id.wav")
     assertEquals(TextToSpeech.SUCCESS,tts.synthesizeToFile(text,null,file,id))
     assertTrue("$id synthesis completed",done.await(30,TimeUnit.SECONDS))
     assertTrue("$id synthesis succeeded",success)
     assertTrue("$id contains audio",file.length()>44)
     report.append("$id: ${file.length()} bytes\n")
    }
    val selected=tts.voice
    if(lang==AppLanguage.TR) ChildNarration.choices(tts,lang).forEachIndexed { index,voice ->
     assertEquals(TextToSpeech.SUCCESS,tts.setVoice(voice));tts.setSpeechRate(1f);tts.setPitch(1f)
     val done=CountDownLatch(1);var success=false;val id="TR_voice_${index+1}"
     tts.setOnUtteranceProgressListener(object:UtteranceProgressListener() {
      override fun onStart(u:String?)=Unit
      override fun onDone(u:String?) {if(u==id) {success=true;done.countDown()}}
      override fun onError(u:String?) {if(u==id)done.countDown()}
     })
     val file=File(dir,"$id.wav")
     assertEquals(TextToSpeech.SUCCESS,tts.synthesizeToFile("Merhaba! Küçük bir roket, yıldızları keşfetmek için yola çıktı. Önce düşündü, sonra planını adım adım uyguladı.",null,file,id))
     assertTrue(done.await(30,TimeUnit.SECONDS));assertTrue(success);assertTrue(file.length()>44)
     report.append("$id voice=${voice.name} bytes=${file.length()}\n")
    }
    tts.setVoice(selected)
   }
  } finally { File(dir,"report.txt").writeText(report.toString());tts.shutdown() }
 }
}
