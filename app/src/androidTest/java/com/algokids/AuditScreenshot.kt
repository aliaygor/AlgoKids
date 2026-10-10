package com.algokids
import android.content.Context
import android.graphics.Bitmap
object AuditScreenshot {
 fun save(context:Context,bitmap:Bitmap,name:String) {
  if(android.os.Build.VERSION.SDK_INT>=29) {
   val values=android.content.ContentValues().apply {
    put(android.provider.MediaStore.Images.Media.DISPLAY_NAME,name)
    put(android.provider.MediaStore.Images.Media.MIME_TYPE,"image/png")
    put(android.provider.MediaStore.Images.Media.RELATIVE_PATH,"Pictures/AlgoKidsAudit")
   }
   val uri=context.contentResolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
   if(uri!=null)context.contentResolver.openOutputStream(uri)?.use {bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
  }
 }
}
