using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
{
    internal class Dispositivos
    {
        private static IDictionary<string,SZK> SZKs = new Dictionary<string,SZK>();
        private static IDictionary<string ,SZKP> SZKPs = new Dictionary<string ,SZKP>();

        public static SZK create_SZK(SJSon dispositivo) {
            SZK dv1 = new SZK(dispositivo);
            SZKs.Add(dv1.getKey(), dv1);
            return dv1;
        }
        public static SZK get_SZK(string key)
        {
            SZKs.TryGetValue(key, out SZK s);
            return s;
        }
        public static SZKP create_SZKP(SJSon dispositivo)
        {
            if (dispositivo.getString("key_punto_venta") != SConfig.get().getString("key_punto_venta")) {
                return null;
            }
            SZKP dv1 = get_SZKP(dispositivo.getString("key"));
          
            if (dv1 == null) {
                 dv1= new SZKP(dispositivo);
                SZKPs.Add(dv1.getKey(), dv1);
            }
            dv1.setData(dispositivo);
            return dv1;
        }
        public static SZKP get_SZKP(string key)
        {
            SZKPs.TryGetValue(key, out SZKP s);
            return s;
        }

    }
}
