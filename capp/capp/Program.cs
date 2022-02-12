using System;

using zkemkeeper;

namespace capp
{
    internal class Program
    {
        static void Main(string[] args)
        {

            CZKEMClass axCZKEM1 = new CZKEMClass();
            String ip = "192.168.1.201";
            int port = 4370;
            String commKey = "0";
            int idwErrorCode = 0;

            //axCZKEM1.SetCommPassword(Convert.ToInt32(commKey));
            Console.WriteLine($"Intentando conectar a {ip}");
            if (axCZKEM1.Connect_Net(ip, Convert.ToInt32(port)) == true) {
                Console.WriteLine("Conexion exitosa");
            }
            Console.WriteLine("Fin");

        }
    }
}   
