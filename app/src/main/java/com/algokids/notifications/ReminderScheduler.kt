package com.algokids.notifications
import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.algokids.MainActivity
import com.algokids.R
object ReminderScheduler {
 const val CHANNEL="learning_reminders"
 const val ID=210
 fun prefs(c:Context)=c.getSharedPreferences("algokids_progress",Context.MODE_PRIVATE)
 fun permitted(c:Context)= (Build.VERSION.SDK_INT<33 || ContextCompat.checkSelfPermission(c,Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED) && NotificationManagerCompat.from(c).areNotificationsEnabled() && (Build.VERSION.SDK_INT<26 || c.getSystemService(NotificationManager::class.java).getNotificationChannel(CHANNEL)?.importance!=NotificationManager.IMPORTANCE_NONE)
 private fun intent(c:Context)=PendingIntent.getBroadcast(c,ID,Intent(c,ReminderReceiver::class.java).setAction("com.algokids.REMIND"),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
 fun channel(c:Context) {
  if(Build.VERSION.SDK_INT>=26)c.getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(CHANNEL,"AlgoKids",NotificationManager.IMPORTANCE_DEFAULT).apply { description="Optional learning reminders / İsteğe bağlı öğrenme hatırlatmaları";enableVibration(false);setSound(null,null) })
 }
 fun configure(c:Context,days:Int) {
  prefs(c).edit().putInt("reminder_days",days.coerceIn(0,2)).apply();channel(c);schedule(c)
  if(days==0)NotificationManagerCompat.from(c).cancel(ID)
 }
 fun visit(c:Context) {
  val p=prefs(c)
  if(!p.getBoolean("automatic_reminders_v1",false))p.edit().putInt("reminder_days",2).putBoolean("automatic_reminders_v1",true).apply()
  channel(c);p.edit().putLong("last_visit",System.currentTimeMillis()).apply();NotificationManagerCompat.from(c).cancel(ID);schedule(c)
 }
 fun schedule(c:Context) {
  val alarm=c.getSystemService(AlarmManager::class.java);alarm.cancel(intent(c))
  val p=prefs(c);val days=p.getInt("reminder_days",2)
  if(days==0 || !permitted(c))return
  val next=ReminderRules.next(System.currentTimeMillis(),p.getLong("last_visit",System.currentTimeMillis()),p.getLong("reminder_last_sent",0),days)
  alarm.setWindow(AlarmManager.RTC_WAKEUP,next,3600000L,intent(c))
 }
 fun deliver(c:Context,now:Long=System.currentTimeMillis()):Boolean {
  val p=prefs(c);val days=p.getInt("reminder_days",2)
  if(days==0 || !permitted(c) || !ReminderRules.shouldSend(now,p.getLong("last_visit",now),p.getLong("reminder_last_sent",0),days)) { schedule(c);return false }
  val en=p.getString("language","TR")=="EN"
  val recent=runCatching { com.algokids.game.model.GameCategory.valueOf(p.getString("recent_category","")!!) }.getOrNull()
  val category=recent?.let { com.algokids.ui.screens.categoryTitle(it,if(en) com.algokids.ui.screens.AppLanguage.EN else com.algokids.ui.screens.AppLanguage.TR) }
  val title=if(en) "Ready for a little discovery?" else "Küçük bir keşfe hazır mısın?"
  val body=if(category!=null) { if(en) "Continue exploring $category. Your progress is waiting for you." else "$category atölyelerinde keşfe devam et. İlerlemen seni bekliyor." } else if(en) "Plan, try and discover a new solution with AlgoKids." else "AlgoKids ile planla, dene ve yeni bir çözüm keşfet."
  val launch=PendingIntent.getActivity(c,ID,Intent(c,MainActivity::class.java).putExtra("resume_category",recent?.name).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  channel(c)
  try { NotificationManagerCompat.from(c).notify(ID,NotificationCompat.Builder(c,CHANNEL).setSmallIcon(R.drawable.ic_notification).setContentTitle(title).setContentText(body).setStyle(NotificationCompat.BigTextStyle().bigText(body)).setContentIntent(launch).setAutoCancel(true).setOnlyAlertOnce(true).build()) } catch(_:SecurityException) { schedule(c);return false }
  p.edit().putLong("reminder_last_sent",now).apply();schedule(c);return true
 }
}
class ReminderReceiver:BroadcastReceiver() {
 override fun onReceive(context:Context,intent:Intent) {
  if(intent.action=="com.algokids.REMIND")ReminderScheduler.deliver(context) else ReminderScheduler.schedule(context)
 }
}
