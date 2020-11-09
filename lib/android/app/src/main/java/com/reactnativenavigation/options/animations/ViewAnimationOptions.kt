package com.reactnativenavigation.options.animations

import com.reactnativenavigation.options.AnimationOptions
import com.reactnativenavigation.options.StackAnimationOptions.Companion.CommandType
import org.json.JSONObject

class ViewAnimationOptions @JvmOverloads constructor(commandType: CommandType = CommandType.None, json: JSONObject? = null) {

    @JvmField var enter = AnimationOptions()
    @JvmField var exit = AnimationOptions()

    init {
        json?.let { parse(commandType, it) }
    }

    fun mergeWith(other: ViewAnimationOptions) {
        enter.mergeWith(other.enter)
        exit.mergeWith(other.exit)
    }

    fun mergeWithDefault(defaultOptions: ViewAnimationOptions) {
        enter.mergeWithDefault(defaultOptions.enter)
        exit.mergeWithDefault(defaultOptions.exit)
    }

    private fun parse(commandType: CommandType, json: JSONObject?) {
        json?.let {
            if (json.has("enter") || json.has("exit")) {
                json.optJSONObject("enter")?.let { enter = AnimationOptions(it) }
                json.optJSONObject("exit")?.let { exit = AnimationOptions(it) }
            } else {
                when (commandType) {
                    CommandType.Push -> enter = AnimationOptions(json)
                    CommandType.SetStackRoot -> enter = AnimationOptions(json)
                    CommandType.Pop -> exit = AnimationOptions(json)
                    else -> Unit
                }
            }
        }
    }
}