package com.jiefly.gao.ble_jie;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Parcel;
import android.util.Log;

import java.util.List;

/**
 * Created by jiefly on 15-12-28.
 */
public class scan_device {
    private BluetoothLeScanner mBluetoothSchanner;
    private ScanRecord mScanRecord;
    private boolean scanSuccessFlag = false;
    //private boolean stopFlag=false;
    private BluetoothDevice mBluetoothDevice;
    /*BluetoothLeScanner 的回调函数*/
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        /*BluetoothLeScanner的扫描结果*/
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            mScanRecord = result.getScanRecord();
            Log.d("jiefly--->onScanResult:", "scan device success" + "rssi:" + result.getRssi() + "Device Address:" + result.getDevice().getAddress());
            mBluetoothDevice = result.getDevice();
            /*如果扫描到设备 scanSuccessFlag赋值为true，反之则赋值为false*/
            scanSuccessFlag = !result.getDevice().equals(null);
            //result.writeToParcel(new Parcel().,);

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("jiefly-resultsize:", results.size() + "");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            /*启动扫描失败*/
            Log.d("jiefly--->onScanFailed:", "scan device failed" + "errorCode:" + errorCode);
        }
    };
/*获取扫描到的ScanRecord，通过ScanRecord可以获取所扫描的设备的一些具体信息，如uuid，name等*/
    public ScanRecord getScanRecord(BluetoothAdapter mBluetoothAdapter) {
        mBluetoothSchanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothSchanner.startScan(mScanCallback);
        while (!scanSuccessFlag)
            ;
        return mScanRecord;
    }
/*获取扫描到的设备的ip地址*/
    public String getBluetoothDeviceAddress() {

        return mBluetoothDevice.getAddress();
    }
/*停止扫描函数*/
    public boolean stopScan() {
        mBluetoothSchanner.stopScan(mScanCallback);
        // stopFlag=true;
        return true;
    }

    public void setScanSuccessFlag(boolean scanSuccessFlag) {
        this.scanSuccessFlag = scanSuccessFlag;
    }
/*获取扫描结果函数*/
    public boolean getScansuccessFlag() {
        return scanSuccessFlag;
    }
}
