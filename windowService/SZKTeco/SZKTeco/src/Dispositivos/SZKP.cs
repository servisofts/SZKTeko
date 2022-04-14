using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using zkemkeeper;
using System.Threading;
using System.Runtime.InteropServices;
using System.Text.RegularExpressions;
using Newtonsoft.Json.Linq;

namespace SZKTeco
{
    internal class SZKP{


        private IntPtr h = IntPtr.Zero;
        [DllImport(@"C:\Windows\SysWOW64\plcommpro.dll", EntryPoint = "Connect")]
        public static extern IntPtr Connect(string Parameters);
        //public static extern IntPtr Connect(string Parameters);
        [DllImport("plcommpro.dll", EntryPoint = "PullLastError")]
        public static extern int PullLastError();

        private SJSon data;
        private String ip;
        private int port;
        private CZKEM CZKE;
        private int MachineNumber = 1;
        public SZKP(SJSon dispositivo)
        {
            this.data= dispositivo;
            this.ip = dispositivo.getString("ip");
            this.port = dispositivo.getInt("puerto");
            this.CZKE = new CZKEM();
        }

        public void setData(SJSon data) {
            this.data = data;
        }
        public string getKey()
        {
            return this.data.getString("key");
        }

        public void connectar() {
            if (h == IntPtr.Zero)
            {
                Thread t1 = new Thread(new ThreadStart(this.hiloConectar));
                t1.Start();
            }
            else {
                string mac =this.getParam("MAC");
                if (mac == "MAC")
                {
                    this.Desconectar();
                    this.connectar();
                }
                else { 
                  SConsole.log($"[SZKP] Conexion ready {this.data.getString("ip")}:{this.data.getInt("puerto")}");
                }
            }
        }
           
        public bool isConnect()
        {
            return h != IntPtr.Zero;
        }

        public void hiloConectar() {

            SConsole.log($"[SZKP] Intentando Conectar {this.data.getString("ip")}:{this.data.getInt("puerto")}");
            int ret = 0;        // Error ID number

            SJSon objSend = new SJSon();
            objSend.put("component", "dispositivo");
            objSend.put("type", "conectado");
            this.data.put("isConected", false);
            objSend.put("data", this.data);
            if (IntPtr.Zero == h)
            {
                h = Connect($"protocol=TCP,ipaddress={this.data.getString("ip")},port={this.data.getInt("puerto")},timeout=2000,passwd=");

                if (h != IntPtr.Zero)
                {
                    SConsole.log($"[SZKP] Conexion exitosa {this.data.getString("ip")}:{this.data.getInt("puerto")}");
                    string mac = getParam("MAC");
                    this.data.put("mac", mac);
                    this.data.put("isConected", true);
                    objSend.put("data", this.data);
                    objSend.put("estado", "exito");
                    SSocket.Send(objSend.ToString());
                    this.onMessagge();
                    return;
                }
            }
            objSend.put("estado", "error");
            SSocket.Send(objSend.ToString());
            SConsole.error($"[SZKP] Conexion fallida {this.data.getString("ip")}:{this.data.getInt("puerto")}");
            if (!Service1.isRun) return;
            Thread tmsn = new Thread(new ThreadStart(this.reconectar));
            tmsn.Start();
            return;
        }
        private void reconectar()
        {
            Thread.Sleep(5000);
            this.connectar();
        }
        private void hiloSession() {
            Thread tmsn = new Thread(new ThreadStart(this.onMessagge));
            tmsn.Start();
        }

        [DllImport("plcommpro.dll", EntryPoint = "GetRTLog")]
        public static extern int GetRTLog(IntPtr h,ref byte buffer, int bufferSize, string itemvalues);


        private int getRT()
        {
            try
            {
                int BUFFERSIZE= 256;
                byte[] buffer = new byte[BUFFERSIZE];
                int number = GetRTLog(h, ref buffer[0], BUFFERSIZE,"");
                string str = Encoding.Default.GetString(buffer);
                str = str.Replace("\0", string.Empty);
               // SConsole.log($"Ocurrio un evento {number} :: {str}");
                string[] keys = Regex.Split(str, ",");
                if (keys[3] != "0")
                {
                    SConsole.log($"Ocurrio un evento {number} :: {str}");
                    SJSon data = new SJSon();
                    data.put("Fecha", keys[0]);
                    data.put("Pin", keys[1]);
                    data.put("Cardno", keys[2]);
                    data.put("DoorID", keys[3]);
                    data.put("EventType", keys[4]);
                    data.put("InOutState", keys[5]);

                    SJSon dataSend = new SJSon();
                    dataSend.put("component", "dispositivo");
                    dataSend.put("type", "onEvent");
                    dataSend.put("estado", "cargando");
                    dataSend.put("key_punto_venta", SConfig.get().getString("key_punto_venta"));
                    dataSend.put("key_dispositivo", this.data.getString("key"));

                    dataSend.put("data",data);
                    SSocket.Send(dataSend.ToString());
                }
                Thread.Sleep(1000);
                return number;

            }
            catch (Exception ex)
            {
               // SConsole.log($"Error en el hilo onMessgge");
                Thread.Sleep(1000);
                return 0;
            }
        }
        private void onMessagge() {
            SConsole.log("Start Real Time event");
            SZKP instance = this;
            while (h != IntPtr.Zero) {
              int number=  instance.getRT();
            }
            SConsole.log("Device disconnect");
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
        public string getParam(string param)
        {

            //  GATEIPAddress,NetMask,MAC

            string valuesToRead = param;
            int BUFFERSIZE = 2048;
            byte[] buffer = new byte[BUFFERSIZE];
            string tmp = "";
            GetDeviceParam(h, ref buffer[0], BUFFERSIZE, valuesToRead);
            tmp = Encoding.Default.GetString(buffer);
            tmp = tmp.Replace("\0", string.Empty);
            return tmp.Replace(param+"=",string.Empty);

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
        public JArray GetDeviceData_Pull(String tableName, string str)
        {

            string[] keys = Regex.Split(str, "\t");
            int ret = 0;
            int BUFFERSIZE = 1 * 1024 * 1024;
            byte[] buffer = new byte[BUFFERSIZE];
         
            if (IntPtr.Zero != h)
                ret = GetDeviceData(h, ref buffer[0], BUFFERSIZE,tableName, str, "", "");
            else
            {
                this.log("Connect device failed!");
                return null;
            }
            str = Encoding.Default.GetString(buffer);
            str = str.Replace("\0", string.Empty);
           JArray lista = new JArray(); 
            if (ret >= 0) {
                string[] vs = Regex.Split(str, "\r\n");
                int j = 0;
                foreach (string v in vs) {
                    if (j == 0) {
                        j++;
                        continue;
                    }
                    SJSon objData = new SJSon();
                    string[] data = Regex.Split(v, ",");
                    int i = 0;
                    if (data.Length <= 0) continue;
                    if (data[0].Length <= 0) {
                        continue;
                    }
                    foreach (string key in keys) {
                        objData.put(key, data[i]);
                        i++;
                    }
                    lista.Add(objData.tojObject());
                    j++;    
                  
                }
                return lista;

            }
            else
            {
                this.log("Get data failed.The error is " + ret);
                return null;
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




        [DllImport("plcommpro.dll", EntryPoint = "ModifyIPAddress")]
        public static extern int ModifyIPAddress(string commtype, string address, string buffer);
        public void ModifIP(string ip, string mac,string gateway)
        {
            int ret = 0;
            string udp = "UDP";
            string buffer = "";
            buffer = $"MAC={mac},IPAddress={ip}";// + "ComPwd=136166";
            SConsole.log($"ModifyIPAddress operation begin! ({buffer})");
            ret = ModifyIPAddress(udp, gateway, buffer);          
            if (ret >= 0)
            {
                SConsole.log("ModifyIPAddress operation succeed!");
                return;
            }
            else
            {
                SConsole.error("ModifyIPAddress operation failed!" + ret);
                return;
            }
        }


        [DllImport("plcommpro.dll", EntryPoint = "GetDeviceDataCount")]
        public static extern int GetDeviceDataCount(IntPtr h, string tablename, string filter, string options);
        public int GetDeviceDataCount_Pull (string tablename)
        {
            int ret = 0;
            string[] count = new string[20];
            if (IntPtr.Zero != h)
            {
                ret = GetDeviceDataCount(h, tablename, "", "");
            }
            return ret;
        }

        [DllImport("plcommpro.dll", EntryPoint = "DeleteDeviceData")]
        public static extern int DeleteDeviceData(IntPtr h, string tablename, string data, string options);
        public void DeleteDeviceData_Pull(string tablename , string data)
        {
            int ret = 0;
            if (IntPtr.Zero != h)
            {
                ret = DeleteDeviceData(h, tablename, data, "");
                if (ret >= 0)
                    SConsole.log($"Data eliminada {tablename} {data}");
                else
                    SConsole.log($"Data eliminada {tablename} {data}");
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
