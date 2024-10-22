package com.renhejia.robot.commandlib.parser.face

class Face {
    //    private String face;
    var face: String
    private val filePrefix: String? = null
    private val times: String? = null
    var desc: String? = null
    var id: Int = 0


    fun showLog(): String {
        return " $face"
    }

    override fun toString(): String {
        return "{" +
                "face='" + face + '\'' +
                ", filePrefix='" + filePrefix + '\'' +
                ", times='" + times + '\'' +
                ", desc='" + desc + '\'' +
                ", id=" + id +
                '}'
    }

    constructor(face: String) {
        this.face = face
    }

    constructor(face: String, desc: String?) {
        this.face = face
        this.desc = desc
    }


    //
    //    public Face(String face){
    //        this.face = face;
    //    }
    //
    //    public String getFace() {
    //        return face;
    //    }
    //
    //    public void setFace(String face) {
    //        this.face = face;
    //    }
    //
    //    @Override
    //    public String toString() {
    //
    //        return "{" +
    //                '\"' + "face"+ '\"' +":" + '\"' +face + '\"' +
    //                '}';
    //    }
}
