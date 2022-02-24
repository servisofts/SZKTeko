using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
{
    internal class SConsole
    {
        private static void write (string msn)
        {
            try
            {
                //Pass the filepath and filename to the StreamWriter Constructor
                StreamWriter sw = new StreamWriter("C:\\Test.txt", true);
                //Write a line of text
                //Write a second line of text
                sw.NewLine = "\r\n";
                sw.WriteLine(msn);
                //Close the file
                sw.Close();
            }
            catch (Exception e)
            {
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
