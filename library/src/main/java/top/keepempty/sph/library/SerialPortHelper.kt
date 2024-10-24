package top.keepempty.sph.library

import com.renhejia.robot.commandlib.log.LogUtils.logi
import top.keepempty.sph.library.SerialPortHelper

/**
 * 串口助手
 */
class SerialPortHelper
/**
 * 初始化串口操作
 * @param maxSize 串口每次读取数据的最大长度
 */ @JvmOverloads constructor(
    /**
     * 最大接收数据的长度
     */
    private val maxSize: Int,
    /**
     * 是否需要返回最大数据接收长度
     */
    private val isReceiveMaxSize: Boolean = false,
    private var serialPortConfig: SerialPortConfig = SerialPortConfig()
) {
    /**
     * 判断串口是否打开
     */
    var isOpenDevice: Boolean = false
        private set

    private val sphThreads: SphThreads? = null

    /**
     * 数据回调
     */
    private var onResultCallback: SphResultCallback? = null

    /**
     * 数据处理
     */
    private val processingData: SphDataProcess? = null


    /**
     * 初始化串口操作
     * @param maxSize           串口每次读取数据的最大长度
     * @param isReceiveMaxSize  是否需要按最大接收数据进行返回
     * @param serialPortConfig  串口数据
     */
    /**
     * 初始化串口操作
     * @param maxSize           串口每次读取数据的最大长度
     * @param isReceiveMaxSize  是否需要按最大接收数据进行返回
     */

    /**
     * 设置数据回调
     * @param onResultCallback 数据回调
     */
    fun setSphResultCallback(onResultCallback: SphResultCallback?) {
        if (sphThreads == null) {
            this.onResultCallback = onResultCallback
            return
        }
        processingData!!.setSphResultCallback(onResultCallback)
    }

    /**
     * 串口设置
     */
    fun setConfigInfo(serialPortConfig: SerialPortConfig) {
        this.serialPortConfig = serialPortConfig
    }

    /**
     * 打开串口设备
     * @param path  串口地址
     */
    fun openDevice(path: String?): Boolean {
        serialPortConfig.path = path
        return openDevice()
    }

    /**
     * 打开串口设备
     * @param path      串口地址
     * @param baudRate  波特率
     */
    fun openDevice(path: String?, baudRate: Int): Boolean {
        serialPortConfig.path = path
        serialPortConfig.baudRate = baudRate
        return openDevice()
    }


    /**
     * 打开串口设备
     */
    fun openDevice(): Boolean {
        requireNotNull(serialPortConfig) { "'SerialPortConfig' must can not be null!!! " }
        requireNotNull(serialPortConfig.path) {
            "You not have setting the device path, " +
                    "you must 'new SerialPortHelper(String path)' or call 'openDevice(String path)' "
        }
        val i = SerialPortJNI.openPort(
            serialPortConfig.path,
            serialPortConfig.baudRate,
            serialPortConfig.dataBits,
            serialPortConfig.stopBits,
            serialPortConfig.parity
        )

        // 是否设置原始模式(Raw Mode)方式来通讯
        if (serialPortConfig.mode != 0) {
            SerialPortJNI.setMode(serialPortConfig.mode)
        }

        // 打开串口成功
        if (i == 1) {
            isOpenDevice = true
            // 创建数据处理
            // processingData = new SphDataProcess(maxSize);
            // processingData.setRecevieMaxSize(isReceiveMaxSize);
            // processingData.setSphResultCallback(onResultCallback);
            // // 开启读写线程
            // sphThreads = new SphThreads(processingData);
        } else {
            isOpenDevice = false
            logi(
                TAG, "cannot open the device !!! " +
                        "path:" + serialPortConfig.path
            )
        }
        return isOpenDevice
    }

    /**
     * 发送串口命令（hex）
     * @param hex  十六进制命令
     * @param flag 备用标识
     */
    /**
     * 发送串口命令（hex）
     * @param hex  十六进制命令
     */
    @JvmOverloads
    fun addCommands(hex: String, flag: Int = 0) {
        val bytes = DataConversion.decodeHexString(hex)
        addCommands(bytes, flag)
    }

    /**
     * 发送串口命令
     * @param commands 串口命令
     * @param flag     备用标识
     */
    /**
     * 发送串口命令
     * @param commands 串口命令
     */
    @JvmOverloads
    fun addCommands(commands: ByteArray?, flag: Int = 0) {
        val comEntry = SphCmdEntity()
        comEntry.commands = commands
        comEntry.flag = flag
        comEntry.commandsHex = DataConversion.encodeHexString(commands)
        addCommands(comEntry)
    }

    /**
     * 发送串口命令
     * @param sphCmdEntity 串口命令数据
     */
    fun addCommands(sphCmdEntity: SphCmdEntity?) {
        if (sphCmdEntity == null) {
            logi(TAG, "SphCmdEntity can't be null !!!")
            return
        }
        if (!isOpenDevice) {
            logi(TAG, "You not open device !!! ")
            return
        }
        // 开启写数据线程
        sphThreads!!.startWriteThread()
        // 添加发送命令
        processingData!!.addCommands(sphCmdEntity)
    }

    /**
     * 关闭串口
     */
    fun closeDevice() {
        SerialPortJNI.closePort()
        sphThreads?.stop()
    }


    companion object {
        private val TAG: String = SerialPortHelper::class.java.simpleName
    }
}
