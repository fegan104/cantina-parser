package com.frankegan

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser


/**
 * Created by @author frankegan on 11/3/18.
 */
fun main(args: Array<String>) {
    val jsonRoot = JsonParser().parse(getResourceAsText("/SystemViewController.json"))

    if (args.isEmpty()) println("Please provide a selector").let { return }

    val selectors = args.map {
        when {
            it.startsWith(".") -> Selector.ClassName(it.drop(1))
            it.startsWith("#") -> Selector.Identifier(it.drop(1))
            else -> Selector.Class(it)
        }
    }

    val selectedViews = getViewsForSelectors(jsonRoot, selectors.toMutableList())
    println("Found ${selectedViews.size} matching view(s) for selector(s) ${args.toList()}:")
    println(selectedViews)
}

/**
 * This method allows you to chain multiple selectors in a view search.
 */
fun getViewsForSelectors(
        json: JsonElement,
        selectors: MutableList<Selector>
): Set<JsonElement> {

    //we can always 'pop' because we checked we had at least 1 arg
    var result = getViewsForSelector(json, selectors.removeAt(0))
    for (s in selectors) {
        result = result.flatMap { getViewsForSelector(it, s)}.toSet()
    }

    return result
}

/**
 * Returns a list of views that match the specified selector.
 */
fun getViewsForSelector(
        json: JsonElement,
        selector: Selector
): Set<JsonElement> {
    return when {
        json.isJsonArray -> json.asJsonArray.flatMap { getViewsForSelector(it, selector) }.toSet()
        json.isJsonObject -> {
            val jsonObject = json.asJsonObject
            mutableListOf<JsonElement>().apply {
                if (selectorMatch(jsonObject, selector)) add(jsonObject)
                addAll(jsonObject.entrySet().flatMap { getViewsForSelector(it.value, selector) })
            }.toSet()
        }
        else -> emptySet()
    }
}

/**
 * This function will return true when the given JsonObject matches the given Selector, false otherwise.
 */
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

/**
 * Convenience method for retrieving json file from resources.
 */
fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path).readText()
}

/**
 * This sealed class represent a strict type hierarchy for the different selectors supported by the program.
 */
sealed class Selector {
    abstract val target: String

    class Class(override val target: String) : Selector()
    class ClassName(override val target: String) : Selector()
    class Identifier(override val target: String) : Selector()
}


