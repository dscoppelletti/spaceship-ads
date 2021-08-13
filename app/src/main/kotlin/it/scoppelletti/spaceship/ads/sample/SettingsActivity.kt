@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.ads.sample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
import it.scoppelletti.spaceship.ads.sample.databinding.SettingsActivityBinding
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.preference.AbstractPreferenceFragment

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val actionBar: ActionBar

        super.onCreate(savedInstanceState)

        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                tryFinish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

@Suppress("unused")
class SettingsFragment : AbstractPreferenceFragment() {

    override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>(MainApp.PROP_ADS)
                ?.startActivityConfig { intent ->
                    intent.putExtra(AbstractConsentActivity.PROP_SETTINGS, true)
                }
    }
}
