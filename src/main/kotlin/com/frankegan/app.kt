package com.frankegan

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser


/**
 * Created by @author frankegan on 11/3/18.
 */
fun main(args: Array<String>) {
    val jsonRoot = JsonParser().parse(getResourceAsText("/SystemViewController.json"))

    if(args.isEmpty()) println("Please provide a selector").let { return }

    val first = args.first()
    val selector = when{
        first.startsWith(".") -> Selector.ClassName(first.drop(1))
        first.startsWith("#") -> Selector.Identifier(first.drop(1))
        else -> Selector.Class(first)
    }

    val selectedViews = getViewsForSelector(jsonRoot, selector)
    println("Found ${selectedViews.size} matching view(s) for selector <$first>:")
    println(selectedViews)
}

fun getViewsForSelector(
        json: JsonElement,
        selector: Selector,
        state: MutableList<JsonElement> = mutableListOf()
): List<JsonElement> {
    return when {
        json.isJsonArray -> {
            state.apply {
                addAll(json.asJsonArray.flatMap { getViewsForSelector(it, selector) })
            }
        }
        json.isJsonObject -> {
            val jsonObject = json.asJsonObject
            state.apply {
                if (selectorMatch(jsonObject, selector)) add(jsonObject)
                addAll(jsonObject.entrySet().flatMap { getViewsForSelector(it.value, selector) })
            }
        }
        else -> listOf()
    }
}

fun selectorMatch(jsonObject: JsonObject, selector: Selector) = when (selector) {
    is Selector.Class -> hasMatchingClass(jsonObject, selector.target)
    is Selector.ClassName -> hasMatchingClassName(jsonObject, selector.target)
    is Selector.Identifier -> hasMatchingIdentifier(jsonObject, selector.target)
}

private fun hasMatchingClass(json: JsonObject, targetClass: String): Boolean {
    val index = "class"
    return try {
        json.has(index)
                && json[index].isJsonPrimitive
                && json[index].asString == targetClass
    } catch (e: Exception) {
        false
    }
}

private fun hasMatchingClassName(json: JsonObject, targetClassName: String): Boolean {
    val index = "classNames"
    return try {
        json.has(index)
                && json[index].isJsonArray
                && json[index].asJsonArray.any { it.asString == targetClassName }
    } catch (e: Exception) {
        false
    }
}

private fun hasMatchingIdentifier(json: JsonObject, targetIdentifier: String): Boolean {
    val index = "identifier"
    return try {
        json.has(index)
                && json[index].isJsonPrimitive
                && json[index].asString == targetIdentifier
    } catch (e: Exception) {
        false
    }
}

fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path).readText()
}

sealed class Selector {
    abstract val target: String

    data class Class(override val target: String) : Selector()
    class ClassName(override val target: String) : Selector()
    class Identifier(override val target: String) : Selector()
}


