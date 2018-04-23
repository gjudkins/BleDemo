package com.bledemo.bledemo

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import java.util.*

import kotlinx.android.synthetic.main.rv_item_scan_result.*
import kotlinx.android.synthetic.main.rv_item_scan_result.view.*

class DemoScanPeripheralViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private lateinit var scanResult: DemoScanPeripheral

    fun bindAttribute(result: DemoScanPeripheral) {
        scanResult = result

        itemView.lblRvItemScanResultName.text = scanResult.deviceName
        itemView.lblRvItemScanResultMAC.text = scanResult.macAddress
        itemView.lblRvItemScanResultAdvertiseFlags.text = scanResult.srAdvertiseFlags?.toString()
        itemView.lblRvItemScanResultRSSI.text = scanResult.rssi.toString()

    }

}

class DemoScanPeripheralRecyclerAdapter(var data: Array<DemoScanPeripheral>) : RecyclerView.Adapter<DemoScanPeripheralViewHolder>() {

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoScanPeripheralViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_scan_result, parent, false)

        return DemoScanPeripheralViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: DemoScanPeripheralViewHolder, position: Int) {
        val item:DemoScanPeripheral = if (data.size > position) data[position] else return

        holder.bindAttribute(item)
    }

}