package biz.eventually.atpl.ui.source

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.StringRes
import android.view.View
import android.view.View.GONE
import android.widget.AdapterView
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.BuildConfig
import biz.eventually.atpl.R
import biz.eventually.atpl.common.IntentIdentifier
import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.settings.SettingsActivity
import biz.eventually.atpl.ui.BaseComponentActivity
import biz.eventually.atpl.ui.about.AboutActivity
import biz.eventually.atpl.ui.subject.SubjectActivity
import com.yalantis.guillotine.animation.GuillotineAnimation
import kotlinx.android.synthetic.main.activity_source.*
import kotlinx.android.synthetic.main.guillotine.*
import org.jetbrains.anko.startActivity
import timber.log.Timber
import javax.inject.Inject

class SourceActivity : BaseComponentActivity() {

    private var mAdapter: SourceAdapter? = null
    private val RIPPLE_DURATION: Long = 250

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: SourceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Starting SourceActivity")

        AtplApplication.component.inject(this)

        setContentView(R.layout.activity_source)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SourceViewModel::class.java)

        app_version.text = "v${BuildConfig.VERSION_APP}"

        // font for the title
        source_welcome.typeface = AtplApplication.tangerine
        settingGuillotineMenu()

        viewModel.sources.observe(this, Observer<List<Source>> {
            if (it?.isEmpty() == true) {
                showHideError(View.VISIBLE)
            } else {
                showHideError(View.GONE)
                displayData(it)
            }
        })

        viewModel.isLoading.observe(this, Observer<Boolean> {
            if (it == true) {
                rotateloading.start()
            } else {
                rotateloading.stop()
            }
        })

        source_refresh.setOnClickListener { loadData() }
    }

    private fun settingGuillotineMenu() {

        guillotine_container.bringToFront()
        GuillotineAnimation.GuillotineBuilder(guillotine_container, guillotine_hamburger, source_about)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(source_container)
                .setClosedOnStart(true)
                .build()

        about_group.setOnClickListener {
            guillotine_hamburger.performClick()
            startActivity<AboutActivity>()
        }
        about_back.setOnClickListener { about_group.performClick() }

        settings_group.setOnClickListener {
            guillotine_hamburger.performClick()
            startActivity<SettingsActivity>()
        }

        settings_back.setOnClickListener { settings_group.performClick() }
    }

    private fun displayData(sources: List<Source>?): Unit {

        sources?.apply {
            mAdapter = SourceAdapter(this@SourceActivity, sources)

            source_listview.adapter = mAdapter
            source_listview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val source = get(position)
                source.idWeb?.let {
                    startActivity<SubjectActivity>(
                            IntentIdentifier.SOURCE_ID to it,
                            IntentIdentifier.SOURCE_NAME to source.name
                    )
                }
            }
            rotateloading.stop()
        }
    }

    private fun loadData() {
        viewModel.refreshData()
    }

    private fun showHideError(show: Int) {
        source_error.visibility = show
        source_refresh.visibility = show
    }

    private fun onError(@StringRes messageId: Int): Unit {
        rotateloading.stop()
        showHideError(View.VISIBLE)
        source_error.text = getString(messageId)
    }
}
