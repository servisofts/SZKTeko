package ZKFP;

import java.util.Base64;

import com.zkteco.biometric.FingerprintSensorEx;

import org.json.JSONObject;

import Observer.Observer;
import Servisofts.SConsole;

public class ZKFP {

    public static ZKFP instance;

    public static ZKFP getInstance() {
        if (instance == null) {
            instance = new ZKFP();
        }
        return instance;
    }

    private long mhDevice = 0;

    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];
    private byte[] imgbuf = null;
    int fpWidth = 0;
    int fpHeight = 0;
    boolean isRun;

    public ZKFP() {
        this.connect();
    }

    public void connect() {
        int ret;
        if (FingerprintSensorEx.Init() != -1) {
            SConsole.log("[ZKFP]", "Dispotivo encontrado.");
            // Observer.notify(new JSONObject().put("component", "ZKFP").put("estado",
            // "ok"));
            ret = FingerprintSensorEx.GetDeviceCount();
            if (ret < 0) {
                FreeSensor();
                return;
            }
            if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
                FreeSensor();
                return;
            }
            byte[] paramValue = new byte[4];
            int[] size = new int[1];
            size[0] = 4;
            FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
            fpWidth = byteArrayToInt(paramValue);
            size[0] = 4;
            FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
            fpHeight = byteArrayToInt(paramValue);
            imgbuf = new byte[fpWidth * fpHeight];
            SConsole.log("[ZKFP]", "Dispotivo iniciado.");
            isRun = true;
            new WorkThread().start();
        }
    }

    private void FreeSensor() {
        isRun = true;
        try { // wait for thread stopping
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*
         * if (0 != mhDB) {
         * FingerprintSensorEx.DBFree(mhDB);
         * mhDB = 0;
         * }
         */
        if (0 != mhDevice) {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }
        FingerprintSensorEx.Terminate();
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ret = 0;
            while (isRun) {
                templateLen[0] = 2048;
                if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen))) {

                    // System.out.println("imgbuf:" + imgbuf);
                    String b64 = Base64.getEncoder().encodeToString(template);
                    SConsole.log(b64);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        // "|="按位或赋值。
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }

}
