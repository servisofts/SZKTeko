using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using SZKTeco;

namespace SZKteco
{
    internal class Service
    {
        public static bool isRun;
        public static Thread t1;

        public Service()
        {
            isRun = false;
        }

        public void start()
        {
            t1 = new Thread(new ThreadStart(this.hilo));
            isRun = true;
            t1.Start();
        }

        public void stop()
        {
            isRun = false;
        }


        public void hilo()
        {
            while (isRun)
            {
                SSocket.getInstance();
                SFP.getInstance();
                Thread.Sleep(3000);
            }
        }
    }
}
