package com.rhj.message

class MessageWidgetContentBean : MessageBean() {
    private var title: String? = null
    var subTitle: String? = null
    var imgUrl: String? = null

    override var message_type: Int
        get() = super.message_type
        set(message_type) {
            super.message_type = message_type
        }

    fun setTitle(title: String?) {
        this.title = title
    }

    override fun toString(): String {
        return "MessageWidgetContentBean{" +
                "title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}'
    }
}
