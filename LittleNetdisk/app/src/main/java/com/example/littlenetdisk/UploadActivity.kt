package com.example.littlenetdisk

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import kotlinx.android.synthetic.main.activity_upload.*
import okhttp3.Call
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import kotlin.random.Random


class UploadActivity : AppCompatActivity() {
    private lateinit var uri: Uri
    private val requestUrl = "http://37x366u444.wicp.vip/OnlineAlbum_Jessie_war/user/image/upload"

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        choosePicture.setOnClickListener {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.action = Intent.ACTION_OPEN_DOCUMENT
            } else {
                intent.action = Intent.ACTION_GET_CONTENT
            }
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        upload.setOnClickListener {
            /*Toast.makeText(this, fid.text.toString().toInt(), Toast.LENGTH_SHORT).show()
            Log.d("tag",fid.text.toString())
            val file =uri.toFile(this)
            if (file!=null){
                Toast.makeText(this, "path=${file.absolutePath}", Toast.LENGTH_SHORT).show()
                //Log.d("tag",file.absolutePath)
                //thread {
                val request = UploadUtil.uploadImage(this,fid.text.toString().toInt(), file, requestUrl)
                //Toast.makeText(this, "request=", Toast.LENGTH_SHORT).show()
                //Log.d("tag","request")
                //}
            }*/
            val prefs=getSharedPreferences("LogActivity",Context.MODE_PRIVATE)
            val result=prefs.getString("cookie","")
            if(result==""||result==null) Toast.makeText(this,"未登录，无法上传",Toast.LENGTH_SHORT).show()
            else{
                var file:File?=null
                if (fid.text.toString()=="") {
                    Toast.makeText(this, "请检查上传路径和上传的图片", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "uri=$uri", Toast.LENGTH_SHORT).show()
                    file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Toast.makeText(this, "大于", Toast.LENGTH_SHORT).show()
                        uriToFileQ(this,uri)
                    } else {
                        Toast.makeText(this, "小于", Toast.LENGTH_SHORT).show()
                        File(getFilePathFromUri(this,uri))
                    }
                    Toast.makeText(this, "path=${file?.absolutePath}", Toast.LENGTH_SHORT).show()
                    if(file!=null){
                        UploadUtil.uploadImageFile(this,result,fid.text.toString(),file,object :okhttp3.Callback{
                            override fun onFailure(call: Call, e: IOException) {
                                Toast.makeText(this@UploadActivity,"上传失败",Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call, response: Response) {
                                when(response.body.toString().toInt()){
                                    200->{
                                        Toast.makeText(this@UploadActivity, "上传成功", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    400->Toast.makeText(this@UploadActivity, "请求失败", Toast.LENGTH_SHORT).show()
                                    401->Toast.makeText(this@UploadActivity, "请检查是否登录", Toast.LENGTH_SHORT).show()
                                    403->Toast.makeText(this@UploadActivity, "权限不够", Toast.LENGTH_SHORT).show()
                                    404->Toast.makeText(this@UploadActivity, "没找到", Toast.LENGTH_SHORT).show()
                                    500->Toast.makeText(this@UploadActivity, "服务器内部方法错误", Toast.LENGTH_SHORT).show()
                                    else->Toast.makeText(this@UploadActivity,"其他错误",Toast.LENGTH_SHORT).show()
                                }
                            }

                        })
                    }else{
                        Toast.makeText(this,"文件有问题",Toast.LENGTH_SHORT).show()
                    }
                }

            }

        /*if (file != null) {
                    Toast.makeText(this, "文件不为null，待上传,path=${file.absolutePath}", Toast.LENGTH_SHORT).show()
                    val request = UploadUtil.uploadImage(this,fid.text.toString().toInt(), file, requestUrl)
                    Log.d("tag", "request: $request")
                    when(request){
                        200->{
                            Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        400->Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show()
                        401->Toast.makeText(this, "请检查是否登录", Toast.LENGTH_SHORT).show()
                        403->Toast.makeText(this, "权限不够", Toast.LENGTH_SHORT).show()
                        404->Toast.makeText(this, "没找到", Toast.LENGTH_SHORT).show()
                        500->Toast.makeText(this, "服务器内部方法错误", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "上传的文件有问题", Toast.LENGTH_SHORT).show()
                }*/


        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (data.data != null) {
                        uri = data.data!!
                        Log.d("tag", "uri= $uri")
                        try {
                            val bitmap = getBitmapFromUri(uri)
                            show.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("tag", "无数据返回")
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    /*private fun alert() {
        AlertDialog.Builder(this).setTitle("提示")
            .setMessage("你选择的不是有效图片")
            .setPositiveButton("确定") { _, _ ->
                picturePath = ""
            }.show()
    }*/
/*
    //uri转path
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        if (context == null || uri == null) {
            return ""
        }
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
        val sdkVersion = Build.VERSION.SDK_INT
        if (sdkVersion >= Build.VERSION_CODES.KITKAT) {
            return getRealFilePath(context, uri)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                if ("primary".equals(type, true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return contentUri?.let { getDataColumn(context, it, selection, selectionArgs) }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return uriToFileApiQ(context, uri);
        } else if ("content".equals(uri.scheme, true)) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment;
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equals(uri.scheme, true)) {
            return uri.path;
        }
        return ""
    }

    private fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority
    private fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority
    private fun isGooglePhotosUri(uri: Uri)="com.google.android.apps.photos.content" == uri.authority
    private fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority


    /*private fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri)=getDataColumn(
        uri,
        null,
        null
    )
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getRealPathFromUriAboveAPI19(context: Context, uri: Uri):String{
        var path=""
        if(DocumentsContract.isDocumentUri(context, uri)){
            val documentId=DocumentsContract.getDocumentId(uri)
            Toast.makeText(context, "documentId=$documentId", Toast.LENGTH_SHORT).show()
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                if ("primary".equals(type, true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            }else if(isMediaDocument(uri)) {
                val id = documentId.split(":")[1]
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)
                path=getDataColumn(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selection,
                    selectionArgs
                )
                Toast.makeText(this, "isMediaDocument:$path", Toast.LENGTH_SHORT).show()
            }else if(isDownloadsDocument(uri)){
                val contentUri=ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    documentId.toLong()
                )
                path=getDataColumn(contentUri, null, null)
                Toast.makeText(this, "isDownloadDocument:$path", Toast.LENGTH_SHORT).show()
            }
        }else if("content".equals(uri.scheme, true)){
            path=getDataColumn(uri, null, null)
            Toast.makeText(this, "isContent:$path", Toast.LENGTH_SHORT).show()
        }else if("file"==uri.scheme){
            path=uri.path.toString()
            Toast.makeText(this, "isFile:$path", Toast.LENGTH_SHORT).show()

        }
        return path
    }
    private fun getDataColumn(uri: Uri, selection: String?, selectionArgs: Array<String>?):String{
        var path=""
        val projection= arrayOf(MediaStore.Images.Media.DATA)
        var cursor:Cursor? = null
        try {
            cursor=contentResolver.query(uri, projection, selection, selectionArgs, null)
            if(cursor!=null&&cursor.moveToFirst()){
                val columnIndex=cursor.getColumnIndexOrThrow(projection[0])
                path=cursor.getString(columnIndex)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            cursor?.close()
        }
        return path
    }*/

    /*fun one(context: Context, uri: Uri):String{
        var filePath=""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            val wholeID = DocumentsContract.getDocumentId(uri)
            val id = wholeID.split(":")[1]
            val column = arrayOf(MediaStore.Images.Media.DATA)
            val sel = MediaStore.Images.Media._ID + "=?"
            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf(
                    id
                ), null
            )
            val columnIndex = cursor?.getColumnIndex(column[0])
            if (cursor?.moveToFirst()==true) {
                filePath = columnIndex?.let { cursor.getString(it) }.toString()
            }
            cursor?.close();
        }else{
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst();
            filePath =columnIndex?.let { cursor.getString(it) }.toString()

        }
        return filePath
    }*/


    private fun getRealFilePath(context: Context, uri: Uri): String {
        if (null == uri) {
            return ""
        }
        val scheme = uri.scheme
        var data = ""
        if (scheme == null) {
            data = uri.path.toString()
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path.toString()
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val projection = arrayOf(MediaStore.Images.ImageColumns.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)

//            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            cursor?.close()
        }
        return ""
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun uriToFileApiQ(context: Context, uri: Uri): String? {
        var file: File? = null
        //android10以上转换
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = File(uri.path)
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件复制到沙盒目录
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            if(cursor!=null)
            if (cursor.moveToFirst()) {
                val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                try {
                    val fis: InputStream? = contentResolver.openInputStream(uri)
                    val cache = File(
                        context.externalCacheDir!!.absolutePath,
                        ((Math.random() + 1) * 1000).roundToInt().toString() + displayName
                    )
                    val fos = FileOutputStream(cache)
                    if (fis != null) {
                        FileUtils.copy(fis, fos)
                    }
                    file = cache
                    fos.close()
                    fis?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return file?.absolutePath
    }*/
    private fun getFilePathFromUri(context: Context,uri: Uri):String{
        var path=""
        if(ContentResolver.SCHEME_FILE == uri.scheme){
            path=uri.path.toString()
            return path
        }
        if(ContentResolver.SCHEME_CONTENT==uri.scheme&&Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
            val cursor=context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA),null,null,null)
            if(cursor!=null){
                if(cursor.moveToFirst()){
                    val index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if(index>-1) path=cursor.getString(index)
                }
                cursor.close()
            }
            return path
        }
        if(ContentResolver.SCHEME_CONTENT==uri.scheme&&Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            if(DocumentsContract.isDocumentUri(context,uri)){
                if(isExternalStorageDocument(uri)){
                    val id=DocumentsContract.getDocumentId(uri)
                    val spilt=id.split(":")
                    val type=spilt[0]
                    if("primary".equals(type,true)){
                        path= Environment.getExternalStorageDirectory().toString()+"/"+spilt[1]
                    }
                }else if(isDownloadsDocument(uri)){
                    val id=DocumentsContract.getDocumentId(uri)
                    val contentUri=ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),id.toLong())
                    path=getDataColumn(context,contentUri,null,null)
                    return path
                }else if(isMediaDocument(uri)){
                    Toast.makeText(context,"isMedia",Toast.LENGTH_SHORT).show()
                    val id=DocumentsContract.getDocumentId(uri)
                    Toast.makeText(context,id,Toast.LENGTH_SHORT).show()
                    val spilt=id.split(":")
                    Toast.makeText(context,spilt[0]+"##"+spilt[1],Toast.LENGTH_SHORT).show()
                    val type=spilt[0]
                    var contentUri:Uri?=null
                    if("image"==type){
                        contentUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }else if("video"==type){
                        contentUri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }else if("audio"==type){
                        contentUri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    Toast.makeText(context,contentUri.toString(),Toast.LENGTH_SHORT).show()
                    val selection="_id=?"
                    val selectionArgs= arrayOf(spilt[1])
                    path =getDataColumn(context,contentUri,selection,selectionArgs)
                    Toast.makeText(context,path,Toast.LENGTH_SHORT).show()
                    return path
                }
            }
        }
        return ""
    }
    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        if(uri!=null){
            try {
                cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                cursor?.close()
            }
        }

        return ""
    }
    private fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority
    private fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority
    private fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority
   /* private fun Uri.toFile(context: Context): File? = when (scheme) {
        ContentResolver.SCHEME_FILE -> toFile()
        ContentResolver.SCHEME_CONTENT -> {
            val cursor = context.contentResolver.query(this, null, null, null, null)
            val file = cursor?.let {
                if (it.moveToFirst()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        //保存到本地
                        val ois = context.contentResolver.openInputStream(this)
                        val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        ois?.let {it->
                            val file = File(context.externalCacheDir!!.absolutePath, "${Random.nextInt(0, 9999)}$displayName")
                            val fos = FileOutputStream(file)
                            FileUtils.copy(ois, fos)
                            fos.close()
                            it.close()
                            file
                        }
                    } else
                    //直接转换
                        File(it.getString(it.getColumnIndex(MediaStore.Images.Media.DATA)))
                } else null

            }
            cursor?.close()
            file
        }
        else -> null
    }*/
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uriToFileQ(context: Context, uri: Uri): File? =
        if (uri.scheme == ContentResolver.SCHEME_FILE)
            File(requireNotNull(uri.path))
        else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件保存到沙盒
            var cursor:Cursor?=null
            val contentResolver = context.contentResolver
            val displayName = run {
                cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.let {
                    if(it.moveToFirst()) it.getString(cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    else null
                }
            }?:"${System.currentTimeMillis()}${Random.nextInt(0, 9999)}.${MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))}"
            cursor?.close()
            val ios = contentResolver.openInputStream(uri)
            if (ios != null) {
                File("${context.externalCacheDir!!.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            } else null
        } else null
}