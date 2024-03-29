@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.ads.sample

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import it.scoppelletti.spaceship.ads.AdsConfig
import it.scoppelletti.spaceship.ads.DefaultAdListener
import it.scoppelletti.spaceship.ads.adRequestBuilder
import it.scoppelletti.spaceship.ads.app.adsComponent
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.lifecycle.ConsentStatusObservable
import it.scoppelletti.spaceship.ads.sample.databinding.MainActivityBinding
import mu.KotlinLogging
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        val adsConfig: AdsConfig

        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adsConfig = adsComponent().adsConfigWrapper().value
        MobileAds.initialize(this)

        adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = adsConfig.unitIds[0]
        binding.adFrame.addView(adView)

        adView.adListener = DefaultAdListener()
        ConsentStatusObservable.status.observe(this) { status ->
            if (status != null) {
                onConsentStatusUpdate(status)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    private fun onConsentStatusUpdate(status: ConsentStatus) {
        logger.debug { "Consent status changed to $status." }

        try {
            adView.loadAd(adRequestBuilder(status).build())
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to set consent status to $status." }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent

        when (item.itemId) {
            R.id.cmd_settings -> {
                intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
