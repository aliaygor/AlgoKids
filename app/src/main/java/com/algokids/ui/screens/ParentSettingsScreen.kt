package com.algokids.ui.screens
import android.Manifest
import android.content.Intent
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.algokids.notifications.ReminderScheduler
import com.algokids.speech.ChildNarration
@Composable fun ParentSettingsScreen(language:AppLanguage,tts:TextToSpeech,ready:Boolean,onBack:()->Unit,privacyOptionsRequired:Boolean=false,onPrivacyOptions:()->Unit={},bannerContent:@Composable ()->Unit={}) {
 val context=LocalContext.current
 var days by remember { mutableIntStateOf(ReminderScheduler.prefs(context).getInt("reminder_days",2)) }
 var pendingDays by rememberSaveable { mutableIntStateOf(2) }
 var message by remember { mutableStateOf("") }
 var age by remember { mutableIntStateOf(ReminderScheduler.prefs(context).getInt("child_age",0)) }
 val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
  if(granted) { days=pendingDays;ReminderScheduler.configure(context,days) }
  else { days=0;ReminderScheduler.configure(context,0);message=label(language,"Bildirim izni verilmedi. Hatırlatıcılar kapalı.","Notifications were not allowed. Reminders remain off.") }
 }
 fun setDays(value:Int) {
  if(value>0 && Build.VERSION.SDK_INT>=33 && !ReminderScheduler.permitted(context)) { pendingDays=value;permission.launch(Manifest.permission.POST_NOTIFICATIONS) }
  else { days=value;ReminderScheduler.configure(context,value) }
 }
 BackHandler(onBack=onBack)
 Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
  TextButton(onClick=onBack) { Text(label(language,"‹ Ana sayfa","‹ Home")) }
  Text(label(language,"Ebeveyn ayarları","Parent settings"),style=MaterialTheme.typography.headlineMedium)
  Text(label(language,"Çocuğun yaşı","Child’s age"),style=MaterialTheme.typography.titleLarge)
  Text(label(language,"Günlük soru için 4–12 yaş arasında bir başlangıç önerisi seçin. Doğum tarihi veya isim istemiyoruz; seçim yalnızca cihazda saklanır. Yaş bir yetenek ölçümü değildir.","Choose a starting suggestion from ages 4–12 for the daily question. No name or birth date is requested; the choice stays on the device. Age does not measure ability."))
  Text(label(language,if(age==0) "Henüz seçilmedi" else "$age yaş",if(age==0) "Not selected" else "Age $age"))
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
   OutlinedButton(onClick={age=(if(age==0) 6 else age-1).coerceIn(4,12);ReminderScheduler.prefs(context).edit().putInt("child_age",age).apply()}) { Text("−") }
   OutlinedButton(onClick={age=(if(age==0) 6 else age+1).coerceIn(4,12);ReminderScheduler.prefs(context).edit().putInt("child_age",age).apply()}) { Text("+") }
   TextButton(onClick={age=0;ReminderScheduler.prefs(context).edit().remove("child_age").apply()}) { Text(label(language,"Sil","Clear")) }
  }
  Text(label(language,"Öğrenme hatırlatıcıları","Learning reminders"),style=MaterialTheme.typography.titleLarge)
  Text(label(language,"Varsayılan olarak iki günde bir, yaklaşık 18.00'de sessiz bir hatırlatıcı gönderilir. Yakın zamanda ziyaret ettiysen gönderilmez. Buradan sıklığı değiştirebilir veya kapatabilirsin. Android teslimi geciktirebilir.","A quiet reminder is scheduled every two days around 6 pm by default. It is skipped after a recent visit. Change the frequency or turn it off here. Android may delay delivery."))
  listOf(0 to label(language,"Kapalı","Off"),1 to label(language,"Günde bir","Once a day"),2 to label(language,"İki günde bir","Every two days")).forEach { (value,title) ->
   OutlinedButton(onClick={setDays(value)},modifier=Modifier.fillMaxWidth().testTag("reminder_$value")) { Text((if(days==value) "✓ " else "")+title) }
  }
  if(!ReminderScheduler.permitted(context))Text(label(language,"Bildirimler için Android izni gerekir. Sistem ayarlarından izin ve kanal tercihini değiştirebilirsiniz.","Android permission is required. You can change permission and channel preferences in system settings."))
  if(!ReminderScheduler.permitted(context)) TextButton(onClick={
   val settings=if(Build.VERSION.SDK_INT>=26) Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE,context.packageName)
    else Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:${context.packageName}"))
   context.startActivity(settings)
  }) { Text(label(language,"Android bildirim ayarlarını aç","Open Android notification settings")) }
  if(message.isNotBlank())Text(message)
  Text(label(language,"Hesap gerekmez. İlerleme, yaş tercihi ve hatırlatıcı zamanları cihazda saklanır. Atölye sonunda seyrek Google AdMob reklamları gösterilebilir; reklamlar internet kullanır. Tüm reklam istekleri çocuklara uygun ve kişiselleştirmesiz yapılandırılır.","No account is required. Progress, age preference and reminder times stay on the device. Occasional Google AdMob ads may appear after workshops and use the internet. All ad requests use child treatment and non-personalized settings."))
  if(privacyOptionsRequired)TextButton(onClick=onPrivacyOptions) { Text(label(language,"Reklam gizlilik seçenekleri","Ad privacy options")) }
  Text(label(language,"Sesli anlatım","Narration"),style=MaterialTheme.typography.titleLarge)
  val available=ready && ChildNarration.configure(tts,language)
  var selectedVoice by remember(language) {mutableStateOf(ReminderScheduler.prefs(context).getString("narrator_voice_${language.name}",null))}
  if(ready) {
   val voices=ChildNarration.choices(tts,language)
   if(voices.isNotEmpty())Text(label(language,"Anlatıcı seçimi: her sesi örnek dinleyerek seçebilirsin. Seçimin bu dildeki oyunlarda ve hikâyelerde korunur. Sesler cihazın motoruna bağlıdır.","Choose a narrator by listening to each sample. Your choice is kept for this language's games and stories. Voices depend on the device's engine."))
   voices.forEachIndexed { index,voice ->
    val family=voice.name.removeSuffix("-local")
    OutlinedButton(onClick={
     selectedVoice=family;ChildNarration.choose(language,voice.name,ReminderScheduler.prefs(context))
     if(ChildNarration.configure(tts,language,story=true))tts.speak(label(language,"Merhaba! Ben senin anlatıcınım. Küçük bir roket, yıldızları keşfetmek için yola çıktı. Önce düşündü, sonra planını adım adım uyguladı.","Hello! I am your narrator. A little rocket set out to explore the stars. First it thought, then it followed its plan one step at a time."),TextToSpeech.QUEUE_FLUSH,null,"voice_choice")
    },modifier=Modifier.fillMaxWidth().testTag("voice_choice_$index")) {Text(label(language,"Anlatıcı ${index+1} · Dinle ve seç","Narrator ${index+1} · Listen and select")+if(selectedVoice==family) " ✓" else "")}
   }
  }
  Text(if(available)label(language,"Bu dil için cihazda çevrimdışı ses hazır. Anlatım sakin ve anlaşılır bir hız kullanır.","An offline voice is ready for this language. Narration uses a calm, clear pace.") else label(language,"Bu dil için çevrimdışı ses paketi bulunamadı. Yanlış dilde ses kullanılmaz; yazılı yönergeler her zaman kullanılabilir.","No offline voice was found for this language. Another language will not be substituted; written instructions are always available."))
  Button(onClick={speak(tts,label(language,"Merhaba! Birlikte keşfedelim. Önce düşünelim, sonra adım adım deneyelim. Kırk, altmış, yetmiş. Ce, çe, yumuşak ge.","Hello! Let us explore together. Think first, then try one step at a time. Forty, sixty, seventy."),language,true)},enabled=available,modifier=Modifier.testTag("voice_sample")) { Text(label(language,"Örnek anlatımı dinle","Listen to a sample")) }
  OutlinedButton(onClick={runCatching { context.startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }}) { Text(label(language,"Cihazın ses paketlerini aç","Open device voice packages")) }
  bannerContent()
 }
}
