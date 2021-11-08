package API.MVC2.view;

import API.MVC2.model.MyModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class NestedTableCellRenderer implements TableCellRenderer {

    private final JTable masterTable;

    public NestedTableCellRenderer(JTable masterTable) {
        this.masterTable = masterTable;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if ( value instanceof List ) {
            List<?> valueList = (List<?>) value;

            JTable innerTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
                }
            };
            ((DefaultTableModel) innerTable.getModel()).addColumn("", new Vector<>(valueList));
            innerTable.setDefaultRenderer(Object.class, new ColoredInnerTableCellRenderer());
            innerTable.setComponentPopupMenu(masterTable.getComponentPopupMenu());
            innerTable.setTableHeader(null);

            return new JScrollPane(innerTable);
        }
        else if ( value instanceof Map ) {
            final LinkedHashMap<Object,Object> passed = (LinkedHashMap<Object, Object>) value;
            MyModel innerModel = new MyModel(passed);
            ComplexTable innerTable = new ComplexTable(innerModel);

            innerTable.setDefaultRenderer(Object.class, new ColoredInnerTableCellRenderer());
            innerTable.setComponentPopupMenu(masterTable.getComponentPopupMenu());

            return new JScrollPane(innerTable);
        }
//        else if ( value instanceof JSONObject ) {
//System.out.println("value instanceof JSONObject : " + value);
//            final JSONObject jsonObj = (JSONObject) value;
//
//            MyModel aModel = new MyModel();
//            JTable innerTable = new JTable(aModel) {
//                @Override
//                public boolean isCellEditable(int row, int column) {
//                    return true;
//                }
//            };
//
//            innerTable.setDefaultRenderer(Object.class, new ColoredInnerTableCellRenderer());
//            innerTable.setComponentPopupMenu(masterTable.getComponentPopupMenu());
//
//            for (Object key : jsonObj.keySet()) {
//                aModel.addRow(new Object[] { key, jsonObj.get((String) key) });
//            }
//
//            return new JScrollPane(innerTable);
//        }
        else {
            return table.getDefaultRenderer(value.getClass())
                    .getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
        }
    }

}

class ColoredInnerTableCellRenderer extends DefaultTableCellRenderer {
    private final int[] colorsArray = { 0xbbbbbb, 0xff0000, 0xb28959, 0x318c23, 0xc200f2, 0xbf0000, 0x735839 };

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(new Color(colorsArray[row % colorsArray.length]));

        return c;
    }

}
