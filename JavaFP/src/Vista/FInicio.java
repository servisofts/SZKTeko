package Vista;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.json.JSONObject;

import Observer.Observer;
import Observer.ObserverListeners;

public class FInicio extends JFrame implements ObserverListeners {

    private JLabel lbl;

    public FInicio() {
        Observer.register(this);
        // setExtendedState(JFrame.MAXIMIZED_BOTH);
        // this.setUndecorated(true);
        this.setLayout(null);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setSize(600, 600);
        this.setLocation(200, 100);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addComponents();
    }

    public void addComponents() {
        lbl = new JLabel("hola");
        lbl.setBounds(0, 0, 600, 600);
        // lbl.setBounds(400, 400, 100, 100);
        this.add(lbl);

    }

    @Override
    public void update(JSONObject obj) {
        lbl.setText(obj.toString());
        // TODO Auto-generated method stub

    }
}
