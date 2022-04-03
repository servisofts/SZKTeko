package Observer;

import java.util.ArrayList;

import org.json.JSONObject;

public class Observer {
    private static ArrayList<ObserverListeners> listeners;

    public static ArrayList<ObserverListeners> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        return listeners;
    }

    public static void register(ObserverListeners obj) {
        getListeners().add(obj);
    }

    public static void remove(ObserverListeners obj) {
        getListeners().remove(obj);
    }

    public static void notify(JSONObject obj) {
        for (ObserverListeners listener : getListeners()) {
            listener.update(obj);
        }
    }

}
