package com.frankegan

import com.google.gson.JsonElement
import com.google.gson.JsonParser


/**
 * Created by @author frankegan on 11/3/18.
 */
fun main(args: Array<String>) {
    val fileContent = getResourceAsText("/SystemViewController.json")

    val parser = JsonParser()
    val rootObj = parser.parse(fileContent)

    println(getClasses(rootObj))

}

fun getClasses(json: JsonElement, state: MutableList<String> = mutableListOf()): List<String> {
    return when {
        json.isJsonArray -> {
            state.apply {
                addAll(json.asJsonArray.flatMap { getClasses(it, state) })
            }
        }
        json.isJsonObject -> {
            val jsonObject = json.asJsonObject
            val newClasses = jsonObject.entrySet()
                    .filter { it.key == "class" }
                    .map { it.value.asString }
            state.apply {
                addAll(newClasses)
                addAll(jsonObject.entrySet().flatMap { getClasses(it.value) })
            }
        }
        else -> listOf()
    }
}

private fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path).readText()
}

sealed class Selector {
    class Class
    class ClassNames
    class Identifier
}
