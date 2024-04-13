using System;
using System.Drawing;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;
using libzkfpcsharp;

namespace SFingerPrint
{
    internal class SFP
    {
        private static SFP INSTANCE;
        public static SFP getInstance()
        {
            if (INSTANCE == null)
            {
                INSTANCE = new SFP();
            }
            return INSTANCE;
        }


        private Thread t1;
        private Boolean connected = false;
        private Boolean run = false;
        IntPtr mDevHandle = IntPtr.Zero;
        IntPtr mDBHandle = IntPtr.Zero;

        byte[] FPBuffer;
        byte[][] RegTmps = new byte[3][];
        byte[] CapTmp = new byte[2048];


        int cbCapTmp = 2048;


        private int mfpWidth = 0;
        private int mfpHeight = 0;
        private int mfpDpi = 0;
        public SFP()
        {
            t1 = new Thread(new ThreadStart(this.hilo));
            t1.Start();
        }

        public void hilo() {
            run = true;
            while (run)
            {
                if (!connected)
                {
                    this.connectar();
                }

                Thread.Sleep(1000);
            }

        }

        public void connectar()
        {

            SConsole.log("Intentando conectar con el lector de huella");
            int ret = zkfperrdef.ZKFP_ERR_OK;
            if ((ret = zkfp2.Init()) == zkfperrdef.ZKFP_ERR_OK)
            {
                int nCount = zkfp2.GetDeviceCount();
                if (nCount > 0)
                {
                    conect_device(0);
                    SConsole.log("Conexion exitosa.");
                    connected = true;
                }
                else
                {
                    connected = false;
                    zkfp2.Terminate();
                    SConsole.log("No device connected!");
                }
            }
            else
            {
                connected = false;
                SConsole.error("No device connected!");
            }
        }

        public void conect_device(int idDevice)
        {
            int ret = zkfp.ZKFP_ERR_OK;
            if (IntPtr.Zero == (mDevHandle = zkfp2.OpenDevice(idDevice)))
            {
                SConsole.error("OpenDevice fail");
                return;
            }
            if (IntPtr.Zero == (mDBHandle = zkfp2.DBInit()))
            {
                SConsole.error("Init DB fail");
                zkfp2.CloseDevice(mDevHandle);
                mDevHandle = IntPtr.Zero;
                return;
            }
            for (int i = 0; i < 3; i++)
            {
                RegTmps[i] = new byte[2048];
            }
            byte[] paramValue = new byte[4];
            int size = 4;
            zkfp2.GetParameters(mDevHandle, 1, paramValue, ref size);
            zkfp2.ByteArray2Int(paramValue, ref mfpWidth);

            size = 4;
            zkfp2.GetParameters(mDevHandle, 2, paramValue, ref size);
            zkfp2.ByteArray2Int(paramValue, ref mfpHeight);

            FPBuffer = new byte[mfpWidth * mfpHeight];

            size = 4;
            zkfp2.GetParameters(mDevHandle, 3, paramValue, ref size);
            zkfp2.ByteArray2Int(paramValue, ref mfpDpi);

           SConsole.log("reader parameter, image width:" + mfpWidth + ", height:" + mfpHeight + ", dpi:" + mfpDpi + "\n");

            Thread captureThread = new Thread(new ThreadStart(DoCapture));
            captureThread.IsBackground = true;
            captureThread.Start();
        }

        int streamSize = 2048;
        byte[] streamIn = new byte[2048];

        private void DoCapture()
        {
            int cantidad = 0;
    
            while (run)
            {
                streamSize = 2048;
                int ret = zkfp2.AcquireFingerprint(mDevHandle, FPBuffer, streamIn, ref streamSize);
                if (ret == zkfp.ZKFP_ERR_OK)
                {
                  
                    // SConsole.log(strShow);

                   
                    if (cantidad > 0)
                    {
                        int presition = zkfp2.DBMatch(mDBHandle, streamIn, RegTmps[cantidad - 1]);
                        if (presition <= 0)
                        {
                            SConsole.log($"Error in match: {presition}");
                            continue;
                        }
                    }
                    Array.Copy(streamIn, RegTmps[cantidad], streamSize);
                    cantidad += 1;
                    SConsole.log($"FP # {cantidad}  ret:{ret}");
                    if (cantidad >= 3)
                    {
                        if (zkfp.ZKFP_ERR_OK == (ret = zkfp2.DBMerge(mDBHandle, RegTmps[0], RegTmps[1], RegTmps[2], streamIn, ref streamSize)))
                        {
                            String strShow = zkfp2.BlobToBase64(streamIn, streamSize);
                            SConsole.log("enroll succ\n");
                            SConsole.log(strShow);
                        }
                        else
                        {
                            SConsole.error("enroll fail, error code=" + ret + "\n");
                        }
                        SConsole.log("GUARDADO");
                        cantidad = 0;
                        continue;
                    }

                }
                Thread.Sleep(200);
            }
        }

    }
}


