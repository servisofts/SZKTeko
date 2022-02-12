using System;

using zkemkeeper;

namespace capp
{
    internal class Program
    {
        static void Main(string[] args)
        {
        
            CZKEMClass axCZKEM1 = new CZKEMClass();
            String ip = "192.168.1.201";
            int port = 10;
            String commKey = "0";
            int idwErrorCode = 0;

            axCZKEM1.SetCommPassword(Convert.ToInt32(commKey));

            axCZKEM1.Connect_Net(ip, Convert.ToInt32(port));

        }
    }
}
