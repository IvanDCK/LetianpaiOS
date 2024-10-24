package top.keepempty.sph.library

/**
 * 串口数据回调
 */
interface SphResultCallback {
    /**
     * 发送命令
     * @param sendCom 串口发送的命令
     */
    fun onSendData(sendCom: SphCmdEntity?)

    /**
     * 收到的数据
     * @param data 串口收到的数据
     */
    fun onReceiveData(data: SphCmdEntity?)

    /**
     * 发送，收取完成
     */
    fun onComplete()
}
