package com.jacky.biz_cusview.fragment

import android.graphics.Color
import android.graphics.Paint
import android.graphics.SurfaceTexture
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.jacky.biz_cusview.R
import com.jacky.foundation.log.HiLog
import com.jacky.foundation.log.HiLogType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * TextureView与SurfaceView相比，TextureView并没有创建一个单独的Surface用来绘制，
 * 这使得它可以像一般的View一样执行一些变换操作，设置透明度等。
 *
 * 另外，TextureView必须在硬件加速开启的窗口中。
 */
class TextureViewFragment : Fragment() {

    private lateinit var textureView: TextureView
    private var player: MediaPlayer = MediaPlayer()
    private var isPrepared: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.fragment_texture_view, container, false)
        initView(rootView)
        // 开启硬件加速
        // requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        return rootView
    }

    private fun initView(rootView: View) {
        textureView = rootView.findViewById(R.id.textureView)
        player.setOnPreparedListener { mp ->
            HiLog.log(HiLogType.D, TAG, "onPrepared")
            isPrepared = true
        }

        rootView.findViewById<Button>(R.id.canvas).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val canvas = textureView.lockCanvas()!!
                val paint = Paint()
                paint.setColor(Color.RED)
                canvas.drawCircle(100f, 100f, 100f, paint)
                textureView.unlockCanvasAndPost(canvas)
                HiLog.log(HiLogType.D, TAG, "draw threadName:${Thread.currentThread().name}")
            }
        }
        textureView.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                HiLog.log(HiLogType.D, TAG,  "onSurfaceTextureAvailable")
                val surfaceX = Surface(surface)
                player.setSurface(surfaceX)
                player.setDataSource(requireContext().assets.openFd("video.mp4"))
                player.prepareAsync()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                HiLog.log(HiLogType.D, TAG,  "onSurfaceTextureSizeChanged")
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                HiLog.log(HiLogType.D, TAG,  "onSurfaceTextureDestroyed")
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                HiLog.log(HiLogType.D, TAG,  "onSurfaceTextureUpdated")
            }
        }
        textureView.surfaceTexture?.setOnFrameAvailableListener(object : OnFrameAvailableListener {
            override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
                HiLog.log(HiLogType.D, TAG,  "onFrameAvailable")
            }
        })

        rootView.findViewById<Button>(R.id.playVideo).setOnClickListener {
            if (isPrepared) {
                player.start()
                // 与surfaceView不同的是：画面内容会跟view一起旋转。
                textureView.animate().rotationBy(360f).setDuration(9000).start()
            }
        }
    }

    companion object {
        private const val TAG = "TextureViewFragment"
    }
}