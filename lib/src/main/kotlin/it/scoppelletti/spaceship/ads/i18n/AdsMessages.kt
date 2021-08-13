/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.ads.i18n

import it.scoppelletti.spaceship.i18n.MessageSpec

/**
 * String resources.
 *
 * @since 1.0.0
 */
public interface AdsMessages {

    fun errorConfig(): MessageSpec

    fun errorLookupFailed(lookupFailedIds: List<String>): MessageSpec

    fun errorNotFound(notFoundIds: List<String>): MessageSpec

    fun errorPublisher(
            lookupFailedIds: List<String>,
            notFoundIds: List<String>
    ): MessageSpec

    fun errorUserNotLocatedInEea(): MessageSpec
}
