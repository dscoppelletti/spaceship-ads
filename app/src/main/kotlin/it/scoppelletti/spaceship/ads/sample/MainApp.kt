@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.ads.sample

import android.app.Application
import it.scoppelletti.spaceship.ads.AdsConfig
import it.scoppelletti.spaceship.ads.AdsConfigWrapper
import it.scoppelletti.spaceship.ads.inject.AdsComponent
import it.scoppelletti.spaceship.ads.inject.AdsComponentProvider
import it.scoppelletti.spaceship.ads.sample.inject.SampleComponent
import it.scoppelletti.spaceship.ads.sample.inject.DaggerSampleComponent
import it.scoppelletti.spaceship.html.inject.HtmlComponent
import it.scoppelletti.spaceship.inject.AppComponent
import it.scoppelletti.spaceship.inject.StdlibComponent

class MainApp : Application(), AdsComponentProvider {

    private lateinit var _appComponent: SampleComponent

    override fun onCreate() {
        val adsConfigWrapper: AdsConfigWrapper

        super.onCreate()

        _appComponent = DaggerSampleComponent.factory()
                .create(this)

        adsConfigWrapper = _appComponent.adsConfigWrapper()
        adsConfigWrapper.value =  AdsConfig(BuildConfig.ADS_SERVICEURL,
                BuildConfig.ADS_PUBLISHERID, BuildConfig.ADS_APPID,
                listOf(BuildConfig.ADS_UNITID))
    }

    override fun adsComponent(): AdsComponent = _appComponent

    override fun appComponent(): AppComponent = _appComponent

    override fun htmlComponent(): HtmlComponent = _appComponent

    override fun stdlibComponent(): StdlibComponent = _appComponent

    companion object {
        const val PROP_ADS = "it.scoppelletti.spaceship.ads.sample.ads"
    }
}

