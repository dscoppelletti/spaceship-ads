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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.ads.consent

import com.squareup.moshi.JsonClass

/**
 * Debug value for testing geography.
 *
 * @see   it.scoppelletti.spaceship.ads.consent.AdService
 * @since 1.0.0
 *
 * @property code Code for controlling the REST API.
 */
@JsonClass(generateAdapter = false)
public enum class DebugGeography(public val code: Int) {

    /**
     * Debug is disabled.
     */
    DISABLED(0),

    /**
     * Simulates a user located in the European Economic Area.
     */
    @Suppress("unused")
    EEA(1),

    /**
     * Simulates a user not located in the European Economic Area.
     */
    @Suppress("unused")
    NOT_EEA(2)
}
