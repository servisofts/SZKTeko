using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace capp
{
    internal class Manejador
    {
        public static void onMessage(SJSon obj, SSocket session) {
            switch (obj.getString("component"))
            {
                case Servicio.COMPONENT:
                    Servicio.onMessage(obj, session);
                    break;
                case punto_venta.COMPONENT:
                    punto_venta.onMessage(obj, session);
                    break;
                case dispositivo.COMPONENT:
                    dispositivo.onMessage(obj, session);
                    break;
            }
        }
    }
}
