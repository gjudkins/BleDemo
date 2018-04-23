package com.bledemo.bledemo

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.bledemo.bledemo.bluetooth.BleProfileService
import com.bledemo.bledemo.bluetooth.DemoBleService
import com.bledemo.bledemo.bluetooth.DemoDeviceManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_ble_sniffing.*
import kotlinx.android.synthetic.main.content_ble_sniffing.*

class BleSniffViewModel : ViewModel() {
    val recyclerAdapter = DemoScanPeripheralRecyclerAdapter(arrayOf())
    var isScanning:Boolean = false
}


class BleSniffingActivity : AppCompatActivity() {
    lateinit var viewModel:BleSniffViewModel
    lateinit var snackbar: Snackbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_sniffing)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(BleSniffViewModel::class.java)

        snackbar = Snackbar.make(fab, "Searching for BLE peripherals", Snackbar.LENGTH_INDEFINITE)

        DemoDeviceManager.permissionCheck(this)

        initBroadcastReceiver()
        initRecyclerView()

        fab.setOnClickListener {
            DemoBleService.startDemoBleService(this)
            viewModel.isScanning = true
            if (!snackbar.isShown) snackbar.show()
        }

        if (viewModel.isScanning && !snackbar.isShown) snackbar.show()
    }

    private fun initRecyclerView() {
        rvBleSniff.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvBleSniff.adapter = viewModel.recyclerAdapter
    }
    private fun initBroadcastReceiver(){
        val intentFilters = IntentFilter()
        intentFilters.addAction(BleProfileService.BROADCAST_ERROR)
        intentFilters.addAction(BleProfileService.BROADCAST_RESULT_BATCH)
        intentFilters.addAction(BleProfileService.BROADCAST_SCAN_STOPPED)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilters)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            when (intent.action) {
                BleProfileService.BROADCAST_RESULT_BATCH -> {
                    val json:String = intent.getStringExtra(BleProfileService.EXTRA_RESULT_BATCH)
                    val deviceList = Gson().fromJson(json, Array<DemoScanPeripheral>::class.java)

                    viewModel.recyclerAdapter.data = deviceList
                    viewModel.recyclerAdapter.notifyDataSetChanged()

                    viewModel.isScanning = true
                }
                BleProfileService.BROADCAST_ERROR -> {
                    val msg = intent.getStringExtra(BleProfileService.EXTRA_ERROR_MESSAGE)
                    Toast.makeText(this@BleSniffingActivity, msg, Toast.LENGTH_SHORT).show()
                    viewModel.isScanning = false
                    if (snackbar.isShown) snackbar.dismiss()
                }
                BleProfileService.BROADCAST_SCAN_STOPPED -> {
                    viewModel.isScanning = false
                    if (snackbar.isShown) snackbar.dismiss()
                }
            }

        }
    }


}
