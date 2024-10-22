package com.rhj.audio

class DmTaskResultBean {
    var from: String? = null
    var sessionId: String? = null
    var recordId: String? = null
    var skillId: String? = null
    var skillName: Boolean? = null
    var taskId: String? = null
    var shouldEndSession: Boolean? = null
    var intentName: String? = null
    var task: String? = null
    var nlg: String? = null
    var ssml: String? = null
    @kotlin.jvm.JvmField
    var speakUrl: String? = null
    var widget: Any? = null
    var dmInput: String? = null
    var endSessionReason: Any? = null
    var display: String? = null
    var watchId: String? = null

    override fun toString(): String {
        return "DmTaskResultBean{" +
                "from='" + from + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", recordId='" + recordId + '\'' +
                ", skillId='" + skillId + '\'' +
                ", skillName=" + skillName +
                ", taskId='" + taskId + '\'' +
                ", shouldEndSession=" + shouldEndSession +
                ", intentName='" + intentName + '\'' +
                ", task='" + task + '\'' +
                ", nlg='" + nlg + '\'' +
                ", ssml='" + ssml + '\'' +
                ", speakUrl='" + speakUrl + '\'' +
                ", widget=" + widget +
                ", dmInput='" + dmInput + '\'' +
                ", endSessionReason=" + endSessionReason +
                ", display='" + display + '\'' +
                ", watchId='" + watchId + '\'' +
                '}'
    }
}
