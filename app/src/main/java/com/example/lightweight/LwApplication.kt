package com.example.lightweight

import android.app.Application

class LwApplication: Application() {
    val container: AppContainer by lazy {  AppContainer(applicationContext) }
}