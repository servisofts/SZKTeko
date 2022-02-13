using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using zkemkeeper;
using System.Threading;

namespace capp
{
    internal class SZK{
            
        private String ip;
        private int port;
        private CZKEMClass CZKE;
        public SZK(String ip, int port)
        {
            this.ip = ip;
            this.port = port;
            this.CZKE = new CZKEMClass();
        }

        public Boolean connectar() {
          
            this.log($"Intentando Conectar {this.ip}:{this.port}");
            if (this.CZKE.Connect_Net(this.ip, Convert.ToInt32(this.port)) == true)
            {
                this.log("Conexion exitosa!!!");
                return true;
            }
            this.log("Conexion fallida");
            return false;
        }

        public void getDeviceInfo()
        {
            String mac = "";
            this.CZKE.GetDeviceMAC(0, ref mac);
            this.log($"{mac}");
            this.log($"{mac}");
        }


        private void log(String mensaje) {
               Console.WriteLine(mensaje);
        }

    }
}
