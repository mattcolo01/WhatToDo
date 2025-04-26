package com.colombo.whattodo

import android.app.Application
import com.colombo.whattodo.ads.AdManager
import com.colombo.whattodo.ui.theme.WhatToDoTheme

class WhatToDoApplication : Application() {
    lateinit var adManager: AdManager
        private set

    override fun onCreate() {
        super.onCreate()
        adManager = AdManager(this)
    }
} 