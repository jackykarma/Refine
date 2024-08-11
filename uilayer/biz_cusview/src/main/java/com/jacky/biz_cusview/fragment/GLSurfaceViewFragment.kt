package com.jacky.biz_cusview.fragment

import android.app.ActivityManager
import android.content.Context
import android.graphics.SurfaceTexture
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnVideoSizeChangedListener
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.jacky.biz_cusview.R
import com.jacky.foundation.log.HiLog
import com.jacky.foundation.log.HiLogType
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 为啥需要用GLSurfaceView来渲染视频呢？
 * 场景如：渲染视频需要加滤镜等特效，而滤镜算法是OpenGL来做的。那么视频渲染也需要用OpenGL，自然要用GLSurfaceView
 * fixme:遗留视频暂未渲染出图像
 */
class GLSurfaceViewFragment : Fragment() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var triangle: Triangle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.fragment_glsurface_view, container, false)
        initView(rootView)
        return rootView
    }

    private fun initView(rootView: View) {
        glSurfaceView = rootView.findViewById(R.id.glSurfaceView)
        val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configInfo = activityManager.deviceConfigurationInfo
        val supportES2 = configInfo.reqGlEsVersion >= 0x0002000
        HiLog.log(HiLogType.D, TAG, "initView supportES2:$supportES2")
        glSurfaceView.setEGLContextClientVersion(3)

        rootView.findViewById<Button>(R.id.canvas).setOnClickListener {
            glSurfaceView.setRenderer(object : Renderer {
                override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                    HiLog.log(HiLogType.D, TAG, "onSurfaceCreated")
                    // 设置背景颜色
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                    // 初始化triangle
                    triangle = Triangle()
                }

                override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                    HiLog.log(HiLogType.D, TAG, "onSurfaceChanged")
                    GLES20.glViewport(0, 0, width, height);
                }

                override fun onDrawFrame(gl: GL10?) {
                    // HiLog.log(HiLogType.D, TAG, "onDrawFrame")
                    // GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                    // 绘制三角形
                    triangle.draw()
                }
            })
            glSurfaceView.visibility = View.VISIBLE
        }
        rootView.findViewById<Button>(R.id.playVideo).setOnClickListener {
            glSurfaceView.setRenderer(GLVideoRenderer())
            glSurfaceView.visibility = View.VISIBLE
        }

    }

    inner class Triangle {

        // number of coordinates per vertex in this array
        val COORDS_PER_VERTEX: Int = 3
        var triangleCoords: FloatArray = floatArrayOf( // in counterclockwise order:
            0.0f, 0.5f, 0.0f,  // top
            -0.5f, -0.5f, 0.0f,  // bottom left
            0.5f, -0.5f, 0.0f // bottom right
        )

        // Set color with red, green, blue and alpha (opacity) values
        var color: FloatArray = floatArrayOf(255f, 0f, 0f, 1.0f)

        // 顶点着色器是GPU上运行的小程序、代码片段
        private val vertexShaderCode =
            "attribute vec4 vPosition;" +
            "void main() {" +
            " gl_Position = vPosition;" +
            "}"

        private val fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            " gl_FragColor = vColor;" +
            "}"

        private val vertexBuffer: FloatBuffer

        private var positionHandle = 0
        private var colorHandle = 0

        private val vertexCount: Int = triangleCoords.size // COORDS_PER_VERTEX
        private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
        private var program: Int

        init {
            // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
            val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
            // 数组排列用nativeOrder
            bb.order(ByteOrder.nativeOrder())
            // 从ByteBuffer创建一个浮点缓冲区
            vertexBuffer = bb.asFloatBuffer()
            // 将坐标添加到FloatBuffer
            vertexBuffer.put(triangleCoords)
            // 设置缓冲区来读取第一个坐标
            vertexBuffer.position(0)

            val vertexShader: Int = loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode
            )
            val fragmentShader: Int = loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode
            )

            // 创建空的OpenGL ES程序
            program = GLES20.glCreateProgram()
            // 添加顶点着色器到程序中
            GLES20.glAttachShader(program, vertexShader)
            // 添加片段着色器到程序中
            GLES20.glAttachShader(program, fragmentShader)
            // 创建OpenGL ES程序可执行文件
            GLES20.glLinkProgram(program)
        }

        private fun loadShader(type: Int, shaderCode: String?): Int {
            // 创造顶点着色器类型(GLES20.GL_VERTEX_SHADER)
            // 或者是片段着色器类型 (GLES20.GL_FRAGMENT_SHADER)

            val shader = GLES20.glCreateShader(type)
            // 添加上面编写的着色器代码并编译它
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }

        fun draw() {
            // 将程序添加到OpenGL ES环境
            GLES20.glUseProgram(program)
            // 获取顶点着色器的位置的句柄
            positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
            // 启用三角形顶点位置的句柄
            GLES20.glEnableVertexAttribArray(positionHandle)
            // 准备三角形坐标数据
            GLES20.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer
            )
            // 获取片段着色器的颜色的句柄
            colorHandle = GLES20.glGetUniformLocation(program, "vColor")
            // 设置绘制三角形的颜色
            GLES20.glUniform4fv(colorHandle, 1, color, 0)
            // 绘制三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
            // 禁用顶点数组
            GLES20.glDisableVertexAttribArray(positionHandle)
        }
    }

    inner class GLVideoRenderer() :
        Renderer, OnFrameAvailableListener, OnVideoSizeChangedListener {
        private var aPositionLocation = 0
        private var programId = 0
        private val vertexBuffer: FloatBuffer
        private val vertexData = floatArrayOf(
            1f, -1f, 0f,
            -1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
        )

        private val vertexShaderCode = "attribute vec4 aPosition;" +
                "attribute vec4 aTexCoord;" +
                "varying vec2 vTexCoord;" +
                "uniform mat4 uMatrix;" +
                "uniform mat4 uSTMatrix;" +
                "void main() {" +
                "vTexCoord = (uSTMatrix * aTexCoord).xy;" +
                "gl_Position = uMatrix*aPosition;" +
                "}"

        private val fragmentShaderCode = "#extension GL_OES_EGL_image_external : require" +
                "precision mediump float;" +
                "varying vec2 vTexCoord;" +
                "uniform samplerExternalOES sTexture;" +
                "void main() {" +
                "    gl_FragColor=texture2D(sTexture, vTexCoord);" +
                "}"

        private val projectionMatrix = FloatArray(16)
        private var uMatrixLocation = 0

        private val textureVertexData = floatArrayOf(
            1f, 0f,
            0f, 0f,
            1f, 1f,
            0f, 1f
        )
        private val textureVertexBuffer: FloatBuffer
        private var uTextureSamplerLocation = 0
        private var aTextureCoordLocation = 0
        private var textureId = 0

        private var surfaceTexture: SurfaceTexture? = null
        val mediaPlayer: MediaPlayer
        private val mSTMatrix = FloatArray(16)
        private var uSTMMatrixHandle = 0

        private var updateSurface = false
        private var playerPrepared = false
        private var screenWidth = 0
        private var screenHeight = 0

        init {
            synchronized(this) {
                updateSurface = false
            }
            vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData)
            vertexBuffer.position(0)

            textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData)
            textureVertexBuffer.position(0)

            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer.setDataSource(context!!.assets.openFd("video.mp4"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.isLooping = true

            mediaPlayer.setOnVideoSizeChangedListener(this)
        }

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            HiLog.log(HiLogType.D, TAG,  "onSurfaceCreated")
            val vertexShader: Int = loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode
            )
            val fragmentShader: Int = loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode
            )

            // 创建空的OpenGL ES程序
            programId = GLES20.glCreateProgram()
            // 添加顶点着色器到程序中
            GLES20.glAttachShader(programId, vertexShader)
            // 添加片段着色器到程序中
            GLES20.glAttachShader(programId, fragmentShader)
            // 创建OpenGL ES程序可执行文件
            GLES20.glLinkProgram(programId)

            aPositionLocation = GLES20.glGetAttribLocation(programId, "aPosition")
            uMatrixLocation = GLES20.glGetUniformLocation(programId, "uMatrix")
            uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix")
            uTextureSamplerLocation = GLES20.glGetUniformLocation(programId, "sTexture")
            aTextureCoordLocation = GLES20.glGetAttribLocation(programId, "aTexCoord")

            // 生成纹理
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)

            textureId = textures[0]
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
            /*GLES11Ext.GL_TEXTURE_EXTERNAL_OES的用处？
      之前提到视频解码的输出格式是YUV的（YUV420p，应该是），那么这个扩展纹理的作用就是实现YUV格式到RGB的自动转化，
      我们就不需要再为此写YUV转RGB的代码了*/
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST.toFloat()
            )
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            // 纹理再构建SurfaceTexture，然后再构建Surface，给到Player接收播放器数据
            surfaceTexture = SurfaceTexture(textureId)
            surfaceTexture!!.setOnFrameAvailableListener(this) // 监听是否有新的一帧数据到来

            val surface = Surface(surfaceTexture)
            mediaPlayer.setSurface(surface)
            // surface.release()

            if (!playerPrepared) {
                try {
                    HiLog.log(HiLogType.D, TAG, "prepare player")
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        HiLog.log(HiLogType.D, TAG, "onPrepared")
                        playerPrepared = true
                        mediaPlayer.start()
                    }
                } catch (t: IOException) {
                    HiLog.log(HiLogType.E, TAG, "onPrepared failed")
                }
            }
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            HiLog.d(HiLogType.D,TAG, "onSurfaceChanged: $width $height")
            screenWidth = width
            screenHeight = height
        }

        override fun onDrawFrame(gl: GL10) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
            synchronized(this) {
                if (updateSurface) {
                    surfaceTexture!!.updateTexImage() // 获取新数据
                    surfaceTexture!!.getTransformMatrix(mSTMatrix) // 让新的纹理和纹理坐标系能够正确的对应,mSTMatrix的定义是和projectionMatrix完全一样的。
                    updateSurface = false
                }
            }
            GLES20.glUseProgram(programId)
            GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)
            GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0)

            vertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(aPositionLocation)
            GLES20.glVertexAttribPointer(
                aPositionLocation, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer
            )

            textureVertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(aTextureCoordLocation)
            GLES20.glVertexAttribPointer(
                aTextureCoordLocation,
                2,
                GLES20.GL_FLOAT,
                false,
                8,
                textureVertexBuffer
            )

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)

            GLES20.glUniform1i(uTextureSamplerLocation, 0)
            GLES20.glViewport(0, 0, screenWidth, screenHeight)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        }

        @Synchronized
        override fun onFrameAvailable(surface: SurfaceTexture) {
            updateSurface = true
        }

        private fun loadShader(type: Int, shaderCode: String?): Int {
            // 创造顶点着色器类型(GLES20.GL_VERTEX_SHADER)
            // 或者是片段着色器类型 (GLES20.GL_FRAGMENT_SHADER)

            val shader = GLES20.glCreateShader(type)
            // 添加上面编写的着色器代码并编译它
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }

        override fun onVideoSizeChanged(mp: MediaPlayer, width: Int, height: Int) {
            HiLog.d(HiLogType.D, TAG, "onVideoSizeChanged: $width $height")
            updateProjection(width, height)
        }

        private fun updateProjection(videoWidth: Int, videoHeight: Int) {
            val screenRatio = screenWidth.toFloat() / screenHeight
            val videoRatio = videoWidth.toFloat() / videoHeight
            if (videoRatio > screenRatio) {
                Matrix.orthoM(
                    projectionMatrix,
                    0,
                    -1f,
                    1f,
                    -videoRatio / screenRatio,
                    videoRatio / screenRatio,
                    -1f,
                    1f
                )
            } else Matrix.orthoM(
                projectionMatrix,
                0,
                -screenRatio / videoRatio,
                screenRatio / videoRatio,
                -1f,
                1f,
                -1f,
                1f
            )
        }
    }

    companion object {
        private const val TAG = "GLSurfaceViewFragment"
    }
}