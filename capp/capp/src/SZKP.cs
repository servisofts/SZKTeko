using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using zkemkeeper;
using System.Threading;
using System.Runtime.InteropServices;

namespace capp
{
    internal class SZKP{

        IntPtr h = IntPtr.Zero;
        [DllImport(@"C:\Windows\SysWOW64\plcommpro.dll", EntryPoint = "Connect")]
        public static extern IntPtr Connect(string Parameters);
        //public static extern IntPtr Connect(string Parameters);
        [DllImport("plcommpro.dll", EntryPoint = "PullLastError")]
        public static extern int PullLastError();


        private String ip;
        private int port;
        private CZKEM CZKE;
        private int MachineNumber = 1;
        public SZKP(String ip, int port)
        {
            this.ip = ip;
            this.port = port;
            this.CZKE = new CZKEM();
        }

        public Boolean connectar() {
          
            this.log($"Intentando Conectar {this.ip}:{this.port}");
            int ret = 0;        // Error ID number
                                //    Cursor = Cursors.WaitCursor;
            if (IntPtr.Zero == h)
            {
                h = Connect($"protocol=TCP,ipaddress={this.ip},port={this.port},timeout=2000,passwd=");

                if (h != IntPtr.Zero)
                {
                    this.log("Conexion exitosa!!!");
                    return true;
                }
            }
            this.log("Conexion fallida");
            return false;
        }

        [DllImport("plcommpro.dll", EntryPoint = "Disconnect")]
        public static extern void Disconnect(IntPtr h);

        public void Desconectar()
        {
            if (IntPtr.Zero != h)
            {
                Disconnect(h);
                h = IntPtr.Zero;
            }
            this.log("Desconectado");
        }

        [DllImport("plcommpro.dll", EntryPoint = "GetDeviceParam")]
        public static extern int GetDeviceParam(IntPtr h, ref byte buffer, int buffersize, string itemvalues);
        public void GetDeviceParam_Pull()
        {
            string valuesToRead = "GATEIPAddress,NetMask";
            int ret = 0;
            int BUFFERSIZE = 10 * 1024 * 1024;
            byte[] buffer = new byte[BUFFERSIZE];
            string tmp = null;
            ret = GetDeviceParam(h, ref buffer[0], BUFFERSIZE, valuesToRead);
            tmp = Encoding.Default.GetString(buffer);
            this.log("GetDeviceParam: " + tmp + ".");
        }


        [DllImport("plcommpro.dll", EntryPoint = "SetDeviceParam")]
        public static extern int SetDeviceParam(IntPtr h, string itemvalues);
        public void SetDeviceParam_Pull(String ip)
        {
            // Upgrade GATEIPAddress and NetMask values
            string valuesToSet = $"GATEIPAddress = {ip},NetMask = 255.255.255.0";
            int ret = 0;
            ret = SetDeviceParam(h, valuesToSet);    //set the select params values to device
            if (ret >= 0)
               this.log("SetDeviceParam successfu!");
            else
                PullLastError();
        }

        [DllImport("plcommpro.dll", EntryPoint = "ControlDevice")]
        public static extern int ControlDevice(IntPtr h, int operationid, int param1, int param2, int param3, int param4, string options);
        public void ControlDevice_Pull(int operID, int doorOrAuxoutID, int outputAddrType, int doorAction)
        {
            int ret = 0;
            if (IntPtr.Zero != h)
            {
                this.log(operID.ToString() + "," + doorOrAuxoutID.ToString() + "," + outputAddrType.ToString() + "," + doorAction.ToString());
                //call ControlDevice funtion from PullSDK
                ret = ControlDevice(h, operID, doorOrAuxoutID, outputAddrType, doorAction, 0, "");
            }
            else
            {
                this.log("Connect device failed!The error is " + PullLastError() + " .");
                return;
            }
            if (ret >= 0)
            {
                this.log("The operation succeed!");
                return;
            }
            else
            {
                this.log("Failed action " + PullLastError() + " .");
            }
        }



        [DllImport("plcommpro.dll", EntryPoint = "GetDeviceData")]
        public static extern int GetDeviceData(IntPtr h, ref byte buffer, int buffersize, string tablename, string filename, string filter, string options);
        public void GetDeviceData_Pull(String tableName, string str)
        {
            int ret = 0;
            int BUFFERSIZE = 1 * 1024 * 1024;
            byte[] buffer = new byte[BUFFERSIZE];
         
            if (IntPtr.Zero != h)
                ret = GetDeviceData(h, ref buffer[0], BUFFERSIZE,tableName, str, "", "");
            else
            {
                this.log("Connect device failed!");
                return;
            }
            str = Encoding.Default.GetString(buffer);
            if (ret >= 0) {
                this.log("Get " + ret + " records");
                this.log(str + "");

            }
            else
            {
                this.log("Get data failed.The error is " + ret);
                return;
            }
        }

        [DllImport("plcommpro.dll", EntryPoint = "SetDeviceData")]
        public static extern int SetDeviceData(IntPtr h, string tablename, string data, string options);
        public void SetDeviceData_Pull(string devtablename,string data, string options = "")
        {
            int ret = 0;

            if (IntPtr.Zero != h)
            {
                ret = SetDeviceData(h, devtablename, data, options);
                if (ret >= 0)
                {
                   this.log("SetDeviceData operation succeed!");
                }
                else
                    this.log("SetDeviceData operation failed!");
            }
            else
            {
                this.log("Connect device failed!");
            }

        }
        private void log(String mensaje) {
               Console.WriteLine(mensaje);
        }
        private void error()
        {
            int error = 0;
            this.CZKE.GetLastError(ref error);
            Console.WriteLine($"Error Code={error}");
        }
    }
}
