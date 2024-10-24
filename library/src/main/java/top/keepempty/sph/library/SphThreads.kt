package top.keepempty.sph.library

/**
 * 串口读写线程
 *
 */
class SphThreads(
    /**
     * 数据处理
     */
    private val processingData: SphDataProcess
) {
    /**
     * 读写线程
     */
    private val readThread: Thread?
    private var writeThread: Thread? = null

    init {
        readThread = Thread(ReadThread())
        readThread.start()
    }

    /**
     * 开启发送数据线程
     */
    fun startWriteThread() {
        if (writeThread == null) {
            writeThread = Thread(WriteThread())
            writeThread!!.start()
        }
    }

    /**
     * 数据读取线程
     */
    inner class ReadThread : Runnable {
        override fun run() {
            while (!readThread!!.isInterrupted) {
                // 创建数据接收数组
                processingData.createReadBuff()
                // 读取数据
                val bytes = SerialPortJNI.readPort(processingData.maxSize)
                if (bytes != null) {
                    val revLength = bytes.size
                    if (revLength > 0) {
                        processingData.processingRecData(bytes, revLength)
                    }
                }
            }
        }
    }

    /**
     * 数据写入线程
     */
    inner class WriteThread : Runnable {
        override fun run() {
            while (!writeThread!!.isInterrupted) {
                processingData.writeData()
            }
        }
    }

    /**
     * 停止线程
     */
    fun stop() {
        readThread?.interrupt()
        if (writeThread != null) {
            writeThread!!.interrupt()
        }
    }
}
