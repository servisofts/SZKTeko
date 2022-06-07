using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SZKTeco
{
    internal class Java
    {
        public static void jar()
        {
            //System.Diagnostics.Debugger.Launch();
            ProcessStartInfo psi = new ProcessStartInfo("java", " -jar \"C:\\SZKteco\\JavaFP.jar");
            //psi.WorkingDirectory = "C:\\Program Files\\Installed Shiny Swing jar app\\"; // Do not miss this line so you awesome Swing app will show default java icon and no images
            psi.CreateNoWindow = true;
            psi.UseShellExecute = false;
            Process proc;
            if ((proc = Process.Start(psi)) == null)
            {
                throw new InvalidOperationException("??");
            }
        }
    }
}
