package com.rhj.message

class MessageOutputTextBean : MessageBean() {
    var text: String? = null

    override fun toString(): String {
        return "MessageOutputTextBean{" +
                "text='" + text + '\'' +
                '}'
    }
}
