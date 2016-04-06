package com.jiefly.gao.ble_jie;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanRecord;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class Main extends Activity {
    private int x;
    private int y;
    private final static int STATE_DISCONNECTED = 0;
    private final static int STATE_CONNECTED = 2;
    private final static int GET_RSSI_VALUE = 4;
    private final static int SCAN_SUCCESS = 1;
    private final static int SERVICE_SUCCESS = 5;
    /*ble设备用于串口通信的service的Uuid*/
    private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private final static int DISCOVER_SERVICE_SUCCESS = 3;
    private boolean moveXFlag = false;
    private boolean moveYFlag = false;
    private boolean scanFlag = false;
    private boolean connectFlag = false;
    private boolean serviceFlag = false;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt, usedBluetoothGatt;
    private scan_device mscan_device = new scan_device();
    private ScanRecord mScanRecord;
    private Button scanBtn, connectBtn, upBtn, downBtn, leftBtn, rightBtn, gravityControlBtn;
    private String nameStr;
    private String uuidStr;
    private String addressStr;
    private BluetoothLeScanner mBluetoothScanner;
    private TextView scanResultTV;
    private TextView rssiTV;
    private TextView connectStatueTV;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGattCharacteristic transBluetoothGattCharacteristic;
    private SensorManager sensorManager;
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            System.out.println("status------------------------------->" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Message message = new Message();
                message.what = DISCOVER_SERVICE_SUCCESS;
                mHandler.sendMessage(message);
                Log.d("GATT_SUCCESS:", status + "");
                Log.d("IncludeServiceSize:", gatt.getServices().size() + "");
                usedBluetoothGatt = gatt;
                mBluetoothGattService = gatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                transBluetoothGattCharacteristic = mBluetoothGattService.getCharacteristic(UUID.fromString(UUID_KEY_DATA));
                Message serviceMessage = new Message();
                serviceMessage.what = SERVICE_SUCCESS;
                mHandler.sendMessage(serviceMessage);
                serviceFlag = true;
               /*List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
                for (BluetoothGattService bluetoothGattService : bluetoothGattServices) {
                    Log.e("Service uuid:", bluetoothGattService.getUuid() + "");
                    List<BluetoothGattCharacteristic> gattCharacteristics = bluetoothGattService.getCharacteristics();
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : gattCharacteristics) {
                        System.out.println("GattCharacteristic uuid:" + bluetoothGattCharacteristic.getUuid() + "");
                        System.out.println("char permission:" + bluetoothGattCharacteristic.getPermissions());
                        if (bluetoothGattCharacteristic.getUuid().toString().equals("0000ffe1-0000-1000-8000-00805f9b34fb")) {
                            bluetoothGattCharacteristic.setValue("hahahahah");
                            transBluetoothGattCharacteristic=bluetoothGattCharacteristic;
                            //0000ffe0-0000-1000-8000-00805f9b34fb
                            System.out.println("send message");
                            mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                            transBluetoothGattCharacteristic.setValue("111111");
                            mBluetoothGatt.writeCharacteristic(transBluetoothGattCharacteristic);
                        }
                        System.out.println("Value"+Arrays.toString(bluetoothGattCharacteristic.getValue()));
                    }
                }
            } else {
                Log.w("jieee", "receive onsercicesdiscovered:" + status);*/
            }

        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            switch (newState) {
                case STATE_DISCONNECTED:
                    connectFlag = false;
                    Message messageDiscon = new Message();
                    messageDiscon.what = STATE_DISCONNECTED;
                    mHandler.sendMessage(messageDiscon);
                    break;
                case STATE_CONNECTED:
                    Message messageCon = new Message();
                    messageCon.what = STATE_DISCONNECTED;
                    mHandler.sendMessage(messageCon);
                    break;
                default:
                    break;
            }
            System.out.println("<------------------------------------------------------->");
            Log.e("change state", newState + "");
            System.out.println("<------------------------------------------------------->");
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("write:", "success");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            // System.out.println("Rssi:"+rssi);
            Message message = new Message();
            message.what = GET_RSSI_VALUE;
            message.arg1 = rssi;
            mHandler.sendMessage(message);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d("read", "success");
            System.out.println("----------------------------->");
            System.out.println(characteristic.getUuid() + "\n" + "InstanceId-->" + characteristic.getInstanceId() + "\n" + "Voalue--->" + new String(characteristic.getValue()));
            /*List<BluetoothGattDescriptor> mBluetoothGattDescriptor = characteristic.getDescriptors();
            for (BluetoothGattDescriptor bluetoothGattDescriptor : mBluetoothGattDescriptor) {
                System.out.println(bluetoothGattDescriptor.getUuid() + "");
                System.out.println(Arrays.toString(bluetoothGattDescriptor.getValue()) + "\n" + "permission:" + bluetoothGattDescriptor.getPermissions()
                );

            }
            */
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            System.out.println("----------------------------->");
            Log.d("Characteristic", "changed");
            gatt.readCharacteristic(characteristic);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectStatueTV = (TextView) findViewById(R.id.connectStatueTV);
        connectStatueTV.setText("Disconnected");
        connectStatueTV.setVisibility(connectStatueTV.INVISIBLE);
        rssiTV = (TextView) findViewById(R.id.rssiTV);
        scanResultTV = (TextView) findViewById(R.id.scanResultTV);
        scanResultTV.setText("Name:" + "\n" + "Uuid:");

        scanBtn = (Button) findViewById(R.id.scanBtn);
        gravityControlBtn = (Button) findViewById(R.id.gravityControlBtn);
        connectBtn = (Button) findViewById(R.id.connectBtn);

        upBtn = (Button) findViewById(R.id.upBtn);
        downBtn = (Button) findViewById(R.id.downBtn);
        leftBtn = (Button) findViewById(R.id.leftBtn);
        rightBtn = (Button) findViewById(R.id.rightBtn);

        gravityControlBtn.setOnClickListener(new mOnClickListener());
        gravityControlBtn.setClickable(false);
        scanBtn.setOnClickListener(new mOnClickListener());
        connectBtn.setOnClickListener(new mOnClickListener());

        upBtn.setOnTouchListener(new mOnTouchListener());
        downBtn.setOnTouchListener(new mOnTouchListener());
        leftBtn.setOnTouchListener(new mOnTouchListener());
        rightBtn.setOnTouchListener(new mOnTouchListener());

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Service.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null)
            Toast.makeText(this, "your device do not support blt", Toast.LENGTH_SHORT).show();
        if (!bluetoothAdapter.isEnabled()) {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
        connectBtn.setClickable(scanFlag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }

    class mOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (connectFlag) {
                switch (v.getId()) {
                    case R.id.downBtn:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.d("pass", "Button Up");
                            sendData("\u0002");
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Log.d("pass", "Button Down");
                            sendData("\u0010");
                        }
                        break;
                    case R.id.rightBtn:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.d("pass", "Button Up");
                            sendData("\u0008");
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Log.d("pass", "Button Down");
                            sendData("\u0010");
                        }
                        break;
                    case R.id.leftBtn:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.d("pass", "Button Up");
                            sendData("\u0004");
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Log.d("pass", "Button Down");
                            sendData("\u0010");
                        }
                        break;
                    case R.id.upBtn:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.d("pass", "Button Up");
                            sendData("\u0001");
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Log.d("pass", "Button Down");
                            sendData("\u0010");
                        }
                        break;

                }

            } else {
                Toast.makeText(Main.this, "please wait connect success", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    class mOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.connectBtn:
                    new Thread(new connectGattThread()).start();
                    connectStatueTV.setVisibility(connectStatueTV.VISIBLE);
                    connectStatueTV.setText("Connecting...");
                    break;
                case R.id.scanBtn:
                    new Thread(new scanThread()).start();
                    break;
                case R.id.gravityControlBtn:
                    initGravityControl();
                    break;
            }
        }
    }

    public void initGravityControl() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private SensorEventListener listener = new SensorEventListener() {
        float[] accelerometerValues = new float[3];
        float[] magneticValues = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometerValues = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magneticValues = event.values.clone();
                    break;
            }
            float[] R = new float[9];
            float[] Values = new float[3];
            SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
            SensorManager.getOrientation(R, Values);
            x = (int) Math.toDegrees(Values[1]);
            y = (int) Math.toDegrees(Values[2]);
            if (!moveXFlag) {
                if (x > 30 || x < -30) {
                    moveXFlag = true;
                    if (x > 30) {
                        Log.d("|---->Right<----|", x + "");
                        sendData("\u0001");
                    } else {
                        Log.d("|---->Left<----|", x + "");
                        sendData("\u0002");
                    }
                }
            } else {
                if ((x > -30 & x < 30)) {
                    Log.d("|---->Stop<---x-|", x + "");
                    Log.d("|---->Stop<---y-|", y + "");
                    sendData("\u0010");
                    moveXFlag = false;
                }
            }
            if (!moveYFlag) {
                if (y > 30 || y < -30) {
                    moveYFlag = true;
                    if (y > 30) {
                        Log.d("|---->Up<----|", x + "");
                        sendData("\u0004");
                    } else {
                        Log.d("|---->Down<----|", x + "");
                        sendData("\u0008");
                    }
                }
            } else {
                if ((y > -30 & y < 30)) {
                    Log.d("|---->Stop<---x-|", x + "");
                    Log.d("|---->Stop<---y-|", y + "");
                    sendData("\u0010");
                    moveYFlag = false;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    class scanThread implements Runnable {
        @Override
        public void run() {
            mBluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
            mScanRecord = mscan_device.getScanRecord(bluetoothAdapter);
            while (!mscan_device.getScansuccessFlag()) ;
            System.out.println("Device Name:" + mScanRecord.getDeviceName() + "\n" + "Device uuid:"
                    + mScanRecord.getServiceUuids() + "Device Address" + mscan_device.getBluetoothDeviceAddress());
            addressStr = mscan_device.getBluetoothDeviceAddress();
            nameStr = mScanRecord.getDeviceName();
            uuidStr = mScanRecord.getServiceUuids().get(0).toString();
            Message message = new Message();
            message.what = SCAN_SUCCESS;
            mHandler.sendMessage(message);
        }
    }

    class connectGattThread implements Runnable {
        @Override
        public void run() {
            mBluetoothDevice = bluetoothAdapter.getRemoteDevice(addressStr);
            System.out.println(mBluetoothDevice.getName());
            mBluetoothGatt = mBluetoothDevice.connectGatt(Main.this, false, mBluetoothGattCallback);
            System.out.println("设备名称：" + mBluetoothGatt.getDevice().getName() + "\n" + "设备地址：" + mBluetoothGatt.getDevice().getAddress()
                    + "\n");
            while (!serviceFlag) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothGatt.discoverServices();
                // mBluetoothGatt.readRemoteRssi();
            }
            serviceFlag = false;

        }
    }

    public void sendData(String value) {
        transBluetoothGattCharacteristic.setValue(value);
        mBluetoothGatt.writeCharacteristic(transBluetoothGattCharacteristic);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISCOVER_SERVICE_SUCCESS:
                    break;
                case SERVICE_SUCCESS:
                    Toast.makeText(Main.this, "connect success ,you can control now", Toast.LENGTH_SHORT).show();
                    gravityControlBtn.setClickable(true);
                    connectFlag = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (connectFlag) {
                                mBluetoothGatt.readRemoteRssi();
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                   /* mBluetoothGatt.setCharacteristicNotification(transBluetoothGattCharacteristic, true);
                    mBluetoothGatt.readCharacteristic(transBluetoothGattCharacteristic);*/
                    break;
                case GET_RSSI_VALUE:
                    rssiTV.setText("Rssi:" + msg.arg1);
                case SCAN_SUCCESS:
                    scanResultTV.setText("Name:" + nameStr + "\n" + "Uuid:" + uuidStr);
                    scanFlag = true;
                    connectBtn.setClickable(scanFlag);
                    // mscan_device.stopScan();
                    mscan_device.setScanSuccessFlag(false);
                case STATE_CONNECTED:
                    connectStatueTV.setText("Connected");
                    break;
                case STATE_DISCONNECTED:
                    connectStatueTV.setText("Disconnected");
                    gravityControlBtn.setClickable(false);
                    scanResultTV.setText("Name:" + "\n" + "Uuid:");
                    rssiTV.setText("Rssi:");
                    //Toast.makeText(Main.this,"your device disconnected....",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

  /*  static class mHandler extends Handler {
        WeakReference<Activity> mActivityReference;

        mHandler(Activity activity) {
            mActivityReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case DISCOVER_SERVICE_SUCCESS:
                        break;
                    case SERVICE_SUCCESS:
                        Toast.makeText(Main.this, "connect success ,you can control now", Toast.LENGTH_SHORT).show();
                        gravityControlBtn.setClickable(true);
                        connectFlag = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (connectFlag) {
                                    mBluetoothGatt.readRemoteRssi();
                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    mBluetoothGatt.setCharacteristicNotification(transBluetoothGattCharacteristic, true);
                    mBluetoothGatt.readCharacteristic(transBluetoothGattCharacteristic);
                        break;
                    case GET_RSSI_VALUE:
                        rssiTV.setText("Rssi:" + msg.arg1);
                    case SCAN_SUCCESS:
                        scanResultTV.setText("Name:" + nameStr + "\n" + "Uuid:" + uuidStr);
                        scanFlag = true;
                        connectBtn.setClickable(scanFlag);
                        // mscan_device.stopScan();
                        mscan_device.setScanSuccessFlag(false);
                    case STATE_CONNECTED:
                        connectStatueTV.setText("Connected");
                        break;
                    case STATE_DISCONNECTED:
                        connectStatueTV.setText("Disconnected");
                        gravityControlBtn.setClickable(false);
                        scanResultTV.setText("Name:" + "\n" + "Uuid:");
                        rssiTV.setText("Rssi:");
                        //Toast.makeText(Main.this,"your device disconnected....",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }*/
}

