using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using zkemkeeper;
using System.Threading;

namespace SZKTeco
{
    internal class SZK{
            
        private String ip;
        private int port;
        private CZKEM CZKE;
        private int MachineNumber = 1;
        private SJSon data;
    
        private bool isConect = false;

        public SZK(SJSon dispositivo)
        {
            this.data = dispositivo;
            this.ip = dispositivo.getString("ip");
            this.port = dispositivo.getInt("puerto");
            this.CZKE = new CZKEM();
        }
        public string getKey() {
            return this.data.getString("key");
        }

        public void connectar() {
            Thread t1 = new Thread(new ThreadStart(this.hiloConectar));
            t1.Start();
           
        }
        public void hiloConectar() {
            SConsole.log($"[SZK] Intentando Conectar {this.ip}:{this.port}");
            isConect = false;
            SJSon objSend = new SJSon();
            objSend.put("component", "dispositivo");
            objSend.put("type", "conectado");
            this.data.put("isConected", false);
            objSend.put("data", this.data);
            if (this.CZKE.Connect_Net(this.ip, Convert.ToInt32(this.port)) == true)
            {
                SConsole.log($"[SZK] Conexion exitosa {this.ip}:{this.port}");
                isConect = true;
                string mac= this.getMacAddress();
                this.data.put("mac", mac);
                this.data.put("isConected", true);
                objSend.put("data", this.data);
                objSend.put("estado", "exito");
                SSocket.Send(objSend.ToString());
                return;
            }
            SConsole.error($"[SZK] Conexion fallida {this.ip}:{this.port}");

            objSend.put("estado", "error");
            SSocket.Send(objSend.ToString());
            isConect = false;
        }
        public void setUser()
        {

            this.CZKE.EnableDevice(1, false);//disable the device
            if (this.CZKE.SSR_SetUserInfo(this.MachineNumber, "1", "Juan", "", 1, true) == true)
            {
                this.log("Insertado");
            }
            else
            {
                this.error();
            }
            this.CZKE.RefreshData(this.MachineNumber);
            this.CZKE.EnableDevice(1, true);//enable the device

        }
        public string getMacAddress()
        {
            String mac = "";
            this.CZKE.GetDeviceMAC(1, ref mac);

            return mac;
        }
        public void getDeviceInfo()
        {
            String mac = "";
            this.CZKE.GetDeviceMAC(1, ref mac);
            this.log($"{mac}");


            this.CZKE.EnableDevice(1, true);
            int number = 0;
            int mn = 0;
            int bn = 0;
            int dmp = 0;
            int enable=1;

            this.CZKE.GetAllUserID(1, ref number, ref mn, ref bn, ref dmp, ref enable);

            this.log($"Roles:  {number}");
            
        }

        public int getCapacityInfo()
        {
            int ret = 0;

            int adminCnt = 0;
            int userCount = 0;
            int fpCnt = 0;
            int recordCnt = 0;
            int pwdCnt = 0;
            int oplogCnt = 0;
            int faceCnt = 0;

       
            this.CZKE.EnableDevice(1, false);//disable the device

            this.CZKE.GetDeviceStatus(1, 2, ref userCount);
            this.CZKE.GetDeviceStatus(1, 1, ref adminCnt);
            this.CZKE.GetDeviceStatus(1, 3, ref fpCnt);
            this.CZKE.GetDeviceStatus(1, 4, ref pwdCnt);
            this.CZKE.GetDeviceStatus(1, 5, ref oplogCnt);
            this.CZKE.GetDeviceStatus(1, 6, ref recordCnt);
            this.CZKE.GetDeviceStatus(1, 21, ref faceCnt);

            this.CZKE.EnableDevice(1, true);//enable the device

            this.log($" adminCnt: {adminCnt}");
            this.log($" userCount: {userCount}");

            return ret;
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
