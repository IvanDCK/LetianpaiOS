package com.renhejia.robot.display

import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.provider.Settings
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.display.RobotClockView
import com.renhejia.robot.display.utils.BitmapUtil
import com.renhejia.robot.display.utils.SpineSkinUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class RobotClockView : View, RobotPlatformListener {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mResPool: SpineSkinResPool? = null
    private var mSkin: RobotClockSkin? = null
    private var mPaintFlagsDrawFilter: PaintFlagsDrawFilter? = null
    private var mPlatformState = RobotPlatformState()
    private var mWidth = 0
    private var mHeight = 0
    private var mContext: Context? = null
    private var isRefreshNow = false //
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private val mThread: Thread? = null

    var hourFormat: Int = SPINE_CLOCK_HOURS_24

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context

        //[niu][20191211]Format 12/24 hour time base on system setting
        hourFormat =
            if (DateFormat.is24HourFormat(context)) SPINE_CLOCK_HOURS_24 else SPINE_CLOCK_HOURS_12

        //        Timer timer = new Timer();
//        Calendar calendar = Calendar.getInstance();
//        int second = calendar.get(Calendar.SECOND);
        mPaintFlagsDrawFilter =
            PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        mPaint.isFakeBoldText = true
        mPaint.isAntiAlias = true
        //
//        Calendar calendar = Calendar.getInstance();
//        int second = calendar.get(Calendar.SECOND);
//        timer.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                postInvalidate();
//            }
//        }, (60 - second) * 1000, 1000 * 60);
    }

    var mSettingsObserver: ContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            //            LogUtil.d(TAG, "is24HourFormat onChanged:" + Settings.System.getString(getContext().getContentResolver(), Settings.System.TIME_12_24));
            hourFormat =
                if (DateFormat.is24HourFormat(context)) SPINE_CLOCK_HOURS_24 else SPINE_CLOCK_HOURS_12
            //this.mHourFormat = this.hourFormat

            postInvalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //        LogUtil.d(TAG, "registerContentObserver.....");
        context.contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.TIME_12_24),
            true,
            mSettingsObserver
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //        LogUtil.d(TAG, "unregisterContentObserver.....");
        context.contentResolver.unregisterContentObserver(mSettingsObserver)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = measureWidth
        mHeight = measureHeight
        if (mSkin != null) {
            mSkin!!.resize(Rect(0, 0, mWidth, mHeight))
        }
    }

    fun setSkin(skin: RobotClockSkin) {
        mResPool = skin.resPool
        mSkin = skin
        if (mWidth != 0 && mHeight != 0) {
            mSkin!!.resize(Rect(0, 0, mWidth, mHeight))
        }
    }

    private fun drawStep(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val step = mSkin!!.step
        if (step != null) {
            val stepString = mPlatformState.stepNumber.toString()
            drawNumbers(canvas, step, stepString)
        }
    }

    private fun drawAirTemp(canvas: Canvas) {
        if (mSkin == null) {
            return
        }
        val airTemp = mSkin!!.airTemp
        if (airTemp != null) {
            var tempString = "--c"
            if (mPlatformState.weatherState != RobotClockSkin.Companion.WEATHER_TYPE_NO_INFO) {
//                tempString = mPlatformState.currentTemp + "c";
                tempString = mPlatformState.currentTemp.toString() + "°"
            }

            drawNumbers(canvas, airTemp, tempString)
        }
    }

    private fun drawWeekAnchor(canvas: Canvas, date: Date) {
        if (mSkin == null) {
            return
        }

        val anchor = mSkin!!.weekAnchor
        if (anchor != null) {
            val now = Calendar.getInstance()
            now.time = date
            // boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
            val weekDay = now[Calendar.DAY_OF_WEEK]
            var weekId = 0
            when (weekDay) {
                Calendar.SUNDAY -> weekId = 7
                Calendar.MONDAY -> weekId = 1
                Calendar.TUESDAY -> weekId = 2
                Calendar.WEDNESDAY -> weekId = 3
                Calendar.THURSDAY -> weekId = 4
                Calendar.FRIDAY -> weekId = 5
                Calendar.SATURDAY -> weekId = 6
                else -> {}
            }

            val imageFile = anchor.getImgFile()

            val bitmap =
                imageFile?.let { mResPool!!.getBitmap(it, mSkin!!.getxRadio(), mSkin!!.getyRadio()) }
            if (bitmap != null) {
                val matrix1 = Matrix()
                val imgAnchor = anchor.getDispAnchor()
                val displayPoint = Point(
                    anchor.getDispRect()!!.centerX(), anchor.getDispRect()!!.centerY()
                )
                matrix1.postTranslate(
                    (displayPoint.x - imgAnchor!!.x).toFloat(),
                    (displayPoint.y - imgAnchor.y).toFloat()
                )
                matrix1.postRotate(
                    (weekId * 360 / 7).toFloat(),
                    displayPoint.x.toFloat(),
                    displayPoint.y.toFloat()
                )
                canvas.drawBitmap(bitmap, matrix1, mPaint)
            }
        }
    }

    private fun drawBatteryAnchor(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val anchor = mSkin!!.batteryAnchor
        if (anchor != null) {
            val batteryLevel = mPlatformState.batteryLevel

            val imgAnchor: Point?
            val anchorDispRect = anchor.getDispRect()
            val displayPoint = Point(
                anchorDispRect!!.centerX(), anchorDispRect.centerY()
            )

            val bitmap =
                anchor.getImgFile()
                    ?.let { mResPool!!.getBitmap(it, mSkin!!.getxRadio(), mSkin!!.getyRadio()) }

            if (bitmap != null) {
                val matrix1 = Matrix()
                imgAnchor = anchor.getDispAnchor()
                matrix1.postTranslate(
                    (displayPoint.x - imgAnchor!!.x).toFloat(),
                    (displayPoint.y - imgAnchor.y).toFloat()
                )
                matrix1.postRotate(
                    (batteryLevel * 33 / 10).toFloat(),
                    displayPoint.x.toFloat(),
                    displayPoint.y.toFloat()
                )
                canvas.drawBitmap(bitmap, matrix1, mPaint)
            }
        }
    }

    private fun drawRotateBackground(canvas: Canvas, rotateBg: RobotSkinAnchor?) {
        if (mSkin == null) {
            return
        }

        //TODO 背景选装
        if (rotateBg != null && rotateBg.getDispRect() != null) {
            val imgAnchor: Point?
            val displayPoint = Point(
                rotateBg.getDispRect()!!.centerX(), rotateBg.getDispRect()!!.centerY()
            )

            var bitmap =
                mResPool!!.getBitmap(rotateBg.getImgFile()!!, mSkin!!.getxRadio(), mSkin!!.getyRadio())
            bitmap = rsBlur(mContext, bitmap, 10f, 1f)
            val cal = Calendar.getInstance()
            cal.time = Date(System.currentTimeMillis())
            val nSecond = cal[Calendar.SECOND]
            val mMillisecond = cal[Calendar.MILLISECOND]
            if (bitmap != null) {
                val matrix1 = Matrix()
                imgAnchor = rotateBg.getDispAnchor()
                if (imgAnchor == null) {
                    logi("testFileName", "imgAnchor is null")
                }
                matrix1.postTranslate(
                    (displayPoint.x - imgAnchor!!.x).toFloat(),
                    (displayPoint.y - imgAnchor.y).toFloat()
                )
                //                matrix1.postRotate((nSecond % 8)*45+ (int)(mMillisecond * 45/1000), displayPoint.x, displayPoint.y);
                matrix1.postRotate(
                    ((nSecond % 15) * 24 + (mMillisecond * 24 / 1000)).toFloat(),
                    displayPoint.x.toFloat(),
                    displayPoint.y.toFloat()
                )
                //                matrix1.postRotate(nSecond*6, displayPoint.x, displayPoint.y);
                canvas.drawBitmap(bitmap, matrix1, mPaint)
            }
        }
    }

    private fun drawNumbers(canvas: Canvas, numbers: RobotSkinNumber, text: String) {
        if (mSkin == null) {
            return
        }
        //        LogUtils.logi("testFileName","drawAirTemp:======= 6 ");
        val align = numbers.getAlign()

        //        mPaint.setColor(Color.YELLOW);
//        canvas.drawRect(numbers.getDispRect(), mPaint);
        if (align == (RobotClockSkin.Companion.ALIGN_LEFT or RobotClockSkin.Companion.ALIGN_TOP)) {
            val top = numbers.getDispRect()!!.top.toFloat()
            var left = numbers.getDispRect()!!.left.toFloat()

            val calendar = Calendar.getInstance()
            val millisecond = calendar[Calendar.MILLISECOND]

            for (i in 0 until text.length) {
                val subTime = text.substring(i, i + 1)
                val bitmap = mResPool!!.getBitmap(
                    numbers.getImgFilename(subTime),
                    mSkin!!.getxRadio(),
                    mSkin!!.getyRadio()
                )
                //                Bitmap bitmap = mResPool.getBitmap(numbers.getImgFilenameFlash(subTime,millisecond), mSkin.getxRadio(), mSkin.getyRadio());
//                if (bitmap == null) {
//                    bitmap = mResPool.getBitmap(numbers.getImgFilename(subTime), mSkin.getxRadio(), mSkin.getyRadio());
//                }
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, left, top, mPaint)
                    left += numbers.getFileSpace().toFloat()
                    left += bitmap.width.toFloat()
                }
            }
        } else {
            var width = 0
            var height = 0
            if (numbers == null) {
                logi("robot_exceptipn", "numbers is null")
            } else if (numbers.getDispRect() == null) {
                logi("robot_exceptipn", "numbers.getDispRect() is null")
            } else if (numbers.getDispRect()!!.top == 0) {
                logi("robot_exceptipn", "numbers.getDispRect() is null === 0")
            }
            if (numbers?.getDispRect() == null) {
                return
            }
            var top = numbers.getDispRect()!!.top
            var left = numbers.getDispRect()!!.left

            for (i in 0 until text.length) {
                val subTime = text.substring(i, i + 1)
                val calendar = Calendar.getInstance()
                val millisecond = calendar[Calendar.MILLISECOND]
                val bitmap = mResPool!!.getBitmap(
                    numbers.getImgFilename(subTime),
                    mSkin!!.getxRadio(),
                    mSkin!!.getyRadio()
                )
                //                Bitmap bitmap = mResPool.getBitmap(numbers.getImgFilenameFlash(subTime,millisecond), mSkin.getxRadio(), mSkin.getyRadio());
//                if (bitmap == null) {
//                    bitmap = mResPool.getBitmap(numbers.getImgFilename(subTime), mSkin.getxRadio(), mSkin.getyRadio());
//                }
                if (bitmap != null) {
                    width += numbers.getFileSpace()
                    width += bitmap.width

                    if (bitmap.height > height) {
                        height = bitmap.height
                    }
                }
            }

            if (align == (RobotClockSkin.Companion.ALIGN_CENTER or RobotClockSkin.Companion.ALIGN_TOP)) {
                left = numbers.getDispRect()!!.left + (numbers.getDispRect()!!.width() - width) / 2
                top = numbers.getDispRect()!!.top
            } else if (align == (RobotClockSkin.Companion.ALIGN_RIGHT or RobotClockSkin.Companion.ALIGN_TOP)) {
                left = numbers.getDispRect()!!.right - width
                top = numbers.getDispRect()!!.top
            } else if (align == (RobotClockSkin.Companion.ALIGN_LEFT or RobotClockSkin.Companion.ALIGN_MIDDLE)) {
                left = numbers.getDispRect()!!.left
                top = numbers.getDispRect()!!.top + (numbers.getDispRect()!!.height() - height) / 2
            } else if (align == (RobotClockSkin.Companion.ALIGN_CENTER or RobotClockSkin.Companion.ALIGN_MIDDLE)) {
                left = numbers.getDispRect()!!.left + (numbers.getDispRect()!!.width() - width) / 2
                top = numbers.getDispRect()!!.top + (numbers.getDispRect()!!.height() - height) / 2
            } else if (align == (RobotClockSkin.Companion.ALIGN_RIGHT or RobotClockSkin.Companion.ALIGN_MIDDLE)) {
                left = numbers.getDispRect()!!.left + numbers.getDispRect()!!.width() - width
                top = numbers.getDispRect()!!.top + (numbers.getDispRect()!!.height() - height) / 2
            } else if (align == (RobotClockSkin.Companion.ALIGN_LEFT or RobotClockSkin.Companion.ALIGN_BOTTOM)) {
                left = numbers.getDispRect()!!.left
                top = numbers.getDispRect()!!.top + numbers.getDispRect()!!.height() - height
            } else if (align == (RobotClockSkin.Companion.ALIGN_CENTER or RobotClockSkin.Companion.ALIGN_BOTTOM)) {
                left = numbers.getDispRect()!!.left + (numbers.getDispRect()!!.width() - width) / 2
                top = numbers.getDispRect()!!.top + numbers.getDispRect()!!.height() - height
            } else if (align == (RobotClockSkin.Companion.ALIGN_RIGHT or RobotClockSkin.Companion.ALIGN_BOTTOM)) {
                left = numbers.getDispRect()!!.left + numbers.getDispRect()!!.width() - width
                top = numbers.getDispRect()!!.top + numbers.getDispRect()!!.height() - height
            }
            val calendar = Calendar.getInstance()
            val millisecond = calendar[Calendar.MILLISECOND]
            for (i in 0 until text.length) {
                val subTime = text.substring(i, i + 1)
                val bitmap = mResPool!!.getBitmap(
                    numbers.getImgFilename(subTime),
                    mSkin!!.getxRadio(),
                    mSkin!!.getyRadio()
                )
                //                Bitmap bitmap = mResPool.getBitmap(numbers.getImgFilenameFlash(subTime,millisecond), mSkin.getxRadio(), mSkin.getyRadio());
//                if (bitmap == null) {
//                    bitmap = mResPool.getBitmap(numbers.getImgFilename(subTime), mSkin.getxRadio(), mSkin.getyRadio());
//                }
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), mPaint)
                    left += numbers.getFileSpace()
                    left += bitmap.width
                }
            }
        }
    }

    private fun drawDigitTime(canvas: Canvas, digitTime: RobotSkinNumber, date: Date) {
        val timeString = digitTime.getTimeString(date, hourFormat)
        if (timeString.isNotEmpty()) {
            drawNumbers(canvas, digitTime, timeString)
        }
    }

    private fun drawAnalogTime(canvas: Canvas, analogTime: RobotSkinAnalogTime?, date: Date) {
        if (mSkin == null) {
            return
        }

        if (analogTime != null) {
            val cal = Calendar.getInstance()
            cal.time = date

            val nHour = cal[Calendar.HOUR]
            val nMinute = cal[Calendar.MINUTE]
            val nSecond = cal[Calendar.SECOND]
            val nMilliSecond = cal[Calendar.MILLISECOND]

            val imgAnchor: Point?
            val displayPoint = Point(
                analogTime.getDispRect()!!.centerX(), analogTime.getDispRect()!!.centerY()
            )

            var bitmap = mResPool!!.getBitmap(
                analogTime.getHourFilename(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )

            //            if (bitmap != null) {
//                Matrix matrix1 = new Matrix();
//                imgAnchor = analogTime.getDispHourAnchor();
//                matrix1.postTranslate(displayPoint.x - imgAnchor.x, displayPoint.y - imgAnchor.y);
//                matrix1.postRotate((nHour * 3600 + nMinute * 60 + nSecond) * 360 / (12 * 3600), displayPoint.x, displayPoint.y);
//                canvas.drawBitmap(bitmap, matrix1, mPaint);
//            }
            bitmap = mResPool!!.getBitmap(
                analogTime.getMinuteFilename(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )

            //
//            if (bitmap != null) {
//                Matrix matrix1 = new Matrix();
//                imgAnchor = analogTime.getDispMinuteAnchor();
//                matrix1.postTranslate(displayPoint.x - imgAnchor.x, displayPoint.y - imgAnchor.y);
//                matrix1.postRotate((nMinute * 60 + nSecond) * 360 / (3600), displayPoint.x, displayPoint.y);
//                canvas.drawBitmap(bitmap, matrix1, mPaint);
//            }

//            if (analogTime.getgetDispSecondAnchor()()!= null) {
//                bitmap = mResPool.getBitmap(analogTime.getgetSecondFilename()(), mSkin.getxRadio(), mSkin.getyRadio());
//
//                if (bitmap != null) {
//                    Matrix matrix1 = new Matrix();
//                    imgAnchor = analogTime.getgetDispSecondAnchor()();
//                    matrix1.postTranslate(displayPoint.x - imgAnchor.x, displayPoint.y - imgAnchor.y);
////                    matrix1.postRotate((nSecond) * 360 / (60), displayPoint.x, displayPoint.y);
//                    matrix1.postRotate((nSecond) * 360 *5 / (60), displayPoint.x, displayPoint.y);
//                    canvas.drawBitmap(bitmap, matrix1, mPaint);
//                }
//            }
//
            if (analogTime.getDispSecondAnchor() != null) {
                bitmap = mResPool!!.getBitmap(
                    analogTime.getSecondFilename(),
                    mSkin!!.getxRadio(),
                    mSkin!!.getyRadio()
                )

                if (bitmap != null) {
                    val matrix1 = Matrix()
                    imgAnchor = analogTime.getDispSecondAnchor()
                    matrix1.postTranslate(
                        (displayPoint.x - imgAnchor!!.x).toFloat(),
                        (displayPoint.y - imgAnchor.y).toFloat()
                    )
                    //                    matrix1.postRotate((nSecond) * 360 / (60), displayPoint.x, displayPoint.y);
                    matrix1.postRotate(
                        ((nSecond * 1000 + nMilliSecond) * 360 * 10 / (60 * 1000)).toFloat(),
                        displayPoint.x.toFloat(),
                        displayPoint.y.toFloat()
                    )
                    canvas.drawBitmap(bitmap, matrix1, mPaint)
                }
            }
        }
    }

    private fun drawBatteryAngle(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.batteryAngle
        if (icon != null) {
            val batteryLevel = mPlatformState.batteryLevel
            val total = icon.getTotal()
            if (total == 0) {
                return
            }
            val batteryVal = (batteryLevel + (100 / (total * 2))) / (100 / total)

            val bitmapFull =
                mResPool!!.getBitmap(icon.getBatteryFanFull(), mSkin!!.getxRadio(), mSkin!!.getyRadio())
            val bitmapEmpty =
                mResPool!!.getBitmap(icon.getBatteryFanEmpty(), mSkin!!.getxRadio(), mSkin!!.getyRadio())
            if (bitmapEmpty == null || bitmapFull == null) {
                return
            }

            if (bitmapFull != null) {
                val matrix1 = Matrix()
                val imgAnchor = icon.getDisplayAnchor()
                matrix1.postTranslate(
                    (icon.getOrigAnchor()!!.x - imgAnchor!!.x).toFloat(),
                    (icon.getOrigAnchor()!!.y - imgAnchor!!.y).toFloat()
                )
                for (i in 0 until total) {
                    if (i == 0) {
                        matrix1.postRotate(
                            icon.getStartAngle().toFloat(),
                            icon.getOrigAnchor()!!.x.toFloat(),
                            icon.getOrigAnchor()!!.y.toFloat()
                        )
                    } else {
                        matrix1.postRotate(
                            (icon.getImageAngle() + icon.getIntervalAngle()).toFloat(),
                            icon.getOrigAnchor()!!.x.toFloat(),
                            icon.getOrigAnchor()!!.y.toFloat()
                        )
                    }

                    if (i < batteryVal) {
                        canvas.drawBitmap(bitmapFull, matrix1, mPaint)
                    } else {
                        canvas.drawBitmap(bitmapEmpty, matrix1, mPaint)
                    }
                }
            }
        }
    }

    private fun drawBatterySquares(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.batterySquares
        if (icon != null) {
            val batteryLevel = mPlatformState.batteryLevel
            val total = icon.getTotal()
            if (total == 0) {
                return
            }
            val batteryVal = (batteryLevel + (100 / (total * 2))) / (100 / total)

            val bitmapFull = mResPool!!.getBitmap(
                icon.getBatterySquaresFull(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            val bitmapEmpty = mResPool!!.getBitmap(
                icon.getBatterySquaresEmpty(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            drawSquares(canvas, icon, bitmapFull, bitmapEmpty, total, batteryVal)
        }
    }

    private fun drawStepSquares(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val stepIcon = mSkin!!.stepSquares
        if (stepIcon != null) {
            val stepNum = mPlatformState.stepNumber
            val total = stepIcon.getTotal()
            if (total == 0) {
                return
            }
            var stepLevel = stepNum / 1000
            if (stepLevel > total) {
                stepLevel = total
            }
            val bitmapFull = mResPool!!.getBitmap(
                stepIcon.getStepSquaresFull(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            val bitmapEmpty = mResPool!!.getBitmap(
                stepIcon.getStepSquaresEmpty(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            drawSquares(canvas, stepIcon, bitmapFull, bitmapEmpty, total, stepLevel)
        }
    }

    private fun drawSquares(
        canvas: Canvas,
        icon: RobotSkinImageWithSpace?,
        bitmapFull: Bitmap?,
        bitmapEmpty: Bitmap?,
        total: Int,
        currentLevel: Int
    ) {
        var displayX = 0
        var displayY = 0
        if (icon != null) {
            if (total == 0) {
                return
            }

            if ((bitmapEmpty != null) && (bitmapFull != null)) {
                displayX = icon.getDispRect()!!.left
                displayY = icon.getDispRect()!!.top

                if (currentLevel >= 0) {
                    for (j in 0 until currentLevel) {
                        canvas.drawBitmap(
                            bitmapFull,
                            displayX.toFloat(),
                            displayY.toFloat(),
                            mPaint
                        )
                        if (icon.getAligns() == RobotClockSkin.Companion.ALIGN_LEFT_TO_RIGHT) {
                            displayX += bitmapFull.width + icon.getFileSpace()
                        } else if (icon.getAligns() == RobotClockSkin.Companion.ALIGN_RIGHT_TO_LEFT) {
                            displayX -= bitmapFull.width + icon.getFileSpace()
                        } else if (icon.getAligns() == RobotClockSkin.Companion.ALIGN_TOP_TO_BOTTOM) {
                            displayY += bitmapFull.height + icon.getFileSpace()
                        } else if (icon.getAligns() == RobotClockSkin.Companion.ALIGN_BOTTOM_TO_TOP) {
                            displayY -= bitmapFull.height + +icon.getFileSpace()
                        }
                    }

                    for (i in 0 until total - currentLevel) {
                        canvas.drawBitmap(
                            bitmapEmpty,
                            displayX.toFloat(),
                            displayY.toFloat(),
                            mPaint
                        )
                        if (icon.getAligns() == RobotClockSkin.Companion.ALIGN_LEFT_TO_RIGHT) {
                            displayX += bitmapEmpty.width + icon.getFileSpace()
                        } else if (icon.getAligns() == RobotClockSkin.Companion.ALIGN_RIGHT_TO_LEFT) {
                            displayX -= bitmapEmpty.width + icon.getFileSpace()
                        } else if (icon.getAligns() == RobotClockSkin.Companion.ALIGN_TOP_TO_BOTTOM) {
                            displayY += bitmapEmpty.height + icon.getFileSpace()
                        } else if (icon.getAligns() == RobotClockSkin.Companion.ALIGN_BOTTOM_TO_TOP) {
                            displayY -= bitmapEmpty.height + icon.getFileSpace()
                        }
                    }
                }
            }
        }
    }


    private fun drawStepSkinArc(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val stepSkinArc = mSkin!!.stepSkinArc

        if (stepSkinArc != null) {
            val stepCount = mPlatformState.stepNumber
            var stepProgress = stepCount / 100
            if (stepProgress >= 100) {
                stepProgress = 100
            }
            val paint = Paint()
            paint.isAntiAlias = true
            paint.style = Paint.Style.STROKE
            paint.strokeCap = Paint.Cap.ROUND
            if (stepProgress > 0) {
                paint.color = stepSkinArc.getColor()
                paint.strokeWidth = stepSkinArc.getStrokeWidth()
                canvas.drawArc(
                    RectF(stepSkinArc.getDispRect()),
                    stepSkinArc.getStartAngle().toFloat(),
                    (stepProgress.toFloat() / 100) * stepSkinArc.getSweepAngle(),
                    false,
                    paint
                )
            }
        }
    }


    private fun drawBatteryProgressBar(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val batteryProgress = mSkin!!.batteryProgress

        if (batteryProgress != null) {
            val batteryLevel = mPlatformState.batteryLevel
            val currentProgress =
                (batteryProgress.getDispRect()!!.right - batteryProgress.getDispRect()!!.left) * batteryLevel / 100
            val backgroundPaint = Paint()
            backgroundPaint.isAntiAlias = true
            backgroundPaint.color = batteryProgress.getBgColor()
            canvas.drawRect(batteryProgress.getDispRect()!!, backgroundPaint)

            val foregroundPaint = Paint()
            foregroundPaint.isAntiAlias = true
            foregroundPaint.color = batteryProgress.getFgColor()
            canvas.drawRect(
                Rect(
                    batteryProgress.getDispRect()!!.left,
                    batteryProgress.getDispRect()!!.top,
                    batteryProgress.getDispRect()!!.left + currentProgress,
                    batteryProgress.getDispRect()!!.bottom
                ), foregroundPaint
            )
        }
    }

    private fun drawBattery(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.battery
        if (icon != null) {
            val batteryLevel = mPlatformState.batteryLevel
            val bitmap = mResPool!!.getBitmap(
                icon.getBatteryFilename(batteryLevel),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    icon.getDispRect()!!.left.toFloat(),
                    icon.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawWifi(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.wifi
        if (icon != null) {
            val wifiEnable = mPlatformState.wifiEnabled
            val bitmap = mResPool!!.getBitmap(
                icon.getOnOffFilename(wifiEnable),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    icon.getDispRect()!!.left.toFloat(),
                    icon.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawBluetooth(canvas: Canvas) {
        if (mSkin == null) {
            return
        }
        val icon = mSkin!!.bluetooth
        if (icon != null) {
            val bluetoothEnable = mPlatformState.bluetoothEnabled
            val bitmap = mResPool!!.getBitmap(
                icon.getOnOffFilename(bluetoothEnable),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    icon.getDispRect()!!.left.toFloat(),
                    icon.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawCharge(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.charge
        if (icon != null) {
            val isCharging = mPlatformState.batteryCharging

            val bitmap = mResPool!!.getBitmap(
                icon.getOnOffFilename(isCharging),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    icon.getDispRect()!!.left.toFloat(),
                    icon.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawWeather(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.weather
        if (icon != null) {
            var weatherId = mPlatformState.weatherState
            weatherId = 0
            if (weatherId != RobotPlatformState.Companion.NO_WEATHER) {
                val bitmap = mResPool!!.getBitmap(
                    icon.getWeatherFilename(weatherId),
                    mSkin!!.getxRadio(),
                    mSkin!!.getyRadio()
                )
                if (bitmap != null && icon.getDispRect() != null) {
                    canvas.drawBitmap(
                        bitmap,
                        icon.getDispRect()!!.left.toFloat(),
                        icon.getDispRect()!!.top.toFloat(),
                        mPaint
                    )
                }
            }
        }
    }

    private fun drawNotices(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.notices
        if (icon != null) {
            val bitmap =
                mResPool!!.getBitmap(icon.getNoticeFileName(), mSkin!!.getxRadio(), mSkin!!.getyRadio())
            if (bitmap != null && icon.getDispRect() != null) {
                canvas.drawBitmap(
                    bitmap,
                    icon.getDispRect()!!.left.toFloat(),
                    icon.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawFansIcon(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.fansIcon
        if (icon != null && icon.getDispRect() != null) {
            val bitmap = mResPool!!.getBitmap(
                icon.getFansIconFileName(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    icon.getDispRect()!!.left.toFloat(),
                    icon.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawFansHead(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.fansHead
        if (icon?.getDispRect() != null) {
            val bitmap = mResPool!!.getBitmap(
                icon.getFansHeadFileName(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    icon.getDispRect()!!.left.toFloat(),
                    icon.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawAqiNumber(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val label = mSkin!!.aqiNumber
        if (label != null) {
            var aqiNumber = ""

            if (mPlatformState.weatherState != RobotClockSkin.Companion.WEATHER_TYPE_NO_INFO) {
                aqiNumber = mPlatformState.airQuality.toString()
            }

            drawLabel(canvas, label, aqiNumber)
        }
    }

    private fun drawStepNumber(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val label = mSkin!!.stepNumber
        if (label != null) {
            var stepNumber = ""
            stepNumber = mPlatformState.stepNumber.toString()

            drawLabel(canvas, label, stepNumber)
        }
    }

    private fun drawBatteryNumber(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        if (mPlatformState.batteryLevel == 0) {
            return
        }
        val label = mSkin!!.batteryNumber
        if (label != null) {
            var batteryLevel = ""
            batteryLevel =
                if ((label.getDataFormat() != null) && (label.getDataFormat() == NO_PERCENT_SIGN)) {
                    mPlatformState.batteryLevel.toString()
                } else {
                    mPlatformState.batteryLevel.toString() + "%"
                }

            drawLabel(canvas, label, batteryLevel)
        }
    }

    private fun drawTemperature(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val label = mSkin!!.temperature
        if (label != null) {
            var temperature = ""
            if (mPlatformState.currentTemp == RobotPlatformState.Companion.NO_TEMP) {
                temperature = "--" + "℃"
                label.setColor(TEXT_COLOR_GARY)
            } else {
                temperature = mPlatformState.currentTemp.toString() + "℃"
            }

            drawLabel(canvas, label, temperature)
        }
    }

    private fun drawTemperatureRange(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val label = mSkin!!.temperatureRange
        if (label != null) {
            val temperature = mPlatformState.tempRange
            drawLabel(canvas, label, temperature!!)
        }
    }

    private fun drawLabel(canvas: Canvas, label: RobotSkinLabel, text: String) {
        mPaint.textSize = label.getDispSize().toFloat()
        mPaint.color = label.getColor()
        if (label.getStyle() == RobotClockSkin.Companion.STYLE_ITALIC) {
            mPaint.textSkewX = -0.25.toFloat()
        }

        val fontMetrics = mPaint.fontMetricsInt
        val baseline = fontMetrics.top + fontMetrics.descent

        val align = label.getAlign()

        if (align == (RobotClockSkin.Companion.ALIGN_LEFT or RobotClockSkin.Companion.ALIGN_TOP)) {
//            mPaint.setTextSkewX((float) -0.25);
            if (label != null && (label.getDispRect() != null)) {
                canvas.drawText(
                    text,
                    label.getDispRect()!!.left.toFloat(),
                    (label.getDispRect()!!.top - baseline).toFloat(),
                    mPaint
                )
            }
        } else {
            val minRect = Rect()
            mPaint.getTextBounds(text, 0, text.length, minRect)
            if (label == null || (label.getDispRect() == null)) {
                return
            }
            val topY = label.getDispRect()!!.top - baseline
            val middleY =
                label.getDispRect()!!.top + label.getDispRect()!!.height() / 2 - minRect.height() / 2 - baseline
            val bottomY = label.getDispRect()!!.bottom - baseline - minRect.height()

            val centerX = label.getDispRect()!!.left + (label.getDispRect()!!.width() - minRect.width()) / 2
            val leftX = label.getDispRect()!!.left
            val rightX = label.getDispRect()!!.right - minRect.width()

            if (align == (RobotClockSkin.Companion.ALIGN_CENTER or RobotClockSkin.Companion.ALIGN_TOP)) {
                canvas.drawText(text, centerX.toFloat(), topY.toFloat(), mPaint)
            } else if (align == (RobotClockSkin.Companion.ALIGN_RIGHT or RobotClockSkin.Companion.ALIGN_TOP)) {
                canvas.drawText(text, rightX.toFloat(), topY.toFloat(), mPaint)
            } else if (align == (RobotClockSkin.Companion.ALIGN_LEFT or RobotClockSkin.Companion.ALIGN_MIDDLE)) {
                canvas.drawText(text, leftX.toFloat(), middleY.toFloat(), mPaint)
            } else if (align == (RobotClockSkin.ALIGN_CENTER or RobotClockSkin.Companion.ALIGN_MIDDLE)) {
                canvas.drawText(text, centerX.toFloat(), middleY.toFloat(), mPaint)
            } else if (align == (RobotClockSkin.Companion.ALIGN_RIGHT or RobotClockSkin.Companion.ALIGN_MIDDLE)) {
                canvas.drawText(text, rightX.toFloat(), middleY.toFloat(), mPaint)
            } else if (align == (RobotClockSkin.Companion.ALIGN_LEFT or RobotClockSkin.Companion.ALIGN_BOTTOM)) {
                canvas.drawText(text, leftX.toFloat(), bottomY.toFloat(), mPaint)
            } else if (align == (RobotClockSkin.Companion.ALIGN_CENTER or RobotClockSkin.Companion.ALIGN_BOTTOM)) {
                canvas.drawText(text, centerX.toFloat(), bottomY.toFloat(), mPaint)
            } else if (align == (RobotClockSkin.Companion.ALIGN_RIGHT or RobotClockSkin.Companion.ALIGN_BOTTOM)) {
                canvas.drawText(text, rightX.toFloat(), bottomY.toFloat(), mPaint)
            } else {
                canvas.drawText(text, leftX.toFloat(), topY.toFloat(), mPaint)
            }
        }
        mPaint.textSkewX = 0f
    }

    private fun getAqiText(aqiNumber: Int): String {
        return if (aqiNumber < 51) {
            "优"
        } else if (aqiNumber < 101) {
            "良"
        } else if (aqiNumber < 151) {
            "轻度污染"
        } else if (aqiNumber < 201) {
            "中度污染"
        } else if (aqiNumber < 301) {
            "重度污染"
        } else {
            "严重污染"
        }
    }

    private fun drawAqiText(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val label = mSkin?.aqiText
        if (label != null) {
            var aqiText = ""
            if (mPlatformState.weatherState != RobotClockSkin.Companion.WEATHER_TYPE_NO_INFO) {
                aqiText = getAqiText(mPlatformState.airQuality)
                ////                aqiText = "晴 26°  3个日历提醒";
//                aqiText = "晴 26°";
            }
            drawLabel(canvas, label, aqiText)
        }
    }

    private fun drawNoticeText(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val label = mSkin!!.noticeText
        if (label != null) {
            var noticeText = ""
            //TODO
            noticeText = " 1个日历提醒"

            drawLabel(canvas, label, noticeText)
        }
    }

    private fun drawVolume(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val icon = mSkin!!.volume
        if (icon != null) {
            val volume = mPlatformState.mediaVolume
            val bitmap = mResPool!!.getBitmap(
                icon.getVolumeFilename(volume),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    icon.getDispRect()!!.left.toFloat(),
                    icon.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    //    private void drawBackgroup(Canvas canvas) {
    //
    //        SpineSkinImage background = mSkin.getBackground();
    //        if (background != null) {
    //            Bitmap bitmap = mResPool.getBitmap(background.getBackgroundFilename());
    //            if (bitmap != null) {
    //                canvas.drawBitmap(bitmap, background.getgetOrigRect()(), background.getDispRect(), mPaint);
    //            }
    //        }
    //    }
    private fun drawBackgroup(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val background = mSkin!!.background
        var bitmap: Bitmap?
        if (background != null) {
            if (SpineSkinUtils.isChangeSkinBackgroundExit(mContext, mResPool!!.getSkinPath())) {
                if (mResPool!!.getBitmap(background.getCustomizedBackgroundFilename()) == null) {
                    bitmap = BitmapUtil.convertImageToBitmap(
                        SpineSkinUtils.getFileNameOfChangeSkinBackground(
                            mContext,
                            mResPool!!.getSkinPath()
                        )
                    )
                    mResPool!!.fillMapBitmap(background.getCustomizedBackgroundFilename(), bitmap)
                }
                bitmap = mResPool!!.getBitmap(background.getCustomizedBackgroundFilename())
            } else {
                bitmap = mResPool!!.getBitmap(background.getBackgroundFilename())
            }
            if (bitmap != null && background != null && background.getOrigRect() != null && background.getDispRect() != null) {
                canvas.drawBitmap(bitmap, background.getOrigRect(),
                    background.getDispRect()!!, mPaint)
            }
        }
    }

    private fun drawForeground(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val foreground = mSkin!!.foreground
        if (foreground != null) {
            val bitmap = mResPool!!.getBitmap(
                foreground.getForeground(),
                mSkin!!.getxRadio(),
                mSkin!!.getyRadio()
            )
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    foreground.getDispRect()!!.left.toFloat(),
                    foreground.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawMiddle(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val middle = mSkin!!.middle
        if (middle != null) {
            val bitmap =
                mResPool!!.getBitmap(middle.getMiddle(), mSkin!!.getxRadio(), mSkin!!.getyRadio())
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    middle.getDispRect()!!.left.toFloat(),
                    middle.getDispRect()!!.top.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun drawLabelTime(canvas: Canvas, label: RobotSkinLabel, date: Date, hourFormat: Int) {
        var strFormat = label.getDataFormat()

        if (hourFormat == SPINE_CLOCK_HOURS_24) {
            strFormat = strFormat!!.replace("a", "")
            strFormat = strFormat.replace("hh", "HH")
        }

        if (strFormat!!.length > 0) {
            if (strFormat.length == 1) {
//                SimpleDateFormat format = new SimpleDateFormat(strFormat, Locale.getDefault());
//                String strDate = SpineSkinFileMap.getFilePostfix(format.format(date), label.getgetLanguageFormat()());
////                mPaint.setTextSkewX((float) -0.25);
//                drawLabel(canvas, label, strDate);

                val format = SimpleDateFormat(strFormat, Locale.getDefault())

                val strDate =
                    RobotSkinFileMap.getFilePostfix(format.format(date), label.getLanguageFormat())
                if ((Locale.getDefault().language) == THAI || (Locale.getDefault().language) == GERMAN) {
                    if ((label.getLanguageFormat() != null) && (label.getLanguageFormat() == THAI)) {
                        drawLabel(canvas, label, strDate!!)
                    } else if (label.getLanguageFormat() == null) {
                        label.setDispSize(14)
                        drawLabel(canvas, label, strDate!!)
                    }
                } else if (!((label.getLanguageFormat() != null) && (label.getLanguageFormat() == THAI))) {
                    drawLabel(canvas, label, strDate!!)
                }
            } else {
                if (strFormat == YYYYMMDD_FORMAT || strFormat == MMDD_FORMAT) {
                    val format = SimpleDateFormat(strFormat, Locale.getDefault())
                    val strDate = RobotSkinFileMap.getFilePostfix(format.format(date))
                    //                    mPaint.setTextSkewX((float) -0.25);
                    drawLabel(canvas, label, strDate!!)
                } else {
                    val strBestFormat =
                        DateFormat.getBestDateTimePattern(Locale.getDefault(), strFormat)
                    val strDate = DateFormat.format(strBestFormat, date).toString()
                    //                    mPaint.setTextSkewX((float) -0.25);
                    drawLabel(canvas, label, strDate)
                }
            }
        }
    }

    private fun drawTimes(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val date = Date(System.currentTimeMillis())

        val digitTimes = mSkin!!.digitTimes
        if (digitTimes != null) {
            for (dt in digitTimes) {
                drawDigitTime(canvas, dt, date)
            }
        }

        drawWeekAnchor(canvas, date)

        drawMiddle(canvas)

        val labelTimes = mSkin!!.labelTimes
        if (labelTimes != null) {
            for (lt in labelTimes) {
                drawLabelTime(canvas, lt, date, hourFormat)
            }
        }

        val analogTimes = mSkin!!.analogTimes
        if (analogTimes != null) {
            for (at in analogTimes) {
                drawAnalogTime(canvas, at, date)
            }
        }
    }

    private fun drawCountdownEvent(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        //TODO 需要更新数据维护逻辑
        val events = mSkin!!.countdownEvent
        var eventText = ""

        if (events != null) {
            for (i in events.indices) {
                eventText = if (i == 0) {
                    "1天后"
                } else {
                    "结婚纪念日"
                }
                drawLabel(canvas, events[i], eventText)
            }
        }
    }

    private fun drawBackgrounds(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        val backgrounds = mSkin!!.backgrounds

        if (backgrounds != null) {
            for (bg in backgrounds) {
//                LogUtils.logi("testFileName","bg.getImgFile(): "+ bg.getImgFile());
                drawRotateBackground(canvas, bg)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawFilter = mPaintFlagsDrawFilter
        drawBackgroup(canvas)
        drawBackgrounds(canvas)
        drawBatteryProgressBar(canvas)
        drawBluetooth(canvas)
        drawWifi(canvas)
        drawVolume(canvas)
        drawBattery(canvas)
        drawStep(canvas)
        drawBatteryAnchor(canvas)
        drawAirTemp(canvas)
        drawWeather(canvas)
        drawCharge(canvas)
        drawAqiNumber(canvas)
        drawAqiText(canvas)
        drawStepNumber(canvas)
        drawBatteryNumber(canvas)
        drawTemperature(canvas)
        drawStepSkinArc(canvas)
        drawBatterySquares(canvas)
        drawBatteryAngle(canvas)
        drawStepSquares(canvas)
        drawTimes(canvas)
        drawForeground(canvas)
        drawNotices(canvas)
        drawNotice(canvas)
        drawNoticeText(canvas)
        drawCountDownTimer(canvas)
        drawCountdownEvent(canvas)
        drawFansInfo(canvas)
        drawFansIcon(canvas)
        drawFansHead(canvas)
        drawTemperatureRange(canvas)
    }

    private fun drawCountDownTimer(canvas: Canvas) {
    }

    /**
     * 绘制提醒功能
     *
     * @param canvas
     */
    private fun drawNotice(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        //TODO 需要更新数据维护逻辑
        val notices = mSkin!!.notice
        var noticesText = ""

        if (notices != null) {
            for (i in notices.indices) {
                if (i == 0) {
                    noticesText = "今天 15:09"
                } else if (i == 1) {
                    noticesText = "去超市买菜，要买白萝卜"
                } else if (i == 2) {
                    noticesText = "今天 18:09"
                } else if (i == 3) {
                    noticesText = "去户外遛狗30分钟"
                } else if (i == 4) {
                    noticesText = "明天 8:30"
                } else if (i == 5) {
                    noticesText = "起床卧推20个"
                }
                drawLabel(canvas, notices[i]!!, noticesText)
            }
        }
    }

    /**
     * 绘制提醒功能
     *
     * @param canvas
     */
    private fun drawFansInfo(canvas: Canvas) {
        if (mSkin == null) {
            return
        }

        //TODO 需要更新数据维护逻辑
        val fansInfo = mSkin!!.fansInfo
        var fansText = ""

        if (fansInfo != null) {
            for (i in fansInfo.indices) {
                if (i == 0) {
                    fansText = "圆梦是个PM"
                } else if (i == 1) {
                    fansText = "1423.1w"
                } else if (i == 2) {
                    fansText = "粉丝"
                }
                drawLabel(canvas, fansInfo[i]!!, fansText)
            }
        }
    }

    fun getCtrlId(x: Int, y: Int): Int {
        if (mSkin?.bluetooth != null && mSkin!!.bluetooth!!.getDispTouchRect() != null) {
            if (mSkin!!.bluetooth!!.getDispTouchRect()?.contains(x, y) == true) {
                return RobotClockSkin.Companion.CTRL_BLUETOOTH_ID
            }
        }

        if (mSkin?.volume != null && mSkin!!.volume!!.getDispTouchRect() != null) {
            if (mSkin!!.volume?.getDispTouchRect()?.contains(x, y) == true) {
                return RobotClockSkin.Companion.CTRL_MEDIA_VOLUME_ID
            }
        }

        if (mSkin?.wifi != null && mSkin?.wifi!!.getDispTouchRect() != null) {
            if (mSkin?.wifi!!.getDispTouchRect()?.contains(x, y) == true) {
                return RobotClockSkin.Companion.CTRL_WIFI_ID
            }
        }

        if (mSkin?.step != null && mSkin?.step!!.getDispTouchRect() != null) {
            if (mSkin?.step!!.getDispTouchRect()?.contains(x, y) == true) {
                return RobotClockSkin.Companion.CTRL_STEP_ID
            }
        }

        if (mSkin?.airTemp != null && mSkin?.airTemp!!.getDispTouchRect() != null) {
            if (mSkin?.airTemp!!.getDispTouchRect()?.contains(x, y) == true) {
                return RobotClockSkin.Companion.CTRL_WEATHER_ID
            }
        }

        if (mSkin?.weather != null && mSkin?.weather!!.getDispTouchRect() != null) {
            if (mSkin?.weather!!.getDispTouchRect()?.contains(x, y) == true) {
                return RobotClockSkin.Companion.CTRL_WEATHER_ID
            }
        }

        if (mSkin?.aqiNumber != null && mSkin?.aqiNumber!!.getDispTouchRect() != null) {
            if (mSkin?.aqiNumber!!.getDispTouchRect()?.contains(x, y) == true) {
                return RobotClockSkin.Companion.CTRL_WEATHER_ID
            }
        }

        if (mSkin?.aqiText != null && mSkin?.aqiText!!.getDispTouchRect() != null) {
            if (mSkin?.aqiText!!.getDispTouchRect()?.contains(x, y) == true) {
                return RobotClockSkin.Companion.CTRL_WEATHER_ID
            }
        }

        return RobotClockSkin.Companion.CTRL_NONE_ID
    }

    override fun updateBatteryLevel(batteryLevel: Int) {
        if (mPlatformState.batteryLevel != batteryLevel) {
            mPlatformState.batteryLevel = batteryLevel
            invalidate()
        }
    }

    override fun updateWifiEnabled(wifiEnabled: Boolean) {
        if (mPlatformState.wifiEnabled != wifiEnabled) {
            mPlatformState.wifiEnabled = wifiEnabled
            invalidate()
        }
    }

    override fun updateStepNumber(stepNumber: Int) {
        if (mPlatformState.stepNumber != stepNumber) {
            mPlatformState.stepNumber = stepNumber
            invalidate()
        }
    }

    override fun updateMediaVolume(mediaVolume: Int) {
        if (mPlatformState.mediaVolume != mediaVolume) {
            mPlatformState.mediaVolume = mediaVolume
            invalidate()
        }
    }

    override fun updateBluetoothEnabled(bluetoothEnabled: Boolean) {
        if (mPlatformState.bluetoothEnabled != bluetoothEnabled) {
            mPlatformState.bluetoothEnabled = bluetoothEnabled
            invalidate()
        }
    }

    override fun updateBatteryCharging(batteryCharging: Boolean) {
        if (mPlatformState.batteryCharging != batteryCharging) {
            mPlatformState.batteryCharging = batteryCharging
            invalidate()
        }
    }

    override fun updateWeather(weatherState: Int, currentTemp: Int, airQuality: Int) {
        if (mPlatformState.weatherState != weatherState || mPlatformState.currentTemp != currentTemp || mPlatformState.airQuality != airQuality) {
            mPlatformState.weatherState = weatherState
            mPlatformState.currentTemp = currentTemp
            mPlatformState.airQuality = airQuality

            invalidate()
        }
    }

    override fun updateAll(state: RobotPlatformState) {
        mPlatformState = state
        invalidate()
    }

    val isRefreshPerSecond: Boolean
        get() {
            if (mSkin == null) {
                return false
            }
            return (isRefreshNow && mSkin!!.isRefreshPerSecond)
        }

    fun setRefreshNow(refreshNow: Boolean) {
        isRefreshNow = refreshNow
        if (isRefreshNow) {
            startTimerTask()
        } else {
            stopTimerTask()
        }
    }

    private fun initTimerTask() {
        mTimer = null
        mTimerTask = null
        if (mTimer == null) {
            mTimer = Timer()
        }
        if (mTimerTask == null) {
            mTimerTask = object : TimerTask() {
                override fun run() {
//                    LogUtils.logi("test", "refrash~~~~~");
                    postInvalidate()
                }
            }
        }
    }

    private fun startTimerTask() {
        initTimerTask()
        mTimer!!.schedule(mTimerTask, 0, 50)
    }

    //    private void startTimerTask() {
    //        initTimerTask();
    //        if (isRefreshPerSecond()) {
    //            mTimer.schedule(mTimerTask, 0, 1000);
    //        } else {
    //            Calendar calendar = Calendar.getInstance();
    //            int second = calendar.get(Calendar.SECOND);
    //            mTimer.schedule(mTimerTask, (60 - second) * 1000, 1000 * 60);
    //        }
    //    }
    private fun stopTimerTask() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
            mTimerTask = null
        }
    }

    companion object {
        private val TAG: String = RobotClockView::class.java.simpleName

        const val SPINE_CLOCK_HOURS_DEF: Int = 0
        const val SPINE_CLOCK_HOURS_12: Int = 1
        const val SPINE_CLOCK_HOURS_24: Int = 2
        const val TEXT_COLOR_GARY: Int = 7566197
        const val YYYYMMDD_FORMAT: String = "yyyy.MM.dd"
        const val MMDD_FORMAT: String = "MM.dd"
        const val THAI: String = "th"
        const val GERMAN: String = "de"

        private const val NO_PERCENT_SIGN = "no_percent"

        // 5分19帧   7秒19帧
        /**
         * 高斯模糊
         *
         * @param context
         * @param source
         * @param radius
         * @param scale
         * @return
         */
        private fun rsBlur(
            context: Context?,
            source: Bitmap?,
            radius: Float,
            scale: Float
        ): Bitmap? {
            if (source == null) {
                return null
            }
            val scaleWidth = (source.width * scale).toInt()
            val scaleHeight = (source.height * scale).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(
                source, scaleWidth,
                scaleHeight, false
            )

            val inputBitmap = scaledBitmap

            //TODO 是否更新刷新机制以节约资源
//        LogUtils.logi("RenderScriptActivity", "size:" + inputBitmap.getWidth() + "," + inputBitmap.getHeight());

            //创建RenderScript
            val renderScript = RenderScript.create(context)

            //创建Allocation
            val input = Allocation.createFromBitmap(
                renderScript,
                inputBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            val output = Allocation.createTyped(renderScript, input.type)

            //创建ScriptIntrinsic
            val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

            intrinsicBlur.setInput(input)

            intrinsicBlur.setRadius(radius)

            intrinsicBlur.forEach(output)

            output.copyTo(inputBitmap)

            renderScript.destroy()

            return inputBitmap
        }
    }
}

