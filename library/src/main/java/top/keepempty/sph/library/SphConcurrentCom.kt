package top.keepempty.sph.library

/**
 * 串口读写命令线程同步控制
 */
class SphConcurrentCom {
    private var isGet = false

    /**
     * 获取当前命令
     * @return currentCmdEntity
     */
    var currentCmdEntity: SphCmdEntity? = null
        private set

    /**
     * 串口发送命令集合
     */
    private val mEntryList = ArrayList<SphCmdEntity?>()

    /**
     * 添加串口发送命令
     * @param command 命令数据
     */
    fun addCommands(command: SphCmdEntity?) {
        mEntryList.add(command)
    }

    val isCmdEmpty: Boolean
        /**
         * 判断命令是否为空
         * @return
         */
        get() = mEntryList.isEmpty()

    /**
     * 从命令集合中取命令数据
     * @return currentCmdEntity
     */
    @Synchronized
    fun get(): SphCmdEntity? {
        while (isGet) {
            try {
                (this as Object).wait()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        currentCmdEntity = mEntryList.removeAt(0)
        (this as Object).notify()
        return currentCmdEntity
    }

    /**
     * 命令接收完成
     */
    @Synchronized
    fun doneCom() {
        if (isCmdEmpty) {
            currentCmdEntity = null
            return
        }
        while (!isGet) {
            try {
                (this as Object).wait()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        currentCmdEntity = null
        (this as Object).notify()
    }

    /**
     * 设置读写同步状态
     * @param status
     */
    fun setStatus(status: Boolean) {
        this.isGet = status
    }
}
