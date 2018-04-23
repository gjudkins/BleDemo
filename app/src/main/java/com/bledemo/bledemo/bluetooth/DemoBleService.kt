package com.bledemo.bledemo.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import com.bledemo.bledemo.toDemoScanPeripherals
import com.google.gson.Gson
import no.nordicsemi.android.support.v18.scanner.*
import java.util.ArrayList


class DemoBleService : BleProfileService() {

    private var mIsScanning: Boolean = false
    private val mHandler = Handler()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            // do nothing
        }

        override fun onBatchScanResults(results: List<ScanResult>?) {
            if (!mIsScanning) return


            val sortedPeripherals = results?.sortedBy { it.rssi }?.reversed()?.toDemoScanPeripherals()

            val broadcast = Intent(BROADCAST_RESULT_BATCH)
            broadcast.putExtra(BleProfileService.EXTRA_RESULT_BATCH, Gson().toJson(sortedPeripherals))
            LocalBroadcastManager.getInstance(this@DemoBleService).sendBroadcast(broadcast)

        }

        override fun onScanFailed(errorCode: Int) {
            // should never be called
        }
    }

    override fun initializeManager(): BleManager<*> {
        return DemoDeviceManager.getInstance(this)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startScan()
        return super.onStartCommand(intent, flags, startId)
    }

    fun startScan() {
        if (mIsScanning || !bluetoothIsTurnedOn()) return

        val scanner = BluetoothLeScannerCompat.getScanner()
        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000).setUseHardwareBatchingIfSupported(false).build()
        val filters = ArrayList<ScanFilter>()
        //        filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(DemoDeviceManager.DEMO_SERVICE_UUID)).build());
        scanner.startScan(filters, settings, scanCallback)

        mIsScanning = true
        mHandler.postDelayed({
            if (mIsScanning) {
                stopScan()
            }
        }, SCAN_DURATION)
    }

    /**
     * Stop scan if user tap Cancel button
     */
    private fun stopScan() {
        if (mIsScanning) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            mIsScanning = false


            val broadcast = Intent(BROADCAST_SCAN_STOPPED)
//            broadcast.putExtra(BleProfileService.EXTRA_SCAN_STOPPED, true)
            LocalBroadcastManager.getInstance(this@DemoBleService).sendBroadcast(broadcast)
        }
    }

    private fun bluetoothIsTurnedOn(): Boolean {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled
    }

    companion object {
        private val SCAN_DURATION: Long = 10000

        fun startDemoBleService(context: Context) {
            val serviceIntent = Intent(context, DemoBleService::class.java)
            serviceIntent.putExtra(BleProfileService.EXTRA_DEVICE_ADDRESS, "")
            context.startService(serviceIntent)
        }

        fun stopDemoBleService(context: Context) {
            val serviceIntent = Intent(context, DemoBleService::class.java)
            context.stopService(serviceIntent)
        }
    }
}