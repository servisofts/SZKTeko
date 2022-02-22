using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace capp
{
    internal class punto_venta
    {
        public const string COMPONENT = "punto_venta";

        public static void onMessage(SJSon obj, SSocket session)
        {
            switch (obj.getString("type"))
            {
                case "identificarse":
                    identificarse(obj, session);
                    break;
                case "reboot":
                    reboot(obj, session);
                    break;
            }
        }

        private static void identificarse(SJSon obj, SSocket session)
        {

            if (obj.getString("estado") == "exito")
            {
                SConsole.log("Identificado con exito!!!");
                State.punto_venta.put("data", obj.getSJSonObject("data"));
                SJSon toSend = new SJSon();
                toSend.put("component", "dispositivo");
                toSend.put("type", "getAll");
                toSend.put("estado", "cargando");
                toSend.put("key_punto_venta", Menu.key_punto_venta);
                SSocket.Send(toSend.ToString());
            }
            else
            {
                SConsole.log("ERROR al identificarse");

            }

        }
        private static void reboot(SJSon obj, SSocket session)
        {
            if (obj.getSJSonObject("punto_venta").getString("key") == Menu.key_punto_venta)
            {
                SSocket.reset();
            }
        }
    
    }
}
