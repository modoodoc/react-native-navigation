package com.reactnativenavigation.utils

import com.reactnativenavigation.options.AnimationOptions
import com.reactnativenavigation.options.StackAnimationOptions.Companion.CommandType
import com.reactnativenavigation.options.ValueAnimationOptions
import com.reactnativenavigation.options.animations.ViewAnimationOptions
import org.json.JSONObject


fun createEnterExitAnimation(commandType: CommandType): ViewAnimationOptions {
    val alpha = JSONObject().apply {
        put("alpha", JSONObject().apply {
            put("from", 0)
            put("to", 0)
        })
    }
    val animation = JSONObject().apply {
        put("enter", alpha)
        put("exit", alpha)
    }
    return ViewAnimationOptions(commandType, animation)
}

fun createContentJson(vararg values: Pair<String, ValueAnimationOptions>): AnimationOptions {
    val content = JSONObject()
    values.forEach {
        content.put(it.first, JSONObject().apply {
            put("from", it.second.from)
            put("to", it.second.to)
        })
    }
    return AnimationOptions(content)
}
