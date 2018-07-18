package com.tieudieu.passwordviewexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tieudieu.passwordview.PasswordView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        password_view.setOnPasswordEnterListener(object: PasswordView.OnPasswordEnterListener {
            override fun onPasswordEntered(password: String?) {

                Log.e("MainActivity", "xxx-onPassEntered-" + password)
            }

        })
    }
}
