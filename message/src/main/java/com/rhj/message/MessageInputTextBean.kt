package com.rhj.message

class MessageInputTextBean : MessageBean() {
    var text: String? = null

    override fun toString(): String {
        return "MessageInputTextBean{" +
                "text='" + text + '\'' +
                '}'
    }
}
