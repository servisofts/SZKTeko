using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
{
    internal class lector_huella
    {
        public const string COMPONENT = "lector_huella";

        public static void onMessage(SJSon obj, SSocket session)
        {
           switch (obj.getString("type"))
           {
                case "conectar":
                    conectar(obj, session);
                    break;
   
            }
        }

        private static void conectar(SJSon obj, SSocket session)
        {
            obj.put("noSend", false);

            if (!SFP.getInstance().isConnect())
            {
                obj.put("estado", "error");
                obj.put("error", "no device");
                return;
            }
            obj.put("estado", "exito");
           
        }

    }
}
