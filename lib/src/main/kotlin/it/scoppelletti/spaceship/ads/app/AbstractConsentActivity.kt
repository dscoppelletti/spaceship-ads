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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.ads.app

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.ads.AdsExt
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.databinding.ConsentActivityBinding
import it.scoppelletti.spaceship.ads.i18n.AdsMessages
import it.scoppelletti.spaceship.ads.lifecycle.ConsentCommand
import it.scoppelletti.spaceship.ads.lifecycle.ConsentStatusObservable
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.appComponent
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx

/**
 * Prompts the user for consent to receive perzonalized advertising.
 *
 * @see   it.scoppelletti.spaceship.ads.app.ConsentLoadFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentUnderageFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentReminderFragment
 * @since 1.0.0
 */
@UiThread
public abstract class AbstractConsentActivity : AppCompatActivity() {

    private var settings: Boolean = false
    private lateinit var activityModel: ConsentViewModel
    private lateinit var binding: ConsentActivityBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var adsMessages: AdsMessages

    override fun onCreate(savedInstanceState: Bundle?) {
        val navHost: NavHostFragment
        val viewModelProvider: ViewModelProviderEx

        super.onCreate(savedInstanceState)

        binding = ConsentActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        settings = intent.getBooleanExtra(AbstractConsentActivity.PROP_SETTINGS,
                false)

        navHost = supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        appBarConfig = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfig)

        supportFragmentManager.setFragmentResultListener(
                ExceptionDialogFragment.TAG, this) { _, _ ->
            tryFinish()
        }

        viewModelProvider = appComponent().viewModelProvider()
        activityModel = viewModelProvider.get(this,
                ConsentViewModel::class.java)
        adsMessages = adsComponent().adsMessages()

        activityModel.command.observe(this) { command ->
            command?.poll()?.let {
                viewCommandHandler(it)
            }
        }
    }

    private fun viewCommandHandler(command: ConsentCommand) {
        when (command) {
            is ConsentCommand.LoadData -> {
                binding.progressIndicator.show()
                activityModel.load()
            }

            is ConsentCommand.DataLoaded -> {
                binding.progressIndicator.hide()
                if (settings && activityModel.data.consentStatus ==
                        ConsentStatus.NOT_IN_EEA) {
                    showExceptionDialog(ApplicationException(
                            adsMessages.errorUserNotLocatedInEea()))
                    return
                }

                if (activityModel.data.consentStatus == ConsentStatus.UNKNOWN) {
                    navController.navigate(
                            ConsentLoadFragmentDirections.actionConsentAge())
                } else {
                    doComplete()
                }
            }

            is ConsentCommand.SaveData -> {
                binding.progressIndicator.show()
                activityModel.save(command.status)
            }

            is ConsentCommand.DataSaved -> {
                binding.progressIndicator.hide()
                doComplete()
            }

            is ConsentCommand.Error -> {
                binding.progressIndicator.hide()
                showExceptionDialog(command.ex)
            }
        }
    }

    private fun doComplete() {
        ConsentStatusObservable.setStatus(
                activityModel.data.consentStatus)
        if (settings || onComplete()) {
            tryFinish()
        }
    }

    /**
     * Called when the user completes her choice.
     *
     * It is not called if this activity has been has been launched as a
     * settings activity.
     *
     * @return Returns `true` if the method has been succeded, `false`
     *         otherwise.
     */
    public abstract fun onComplete(): Boolean

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if ((navController.currentDestination?.id ?: 0) ==
                    R.id.dest_consent_age) {
                tryFinish()
                return true
            }
        }

        if (item.onNavDestinationSelected(navController)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    public companion object {

        /**
         * Property indicating whether this activity has been launched as a
         * settings activity.
         */
        public const val PROP_SETTINGS: String = AdsExt.PROP_SETTINGS
    }
}
