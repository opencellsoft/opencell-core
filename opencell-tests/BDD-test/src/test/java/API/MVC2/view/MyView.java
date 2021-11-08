package API.MVC2.view;

import API.MVC2.controllers.SaveController;
import API.MVC2.model.MyModel;
import functional.SQLite.SQLiteConnection;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.LinkedHashMap;

public class MyView {

    private LinkedHashMap<Object,Object> prepareData() {
        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

        try
        {
            // Read Json data from database and convert to JSONObject
            String jsonString = SQLiteConnection.selectJsonTable("Seller", "UpdateSeller", "TableUpdateSeller");

            map = new ObjectMapper().readValue(jsonString, LinkedHashMap.class);
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return map;
    }

    public MyView() {
        // Create views Swing UI components : a save button, an add row button, a remove row button
        JButton saveButton = new JButton("Save data");

        // Create table model
        MyModel model = new MyModel(prepareData());
        ComplexTable complexTable = new ComplexTable(model);

        // Create save button controller and add listener to save button
        SaveController saveController = new SaveController(complexTable, model);
        saveButton.addActionListener(saveController);

        // Set the view layout
        JPanel ctrlPanel = new JPanel();
        ctrlPanel.add(saveButton);

        // ScrollPane
        JScrollPane tableScrollPane = new JScrollPane(complexTable);    // Create Scroll in Jtable
        tableScrollPane.setPreferredSize(new Dimension(1000, 500));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Editing Json data", TitledBorder.CENTER, TitledBorder.TOP));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ctrlPanel, tableScrollPane);
        splitPane.setDividerLocation(35);
        splitPane.setEnabled(false);

        // Display it all in a scrolling window and make the window appear
        JFrame frame = new JFrame("JTable filled from Json");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(splitPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
