package com.algokids
import android.Manifest
import android.os.Build
import android.app.NotificationManager
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.algokids.notifications.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
@RunWith(AndroidJUnit4::class)
class ReminderIntegrationTest {
 @Test fun permissionSchedulingDeliveryDeduplicationAndDisable() {
  val instrumentation=InstrumentationRegistry.getInstrumentation()
  val c=instrumentation.targetContext
  val edit=ReminderScheduler.prefs(c).edit().putBoolean("ads_disabled_for_tests",true).putString("language","TR")
  com.algokids.game.model.ChallengeCatalog.all.forEach { workshop ->
   edit.putBoolean("${com.algokids.data.Curriculum.progressKey(workshop.id)}_done",true)
   workshop.rounds.indices.forEach { edit.putInt(com.algokids.data.LearningPath.key(workshop.id,"TR",it),100) }
  }
  edit.commit()
  if(Build.VERSION.SDK_INT>=33)InstrumentationRegistry.getInstrumentation().uiAutomation.grantRuntimePermission(c.packageName,Manifest.permission.POST_NOTIFICATIONS)
  val scenario=ActivityScenario.launch(MainActivity::class.java)
  scenario.onActivity { /* Wait until onStart/onResume have finished before setting the simulated clock. */ }
  instrumentation.waitForIdleSync()
  val p=ReminderScheduler.prefs(c)
  val now=Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY,18) }.timeInMillis
  p.edit().putString("language","TR").putString("recent_category","MEMORY").putLong("last_visit",now-3*ReminderRules.DAY).putLong("reminder_last_sent",0).commit()
  ReminderScheduler.configure(c,2)
  assertTrue("days=${p.getInt("reminder_days",0)} permitted=${ReminderScheduler.permitted(c)} visit=${p.getLong("last_visit",0)} now=$now",ReminderScheduler.deliver(c,now))
  val notification=c.getSystemService(NotificationManager::class.java).activeNotifications.single { it.id==ReminderScheduler.ID }
  assertEquals("Küçük bir keşfe hazır mısın?",notification.notification.extras.getString("android.title"))
  assertFalse(ReminderScheduler.deliver(c,now))
  // System-panel click is verified separately with the actual device UI;
  // instrumentation accessibility actions are unreliable on this API 37 image.
  p.edit().putString("language","EN").putLong("last_visit",now-3*ReminderRules.DAY).putLong("reminder_last_sent",0).commit()
  assertTrue(ReminderScheduler.deliver(c,now))
  assertEquals("Ready for a little discovery?",c.getSystemService(NotificationManager::class.java).activeNotifications.single { it.id==ReminderScheduler.ID }.notification.extras.getString("android.title"))
  ReminderScheduler.configure(c,0)
  val cancellationDeadline=android.os.SystemClock.elapsedRealtime()+3000
  while(c.getSystemService(NotificationManager::class.java).activeNotifications.any { it.id==ReminderScheduler.ID } && android.os.SystemClock.elapsedRealtime()<cancellationDeadline)android.os.SystemClock.sleep(50)
  assertTrue(c.getSystemService(NotificationManager::class.java).activeNotifications.none { it.id==ReminderScheduler.ID })
  assertFalse(ReminderScheduler.deliver(c,now))
  p.edit().putString("language","TR").commit()
  scenario.close()
 }
 @Test fun automaticTwoDayDefaultAndManualOffPersist() {
  val c=InstrumentationRegistry.getInstrumentation().targetContext
  val p=ReminderScheduler.prefs(c)
  p.edit().remove("automatic_reminders_v1").putInt("reminder_days",0).commit()
  ReminderScheduler.visit(c)
  assertEquals(2,p.getInt("reminder_days",0))
  ReminderScheduler.configure(c,0)
  ReminderScheduler.visit(c)
  assertEquals(0,p.getInt("reminder_days",2))
 }
}
