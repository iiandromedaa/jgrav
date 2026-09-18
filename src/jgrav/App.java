package jgrav;
import javax.swing.SwingUtilities;

import jgrav.gui.JGravGUI;

public class App {

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JGravGUI();
            }
        });
    }

}
