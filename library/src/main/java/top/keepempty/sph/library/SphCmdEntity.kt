package top.keepempty.sph.library

/**
 * 串口命令
 */
class SphCmdEntity {
    constructor()

    constructor(commands: ByteArray?) {
        this.commands = commands
        this.commandsHex = DataConversion.encodeHexString(commands)
    }

    /**
     * 串口发送或者返回的命令
     */
    var commands: ByteArray? = null

    /**
     * 串口发送或者返回的命令(hex)
     */
    var commandsHex: String? = null

    /**
     * 发送命令超时时间
     */
    var timeOut: Long = 0

    /**
     * 是否重复发送命令
     */
    var reWriteCom: Boolean = false

    /**
     * 数据重发次数
     */
    var reWriteTimes: Int = 0

    /**
     * 备用标识
     */
    var flag: Int = 0

    /**
     * 串口回复数据条数
     */
    var receiveCount: Int = 1
}
