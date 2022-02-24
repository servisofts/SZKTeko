using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace SZKTeco
{
    public partial class Service1 : ServiceBase
    {
        public Service1()
        {
            InitializeComponent();
        }

        public static Boolean isRun = true;
        protected override void OnStart(string[] args)
        {
            System.Diagnostics.Debugger.Launch();
            Thread t1 = new Thread(new ThreadStart(this.hilo));
            isRun=true;
            t1.Start();
       
        }

        public void hilo() {
            while (isRun)
            {
                SConsole.log("Hola");
                Thread.Sleep(1000);
            }
        }
        protected override void OnStop()
        {
            isRun = false;
        }
    }
}
