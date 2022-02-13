using System;


namespace capp
{
    internal class Program
    {
        static void Main(string[] args)
        {

            // SZK device1 = new SZK("192.168.1.201", 4370);
            //device1.connectar();
            //device1.setUser();

            //SZKP device2 = new SZKP("192.168.1.201", 4370);
            //device2.connectar();
            // device2.SetDeviceParam_Pull("192.168.0.201"); //TODO
            //device2.GetDeviceParam_Pull();
            //device2.ControlDevice_Pull(1, 0, 4, 0); //TODO
            //device2.GetDeviceData_Pull("user", "CardNo\tPin\tPassword\tGroup\tStartTime\tEndTime");
            //device2.GetDeviceData_Pull("userauthorize", "Pin\tAuthorizeDoorId\tAuthorizeTimezoneId");
            //transaction
            //device2.GetDeviceData_Pull("transaction", "Pin\tCardno\tVerified\tDoorID\tEventType\tInOutState\tTime_second");

            //device2.SetDeviceData_Pull("userauthorize", "Pin=2\tAuthorizeDoorId=3\tAuthorizeTimezoneId=1");
            //device2.GetDeviceData_Pull();
            // device2.Desconectar();

            SSocket so = new SSocket("192.168.0.199", 9000);
     
            while (true)
            {
                String Comand = Console.ReadLine();
                so.Send(Comand);
            }
        }
    }
}   
