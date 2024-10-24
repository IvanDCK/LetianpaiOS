package com.rhj.message

import com.google.gson.annotations.SerializedName

/**
 * Auto-generated: 2023-01-18 17:10:22
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
class MessageWeatherBean : MessageBean() {
    var extra: ExtraDTO? = null
    var webhookResp: WebhookRespDTO? = null
    var name: String? = null
    var widgetName: String? = null
    var cityName: String? = null
    var duiWidget: String? = null
    var dm_intent: String? = null

    @SerializedName("type")
    var type1: String? = null
    var recommendations: List<String>? = null
    var intentName: String? = null
    var skillId: String? = null
    var skillName: String? = null
    var taskName: String? = null

    class ExtraDTO {
        var nlg_message: String? = null
    }

    class WebhookRespDTO {
        var errorcode: Int? = null
        var extra: ExtraDTO? = null
        var brand: BrandDTO? = null
        var cityName: String? = null

        class ExtraDTO {
            var forecast: List<ForecastDTO>? = null
            var hourForecast24: List<HourForecast24DTO>? = null

            @SerializedName("Index")
            var index: IndexDTO? = null
            var sourceId: Int? = null
            var condition: ConditionDTO? = null
            var future: List<FutureDTO>? = null

            class IndexDTO {
                var liveIndex: List<LiveIndexDTO>? = null
                var aqi: AqiDTO? = null
                var humidity: String? = null

                class AqiDTO {
                    @SerializedName("AQIdesc")
                    var aQIdesc: String? = null
                    var pm25: String? = null

                    @SerializedName("AQL")
                    var aQL: String? = null

                    @SerializedName("AQI")
                    var aQI: String? = null
                }

                class LiveIndexDTO {
                    var name: String? = null
                    var desc: String? = null
                    var status: String? = null
                }
            }

            class ConditionDTO {
                var windLevel: String? = null
                var weather: String? = null
                var wind: String? = null
                var temperature: String? = null
            }

            class ForecastDTO {
                var lowTemp: String? = null
                var sunrise: String? = null
                var tempInteval: String? = null
                var weather: String? = null
                var temperature: String? = null
                var windLevel: String? = null
                var tip: String? = null
                var highTemp: String? = null
                var week: String? = null
                var wind: String? = null
                var sunset: String? = null
                var tempTip: String? = null
                var date: String? = null
            }

            class HourForecast24DTO {
                var processTime: String? = null
                var weather: String? = null
                var temperature: Int? = null
            }

            class FutureDTO {
                var windLevel: String? = null
                var wind: String? = null
                var date: String? = null
                var week: String? = null
                var weather: String? = null
                var temperature: String? = null
            }
        }

        class BrandDTO {
            var isexport: String? = null
            var logoMiddle: String? = null
            var logoSmall: String? = null
            var name: String? = null
            var showName: String? = null
            var logoLarge: String? = null
        }
    }
}