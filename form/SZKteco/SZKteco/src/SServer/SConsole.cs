using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SZKteco;

using System;

namespace SZKTeco
{
    internal class SConsole
    {
        private static void write(string msn, string color)
        {
            try
            {
                // Si existe una instancia de Form1, usa CambiarLabel
                if (Form1.INSTANCE != null)
                {
                    Form1.INSTANCE.CambiarLabel(msn, color);
                }
                else
                {
                    // Cambia el color solo para este mensaje
                    switch (color.ToLower())
                    {
                        case "green":
                            Console.ForegroundColor = ConsoleColor.Green;
                            break;
                        case "red":
                            Console.ForegroundColor = ConsoleColor.Red;
                            break;
                        case "yellow":
                            Console.ForegroundColor = ConsoleColor.Yellow;
                            break;
                        default:
                            Console.ResetColor();
                            break;
                    }

                    // Imprime el mensaje con el color especificado
                    Console.WriteLine(msn);

                    // Restablece el color después de imprimir
                    Console.ResetColor();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine($"Error en SConsole.write: {e.Message}");
            }
        }

        public static void log(string mensaje)
        {
            string formattedMessage = $"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ LOG ]: {mensaje}";
            write(formattedMessage, "green"); // Llama a write con el color "green"
        }

        public static void error(string mensaje)
        {
            string formattedMessage = $"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ ERROR ]: {mensaje}";
            write(formattedMessage, "red"); // Llama a write con el color "red"
        }

        public static void warning(string mensaje)
        {
            string formattedMessage = $"[ {DateTime.Now.ToString("HH:mm:ss.fff")} ][ WARNING ]: {mensaje}";
            write(formattedMessage, "yellow"); // Llama a write con el color "yellow"
        }
    }
}
