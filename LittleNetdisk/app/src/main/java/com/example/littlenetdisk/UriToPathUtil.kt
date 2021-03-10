package com.example.littlenetdisk

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.*
import java.lang.StringBuilder

object UriToPathUtil {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getAbsolutePath(context: Context, uri: Uri):String{
        return if(Build.VERSION.SDK_INT>=24){
            getRealPathFromURI(context,uri)
        }else{
            getRealPathFromUri(context,uri)
        }
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getRealPathFromURI(context: Context,uri: Uri):String{
        val rootDataDir=context.filesDir
        val fileName= getFileName(context,uri)
        if(fileName.isNotEmpty()){
            val copyFile= File(rootDataDir.absolutePath+File.separator+fileName+".jpg")
            //Toast.makeText(context,"root:${rootDataDir.absolutePath},file:${File.separator},filename:$fileName,path:${rootDataDir.path},path:${rootDataDir.parent}",Toast.LENGTH_LONG).show()
            copyFile(context,uri,copyFile)
            return copyFile.absolutePath
        }
        return ""
    }
    fun getFileName(context: Context,uri: Uri):String{
        if(uri==null) return ""
        var fileName=""
        val path=uri.path
        //Toast.makeText(context,"uri.path:$path",Toast.LENGTH_LONG).show()
        val cnt:Int?= path?.lastIndexOf('/')
        if(cnt!=-1) {
            if (cnt != null) {
                fileName=path.substring(cnt+1)
            }
        }
        return System.currentTimeMillis().plus(fileName)
    }
    fun copyFile(context: Context,uri: Uri,file: File){
        try {
            val input= context.contentResolver.openInputStream(uri) ?: return
            val output=FileOutputStream(file)
            copyStream(input,output)
            input.close()
            output.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun copyStream(input:InputStream,output: OutputStream) {
        val bufferSize=1024*2
        var buffer=ByteArray(bufferSize)
        val bin=BufferedInputStream(input,bufferSize)
        val bout=BufferedOutputStream(output,bufferSize)
        var count=0
        var n=0
        try{
            n=bin.read(buffer,0,bufferSize)
            while(n!=-1){
                bout.write(buffer,0,n)
                count+=n
                n=bin.read(buffer,0,bufferSize)
            }
            bout.flush()
        }catch (e:Exception){
            e.printStackTrace()
        } finally {
            try{
                bout.close()
            }catch (e:IOException){
                e.printStackTrace()
            }
            try {
                bin.close()
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getRealPathFromUri(context: Context, uri: Uri):String{
        /*var filepath="1"
        if(DocumentsContract.isDocumentUri(context, uri)){
            val documentId=DocumentsContract.getDocumentId(uri)
            val id=documentId.split(":")[1]
            val column=arrayOf(MediaStore.Images.Media.DATA)
            val selection=MediaStore.Images.Media._ID+"=?"
            val selectionArgs= arrayOf(id)
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column,
                selection,
                selectionArgs,
                null
            )
            /*if(cursor==null) Toast.makeText(this,"cursor为null",Toast.LENGTH_SHORT).show()
            else Toast.makeText(this,"cursor不为null",Toast.LENGTH_SHORT).show()*/
            val columnIndex = cursor?.getColumnIndex(column[0])
            if (cursor?.moveToFirst() == true) {
                filepath = columnIndex?.let { cursor.getString(it) }.toString()
            }
            cursor?.close();
        }else{
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            filepath = columnIndex?.let { cursor.getString(it) }.toString()
        }
        return filepath*/
        val sdkVersion= Build.VERSION.SDK_INT
        return if(sdkVersion>=19){
            getRealPathFromUriAboveAPI19(context,uri)
        }else{
            getRealPathFromUriBelowAPI19(context, uri)
        }
    }
    fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri)=getDataColumn(context,uri, null, null)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getRealPathFromUriAboveAPI19(context: Context, uri: Uri):String{
        var filepath=""
        if(DocumentsContract.isDocumentUri(context, uri)){
            val documentId= DocumentsContract.getDocumentId(uri)
            if(isMediaDocument(uri)) {
                val id = documentId.split(":")[1]
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)
                filepath=getDataColumn(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection,selectionArgs)
                Toast.makeText(context,"isMediaDocument:$filepath", Toast.LENGTH_SHORT).show()
            }else if(isDownloadsDocument(uri)){
                val contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),documentId.toLong())
                filepath=getDataColumn(context,contentUri,null,null)
                Toast.makeText(context,"isDownloadDocument:$filepath", Toast.LENGTH_SHORT).show()
            }
        }else if("content".equals(uri.scheme,true)){
            filepath=getDataColumn(context,uri,null,null)
            Toast.makeText(context,"isContent:$filepath", Toast.LENGTH_SHORT).show()
        }else if("file"==uri.scheme){
            filepath=uri.path.toString()
            Toast.makeText(context,"isFile:$filepath", Toast.LENGTH_SHORT).show()

        }
        return filepath
    }
    fun isMediaDocument(uri: Uri)= "com.android.providers.media.documents" == uri.authority
    fun isDownloadsDocument(uri: Uri)= "com.android.providers.downloads.documents" == uri.authority
    @SuppressLint("Recycle")
    fun getDataColumn(context: Context,uri: Uri, selection: String?, selectionArgs: Array<String>?):String{
        var path=""
        val projection= arrayOf(MediaStore.Images.Media.DATA)
        var cursor:Cursor?=null
        try {
            cursor=context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if(cursor!=null&&cursor.moveToFirst()){
                val columnIndex=cursor.getColumnIndex(projection[0])
                path=cursor.getString(columnIndex)
            }
        }catch (e: Exception){
            cursor?.close()
        }
        return path
    }
}

private operator fun Long.plus(fileName: String): String {
    val bulider=StringBuilder()
    bulider.append(this)
    bulider.append(fileName)
    return bulider.toString()
}