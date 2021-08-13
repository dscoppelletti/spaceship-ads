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

package it.scoppelletti.spaceship.ads.lifecycle

import android.text.Html
import android.text.SpannedString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.fromHtml
import it.scoppelletti.spaceship.html.replaceHyperlinks
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import mu.KotlinLogging
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel of the `ConsentPromptFragment` view.
 *
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
 * @since 1.0.0
 *
 * @property text Message text.
 */
public class ConsentPromptViewModel(
        private val tagHandler: Html.TagHandler,
) : ViewModel() {

    private val _text = MutableLiveData<CharSequence>()
    public val text: LiveData<CharSequence> = _text

    /**
     * Builds the displayable styled text from the provided HTML string.
     *
     * @param source  The source HTML string.
     * @param url     URL of the privacy policy.
     * @param onClick Handles the click on the URL.
     */
    public fun buildText(
            source: String,
            url: String,
            onClick: (String) -> Unit
    ) = viewModelScope.launch {
        val text: CharSequence

        if (_text.value != null) {
            return@launch
        }

        try {
            text = fromHtml(source, null, tagHandler)
                    .replaceHyperlinks(onClick) {
                        it == url
                    }

            _text.value = text
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Failed to build text.", ex)
            _text.value = SpannedString.valueOf(ex.localizedMessage)
        }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}

class ConsentPromptViewModelFactory @Inject constructor(

        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler
) : ViewModelProviderEx.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(handle: SavedStateHandle): T =
            ConsentPromptViewModel(tagHandler) as T
}

