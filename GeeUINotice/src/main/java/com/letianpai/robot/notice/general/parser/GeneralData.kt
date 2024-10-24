package com.letianpai.robot.notice.general.parser

class GeneralData {
    @JvmField
    var wea: String? = null
    @JvmField
    var wea_img: String? = null
    @JvmField
    var tem: String? = null
    @JvmField
    var calender_total: Int = 0
    @JvmField
    var city: String? = null


    //    @Override
    //    public String toString() {
    //        return "{" +
    //                "wea='" + wea + '\'' +
    //                ", wea_img='" + wea_img + '\'' +
    //                ", tem='" + tem + '\'' +
    //                ", calender_total=" + calender_total +
    //                '}';
    //    }
    override fun toString(): String {
        return "{" +
                "wea:'" + wea + '\'' +
                ", wea_img:'" + wea_img + '\'' +
                ", tem:'" + tem + '\'' +
                ", calender_total:" + calender_total +
                ", city:'" + city + '\'' +
                '}'
    }
}
