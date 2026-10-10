package com.algokids
/** Grant the test emulator's notification permission before launching the Activity. */
class AuditPermissionRule:org.junit.rules.TestRule {
 override fun apply(base:org.junit.runners.model.Statement,description:org.junit.runner.Description)=
  object:org.junit.runners.model.Statement() {
   override fun evaluate() {
    if(android.os.Build.VERSION.SDK_INT>=33) {
     val i=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
     val command="pm grant ${i.targetContext.packageName} android.permission.POST_NOTIFICATIONS"
     android.os.ParcelFileDescriptor.AutoCloseInputStream(i.uiAutomation.executeShellCommand(command)).use {it.readBytes()}
    }
    base.evaluate()
   }
  }
}
