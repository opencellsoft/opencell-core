package API.MVC2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class SaveController implements ActionListener {

    private final AbstractTableModel model;
    private final JTable table;
    ObjectMapper mapper = new ObjectMapper();

    public SaveController(JTable table, AbstractTableModel model) {
        super();
        this.table = table;
        this.model = model;
    }

//    public void rendererJTable() {
//        TableColumnModel tcm = this.table.getColumnModel();
//        for (int it = 0; it < tcm.getColumnCount(); it++){
//            tcm.getColumn(it).setCellRenderer(jTableCellRenderer);
//        }
//    }
//
//    TableCellRenderer jTableCellRenderer = new TableCellRenderer() {
//        /* These are necessary variables to store the row's height */
//        private int minHeight = -1;
//        private int currHeight = -1;
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            if (!(value instanceof Map) ) {
//                return table.getDefaultRenderer(value.getClass())
//                        .getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
//            }
//            else {
//                final Map<?,?> passed = (Map<?, ?>) value;
//                /* We calculate the row's height to display data
//                 *  This is not complete and has some bugs that
//                 *  will be analyzed in further articles */
//                if (minHeight == -1) {
//                    minHeight = table.getRowHeight();
//                }
//                if (currHeight != passed.size()*minHeight) {
//                    currHeight = passed.size() * minHeight;
//                    table.setRowHeight(row,currHeight);
//                }
//                DefaultTableModel aModel = new DefaultTableModel(new Object[] { "Key", "Value" }, 0);
//
//                for (Map.Entry<?,?> entry : passed.entrySet()) {
//                    aModel.addRow(new Object[] { entry.getKey(), entry.getValue() });
//                }
//
//                /* We create the table that will hold the multivalue
//                 * fields and that will be embedded in the main table */
//                return new JTable(aModel);
//            }
//        }
//    };

    /*
     * Add an action event to the table containing Json data retrieved from the Json file.
     * With this function, we can modify directly the Json data in the table and
     * this modification is taken into account in the original file
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        Map<Object, Object> data = new LinkedHashMap<>();

        for (int count = 0; count < model.getRowCount(); count++) {
            data.put(model.getValueAt(count, 0), model.getValueAt(count, 1));
        }

        try {
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(
                    "C:\\IdeaProjects\\opencell-tests\\BDD-test\\src\\test\\resources\\CRUD\\seller\\UpdateSeller.json")
                    .toFile(), data);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
