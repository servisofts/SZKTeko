using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
{
    internal class SConfig
    {
        private static String PATH = "C:/SZKteco/Config.json";

        private static SJSon DATA;
        public static void init()
        {
            
            StreamReader rs = new StreamReader(PATH);
            string text = "";
            string line;

            while((line = rs.ReadLine()) != null)
            {
                text += line;
            }
            rs.Close();
            DATA= new SJSon(text);
        }

        public static SJSon get()
        {
            if (DATA == null)
            {
                init();
            }
            return DATA;
        }
    }
}
