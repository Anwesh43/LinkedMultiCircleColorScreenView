package com.anwesh.uiprojects.linkedmultiscreencolorview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.multicirclecolorscreenview.MultiScreenColorView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MultiScreenColorView.create(this)
    }
}
