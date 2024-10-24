package com.rhj.message

class MessageWidgetWebBean : MessageBean() {
    var url: String? = null

    override fun toString(): String {
        return "MessageWidgetWebBean{" +
                "url='" + url + '\'' +
                '}'
    }
}
