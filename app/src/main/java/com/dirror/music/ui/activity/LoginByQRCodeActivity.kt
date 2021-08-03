package com.dirror.music.ui.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.dirror.music.databinding.ActivityLoginByQrcodeBinding
import com.dirror.music.manager.User
import com.dirror.music.ui.base.BaseActivity
import com.dirror.music.ui.viewmodel.LoginQRCodeViewModel
import com.dirror.music.util.getStatusBarHeight
import com.dirror.music.util.toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*


class LoginByQRCodeActivity : BaseActivity() {

    companion object {
        const val TAG = "LoginByQRCodeActivity"

    }

    private val viewModel : LoginQRCodeViewModel by viewModels()
    private var syncJob : Job? = null

    lateinit var binding: ActivityLoginByQrcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.doLogin()
    }

    override fun initBinding() {
        binding = ActivityLoginByQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        super.initView()
        binding.ivSearch.setOnClickListener { finish() }
        binding.saveToLocal.setOnClickListener{ saveImageFile() }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.apply {
            mQRCodeBitMutable.observe(this@LoginByQRCodeActivity, {
                binding.qRCodeImage.setImageBitmap(it)
                syncJob?.cancel()
                syncJob =GlobalScope.launch {
                    viewModel.checkLoginStatus()
                }
            })
            mStatusCodeMutable.observe(this@LoginByQRCodeActivity, {
                when(it) {
                    800 -> toast("二维码过期，请退出重试")
                    801 -> toast("等待扫码")
                    802 -> toast("扫码成功，请确认登录")
                    803 -> {
                        if (User.uid == 0L || !User.hasCookie) {
                            toast("获取用户信息失败,请重试")
                            finish()
                            return@observe
                        }
                        toast("登录成功")
                        val intent = Intent("com.dirror.music.LOGIN")
                        intent.setPackage(packageName)
                        sendBroadcast(intent)
                        // 通知 Login 关闭
                        setResult(RESULT_OK, Intent())
                        finish()
                    }
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        syncJob?.cancel()
        syncJob = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.mQRCodeBitMutable.value?.let {
            syncJob?.cancel()
            syncJob =GlobalScope.launch {
                viewModel.checkLoginStatus()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        syncJob?.cancel()
        syncJob = null
    }

    private fun saveImageFile() {
        viewModel.mQRCodeBitMutable.value?.let { bitmap ->
            val picName = "NeteaseLoginQRCode.jpg"
            try {
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos)
                val bis = ByteArrayInputStream(bos.toByteArray())
                insert2Album(bis, picName)
                toast("二维码已保存，请在网易云音乐扫码中选择对应图片")
            } catch (e: Exception) {
                e.printStackTrace()
                toast("二维码保存失败")
            }
        }

    }

    private fun insert2Album(inputStream: InputStream, fileName: String) {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //RELATIVE_PATH 字段表示相对路径-------->(1)
            contentValues.put(
                MediaStore.Images.ImageColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES
            )
        } else {
            val dstPath = (Environment.getExternalStorageDirectory()
                .toString() + File.separator + Environment.DIRECTORY_PICTURES
                    + File.separator + fileName)
            //DATA字段在Android 10.0 之后已经废弃
            contentValues.put(MediaStore.Images.ImageColumns.DATA, dstPath)
        }

        //插入相册------->(2)
        val uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        //写入文件------->(3)
        write2File(uri, inputStream)

        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }

    //uri 关联着待写入的文件
    //inputStream 表示原始的文件流
    private fun write2File(uri: Uri?, inputStream: InputStream?) {
        if (uri == null || inputStream == null) return
        try {
            //从Uri构造输出流
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            val buffer = ByteArray(1024)
            var len = 0
            do {
                //从输入流里读取数据
                len = inputStream.read(buffer)
                if (len != -1) {
                    outputStream?.write(buffer, 0, len)
                    outputStream?.flush()
                }
            } while (len != -1)
            inputStream.close()
            outputStream?.close()
            Log.i(TAG, "while to file $uri finished")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}