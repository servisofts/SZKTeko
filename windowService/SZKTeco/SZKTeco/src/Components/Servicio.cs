using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
{
    internal class Servicio
    {
        public const string COMPONENT = "servicio";
        public static void onMessage(SJSon obj, SSocket session)
        {
           switch (obj.getString("type"))
           {
               case "init":
                   init(obj, session);
                   break;
           }
        }

        private static void init(SJSon obj, SSocket session){

            obj.put("component", "punto_venta");
            obj.put("type", "identificarse");
            obj.put("noSend", false);
            obj.put("key_punto_venta",SConfig.get().getString("key_punto_venta"));

            SConsole.log($"Intentando identificarse con el punto de venta ({SConfig.get().getString("key_punto_venta")})");
        }
    }
}
