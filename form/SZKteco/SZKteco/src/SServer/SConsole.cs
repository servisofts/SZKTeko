using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SZKteco;

namespace SZKTeco
{
    internal class SConsole
    {
        private static void write (string msn)
        {
            try
            {
                //  StreamWriter sw = new StreamWriter("C:\\SZKTeco\\LOGS.txt", true);
                // sw.WriteLine(msn);
                //sw.Close();
                if (Form1.INSTANCE!=null)
                {
                    Form1.INSTANCE.CambiarLabel(msn+"\r\n");
                }
               // Console.WriteLine(msn);
            }
            catch (Exception e)
            {
               Console.WriteLine(e.Message);
            }
        }
        public static void log(string mensaje)
        {
            write($"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ LOG ]: {mensaje}");
        }

        public static void error(string mensaje)
        {
            write($"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ ERROR ]: {mensaje}");
        }

        public static void warning(string mensaje)
        {
            write($"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ WARNING ]: {mensaje}");
        }
    }
}
