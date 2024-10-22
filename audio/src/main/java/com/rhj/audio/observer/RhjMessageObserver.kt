package com.rhj.audio.observer

import android.util.Log
import com.aispeech.dui.dds.DDS
import com.aispeech.dui.dds.agent.MessageObserver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rhj.audio.utils.LogUtils
import com.rhj.message.MessageBean
import com.rhj.message.MessageInputTextBean
import com.rhj.message.MessageMediaListBean
import com.rhj.message.MessageMusicBean
import com.rhj.message.MessageOutputTextBean
import com.rhj.message.MessageWeatherBean
import com.rhj.message.MessageWidgetBean
import com.rhj.message.MessageWidgetContentBean
import com.rhj.message.MessageWidgetListBean
import com.rhj.message.MessageWidgetWebBean
import org.json.JSONException
import org.json.JSONObject


/**
 * 客户端MessageObserver, 用于处理客户端动作的消息响应.
 * SDK 支持的消息
 */
class RhjMessageObserver : MessageObserver {
    private var messageCallback: MessageCallback? = null
    private var wakeupStateChangeCallback: WakeupStateChangeCallback? = null
    private var wakeupDoaCallback: WakeupDoaCallback? = null

    private val mGson = Gson()

    /**
     * https://iot-sz.aispeech.com/doc/cicada-doc/#/FAQ/dds_sdk_android?id=q%ef%bc%9asdk%e6%94%af%e6%8c%81%e7%9a%84%e6%b6%88%e6%81%af%e6%9c%89%e5%93%aa%e4%ba%9b%ef%bc%9f
     */
    private val mSubscribeKeys = arrayOf(
        "sys.dialog.state",
        "context.output.text",
        "context.input.text",
        "context.widget.content",
        "context.widget.list",
        "context.widget.web",
        "context.widget.media",
        "context.widget.custom",
        "sys.wakeup.doa"
    )

    // 注册当前更新消息
    fun register(
        messageCallback: MessageCallback?,
        wakeupStateChangeCallback: WakeupStateChangeCallback?,
        wakeupDoaCallback: WakeupDoaCallback
    ) {
        this.messageCallback = messageCallback
        this.wakeupStateChangeCallback = wakeupStateChangeCallback
        this.wakeupDoaCallback = wakeupDoaCallback
        DDS.getInstance().agent.subscribe(mSubscribeKeys, this)
    }

    fun addSubscribe(strings: Array<String?>?) {
        DDS.getInstance().agent.subscribe(strings, this)
    }

    // 注销当前更新消息
    fun unregister() {
        messageCallback = null
        DDS.getInstance().agent.unSubscribe(this)
    }

    override fun onMessage(message: String, data: String) {
        Log.d("DuiMessageObserver", "message : $message data : $data")

        when (message) {
            "context.output.text" -> {
                val messageOutputTextBean = MessageOutputTextBean()
                try {
                    val jo = JSONObject(data)
                    val txt = jo.optString("text", "")
                    messageOutputTextBean.text = txt
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                messageOutputTextBean.messageType = MessageBean.TYPE_OUTPUT
                updateMessage(messageOutputTextBean)
            }

            "context.input.text" -> {
                val messageInputTextBean = MessageInputTextBean()
                messageInputTextBean.messageType = MessageBean.TYPE_INPUT
                try {
                    val jo = JSONObject(data)
                    if (jo.has("var")) {
                        val `var` = jo.optString("var", "")
                        messageInputTextBean.text = `var`
                    }
                    if (jo.has("text")) {
                        val text = jo.optString("text", "")
                        messageInputTextBean.text = text
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                updateMessage(messageInputTextBean)
            }

            "context.widget.content" -> {
                val messageWidgetContentBean = MessageWidgetContentBean()
                messageWidgetContentBean.messageType = MessageBean.TYPE_WIDGET_CONTENT
                try {
                    val jo = JSONObject(data)
                    val title = jo.optString("title", "")
                    val subTitle = jo.optString("subTitle", "")
                    val imgUrl = jo.optString("imageUrl", "")
                    messageWidgetContentBean.setTitle(title)
                    messageWidgetContentBean.subTitle = subTitle
                    messageWidgetContentBean.imgUrl = imgUrl
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                updateMessage(messageWidgetContentBean)
            }

            "context.widget.list" -> {
                val messageWidgetListBean = MessageWidgetListBean()
                messageWidgetListBean.messageType = MessageBean.TYPE_WIDGET_LIST
                try {
                    val jo = JSONObject(data)
                    val currentPage = jo.optInt("currentPage")
                    messageWidgetListBean.currentPage = currentPage

                    val itemsPerPage = jo.optInt("itemsPerPage")
                    messageWidgetListBean.itemsPerPage = itemsPerPage
                    val array = jo.optJSONArray("content")

                    if (array == null || array.length() == 0) {
                        return
                    }
                    val type = object : TypeToken<List<MessageWidgetBean?>?>() {
                    }.type
                    val list = mGson.fromJson<List<MessageWidgetBean>>(
                        jo.optJSONArray("content").toString(), type
                    )
                    messageWidgetListBean.messageWidgetBean = list
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                updateMessage(messageWidgetListBean)
            }

            "context.widget.web" -> {
                val messageWidgetWebBean = MessageWidgetWebBean()
                messageWidgetWebBean.messageType = MessageBean.TYPE_WIDGET_WEB
                try {
                    val jo = JSONObject(data)
                    val url = jo.optString("url")
                    messageWidgetWebBean.url = url
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                updateMessage(messageWidgetWebBean)
            }

            "context.widget.custom" -> {
                var messageWeatherBean = MessageWeatherBean()
                messageWeatherBean.messageType = MessageBean.TYPE_WIDGET_WEATHER
                try {
                    LogUtils.showLargeLog("RhjMessageObserver", "" + data)
                    val jo = JSONObject(data)
                    val name = jo.optString("name")
                    if (name == "weather") {
                        messageWeatherBean = mGson.fromJson(
                            data,
                            MessageWeatherBean::class.java
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                updateMessage(messageWeatherBean)
            }

            "context.widget.media" -> {
                val jsonObject: JSONObject
                val messageMusicListBean = MessageMediaListBean()
                messageMusicListBean.messageType = MessageBean.TYPE_WIDGET_MEDIA
                try {
                    jsonObject = JSONObject(data)
                    val count = jsonObject.optInt("count")
                    val name = jsonObject.optString("widgetName")
                    messageMusicListBean.count = count
                    messageMusicListBean.widgetName = name
                    val `object` = JSONObject(data)
                    val array = `object`.optJSONArray("content")

                    if (array == null || array.length() == 0) {
                        return
                    }
                    val type = object : TypeToken<List<MessageMusicBean?>?>() {
                    }.type
                    val messageMusicBeans = mGson.fromJson<List<MessageMusicBean>>(
                        `object`.optJSONArray("content").toString(), type
                    )
                    messageMusicListBean.list = messageMusicBeans
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                updateMessage(messageMusicListBean)
            }

            "sys.wakeup.doa" -> {
                LogUtils.logd(
                    "RhjMessageObserver",
                    "onMessage: 声源定位角度$data"
                )
                if (wakeupDoaCallback != null) {
                    wakeupDoaCallback!!.onDoa(data)
                }
            }

            "sys.dialog.state" -> if (wakeupStateChangeCallback != null) {
                wakeupStateChangeCallback!!.onState(data)
            }

            else -> {}
        }
    }

    private fun updateMessage(bean: MessageBean) {
        if (messageCallback != null) {
            messageCallback!!.onMessage(bean)
        }
    }
}
