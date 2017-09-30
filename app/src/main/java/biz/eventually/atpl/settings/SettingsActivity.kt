package biz.eventually.atpl.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import biz.eventually.atpl.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);

        if (savedInstanceState == null) {
            val preferenceFragment = SettingsFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.pref_container, preferenceFragment)
            ft.commit()
        }
    }
}
