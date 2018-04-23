package com.bledemo.bledemo.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;


import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

/**
 * HRSManager class performs BluetoothGatt operations for connection, service discovery, enabling notification and reading characteristics. All operations required to connect to device with BLE HR
 * Service and reading heart rate values are performed here. HRSActivity implements HRSManagerCallbacks in order to receive callbacks of BluetoothGatt operations
 */
public class DemoDeviceManager extends BleManager<BleManagerCallbacks> {
    public final static UUID DEMO_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB");
    public final static UUID DEMO_SCANNER_CONTROLS_UUID = UUID.fromString("0000180D-AA88-0D04-BEA2-17403360E295");

    public static final UUID WRITE_STIM_CHARACTERISTIC_UUID = UUID.fromString("00001545-aa88-0d04-bea2-17403360e295");
    public static final UUID READ_STIM_CHARACTERISTIC_UUID = UUID.fromString("00001577-aa88-0d04-bea2-17403360e295");
    private static final UUID SCAN_PARAMS_CHARACTERISTIC_UUID = UUID.fromString("00001573-aa88-0d04-bea2-17403360e295");
    private static final UUID VIBRATION_CHARACTERISTIC_UUID = UUID.fromString("00001571-aa88-0d04-bea2-17403360e295");
    private static final UUID LED_CHARACTERISTIC_UUID = UUID.fromString("00001572-aa88-0d04-bea2-17403360e295");

    private BluetoothGattCharacteristic writeStimCharacteristic, readStimCharacteristic, scanParamsCharacteristic, vibrationCharacteristic, ledCharacteristic;

    private static DemoDeviceManager managerInstance = null;

    /**
     * singleton implementation of HRSManager class
     */
    public static synchronized DemoDeviceManager getInstance(Context context) {
        if (managerInstance == null) {
            managerInstance = new DemoDeviceManager(context.getApplicationContext());
        }
        return managerInstance;
    }

    private DemoDeviceManager(final Context context) {
        super(context);
    }

    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    /**
     * BluetoothGatt callbacks for connection/disconnection, service discovery, receiving notification, etc
     */
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected Deque<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();
            requests.add(Request.newEnableNotificationsRequest(readStimCharacteristic));
            return requests;
        }

        @Override
        protected boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(DEMO_SCANNER_CONTROLS_UUID);
            if (service != null) {
                writeStimCharacteristic = service.getCharacteristic(WRITE_STIM_CHARACTERISTIC_UUID);
                readStimCharacteristic = service.getCharacteristic(READ_STIM_CHARACTERISTIC_UUID);
                scanParamsCharacteristic = service.getCharacteristic(SCAN_PARAMS_CHARACTERISTIC_UUID);
                vibrationCharacteristic = service.getCharacteristic(VIBRATION_CHARACTERISTIC_UUID);
                ledCharacteristic = service.getCharacteristic(LED_CHARACTERISTIC_UUID);
            }
            return writeStimCharacteristic != null &&
                    readStimCharacteristic != null &&
                    scanParamsCharacteristic != null &&
                    vibrationCharacteristic != null &&
                    ledCharacteristic != null;
        }

        @Override
        protected boolean isOptionalServiceSupported(final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(DEMO_SERVICE_UUID);
            if (service != null) {
                // check for optional services
            }
            return true;
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();
            int resistanceVal = ( value.length > 0 ? byteToInt(characteristic.getValue()[0]) : 0 );
            int tempVal = ( value.length > 1 ? byteToInt(characteristic.getValue()[1]) : 0 );
            int pressureVal = ( value.length > 2 ? byteToInt(characteristic.getValue()[2]) : 0 );

//            mCallbacks.onDemoResponseReceived(characteristic, resistanceVal);
        }

        @Override
        protected void onDeviceDisconnected() {
            writeStimCharacteristic = null;
            readStimCharacteristic = null;
            scanParamsCharacteristic = null;
            vibrationCharacteristic = null;
            ledCharacteristic = null;
        }

        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();
            int resistanceVal = ( value.length > 0 ? byteToInt(characteristic.getValue()[0]) : 0 );
            int tempVal = ( value.length > 1 ? byteToInt(characteristic.getValue()[1]) : 0 );
            int pressureVal = ( value.length > 2 ? byteToInt(characteristic.getValue()[2]) : 0 );

//            mCallbacks.onDemoResponseReceived(characteristic, resistanceVal);
        }

        private int byteToInt(byte mByte) {
            //noinspection UnnecessaryLocalVariable
            int val = mByte & 0xFF;
            return val;
        }
    };


    public static void permissionCheck(final Activity context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permCoarseLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permFineLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int permBt = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
            int permBtAdmin = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN);

            if (permCoarseLoc != PackageManager.PERMISSION_GRANTED || permFineLoc != PackageManager.PERMISSION_GRANTED || permBt != PackageManager.PERMISSION_GRANTED || permBtAdmin != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN }, 1);
            }
        }
    }

}
