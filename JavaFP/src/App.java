import Config.Config;
import SocketCliente.SocketCliete;

public class App {
    public static void main(String[] args) throws Exception {
        SocketCliete.enableReconect(true);
        SocketCliete.Start(Config.getJSON("socket_client").getJSONObject("servicio"));
    }
}
