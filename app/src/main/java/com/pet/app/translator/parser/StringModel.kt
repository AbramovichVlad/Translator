package com.pet.app.translator.parser

class StringModel(val name : String, var text : String, val formatted  : Boolean?, val translatable : Boolean) {

    override fun toString(): String {
        return "StringModel(name='$name', text='$text', formatted=$formatted, translatable=$translatable)"
    }
}