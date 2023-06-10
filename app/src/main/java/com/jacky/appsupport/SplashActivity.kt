package com.jacky.appsupport

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.jacky.basebiz.route.RouterConstant.Page

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_splash)
        ARouter.getInstance().build(Page.HOME_ACTIVITY).navigation()
        finish()
    }
}