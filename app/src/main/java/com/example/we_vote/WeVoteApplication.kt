package com.example.we_vote

import android.app.Application
import com.example.we_vote.di.AppContainer

class WeVoteApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
