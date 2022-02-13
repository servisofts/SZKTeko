using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace capp
{
    internal class SSocket
    {
        private String ip;
        private int port;
        private TcpClient socket;
        private NetworkStream stream;
        private bool estado;
        public SSocket(String ip, int port) { 
            this.ip = ip;
            this.port = port;
            this.Connectar();
        }

        public void Connectar() {
              this.socket = new TcpClient();
             this.socket.Connect(this.ip, this.port);
            Console.WriteLine($"Session Iniciada");
            this.estado = true;
            this.stream = this.socket.GetStream();
            Thread t1 = new Thread(new ThreadStart(this.Receive));
            t1.Start();
        }

        public void Send(String msn) { 
            
            byte[] data = Encoding.UTF8.GetBytes(msn+"\n");
            this.stream.Write(data, 0, data.Length);
            this.stream.Flush();
        }
        public void onClose()
        {
            Console.WriteLine($"Session closed");
            this.estado = false;
        }
        public void Receive()
        {
            while (this.estado == true) {
                Byte[] bytesReceived = new Byte[(int)this.socket.ReceiveBufferSize];
                this.stream.Read(bytesReceived, 0, (int)this.socket.ReceiveBufferSize);
                String data = Encoding.UTF8.GetString(bytesReceived);
                if (data.Length <=0 ) {
                    this.estado = false;
                    this.onClose();
                    return;
                }
                Console.WriteLine($"SERVER : {data}");
            }
            //int bytes= this.socket.Receive(bytesReceived);

        }
    }
}
