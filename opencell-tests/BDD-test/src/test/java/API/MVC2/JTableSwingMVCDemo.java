package API.MVC2;

import API.MVC2.view.MyView;

import javax.swing.*;

public class JTableSwingMVCDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void createAndShowGUI() {
        new MyView();
    }
}
