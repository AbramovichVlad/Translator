package com.pet.app.translator.parser

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.util.Xml
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*


class XmlCreator(val context: Context) {

    fun createXml(list : List<StringModel?>) {
      val xmlText = buildXmlText(list)
        Log.d("tagDataParse", "xmlText $xmlText")
        save(xmlText)
    }

    fun save(text : String) {
        var fos: FileOutputStream? = null
        try {
            fos = context.openFileOutput("string.xml", MODE_PRIVATE)
            fos!!.write(text.toByteArray())
            CoroutineScope(Dispatchers.Main + Job()).launch {
                Toast.makeText(
                    context, "Saved to " + context.getFilesDir().toString() + "/" + "string.xml",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }



    private fun buildXmlText(list : List<StringModel?>): String {
        val  xmlSerializer = Xml.newSerializer()
        val  writer =  StringWriter()

        xmlSerializer.setOutput(writer);

        //Start Document
        xmlSerializer.startDocument("UTF-8", true);
        // xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        //Open Tag <file>
        xmlSerializer.startTag("", "resources");

        list.forEach {
            if(it == null) return@forEach
            xmlSerializer.startTag("", ResTag.STRING.tag);
            xmlSerializer.attribute("", "name", it.name);
            if(it.formatted != null) xmlSerializer.attribute("", "formatted", "${it.formatted}");

            xmlSerializer.text(it.text);
            xmlSerializer.endTag("", ResTag.STRING.tag);
        }


        xmlSerializer.endTag("", "resources");
        xmlSerializer.endDocument();

        return writer.toString();
    }
}