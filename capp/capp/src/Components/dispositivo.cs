using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace capp
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
            }
        }


        private static void getAll(SJSon obj, SSocket session){

            if (obj.getString("estado") == "exito")
            {
                SJSon data = obj.getSJSonObject("data");
                State.dispositivo.put("data", data);
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
            szkp.connectar();
        }

        private static void open(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            SJSon param = obj.getSJSonObject("parameters");
              szkp.ControlDevice_Pull(param.getInt("operID"), param.getInt("doorOrAuxoutID"), param.getInt("outputAddrType"), param.getInt("doorAction"));
        }
        
        private static void changeIp(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            szkp.ModifIP(obj.getSJSonObject("dispositivo").getString("ip"),obj.getSJSonObject("dispositivo").getString("mac"), obj.getSJSonObject("dispositivo").getString("gateway"));
        }
        private static void getUsuarios(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
           JArray lista= szkp.GetDeviceData_Pull("user", "CardNo\tPin\tPassword\tGroup\tStartTime\tEndTime");
            obj.put("estado", "exito");
            obj.put("data", lista);
            SSocket.Send(obj.ToString());
        }
        private static void getDataTable(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            JArray lista= szkp.GetDeviceData_Pull(obj.getSJSonObject("table").getString("name"), obj.getSJSonObject("table").getString("header"));
            //SConsole.log(lista.ToString());
            SConsole.log(obj.getSJSonObject("table").ToString());
            obj.put("estado", "exito");
            obj.put("data", lista);
            SSocket.Send(obj.ToString());
        } 
        private static void deleteDataTable(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
            szkp.DeleteDeviceData_Pull(obj.getSJSonObject("table").getString("name"), obj.getSJSonObject("table").getString("header"));
            SConsole.log("delete data table");
            obj.put("estado", "exito");
            SSocket.Send(obj.ToString());
        }
        private static void registroDataTable(SJSon obj, SSocket session)
        {
            SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
             szkp.SetDeviceData_Pull(obj.getSJSonObject("table").getString("name"), obj.getSJSonObject("table").getString("header"));
            SConsole.log("insert data table");
            obj.put("estado", "exito");
            SSocket.Send(obj.ToString());
        }
    }
}
