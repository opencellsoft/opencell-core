package API.MVC2.controllers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteController implements ActionListener {

    private final DefaultTableModel model;
    private final JTable table;

    public DeleteController(JTable table, DefaultTableModel model) {
        super();
        this.table = table;
        this.model = model;
    }

    /*
     * Add remove action to the button Remove to add a new row after the chosen row
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (table.getSelectedRow() != -1) {
            // add a row below the selected row from the model
            model.removeRow(table.getSelectedRow());
        }
    }

}
