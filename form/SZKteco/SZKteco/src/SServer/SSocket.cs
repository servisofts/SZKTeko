using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Newtonsoft.Json.Linq;
using System.Text.RegularExpressions;
using SZKteco;

namespace SZKTeco
{
    internal class SSocket
    {

        private static SSocket INSTANCE;
        public static SSocket getInstance() {
            if (INSTANCE == null) { 
                INSTANCE = new SSocket(SConfig.get().getString("ip"), SConfig.get().getInt("puerto"));
            }else {
                if (!INSTANCE.isConnect())
                {
                    SConsole.warning("SOCKET DESCONES");
                   // INSTANCE = new SSocket(SConfig.get().getString("ip"), SConfig.get().getInt("puerto"));
                }
            }
            
            return INSTANCE;
        }

        public static SSocket reset()
        {
            if (INSTANCE != null)
            {
                INSTANCE.socket.Close();
            }
            return INSTANCE;
        }
        public static void Send(String msn)
        {
            INSTANCE._Send(msn);
        }

        private String ip;
        private int port;
        private TcpClient socket;
        private NetworkStream stream;
        private bool estado;
        private Thread t1;
        public SSocket(String ip, int port) { 
            this.ip = ip;
            this.port = port;
          
            this.Connectar();
        }

        public bool isConnect()
        {
            if (socket == null) return false;
            return socket.Connected;
        }

        public void Connectar() {
              this.socket = new TcpClient();
            try
            {
                this.socket.Connect(this.ip, this.port);
                SConsole.log($"Conectado con el servidor ({ip}:{port})");
                this.stream = this.socket.GetStream();
                this.estado = true;
                //  t1.Interrupt();
               // if (t1 == null)
                //{
                    t1 = new Thread(new ThreadStart(this.Receive));
                    t1.Start();
                //}
            }
            catch (Exception e) {
                SConsole.error(e.ToString());
                this.onClose();
            }
   
        }

        public void _Send(String msn) {
            //SConsole.log("ENVIANDO MENSAJE >>>" + msn);
            byte[] data = Encoding.UTF8.GetBytes(msn+"\n");
            this.stream.Write(data, 0, data.Length);
            this.stream.Flush();
        }
        int intents = 0;
        public void onClose()
        {

            SConsole.warning($"Server session closed!");
            this.estado = false;
            t1 = null;
            if (this.socket != null)
            {   
                this.socket.Close();
            }
            if (!Service.isRun)
            {
                return;
            }
            INSTANCE = null;
            //Thread.Sleep(3000);
           // this.Connectar();
           // INSTANCE = null;
        }

        public void Receive()
        {
            string mensaje="";
            while (this.estado && Service.isRun) {
                try
                {
                   // System.Diagnostics.Debugger.Launch();
                    Byte[] bytesReceived = new Byte[(int)this.socket.ReceiveBufferSize];
                    this.stream.Read(bytesReceived, 0, (int)this.socket.ReceiveBufferSize);
                    String data = Encoding.UTF8.GetString(bytesReceived);
                    data = data.Replace("\0", string.Empty);

                    if (data.Length <= 0) {
                        this.onClose();
                    }
                    try
                    {

                        String[] text = Regex.Split(data, @"---SSkey---");
                        mensaje = mensaje + text[0];

                        if (text.Length >= 2)
                        {
                            SJSon obj = new SJSon(mensaje);
                            mensaje = "";

                            //Console.WriteLine("\nMSN_IN > ");
                            //Console.WriteLine("\t"+obj);
                            //Console.WriteLine("");
                            obj.put("noSend", true);
                            Manejador.onMessage(obj, this);

                            if (obj.getBool("noSend") == false)
                            {
                                this._Send(obj.ToString());
                            }
                        }
                        else {
                           // System.Diagnostics.Debugger.Launch();
                        }
                    }
                    catch (Exception e) { 
                        SConsole.log("[ onMessagge ] "+ mensaje);
                        SConsole.error("[ onMessagge ] " + e.ToString());
                        mensaje = "";
                        //  SConsole.error(ex.Message);
                       this.onClose();
                        //return;
                    }
                }
                catch (Exception ex) {
                    SConsole.log("[ onMessagge ] " + mensaje);
                    mensaje = "";

                    //  SConsole.error(ex.Message);
                    this.onClose();
                    return;
                }
            

            }
        }
    }
}
