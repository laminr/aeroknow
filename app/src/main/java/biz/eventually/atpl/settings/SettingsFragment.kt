package biz.eventually.atpl.settings

import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import biz.eventually.atpl.R


class SettingsFragment : PreferenceFragmentCompat() {

    private var countdownNbr: EditTextPreference? = null
    private var changeListener: Preference.OnPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
        updateCountdownSummary(newValue.toString())
        true
    }

    override fun onCreatePreferences(bundle: Bundle?, param: String?) {
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
            val summary = context?.getString(R.string.pref_timer_time_summary, value)
            countdownNbr?.summary = summary
        } catch (e: NumberFormatException) { }
    }
}
