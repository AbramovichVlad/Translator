package com.pet.app.translator.translator

import com.pet.app.translator.parser.StringModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Translator() {

    private val translatorText by lazy { TranslatorText("ru") }

    fun translateList(
        listItems: List<StringModel?>,
        action: (ArrayList<StringModel>) -> Unit
    ) {
        val translatedList = ArrayList<StringModel>()
        var countStart = 0
        listItems.forEach {
            if (it != null && it.translatable) {
                countStart++
                translateItem(it) { translatedItem ->
                    translatedList.add(translatedItem)
                    if (translatedList.size == countStart) action.invoke(translatedList)
                }
            }
        }
    }

    private fun translateItem(item: StringModel, action: (StringModel) -> Unit) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            translatorText.translateText(item.text) { translatedText, _ ->
                item.text = translatedText
                action.invoke(item)
            }
        }
    }
}