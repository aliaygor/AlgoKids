package com.algokids.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.algokids.BuildConfig
import com.algokids.ads.WorkshopAds

@Composable fun AppearanceScreen(language:AppLanguage,ads:WorkshopAds,stopAudio:()->Unit,onBack:()->Unit) {
 val p=LocalContext.current.getSharedPreferences("algokids_progress",0)
 var unlocked by remember {mutableStateOf(p.getBoolean("cosmetic_sunset_unlocked",false))}
 var selected by remember {mutableStateOf(p.getString("cosmetic_theme","ocean"))}
 var confirm by remember {mutableStateOf(false)}
 var message by remember {mutableStateOf("")}
 var tick by remember {mutableIntStateOf(0)}
 LaunchedEffect(ads) {while(true) {kotlinx.coroutines.delay(1000);tick++}}
 val rewardReady=tick.let {ads.rewardReady}
 fun select(theme:String) {selected=theme;p.edit().putString("cosmetic_theme",theme).apply()}
 BackHandler(onBack=onBack)
 Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
  TextButton(onClick=onBack) {Text(label(language,"‹ Ana sayfa","‹ Home"))}
  Text(label(language,"Görünüm seçenekleri","Appearance"),style=MaterialTheme.typography.headlineMedium)
  Text(label(language,"Renkler yalnızca ana sayfanın görünümünü değiştirir. Oyunlar, çözümler, puanlar ve kategoriler aynı kalır.","Colours change only the home screen's appearance. Games, solutions, scores and categories stay the same."))
  Button(onClick={select("ocean")},modifier=Modifier.fillMaxWidth().testTag("theme_ocean")) {Text(label(language,"Okyanus · Ücretsiz","Ocean · Free")+if(selected=="ocean") " ✓" else "")}
  Box(Modifier.fillMaxWidth().height(100.dp).background(Color(0xFF703C47))) {Text(label(language,"Gün batımı","Sunset"),color=Color.White,modifier=Modifier.padding(20.dp))}
  if(unlocked) Button(onClick={select("sunset")},modifier=Modifier.fillMaxWidth().testTag("theme_sunset")) {Text(label(language,"Gün batımını seç","Choose sunset")+if(selected=="sunset") " ✓" else "")}
  else if(BuildConfig.REWARDED_ENABLED) {
   Text(label(language,"İsteğe bağlı: ödüllü reklamı tamamlayarak Gün batımı renk temasını açabilirsin. İzlemeden de tüm öğrenme içeriği kullanılabilir.","Optional: complete a rewarded advertisement to unlock the Sunset colour theme. All learning content is available without watching."))
   OutlinedButton(onClick={confirm=true},enabled=rewardReady,modifier=Modifier.fillMaxWidth().testTag("reward_theme")) {Text(label(language,"Reklam izle · Renk temasını aç","Watch ad · Unlock colour theme"))}
   if(!rewardReady)Text(label(language,"Şu an kullanılabilir reklam yok. Daha sonra tekrar bakabilirsin.","No advertisement is available right now. You can check again later."))
  }
  if(message.isNotEmpty())Text(message)
 }
 if(confirm)AlertDialog(onDismissRequest={confirm=false},title={Text(label(language,"Ödüllü reklam","Rewarded advertisement"))},
  text={Text(label(language,"Reklamı tamamladığında yalnızca Gün batımı renk teması açılır. Erken kapatırsan ödül verilmeyebilir. İzlemek isteğe bağlıdır.","Completing the advertisement unlocks only the Sunset colour theme. Closing early may give no reward. Watching is optional."))},
  confirmButton={TextButton(onClick={confirm=false;ads.rewardTheme(stopAudio) {earned -> unlocked=p.getBoolean("cosmetic_sunset_unlocked",false);message=if(earned)label(language,"Renk teması hazır. Yukarıdan seçebilirsin.","The colour theme is ready. Select it above.") else label(language,"Ödül kazanılmadı veya reklam şu an kullanılamıyor. Öğrenmeye devam edebilirsin.","No reward was earned or an advertisement is unavailable. You can continue learning.") }},modifier=Modifier.testTag("confirm_reward")) {Text(label(language,"Reklamı başlat","Start advertisement"))}},
  dismissButton={TextButton(onClick={confirm=false}) {Text(label(language,"Vazgeç","Cancel"))}})
}
