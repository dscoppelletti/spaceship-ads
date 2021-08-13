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

package it.scoppelletti.spaceship.ads.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.ads.consent.ConsentDataLoader
import it.scoppelletti.spaceship.ads.consent.ConsentDataStore
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.model.ConsentData
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel of the `AbstractConsentActivity` view.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @see   it.scoppelletti.spaceship.ads.model.ConsentData
 * @since 1.0.0
 *
 * @property command Command source.
 * @property data    Consent data.
 */
public class ConsentViewModel(
        private val consentDataStore: ConsentDataStore,
        private val consentDataLoader: ConsentDataLoader,
        clock: Clock
) : ViewModel() {

    private val _cmd = MutableLiveData<SingleEvent<ConsentCommand>>()

    public val command : LiveData<SingleEvent<ConsentCommand>> = _cmd
    public var data = ConsentData(year = LocalDateTime.now(clock).year)

    /**
     * Loads the `ConsentData` object.
     */
    public fun load() = viewModelScope.launch {
        try {
            data = consentDataLoader.load()
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _cmd.value = SingleEvent(ConsentCommand.Error(ex))
        }

        _cmd.value = SingleEvent(ConsentCommand.DataLoaded)
    }

    /**
     * Saves the consent status.
     *
     * @param status The consent status.
     */
    public fun save(status: ConsentStatus) = viewModelScope.launch {
        val saving: ConsentData

        saving = data.copy(consentStatus = status)

        try {
            consentDataStore.save(data)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _cmd.value = SingleEvent(ConsentCommand.Error(ex))
        }

        data = saving
        _cmd.value = SingleEvent(ConsentCommand.DataSaved)
    }

    /**
     * Notifies a command.
     *
     * @param command Command.
     */
    public fun action(command: ConsentCommand) {
        _cmd.value = SingleEvent(command)
    }
}

class ConsentViewModelFactory @Inject constructor(
        private val consentDataStore: ConsentDataStore,
        private val consentDataLoader: ConsentDataLoader,

        @Named(StdlibExt.DEP_UTCCLOCK)
        private val clock: Clock
) : ViewModelProviderEx.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(handle: SavedStateHandle): T =
            ConsentViewModel(consentDataStore, consentDataLoader, clock) as T
}
