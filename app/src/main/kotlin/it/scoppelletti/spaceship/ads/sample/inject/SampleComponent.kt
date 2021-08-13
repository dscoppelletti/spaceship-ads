
@file:Suppress("unused")

package it.scoppelletti.spaceship.ads.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.ads.inject.AdsComponent
import it.scoppelletti.spaceship.ads.inject.AdsModule
import it.scoppelletti.spaceship.html.inject.HtmlComponent
import it.scoppelletti.spaceship.inject.AppComponent
import it.scoppelletti.spaceship.inject.AppModule
import it.scoppelletti.spaceship.inject.StdlibComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [ AdsModule::class, AppModule::class ])
interface SampleComponent : AdsComponent, AppComponent, HtmlComponent,
        StdlibComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): SampleComponent
    }
}

