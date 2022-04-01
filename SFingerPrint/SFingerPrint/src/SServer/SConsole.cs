using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SFingerPrint
{
    internal class SConsole
    {
        public static void log(string mensaje)
        {   
               Console.WriteLine($"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ LOG ]: {mensaje}");
        }

        public static void error(string mensaje)
        {
            Console.WriteLine($"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ ERROR ]: {mensaje}");
        }

        public static void warning(string mensaje)
        {
            Console.WriteLine($"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ WARNING ]: {mensaje}");
        }
    }
}
