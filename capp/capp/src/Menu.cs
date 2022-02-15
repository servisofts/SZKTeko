using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace capp
{
    internal class Menu
    {
        //SERVER CONFIG
        public static String SERVER_IP = "192.168.0.199";
        public static int SERVER_PORT = 40032;
       public static String key_punto_venta = "aa1330e1-f6dc-443a-8deb-3b1b6a3e5c23";
        //public static String key_punto_venta = "key_test";
        
        
        
        public Menu() {

            printLogo();
            getKeyPuntoVenta();
           
        }
        public void printLogo() {
            Console.WriteLine(" ");
            Console.WriteLine($"---------------------------------------------");
            Console.WriteLine($"              Servisofts-ZKTeco              ");
            Console.WriteLine($"---------------------------------------------");
            Console.WriteLine($" ");
        }
        public String getKeyPuntoVenta() {
            if (key_punto_venta == null) {
                Console.WriteLine($"Ingrese el TOKEN para continuar.");
                Console.Write("(key_punto_venta): ");
                String TOKEN = Console.ReadLine();
                key_punto_venta = TOKEN.ToString();
            }
            return key_punto_venta;
        }

    }
}
