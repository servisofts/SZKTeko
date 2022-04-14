using Microsoft.Win32;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration.Install;
using System.Linq;
using System.Threading.Tasks;

namespace SZKTeco
{
    [RunInstaller(true)]
    public partial class ProjectInstaller : System.Configuration.Install.Installer
    {
        public ProjectInstaller()
        {
            InitializeComponent();
            // Grab the subkey to our service
            RegistryKey ckey = Registry.LocalMachine.OpenSubKey(
              @"SYSTEM\CurrentControlSet\Services\SZKTeco", true);
            // Good to always do error checking!
            if (ckey != null)
            {
                // Ok now lets make sure the "Type" value is there, 
                //and then do our bitwise operation on it.
                if (ckey.GetValue("Type") != null)
                {
                    ckey.SetValue("Type", ((int)ckey.GetValue("Type") | 256));
                }
            }
        }

        private void serviceProcessInstaller1_AfterInstall(object sender, InstallEventArgs e)
        {

        }
    }
}
