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
        private static String PATH = @"C:\SZKteco";
        private static String FILE_NAME = "Config.json";

        private static SJSon DATA;
        public static void init()
        {

            Directory.CreateDirectory(PATH);
            if (!File.Exists(PATH + @"\" + FILE_NAME))
            {
                using (FileStream fs = System.IO.File.Create(PATH + @"\" + FILE_NAME))
                {
                    using (StreamWriter w = new StreamWriter(fs))
                    {
                        SJSon obj = new SJSon();
                        obj.put("key_punto_venta", "--");
                      
                        obj.put("ip", "servisofts.com");
                        obj.put("puerto", 40032);
                        w.WriteLine(obj.ToString());
                        w.Flush();
                        w.Close();
                    }

                }
            }
            
            StreamReader rs = new StreamReader(PATH+@"\"+FILE_NAME);
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
