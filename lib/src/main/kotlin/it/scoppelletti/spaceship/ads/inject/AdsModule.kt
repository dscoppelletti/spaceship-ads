/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.ads.inject

import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.ads.AdsExt
import it.scoppelletti.spaceship.ads.AdsConfigWrapper
import it.scoppelletti.spaceship.ads.consent.AdService
import it.scoppelletti.spaceship.ads.consent.ConsentDataLoader
import it.scoppelletti.spaceship.ads.consent.ConsentDataStore
import it.scoppelletti.spaceship.ads.consent.DefaultConsentDataLoader
import it.scoppelletti.spaceship.ads.consent.DefaultConsentDataStore
import it.scoppelletti.spaceship.ads.i18n.AdsMessages
import it.scoppelletti.spaceship.ads.i18n.DefaultAdsMessages
import it.scoppelletti.spaceship.ads.lifecycle.ConsentFragmentViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentFragmentViewModelFactory
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPrivacyViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPrivacyViewModelFactory
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPromptViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPromptViewModelFactory
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModelFactory
import it.scoppelletti.spaceship.html.inject.HtmlModule
import it.scoppelletti.spaceship.inject.AppModule
import it.scoppelletti.spaceship.inject.ViewModelKey
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Defines the dependencies exported by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ HtmlModule::class, AppModule::class ])
public abstract class AdsModule {

    @Binds
    public abstract fun bindConsentDataLoader(
            obj: DefaultConsentDataLoader
    ): ConsentDataLoader

    @Binds
    public abstract fun bindConsentDataStore(
            obj: DefaultConsentDataStore
    ): ConsentDataStore

    @Binds
    public abstract fun bindAdsMessages(
            obj: DefaultAdsMessages
    ): AdsMessages

    @Binds
    @IntoMap
    @ViewModelKey(ConsentViewModel::class)
    abstract fun bindConsentViewModelFactory(
            obj: ConsentViewModelFactory
    ): ViewModelProviderEx.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ConsentFragmentViewModel::class)
    abstract fun bindConsentFragmentViewModelFactory(
            obj: ConsentFragmentViewModelFactory
    ): ViewModelProviderEx.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ConsentPrivacyViewModel::class)
    abstract fun bindConsentPrivacyViewModelFactory(
            obj: ConsentPrivacyViewModelFactory
    ): ViewModelProviderEx.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ConsentPromptViewModel::class)
    abstract fun bindConsentPromptViewModelFactory(
            obj: ConsentPromptViewModelFactory
    ): ViewModelProviderEx.Factory

    @Module
    public companion object {

        @Provides
        @Singleton
        @JvmStatic
        public fun provideConfig(): AdsConfigWrapper = AdsConfigWrapper()

        @Provides
        @JvmStatic
        @Named(AdsExt.DEP_HTTPCLIENT)
        public fun provideHttpClient(): OkHttpClient =
                OkHttpClient.Builder().build()

        @Provides
        @JvmStatic
        @Named(AdsExt.DEP_RETROFIT)
        public fun provideRetrofit(
                adsConfigWrapper: AdsConfigWrapper,
                @Named(AdsExt.DEP_HTTPCLIENT) httpClient: Lazy<OkHttpClient>
        ): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(adsConfigWrapper.value.serviceUrl)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .callFactory { req -> httpClient.get().newCall(req) }
                    .build()
        }

        @Provides
        @JvmStatic
        public fun provideAdService(
                @Named(AdsExt.DEP_RETROFIT) retrofit: Retrofit
        ): AdService = retrofit.create(AdService::class.java)
    }
}
