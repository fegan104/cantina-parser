package com.frankegan

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser


/**
 * Created by @author frankegan on 11/3/18.
 */
fun main(args: Array<String>) {
    val fileContent = getResourceAsText("/SystemViewController.json")

    val parser = JsonParser()
    val rootObj = parser.parse(fileContent)

    println(getViewsForClass(rootObj, "StackView"))
    println(getViewsForClass(rootObj, "StackView").size)

}

fun getClasses(json: JsonElement, state: MutableList<String> = mutableListOf()): List<String> {
    return when {
        json.isJsonArray -> {
            state.apply {
                addAll(json.asJsonArray.flatMap { getClasses(it) })
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

fun getViewsForClass(
        json: JsonElement,
        targetClass: String,
        state: MutableList<JsonElement> = mutableListOf()
): List<JsonElement> {
    return when {
        json.isJsonArray -> {
            state.apply {
                addAll(json.asJsonArray.flatMap { getViewsForClass(it, targetClass) })
            }
        }
        json.isJsonObject -> {
            val jsonObject = json.asJsonObject
            state.apply {
                if (hasMatchingClass(jsonObject, targetClass)) add(jsonObject)
                addAll(jsonObject.entrySet().flatMap { getViewsForClass(it.value, targetClass) })
            }
        }
        else -> listOf()
    }
}

private fun hasMatchingClass(json: JsonObject, targetClass: String): Boolean {
    return json.has("class") && json["class"].isJsonPrimitive && json["class"].asString == targetClass
}

private fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path).readText()
}

sealed class Selector {
    class Class
    class ClassNames
    class Identifier
}
