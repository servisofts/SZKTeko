using System;


namespace capp
{
    internal class Program
    {
        static void Main(string[] args)
        {

            SZK device1 = new SZK("192.168.1.201", 4370);
            device1.connectar();
            device1.getDeviceInfo();
        }
    }
}
