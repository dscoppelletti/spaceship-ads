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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.ads.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.databinding.ConsentPrivacyFragmentBinding
import it.scoppelletti.spaceship.ads.lifecycle.ConsentCommand
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPrivacyViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.ads.model.AdProvider
import it.scoppelletti.spaceship.app.appComponent
import it.scoppelletti.spaceship.i18n.AppMessages
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import java.lang.RuntimeException

/**
 * Gives access to the privacy policies.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @since 1.0.0
 */
@UiThread
public class ConsentPrivacyFragment : Fragment(
        R.layout.it_scoppelletti_ads_consentprivacy_fragment
) {

    private lateinit var activityModel: ConsentViewModel
    private lateinit var viewModel: ConsentPrivacyViewModel
    private lateinit var navController: NavController
    private lateinit var appMessages: AppMessages
    private val binding by viewBinding(ConsentPrivacyFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtHeader.movementMethod = LinkMovementMethod.getInstance()
        binding.txtFooter.movementMethod = LinkMovementMethod.getInstance()

        binding.cmdBack?.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        val url: String
        val activity: FragmentActivity
        val viewModelProvider: ViewModelProviderEx

        super.onViewStateRestored(savedInstanceState)

        navController = findNavController()

        activity = requireActivity()
        viewModelProvider = activity.appComponent().viewModelProvider()

        activityModel = viewModelProvider.get(activity,
                ConsentViewModel::class.java)
        viewModel = viewModelProvider.get(this,
                ConsentPrivacyViewModel::class.java)
        appMessages = activity.appComponent().appMessages()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.txtHeader.text = state?.header
            binding.txtFooter.text = state?.footer
        }

        url = getString(R.string.it_scoppelletti_url_privacy)
        viewModel.buildText(getString(R.string.it_scoppelletti_ads_html_header),
                getString(R.string.it_scoppelletti_ads_html_footer, url))

        showProviders(activityModel.data.adProviders)
    }

    /**
     * Shows Ad providers.
     *
     * @param adProviders Collection.
     */
    private fun showProviders(adProviders: List<AdProvider>) {
        var chip: Chip

        for (provider: AdProvider in adProviders) {
            chip = Chip(requireContext()).apply {
                text = provider.name
                isCheckable = false
                setOnClickListener {
                    openUrl(provider.policyUrl)
                }
            }

            ViewCompat.setLayoutDirection(chip,
                    ViewCompat.LAYOUT_DIRECTION_LOCALE)

            binding.grdProviders.addView(chip,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    /**
     * Visits an URL.
     *
     * @param url URL.
     */
    private fun openUrl(url: String) {
        val intent: Intent

        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (ex: RuntimeException) {
            activityModel.action(ConsentCommand.Error(
                    ApplicationException(appMessages.errorStartActivity(), ex)))
        }
    }
}
