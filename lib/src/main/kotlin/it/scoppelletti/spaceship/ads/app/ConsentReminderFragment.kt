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

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.databinding.ConsentReminderFragmentBinding
import it.scoppelletti.spaceship.ads.lifecycle.ConsentCommand
import it.scoppelletti.spaceship.ads.lifecycle.ConsentFragmentViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.app.appComponent
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx

/**
 * Reminds the user that she can change her choice anytime.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @since 1.0.0
 */
@UiThread
public class ConsentReminderFragment : Fragment(
        R.layout.it_scoppelletti_ads_consentreminder_fragment) {

    private lateinit var activityModel: ConsentViewModel
    private lateinit var viewModel: ConsentFragmentViewModel
    private lateinit var navController: NavController
    private val binding by viewBinding(ConsentReminderFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtMessage.movementMethod = LinkMovementMethod.getInstance()

        binding.cmdAgree.setOnClickListener {
            activityModel.action(
                    ConsentCommand.SaveData(ConsentStatus.NON_PERSONALIZED))
        }

        binding.cmdBack.setOnClickListener {
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
                ConsentFragmentViewModel::class.java)

        viewModel.text.observe(viewLifecycleOwner) { text ->
            binding.txtMessage.text = text
        }

        url = getString(R.string.it_scoppelletti_url_privacy)
        viewModel.buildText(getString(
                R.string.it_scoppelletti_ads_html_reminder, url))
    }
}
