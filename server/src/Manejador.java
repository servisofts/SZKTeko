import Servisofts.SConsole;
import org.json.JSONObject;

import Component.Dispositivo;
import Component.PuntoVenta;
import Component.TipoDispositivo;
import Server.SSSAbstract.SSSessionAbstract;

public class Manejador {
    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        if (session != null) {
            SConsole.log(session.getIdSession(), "\t|\t", obj.getString("component"), obj.getString("type"));
        }
        if (obj.isNull("component")) {
            return;
        }
        switch (obj.getString("component")) {
            case PuntoVenta.COMPONENT:
                PuntoVenta.onMessage(obj, session);
                break;
            case Dispositivo.COMPONENT:
                Dispositivo.onMessage(obj, session);
                break;
            case TipoDispositivo.COMPONENT:
                TipoDispositivo.onMessage(obj, session);
                break;
        }
    }
}