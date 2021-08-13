/*
 * Copyright (C) 2021 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.ads.lifecycle

import it.scoppelletti.spaceship.ads.consent.ConsentStatus

/**
 * Commands sent by [ConsentViewModel].
 *
 * @since 1.0.0
 */
public sealed class ConsentCommand {

    /**
     * Loads data.
     */
    public object LoadData : ConsentCommand()

    /**
     * Data loaded.
     */
    public object DataLoaded : ConsentCommand()

    /**
     * Saves data.
     *
     * @property status Consent status.
     */
    public data class SaveData(
        public val status: ConsentStatus
    ) : ConsentCommand()

    /**
     * Data saved.
     */
    public object DataSaved : ConsentCommand()

    /**
     * Error.
     *
     * @property ex Exception.
     */
    public data class Error(
            public val ex: Throwable
    ) : ConsentCommand()
}
