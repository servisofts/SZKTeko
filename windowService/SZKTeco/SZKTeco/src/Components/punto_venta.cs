using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
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
                case "sincronizar":
                    sincronizar(obj, session);
                    break;
                case "ping":
                    ping(obj, session);
                    break;
            }
        }

        private static void identificarse(SJSon obj, SSocket session)
        {

            if (obj.getString("estado") == "exito")
            {
                SConsole.log("Identificado con exito!!!");
                //State.punto_venta.put("data", obj.getSJSonObject("data"));
                SJSon toSend = new SJSon();
                toSend.put("component", "dispositivo");
                toSend.put("type", "getAll");
                toSend.put("estado", "cargando");
                toSend.put("key_tipo_dispositivo", "607b087c-6a92-4d8a-b311-e5c105cefd08");
                toSend.put("key_punto_venta", SConfig.get().getString("key_punto_venta"));
                SSocket.Send(toSend.ToString());
            }
            else
            {
                SConsole.log("ERROR al identificarse");

            }

        }
        private static void reboot(SJSon obj, SSocket session)
        {
            if (obj.getSJSonObject("punto_venta").getString("key") == SConfig.get().getString("key_punto_venta"))
            {
                SSocket.reset();
            }
        }

        private static void ping(SJSon obj, SSocket session)
        {
            if (obj.getString("key_punto_venta") == SConfig.get().getString("key_punto_venta"))
            {
                SConsole.log("Respondiendo ping de sincronizacion");
                obj.put("noSend", false);
                obj.put("estado", "exito");
            }

            /*else {
                obj.put("key_punto_venta", SConfig.get().getString("key_punto_venta"));
                obj.put("noSend", false);
                obj.put("estado", "exito");
                SConsole.log("ping de sincronizacion "+obj.getString("key_punto_venta"));
            }*/

        }
        private static void sincronizar(SJSon obj, SSocket session)
        {
           // System.Diagnostics.Debugger.Launch();

            obj.put("noSend", false);
            bool deleteAll = obj.getBool("delete_all");
            SConsole.log("Sincronizar.");
            String key_dispositivo = obj.getString("key_dispositivo");
           SZKP device=  Dispositivos.get_SZKP(key_dispositivo);
            if (device.isConnect())
            {
                if (deleteAll)
                {
                    device.DeleteDeviceData_Pull("user", "");
                    device.DeleteDeviceData_Pull("userauthorize", "");
                    device.DeleteDeviceData_Pull("templatev10", "");
                }
                
               //JArray arr = obj.getArray("data");
                String data_inser = "";
                String data_inser_aut = "";

               /* for (int i = 0; arr.Count > i; i++)
                {
                    String codigo = arr[i].ToString();
                    data_inser += $"CardNo=0\tPin={codigo}\tName=ca\tPassword=\tGroup=0\tStartTime=0\tEndTime=0";
                    data_inser_aut += $"Pin={codigo}\tAuthorizeDoorId=1\tAuthorizeTimezoneId=1\r\n";
                    data_inser_aut += $"Pin={codigo}\tAuthorizeDoorId=2\tAuthorizeTimezoneId=1";

                    if (i+1 < arr.Count)
                    {
                        data_inser += "\r\n";
                        data_inser_aut += "\r\n";
                    }
                }
                device.SetDeviceData_Pull("user", data_inser);
                device.SetDeviceData_Pull("userauthorize",data_inser_aut);
               */

                String data_inser_huellas = "";
             //   device.DeleteDeviceData_Pull("templatev10", "");
                SJSon huellas = obj.getSJSonObject("huellas");
                String[] huellas_keys = huellas.keys();
                int id_huella = device.GetDeviceDataCount_Pull("templatev10");

                int n_usuarios = 0;
                int n_huellas = 0;

                for (int i = 0; i < huellas_keys.Length; i++) {
                    String pin_usuario  = huellas_keys[i];
                    SJSon usuario = huellas.getSJSonObject(pin_usuario);
                    String[] usuarios_keys = usuario.keys();
                    if (!deleteAll)
                    {
                         device.DeleteDeviceData_Pull("templatev10", $"Pin={pin_usuario}");
                    }
                    //registramos al usuario
                    data_inser += $"CardNo=0\tPin={pin_usuario}\tName=ca\tPassword=\tGroup=0\tStartTime=0\tEndTime=0";
                    data_inser_aut += $"Pin={pin_usuario}\tAuthorizeDoorId=1\tAuthorizeTimezoneId=1\r\n";
                    data_inser_aut += $"Pin={pin_usuario}\tAuthorizeDoorId=2\tAuthorizeTimezoneId=1";
                    n_usuarios++;
                    if (i + 1 < huellas_keys.Length)
                    {
                        data_inser += "\r\n";
                        data_inser_aut += "\r\n";
                    }

                    for (int j = 0; j < usuarios_keys.Length; j++) {
                        if (i + j > 0)
                        {
                            data_inser_huellas += "\r\n";
                        }
                        String dedo = usuarios_keys[j];
                        id_huella += 1;
                        String huella = usuario.getString(dedo);
                        data_inser_huellas += $"Pin={pin_usuario}\tFingerID={id_huella}\tValid=1\tTemplate={huella}\tSize={huella.Length}";
                        n_huellas++;
                    }
                }
                device.SetDeviceData_Pull("user", data_inser);
                device.SetDeviceData_Pull("userauthorize", data_inser_aut);
                device.SetDeviceData_Pull("templatev10", data_inser_huellas);
                obj.put("data", n_usuarios);
                obj.put("huellas", n_huellas);
                obj.put("estado", "exito");
                SConsole.log("Sincronizar finalizado con exito.");
            }
            else {
                obj.put("data", false);
                obj.put("huellas", false);
                obj.put("estado", "error");
                obj.put("error", "desconectado");
            }


        }

    }
}
