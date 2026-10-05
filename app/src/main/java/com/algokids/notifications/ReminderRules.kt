package com.algokids.notifications
import java.util.Calendar
object ReminderRules {
 const val DAY=86400000L
 fun next(now:Long,lastVisit:Long,lastSent:Long,days:Int):Long {
  val earliest=maxOf(now,lastVisit+days.coerceIn(1,2)*DAY,lastSent+days.coerceIn(1,2)*DAY)
  val c=Calendar.getInstance().apply { timeInMillis=earliest;set(Calendar.HOUR_OF_DAY,18);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0) }
  if(c.timeInMillis<earliest)c.add(Calendar.DAY_OF_YEAR,1)
  return c.timeInMillis
 }
 fun shouldSend(now:Long,lastVisit:Long,lastSent:Long,days:Int):Boolean {
  val hour=Calendar.getInstance().apply { timeInMillis=now }.get(Calendar.HOUR_OF_DAY)
  return hour in 9..20 && now-lastVisit>=days*DAY && (lastSent==0L || now-lastSent>=days*DAY)
 }
}
