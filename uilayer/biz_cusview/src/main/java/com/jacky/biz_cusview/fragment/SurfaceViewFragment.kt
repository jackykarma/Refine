package com.jacky.biz_cusview.fragment

import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
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
 * SurfaceView可提供一块Surface，可以用Canvas在子线程绘制View
 * 1. canvas绘制各种2D内容
 * 2. surface可用于接收player的视频: 本质就是按照一定的帧率绘制画面真
 * 3. 用canvas绘制动画（多帧）
 */
class SurfaceViewFragment : Fragment() {

    private lateinit var surfaceView: SurfaceView
    private lateinit var player: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_surface_view, container, false)
        player = MediaPlayer()
        surfaceView = rootView.findViewById(R.id.surfaceView)
        rootView.findViewById<Button>(R.id.playVideo).setOnClickListener {
            // 可以得到画布对象，最终内容都是渲染到其画布上的; surface在底层对应一块图形buffer
            val surface = surfaceView.holder.surface
            player.setDataSource(requireContext().assets.openFd("video.mp4"))
            // player.setDisplay(surfaceView.holder)
            player.setSurface(surface)
            player.prepareAsync()
            player.setOnPreparedListener { mp ->
                HiLog.log(HiLogType.D, TAG, "onPrepared")
                mp.start()
                // FIXME:surfaceView做动画可以。但是其中的画布的内容(视频）是不会做动画的。
                //  比如此处的渲染，view渲染了，但是画面内容不会旋转
                surfaceView.animate().rotationBy(360f).setDuration(9000).start()
            }
        }
        rootView.findViewById<Button>(R.id.canvas).setOnClickListener {
            // 得到SurfaceView画布的Canvas对象，用2D绘制其画布Surface
            GlobalScope.launch(Dispatchers.IO) {
                val canvas = surfaceView.holder.lockCanvas()
                val paint = Paint()
                paint.setColor(Color.RED)
                canvas.drawCircle(100f, 100f, 100f, paint)
                surfaceView.holder.unlockCanvasAndPost(canvas)
                HiLog.log(HiLogType.D, TAG, "surfaceCreated threadName:${Thread.currentThread().name}")
            }
        }
        initView()
        return rootView
    }

    private fun initView() {
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                HiLog.log(HiLogType.D, TAG, "surfaceCreated")
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                HiLog.log(HiLogType.D, TAG, "surfaceChanged")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                HiLog.log(HiLogType.D, TAG, "surfaceDestroyed")
            }
        })
    }

    companion object {
        private const val TAG = "SurfaceViewFragment"
    }
}