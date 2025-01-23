using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
{
    internal class dispositivo
    {
        public const string COMPONENT = "dispositivo";
        private static readonly HttpClient Client = new HttpClient();

        public static void onMessage(SJSon obj, SSocket session)
        {
           switch (obj.getString("type"))
           {
               case "getAll": // ok 1
                    getAll(obj, session);
                   break;
                case "conectar": // ok 1
                    conectar(obj, session);
                    break;
                case "open": // ok 1
                    open(obj, session);
                    break;
                case "getUsuarios": // ok 1
                    getUsuarios(obj, session);
                    break;
                case "changeIp": // ok 1
                    changeIp(obj, session);
                    break;
                case "getDataTable": // ok 1
                    getDataTable(obj, session);
                    break;
                case "deleteDataTable": // ok 1
                    deleteDataTable(obj, session);
                    break;
                case "registroDataTable": // ok 1
                    registroDataTable(obj, session);
                    break;
                case "sincronizarMolinete": // ok 1
                    sincronizarMolinete(obj, session);
                    break;
                case "getUsers":
                    getUsers(obj, session);
                    break;
                case "getDeviceParam":
                    getDeviceParam(obj, session);
                    break;
                case "limpiarLog":
                    limpiarLog(obj, session);
                    break;
                case "testConnection":
                    testConnection(obj, session);
                    break;
                case "sincronizarLog":
                    sincronizarLog(obj, session);
                    break;
            }
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


        private static void getDeviceParam(SJSon obj, SSocket session)
		{
			SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
			if (szkp == null)
			{
				return;
			}
			string vari = szkp.geDeviceParam(obj.getSJSonObject("table").getString("header"));
			SConsole.log(vari);
			SConsole.log(obj.getSJSonObject("table").ToString());
			obj.put("estado", "exito");
			obj.put("data", vari);
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

        private static void getUsers(SJSon obj, SSocket session)
		{
			obj.put("noSend", false);
			SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
			if (szkp == null)
			{
				obj.put("estado", "error");
				return;
			}
			JArray lista = szkp.GetUsersPin();
			obj.put("estado", "exito");
			obj.put("data", lista);
		}
        // Token: 0x06000009 RID: 9 RVA: 0x000025A0 File Offset: 0x000007A0


        private static void limpiarLog(SJSon obj, SSocket session)
		{
			obj.put("noSend", false);
			SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
			if (szkp == null)
			{
				obj.put("estado", "error");
				obj.put("error", "no conect to device");
				return;
			}
			if (!szkp.isConnect())
			{
				obj.put("estado", "error");
				obj.put("error", "no conect to device");
				return;
			}
			szkp.DeleteDeviceData_Pull("transaction", "");
			obj.put("estado", "exito");
		}

        private static void testConnection(SJSon obj, SSocket session)
		{
			obj.put("noSend", false);
			SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
			if (szkp == null)
			{
				obj.put("estado", "error");
				obj.put("error", "no conect to device szkp is null");
				return;
			}
			if (!szkp.isConnect())
			{
				obj.put("estado", "error");
				obj.put("error", "no conect to device skzp.isConnect is false");
				return;
			}
			obj.put("estado", "exito");
		}
        public static void SendDiscordMessage(string webhookUrl, string message)
        {
            var jsonPayload = $"{{\"content\": \"{message}\"}}"; // Cuerpo del mensaje en formato JSON
            var content = new StringContent(jsonPayload, Encoding.UTF8, "application/json");

            try
            {
                // Hacer la solicitud POST de forma sincrónica
                var response = Client.PostAsync(webhookUrl, content).GetAwaiter().GetResult();
                response.EnsureSuccessStatusCode(); // Asegurar que la respuesta sea correcta (código 2xx)
                Console.WriteLine($"Mensaje enviado, código de respuesta: {response.StatusCode}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al enviar el mensaje: {ex.Message}");
            }
        }

        private static void sincronizarLog(SJSon obj, SSocket session)
		{
			obj.put("noSend", false);
			SZKP szkp = Dispositivos.create_SZKP(obj.getSJSonObject("dispositivo"));
			if (szkp == null)
			{
				obj.put("estado", "error");
				obj.put("error", "no conect to device");
				return;
			}
			if (szkp.isConnect())
			{
				JArray lista = szkp.GetDeviceData_Pull("transaction", "Pin\tCardno\tVerified\tDoorID\tEventType\tInOutState\tTime_second");
				obj.put("data", lista);
				obj.put("estado", "exito");
				return;
			}
			obj.put("estado", "error");
			obj.put("error", "no conect to device");
		}

        private static void sincronizarMolinete(SJSon obj, SSocket session)
        {
            obj.put("noSend", false);
            string webhookUrl = "https://discord.com/api/webhooks/1332018978289356880/-lJEg1lIwH9joSQx5jGdVl9nRslvwxCwOyymCAvKiaMMSaRG5RlkXtK6JAfNiqIpOKB9";

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
            JArray huellas_eliminadas = obj.getArray("huellas_eliminadas");

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

            string huellas_to_remove = "";
            for(int i = 0; i< huellas_eliminadas.Count; i++)
            {
                JObject jobject = (JObject)huellas_eliminadas[i];
                int Pin = (int)jobject.GetValue("pin");
                int id_huella = (int)jobject.GetValue("codigo");
                huellas_to_remove += string.Format("Pin={0}\tFingerID={1}\r\n", new object[]
                {
                    Pin,
                    id_huella,
                });
            }

            if (huellas_eliminadas.Count > 0)
            {
                szkp.DeleteDeviceData_Pull("templatev10", huellas_to_remove);
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
           // string message1 =  ("Usuarios Nuevos" + usuarios.Count.ToString() + " users");
//            string message2 = ("Se registraron " + huellas_nuevos.Count.ToString() + " huellas" { huellas_nuevos.Count.ToString()>0 ? "✔":"❌❌" });
            //string message2 = $"Se registraron {huellas_nuevos.Count} huellas {(huellas_nuevos.Count > 0 ? "✔" : "❌❌")}";
            //string message3 = ("Se actualizaron " + huellas_encontrados.Count.ToString() + " huellas");
            //string message4 = ("Se eliminaron " + huellas_eliminadas.Count.ToString() + " huellas");

            /*string message1 = $"Usuarios Nuevos {usuarios.Count} users {(usuarios.Count > 0 ? "✔" : "❌ ✅")}";
            string message2 = $"Se registraron {huellas_nuevos.Count} huellas {(huellas_nuevos.Count > 0 ? "✔" : "❌")}";
            string message3 = $"Se actualizaron {huellas_encontrados.Count} huellas {(huellas_encontrados.Count > 0 ? "✔" : "❌❌")}";
            string message4 = $"Se eliminaron {huellas_eliminadas.Count} huellas {(huellas_eliminadas.Count > 0 ? "✔" : "❌❌")}";
            */

            string message1 = $"{(usuarios.Count > 0 ? "[✅]" : "[✅❌]")} Usuarios Nuevos {usuarios.Count} users";
            string message2 = $"{(huellas_nuevos.Count > 0 ? "[✅]" : "[❌]")} Se registraron {huellas_nuevos.Count} huellas";
            string message3 = $"{(huellas_encontrados.Count > 0 ? "[✅]" : "[❌]")} Se actualizaron {huellas_encontrados.Count} huellas";
            string message4 = $"{(huellas_eliminadas.Count > 0 ? "[✅]" : "[❌]")} Se eliminaron {huellas_eliminadas.Count} huellas";


            szkp.SetDeviceData_Pull("templatev10", templatev10_to_insert, "");

 
            SConsole.error(message1);
            SConsole.warning(message2);
            SConsole.log(message3);
            SConsole.error(message4);

            SendDiscordMessage(webhookUrl, message1);
            SendDiscordMessage(webhookUrl, message2);
            SendDiscordMessage(webhookUrl, message3);
            SendDiscordMessage(webhookUrl, message4);

            obj.put("estado", "exito");
            obj.put("data", "");
            obj.put("dataRemove", "");
            obj.put("huellas_nuevos", "");
            obj.put("huellas_encontrados", "");
        }

    }
}
