package top.keepempty.sph.library


class SerialPortConfig {
    /**
     * 串口地址
     */
    var path: String? = null

    /**
     * 波特率
     */
    var baudRate: Int = 9600

    /**
     * 数据位 取值 位 7或 8
     */
    var dataBits: Int = 8

    /**
     * 停止位 取值 1 或者 2
     */
    var stopBits: Int = 1

    /**
     * 校验类型 取值 N ,E, O,
     */
    var parity: Char = 'n'

    /**
     * 是否使用原始模式(Raw Mode)方式来通讯
     * 取值 0=nothing,
     * 1=Raw mode,
     * 2=no raw mode
     */
    var mode: Int = 0
}
