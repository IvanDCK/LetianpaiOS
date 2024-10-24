package com.rhj.message

class MessageWidgetListBean : MessageBean() {
    var currentPage: Int = 0
    var itemsPerPage: Int = 0
    var messageWidgetBean: List<MessageWidgetBean> = ArrayList()

    override fun toString(): String {
        return "MessageWidgetListBean{" +
                "currentPage=" + currentPage +
                ", itemsPerPage=" + itemsPerPage +
                ", messageWidgetBean=" + messageWidgetBean +
                '}'
    }
}
