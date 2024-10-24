package com.rhj.message


class MessageMusicBean {
    /**
     * subTitle : 张学友
     * extra : {"resType":"mp3","source":0,"origintitle":"吻别"}
     * imageUrl : http://47.98.45.59/c/6460.jpg
     * linkUrl : http://47.98.45.59/34040665.mp3
     * label :
     * title : 吻别
     */
    //for music
    var subTitle: String? = null
    var extra: Extra? = null
    var imageUrl: String? = null
    var linkUrl: String? = null
    var label: String? = null
    var title: String? = null
    var isFavorite: Boolean = false

    class Extra {
        /**
         * resType : mp3
         * source : 0
         * origintitle : 吻别
         */
        var resType: String? = null
        var source: String? = null
        var origintitle: String? = null

        override fun toString(): String {
            return "Extra{" +
                    "resType='" + resType + '\'' +
                    ", source='" + source + '\'' +
                    ", origintitle='" + origintitle + '\'' +
                    '}'
        }
    }


    override fun toString(): String {
        return "Music{" +
                "subTitle='" + subTitle + '\'' +
                ", extra=" + extra +
                ", imageUrl='" + imageUrl + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", label='" + label + '\'' +
                ", title='" + title + '\'' +
                ", isFavorite=" + isFavorite +
                '}'
    }
}
