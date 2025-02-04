using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
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

        private static readonly HttpClient Client = new HttpClient();

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

        private static void init(SJSon obj, SSocket session){

            string webhookUrl = "https://discord.com/api/webhooks/1332018978289356880/-lJEg1lIwH9joSQx5jGdVl9nRslvwxCwOyymCAvKiaMMSaRG5RlkXtK6JAfNiqIpOKB9";
            // El mensaje que deseas enviar
            string message = ($"Punto de venta ({SConfig.get().getString("key_punto_venta")})");

            string message123 = $"Punto de venta ({SConfig.get().getString("key_punto_venta")})";

            obj.put("component", "punto_venta");
            obj.put("type", "identificarse");
            obj.put("noSend", false);
            obj.put("key_punto_venta",SConfig.get().getString("key_punto_venta"));


            SConsole.log(message);
            SendDiscordMessage(webhookUrl, message123);


        }
    }
}
