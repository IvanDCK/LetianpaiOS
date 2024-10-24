package top.keepempty.sph.library

import android.os.Handler
import android.os.Message
import com.renhejia.robot.commandlib.log.LogUtils.logi

/**
 * 串口数据处理
 */
class SphDataProcess(
    /**
     * 最大读取长度，默认1024
     */
    val maxSize: Int
) {
    /**
     * 记录读取数据的大小
     */
    private var mSerialBufferSize = 0

    /**
     * 串口接收数据保存数组
     */
    private var mSerialBuffer: ByteArray? = null

    /**
     * 当前处理中的命令
     */
    private var currentCommand: SphCmdEntity? = null
    /**
     * 获取最大读取长度
     * @return
     */

    /**
     * 是否按最大接收长度进行返回
     */
    private var isReceiveMaxSize = false

    /**
     * 数据回调
     */
    private var onResultCallback: SphResultCallback? = null

    /**
     * 发送、接收串口超时控制
     */
    private val sphHandler = SphHandler(this)

    /**
     * 数据同步控制
     */
    private var concurrentCom: SphConcurrentCom? = null

    /**
     * 串口写入数据
     */
    fun writeData() {
        if (concurrentCom == null) {
            logi("top.keepempty", "concurrentCom is null")
            return
        }

        if (concurrentCom!!.currentCmdEntity == null) {
            if (!concurrentCom!!.isCmdEmpty) {
                currentCommand = concurrentCom!!.get()
                if (onResultCallback != null) {
                    onResultCallback!!.onSendData(currentCommand)
                }
                // 设置了超时时间
                if (currentCommand!!.timeOut > 0) {
                    sphHandler.sendEmptyMessageDelayed(TIMEOUT_WHAT, currentCommand!!.timeOut)
                }
                SerialPortJNI.writePort(currentCommand!!.commands)
                concurrentCom!!.setStatus(true)
            }
        }
    }


    /**
     * 根据配置对串口数据进行处理
     *
     * @param bytes  当前读取的数据字节数组
     * @param revLen 当前读取的数据长度
     */
    fun processingRecData(bytes: ByteArray, revLen: Int) {
        // 按设置的最大返回长度进行返回
        if (isReceiveMaxSize) {
            reCreateData(bytes, revLen)
            return
        }
        resultCallback(bytes)
    }

    /**
     * 处理数据读取反馈，对读取的数据按maxSize进行处理
     * 如果数据一次没有读取完整，通过数组拷贝将数据补全完整
     *
     * @param bytes  当前读取的数据字节数组
     * @param revLen 当前读取的数据长度
     */
    private fun reCreateData(bytes: ByteArray, revLen: Int) {
        if (hasReadDone(revLen) || mSerialBufferSize + revLen > maxSize) {
            // 截取剩余需要读取的长度
            val copyLength = maxSize - mSerialBufferSize
            arrayCopy(bytes, 0, copyLength)
            mSerialBufferSize += copyLength
            checkReCreate(mSerialBuffer)

            // 对反馈数据剩余的数据进行重新拷贝
            val lastLength = revLen - copyLength
            arrayCopy(bytes, copyLength, lastLength)
            mSerialBufferSize = lastLength
            checkReCreate(mSerialBuffer)
        } else {
            // 没有读取完整的情况，继续进行读取
            arrayCopy(bytes, 0, revLen)
            mSerialBufferSize += revLen
            checkReCreate(mSerialBuffer)
        }
    }

    /**
     * 判断当前数据是否读取完整
     *
     * @param revLen 读取数据的长度
     * @return
     */
    private fun hasReadDone(revLen: Int): Boolean {
        return revLen >= maxSize && mSerialBufferSize != maxSize
    }

    /**
     * 判断是否完成重组
     *
     * @param resultBytes
     */
    private fun checkReCreate(resultBytes: ByteArray?) {
        if (mSerialBufferSize == maxSize) {
            resultCallback(resultBytes)
        }
    }

    /**
     * 判断数据是否读取完成，通过回调输出读取数据
     */
    private fun resultCallback(resultBytes: ByteArray?) {
        if (onResultCallback == null) {
            reInit()
            return
        }
        sendMessage(SphCmdEntity(resultBytes), RECEIVECMD_WHAT)
        reInit()
    }

    /**
     * 重置数据
     */
    private fun reInit() {
        mSerialBufferSize = 0
        if (currentCommand == null) {
            return
        }
        val receiveCount = currentCommand!!.receiveCount
        if (receiveCount > 1) {
            currentCommand!!.receiveCount = receiveCount - 1
            return
        }
        receiveDone()
    }

    /**
     * 数据接收完成，恢复写数据线程
     */
    private fun receiveDone() {
        sphHandler.removeMessages(TIMEOUT_WHAT)
        if (concurrentCom != null && currentCommand != null) {
            concurrentCom!!.doneCom()
            concurrentCom!!.setStatus(false)
        }
        if (onResultCallback != null) {
            sendMessage(null, COMPLETECMD_WHAT)
        }
    }

    /**
     * 通过数组拷贝，对数据进行重组
     *
     * @param bytes  当前读取的数据字节数组
     * @param srcPos 需要拷贝的源数据位置
     * @param length 拷贝的数据长度
     */
    private fun arrayCopy(bytes: ByteArray, srcPos: Int, length: Int) {
        System.arraycopy(bytes, srcPos, mSerialBuffer, mSerialBufferSize, length)
    }

    /**
     * 添加串口发送命令
     *
     * @param command
     */
    fun addCommands(command: SphCmdEntity?) {
        // 初始化同步控制
        if (concurrentCom == null) {
            concurrentCom = SphConcurrentCom()
        }
        concurrentCom!!.addCommands(command)
    }

    /**
     * 写入命令
     */
    private fun reWriteCmdOrExit() {
        if (currentCommand!!.reWriteCom) {
            // 重复次数
            val times = currentCommand!!.reWriteTimes
            if (times > 0) {
                SerialPortJNI.writePort(currentCommand!!.commands)
                sphHandler.sendEmptyMessageDelayed(TIMEOUT_WHAT, currentCommand!!.timeOut)
                currentCommand!!.reWriteTimes = times - 1
                if (onResultCallback != null) {
                    onResultCallback!!.onSendData(currentCommand)
                }
            } else {
                receiveDone()
            }
        } else {
            receiveDone()
        }
    }

    /**
     * 设置数据回调
     *
     * @param onResultCallback 数据回调
     */
    fun setSphResultCallback(onResultCallback: SphResultCallback?) {
        this.onResultCallback = onResultCallback
    }

    /**
     * 设置是否按最大接收长度进行返回
     *
     * @param receiveMaxSize
     */
    fun setRecevieMaxSize(receiveMaxSize: Boolean) {
        this.isReceiveMaxSize = receiveMaxSize
    }

    /**
     * 创建数据接收数组
     */
    fun createReadBuff() {
        if (mSerialBuffer == null) {
            mSerialBuffer = ByteArray(maxSize)
        }
    }

    /**
     * 发送串口数据到主线程
     * @param sphCmdEntity  串口数据
     * @param what          数据标识
     */
    private fun sendMessage(sphCmdEntity: SphCmdEntity?, what: Int) {
        val message = Message()
        message.what = what
        message.obj = sphCmdEntity
        sphHandler.sendMessage(message)
    }


    /**
     * 数据通过Handler发送到主线程
     */
    private class SphHandler(private val processingRecData: SphDataProcess) : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            processingRecData.receiveData(msg)
        }
    }

    /**
     * 处理数据回调
     * @param msg
     */
    private fun receiveData(msg: Message) {
        when (msg.what) {
            TIMEOUT_WHAT -> reWriteCmdOrExit()
            SENDCMD_WHAT -> onResultCallback!!.onSendData(msg.obj as SphCmdEntity)
            RECEIVECMD_WHAT -> onResultCallback!!.onReceiveData(msg.obj as SphCmdEntity)
            COMPLETECMD_WHAT -> onResultCallback!!.onComplete()
            else -> {}
        }
    }

    companion object {
        /**
         * 超时handle what值
         */
        private const val TIMEOUT_WHAT = 1

        /**
         * 发送命令
         */
        private const val SENDCMD_WHAT = 2

        /**
         * 接收命令
         */
        private const val RECEIVECMD_WHAT = 3

        /**
         * 完成
         */
        private const val COMPLETECMD_WHAT = 4
    }
}
