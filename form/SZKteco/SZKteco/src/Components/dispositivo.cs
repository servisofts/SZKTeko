using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
{
    internal class dispositivo
    {
        public const string COMPONENT = "dispositivo";

        public static void onMessage(SJSon obj, SSocket session)
        {
           switch (obj.getString("type"))
           {
               case "getAll":
                    getAll(obj, session);
                   break;
                case "conectar":
                    conectar(obj, session);
                    break;
                case "open":
                    open(obj, session);
                    break;
                case "getUsuarios":
                    getUsuarios(obj, session);
                    break;
                case "changeIp":
                    changeIp(obj, session);
                    break;
                case "getDataTable":
                    getDataTable(obj, session);
                    break;
                case "deleteDataTable":
                    deleteDataTable(obj, session);
                    break;
                case "registroDataTable":
                    registroDataTable(obj, session);
                    break;

                case "sincronizarMolinete":
                    registroDataTable(obj, session);
                    break;
            }
        }

        private static void sincronizarMolinete(SJSon obj, SSocket session)
        {
            obj.put("noSend", false);
            SConsole.log("Iniciando sincronizacion");
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            if (szkp == null)
            {
                obj.put("estado", "error");
                obj.put("data", "");
                obj.put("dataRemove", "");
                obj.put("huellas_nuevos", "");
                obj.put("huellas_encontrados", "");
                SConsole.log("dispositivo no conectado");
                return;
            }

            //SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));

            int cantDoor = int.Parse(szkp.geDeviceParam("ReaderCount"));
            JArray usuarios = obj.getArray("data");
            JArray usuarios_remove = obj.getArray("dataRemove");
            JArray huellas_nuevos = obj.getArray("huellas_nuevos");
            JArray huellas_encontrados = obj.getArray("huellas_encontrados");
            string user_to_remove = "";
            for (int i = 0; i < usuarios_remove.Count; i++)
            {
                int Pin = (int)usuarios_remove[i];
                user_to_remove += string.Format("Pin={0}\r\n", Pin);
            }
            if (usuarios_remove.Count > 0)
            {
                szkp.DeleteDeviceData_Pull("user", user_to_remove);
                szkp.DeleteDeviceData_Pull("userauthorize", user_to_remove);
                szkp.DeleteDeviceData_Pull("templatev10", user_to_remove);
                SConsole.log("Se eliminaron " + usuarios_remove.Count.ToString() + " users");
            }
            string user_to_insert = "";
            string userauthorize_to_insert = "";
            for (int j = 0; j < usuarios.Count; j++)
            {
                int Pin = (int)usuarios[j];
                user_to_insert += string.Format("CardNo=0\tPin={0}\tName=a\tPassword=\tGroup=0\tStartTime=0\tEndTime=0\r\n", Pin);
                for (int d = 1; d <= cantDoor; d++)
                {
                    userauthorize_to_insert += string.Format("Pin={0}\tAuthorizeDoorId={1}\tAuthorizeTimezoneId=1\r\n", Pin, d);
                }
            }
            string templatev10_to_insert = "";
            for (int k = 0; k < huellas_nuevos.Count; k++)
            {
                JObject jobject = (JObject)huellas_nuevos[k];
                int Pin = (int)jobject.GetValue("pin");
                int id_huella = (int)jobject.GetValue("codigo");
                string huella_str = (string)jobject.GetValue("huella");
                templatev10_to_insert += string.Format("Pin={0}\tFingerID={1}\tValid=1\tTemplate={2}\tSize={3}\r\n", new object[]
                {
                    Pin,
                    id_huella,
                    huella_str,
                    huella_str.Length
                });
            }
            for (int l = 0; l < huellas_encontrados.Count; l++)
            {
                JObject jobject2 = (JObject)huellas_encontrados[l];
                int Pin = (int)jobject2.GetValue("pin");
                int id_huella2 = (int)jobject2.GetValue("codigo");
                string huella_str = (string)jobject2.GetValue("huella");
                templatev10_to_insert += string.Format("Pin={0}\tFingerID={1}\tValid=1\tTemplate={2}\tSize={3}\r\n", new object[]
                {
                    Pin,
                    id_huella2,
                    huella_str,
                    huella_str.Length
                });
            }
            szkp.SetDeviceData_Pull("user", user_to_insert, "");
            szkp.SetDeviceData_Pull("userauthorize", userauthorize_to_insert, "");
            szkp.SetDeviceData_Pull("templatev10", templatev10_to_insert, "");
            SConsole.log("Se agregaron " + usuarios.Count.ToString() + " users");
            SConsole.log("Se agregaron " + huellas_nuevos.Count.ToString() + " huellas");
            SConsole.log("Se actualizaron " + huellas_encontrados.Count.ToString() + " huellas");
            obj.put("estado", "exito");
            obj.put("data", "");
            obj.put("dataRemove", "");
            obj.put("huellas_nuevos", "");
            obj.put("huellas_encontrados", "");
        }

        private static void getAll(SJSon obj, SSocket session){

            if (obj.getString("estado") == "exito")
            {
                SJSon data = obj.getSJSonObject("data");
              //  State.dispositivo.put("data", data);
                SConsole.log("Todos los dispositivos");
                foreach (string key in data.keys())
                {
                    Dispositivos.create_SZKP(data.getSJSonObject(key)).connectar();
                }
            }

        }
        private static void conectar(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("data"));
            if (szkp == null) return;
            szkp.connectar();
        }

        private static void open(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            if (szkp == null) return;

            SJSon param = obj.getSJSonObject("parameters");
              szkp.ControlDevice_Pull(param.getInt("operID"), param.getInt("doorOrAuxoutID"), param.getInt("outputAddrType"), param.getInt("doorAction"), obj.getString("key_usuario"));
            obj.put("noSend", true);
        }
        
        private static void changeIp(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            if (szkp == null) return;

            szkp.ModifIP(obj.getSJSonObject("dispositivo").getString("ip"),obj.getSJSonObject("dispositivo").getString("mac"), obj.getSJSonObject("dispositivo").getString("gateway"));
        }
        private static void getUsuarios(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            if (szkp == null) return;

            JArray lista = szkp.GetDeviceData_Pull("user", "CardNo\tPin\tPassword\tGroup\tStartTime\tEndTime");
            obj.put("estado", "exito");
            obj.put("data", lista);
            SSocket.Send(obj.ToString());
        }
        private static void getDataTable(SJSon obj, SSocket session)
        {
           // System.Diagnostics.Debugger.Launch();
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            if (szkp == null) return;

            JArray lista = szkp.GetDeviceData_Pull(obj.getSJSonObject("table").getString("name"), obj.getSJSonObject("table").getString("header"));
            //SConsole.log(lista.ToString());
            SConsole.log(obj.getSJSonObject("table").ToString());
            obj.put("estado", "exito");
            obj.put("data", lista);
            //SSocket.Send(obj.ToString());
            obj.put("noSend", false);
        } 
        private static void deleteDataTable(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            if (szkp == null) return;

            szkp.DeleteDeviceData_Pull(obj.getSJSonObject("table").getString("name"), obj.getSJSonObject("table").getString("header"));
           // SConsole.log("delete data table");
            obj.put("estado", "exito");
            SSocket.Send(obj.ToString());
        }
        private static void registroDataTable(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            if (szkp == null) return;

            szkp.SetDeviceData_Pull(obj.getSJSonObject("table").getString("name"), obj.getSJSonObject("table").getString("header"));
            SConsole.log("insert data table");
            obj.put("estado", "exito");
            SSocket.Send(obj.ToString());
        }
    }
}
