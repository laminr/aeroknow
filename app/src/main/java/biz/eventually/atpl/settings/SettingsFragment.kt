package biz.eventually.atpl.settings

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import biz.eventually.atpl.AtplApplication
import biz.eventually.atpl.R


class SettingsFragment : PreferenceFragment() {


    private var countdownNbr: EditTextPreference? = null
    private var changeListener: Preference.OnPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
        updateCountdownSummary(newValue.toString())
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.app_preferences)
        countdownNbr = findPreference(getString(R.string.pref_timer_nbr)) as EditTextPreference
        updateCountdownSummary()
        countdownNbr?.onPreferenceChangeListener = changeListener
    }

    override fun onDestroyView() {
        countdownNbr?.onPreferenceChangeListener = null
        super.onDestroyView()
    }

    private fun updateCountdownSummary(nbr: String? = null) {
        try {
            val value = nbr ?: countdownNbr?.text
            val summary = AtplApplication.get().getString(R.string.pref_timer_time_summary, value)
            countdownNbr?.summary = summary
        } catch (e: NumberFormatException) {
        }
    }
}
