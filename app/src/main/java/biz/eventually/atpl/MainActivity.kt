package biz.eventually.atpl

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import biz.eventually.atpl.ui.source.SourceActivity

// https://www.bignerdranch.com/blog/splash-screens-the-right-way/
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, SourceActivity::class.java)
        startActivity(intent)
        finish()

    }
}
