package biz.eventually.atpl.ui.source

import android.app.Activity
import android.view.View
import biz.eventually.atpl.settings.SettingsActivity
import biz.eventually.atpl.ui.about.AboutActivity
import com.yalantis.guillotine.animation.GuillotineAnimation
import kotlinx.android.synthetic.main.activity_source.*
import kotlinx.android.synthetic.main.guillotine.*
import org.jetbrains.anko.startActivity

/**
 * Created by Thibault de Lambilly on 25/10/17.
 */
class MenuDecorator(val view: Activity): View(view) {

    private val RIPPLE_DURATION: Long = 250

    init {
        settingGuillotineMenu()
    }

    private fun settingGuillotineMenu() {

        view.guillotine_container.bringToFront()
        GuillotineAnimation.GuillotineBuilder(view.guillotine_container, view.guillotine_hamburger, view.source_about)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(view.source_container)
                .setClosedOnStart(true)
                .build()

        view.about_group.setOnClickListener {
            view.guillotine_hamburger.performClick()
            view.startActivity<AboutActivity>()
        }

        view.about_back.setOnClickListener { view.about_group.performClick() }

        view.settings_group.setOnClickListener {
            view.guillotine_hamburger.performClick()
            view.startActivity<SettingsActivity>()
        }

        view.settings_back.setOnClickListener { view.settings_group.performClick() }
    }
}