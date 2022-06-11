package ZKFP;

import java.util.Base64;

import com.zkteco.biometric.FingerprintSensorEx;

import org.json.JSONObject;

import Observer.Observer;
import Servisofts.SConfig;
import Servisofts.SConsole;
import SocketCliente.SocketCliente;

public class ZKFP {

    public static ZKFP instance;

    public static ZKFP getInstance() {
        if (instance == null) {
            instance = new ZKFP();
        }
        return instance;
    }

    private long mhDevice = 0;
    private long mhDB = 0;

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
        SConsole.log("[ZKFP]", "Intentando conectar con el sensor de huellas digitales");
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
            if (0 == (mhDB = FingerprintSensorEx.DBInit())) {
                FreeSensor();
                return;
            }
            int nFmt = 0; // Ansi

            FingerprintSensorEx.DBSetParameter(mhDB, 5010, nFmt);

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
        } else {
            FreeSensor();
        }
    }

    private void FreeSensor() {
        isRun = false;
        try { // wait for thread stopping
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (0 != mhDB) {
            FingerprintSensorEx.DBFree(mhDB);
            mhDB = 0;
        }

        if (0 != mhDevice) {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }
        FingerprintSensorEx.Terminate();
        new reconnectThread(this).start();

    }

    private class reconnectThread extends Thread {
        private ZKFP zkfp;

        public reconnectThread(ZKFP zkfp) {
            this.zkfp = zkfp;
        }

        @Override
        public void run() {
            super.run();
            try { // wait for thread stopping
                Thread.sleep(1000);
                zkfp.connect();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();

            int ret = 0;
            int cantidad = 0;
            int error = 0;
            templateLen[0] = 2048;
            byte[][] templatesTemps = new byte[3][templateLen[0]];
            while (isRun) {
                templateLen[0] = 2048;
                if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen))) {
                    // System.out.println("imgbuf:" + imgbuf);
                    System.arraycopy(template, 0, templatesTemps[cantidad], 0, 2048);
                    if (cantidad > 0) {
                        int presition = FingerprintSensorEx.DBMatch(mhDB, template, templatesTemps[cantidad - 1]);
                        SConsole.log(presition);
                        if (presition > 50) {
                            SConsole.log("[ZKFP]", "Huella dactilar capturada.");
                        } else {
                            SConsole.log("[ZKFP]", "error");
                            SConsole.log("[ZKFP]", "Intente nuevamente");
                            error++;
                            if (error > 3) {
                                error = 0;
                                cantidad = 0;
                            }
                            continue;
                        }
                    }
                    cantidad++;
                    SConsole.log(cantidad);

                    if (cantidad == 3) {
                        FingerprintSensorEx.DBMerge(mhDB, templatesTemps[0], templatesTemps[1], templatesTemps[2],
                                template, templateLen);
                        String b64 = FingerprintSensorEx.BlobToBase64(template, templateLen[0]);
                        SConsole.log(b64);
                        cantidad = 0;

                        JSONObject response = new JSONObject();
                        response.put("type", "registro_huella");
                        response.put("component", "dispositivo");
                        response.put("key_punto_venta", SConfig.getJSON().getString("key_punto_venta"));
                        response.put("key_tipo_dispositivo", "096acabc-3aca-41f3-86e3-d47b0e1add17");
                        response.put("data", b64);
                        SocketCliente.send("zkteco", response.toString());

                    }
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
