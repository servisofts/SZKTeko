import Server.ServerSocketZkteco.ServerSocketZkteco;
import Servisofts.Servisofts;

public class App {
    
    public static void main(String[] args) {
        try {
            Servisofts.ManejadorCliente = ManejadorCliente::onMessage;
            Servisofts.Manejador = Manejador::onMessage;
            Servisofts.DEBUG = false;
            Servisofts.initialize();
            ServerSocketZkteco.Manejador = Manejador::onMessage;
            new ServerSocketZkteco(40032);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}