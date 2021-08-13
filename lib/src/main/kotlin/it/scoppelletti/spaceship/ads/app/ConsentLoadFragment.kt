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
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.lifecycle.ConsentCommand
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.app.appComponent
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx

/**
 * Shows a circular indeterminate progress indicator.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @since 1.0.0
 */
@UiThread
public class ConsentLoadFragment : Fragment(
        R.layout.it_scoppelletti_ads_consentload_fragment
) {

    private lateinit var activityModel: ConsentViewModel

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        val activity: FragmentActivity
        val viewModelProvider: ViewModelProviderEx

        super.onViewStateRestored(savedInstanceState)

        activity = requireActivity()
        viewModelProvider = activity.appComponent()
                .viewModelProvider()
        activityModel = viewModelProvider.get(activity,
                ConsentViewModel::class.java)

        if (savedInstanceState == null) {
            activityModel.action(ConsentCommand.LoadData)
        }
    }
}
