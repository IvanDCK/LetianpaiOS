package com.letianpai.robot.notice.general.parser

class GeneralInfo {
    var code: Int = 0
    @JvmField
    var data: GeneralData? = null
    var msg: String? = null


    override fun toString(): String {
        return "{" +
                "code:" + code +
                ", data:" + data +
                ", msg:'" + msg + '\'' +
                '}'
    } //    @Override
    //    public String toString() {
    //        return "{" +
    //                "code=" + code +
    //                ", data=" + data +
    //                ", msg='" + msg + '\'' +
    //                '}';
    //    }
}
