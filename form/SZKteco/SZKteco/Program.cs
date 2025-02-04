using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Diagnostics;

namespace SZKteco
{
    internal static class Program
    {
        /// <summary>
        /// Punto de entrada principal para la aplicación.
        /// </summary>
        [STAThread]
        static void Main()
        {

          // Obtiene el nombre del proceso de esta aplicación
        string processName = Process.GetCurrentProcess().ProcessName;

        // Busca todas las instancias del proceso
        var runningProcesses = Process.GetProcessesByName(processName);

        // Si ya hay otra instancia en ejecución, la cierra
        foreach (var process in runningProcesses)
        {
            if (process.Id != Process.GetCurrentProcess().Id) // Evita cerrar el proceso actual
            {
                process.Kill(); // Cierra la instancia anterior
                process.WaitForExit(); // Espera a que se cierre completamente
            }
        }

// se inicio la aplicacion



            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            Application.Run(new Form1());

        }
    }
}
