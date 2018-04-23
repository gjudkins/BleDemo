package com.bledemo.bledemo

import no.nordicsemi.android.support.v18.scanner.ScanRecord
import no.nordicsemi.android.support.v18.scanner.ScanResult

class DemoScanPeripheral(scanResult: ScanResult) {
    var deviceName:String? = ""
        get() { return field ?: "(no name)"}
    var macAddress:String = ""
    var rssi:Int = 0
    var timeStampNanos:Long = 0

    var srAdvertiseFlags:Int? = null
    var srBytes:ByteArray? = null
    var srTxPowerLevel:Int = 0

init {
    deviceName = scanResult.device.name
    macAddress = scanResult.device.address
    rssi = scanResult.rssi
    timeStampNanos = scanResult.timestampNanos
    srAdvertiseFlags = scanResult.scanRecord?.advertiseFlags
    srBytes = scanResult.scanRecord?.bytes
    srTxPowerLevel = scanResult.scanRecord?.txPowerLevel ?: 0
}
}
fun Array<ScanResult>.toDemoScanPeripherals(): Array<DemoScanPeripheral> {
    return this.map { DemoScanPeripheral(it) }.toTypedArray()
}
fun List<ScanResult>.toDemoScanPeripherals(): Array<DemoScanPeripheral> {
    return this.map { DemoScanPeripheral(it) }.toTypedArray()
}