package com.pet.app.translator.parser

import android.content.Context
import android.net.Uri
import android.util.Xml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream


class XmlParser(private val context: Context) {


    fun parseXml(uri: Uri, result: (List<StringModel?>) -> Unit) {
        try {
            CoroutineScope(Dispatchers.IO + Job()).launch {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val strings = mutableListOf<StringModel?>()
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setInput(inputStream, null)
                var stringText = ""
                var formatted : Boolean? = null
                var translatable = true
                var name = ""

                while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                    if (isStringTagStart(parser)) {
                        formatted = getFormatted(parser)
                        translatable = getTranslatable(parser)
                        name = getName(parser)
                    }
                    if(parser.eventType == XmlPullParser.TEXT){
                        stringText = parser.text ?: ""
                    }
                    if (isStringTagEnd(parser)){
                        strings.add(StringModel(name, stringText, formatted, translatable))
                    }
                    parser.next()
                }
                result.invoke(strings)
            }

        } catch (_: Exception) {

        }
    }

    private fun isStringTagStart(parser: XmlPullParser): Boolean {
        return parser.eventType == XmlPullParser.START_TAG
                && parser.name == ResTag.STRING.tag
    }

    private fun isStringTagEnd(parser: XmlPullParser): Boolean {
        return parser.eventType == XmlPullParser.END_TAG
                && parser.name == ResTag.STRING.tag
    }

    private fun getName(parser: XmlPullParser): String {
        return parser.getAttributeValue(null, "name") ?: ""
    }

    private fun getTranslatable(parser: XmlPullParser): Boolean {
        return try {
            parser.getAttributeValue(null, "translatable").toBooleanStrictOrNull() ?: true
        } catch (_ : Exception) {
            true
        }
    }

    private fun getFormatted(parser: XmlPullParser): Boolean? {
        return try {
            parser.getAttributeValue(null, "formatted").toBooleanStrictOrNull()
        } catch (_: Exception) {
            null
        }
    }
}