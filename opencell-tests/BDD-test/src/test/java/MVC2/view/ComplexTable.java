package MVC2.view;

import MVC2.model.MyModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class ComplexTable extends JTable {

    public ComplexTable(MyModel model) {
        this.setModel(model);

        TableColumnModel tcm = this.getColumnModel();
        NestedTableCellRenderer nestedTableRenderer = new NestedTableCellRenderer(this);
        NestedTableCellEditor nestedTableCellEditor = new NestedTableCellEditor(this);
        for (int it = 0; it < tcm.getColumnCount(); it++) {
            tcm.getColumn(it).setCellRenderer(nestedTableRenderer);
            tcm.getColumn(it).setCellEditor(nestedTableCellEditor);
        }

//        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        resizeColumnWidth(this);
        resizeRowHeight(this);
    }

    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 5; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1 , width);
            }
//            if (width > 300)
//                width = 300;

            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    public void resizeRowHeight(JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int height = 5; // Min height
            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                height = Math.max(height, comp.getPreferredSize().height + 1);
            }
            if (height > 180)
                height = 180;

            table.setRowHeight(row, height);
        }
    }

//    @Override
//    public TableCellRenderer getCellRenderer(int row, int column) {
//        Object data = this.getModel().getValueAt(row, column);
//        if (data instanceof List) {
//            return nestedTableRenderer;
//        }
//        else if (data instanceof JSONObject) {
//            return nestedTableRenderer;
//        }
//        else {
//            return super.getCellRenderer(row, column);
//        }
//    }

//    @Override
//    public TableCellEditor getCellEditor(int row, int column) {
//        Object data = this.getModel().getValueAt(row, column);
//        if (data instanceof List) {
//            return nestedTableEditor;
//        }
//        else if (data instanceof JSONObject) {
//            return nestedTableEditor;
//        }
//        else {
//            return super.getCellEditor(row, column);
//        }
//    }

//    @Override
//    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
//        Component component = super.prepareRenderer(renderer, row, column);
//        int rendererWidth = component.getPreferredSize().width;
//        TableColumn tableColumn = getColumnModel().getColumn(column);
//        tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
//System.out.println("At row : " + row + " has height : " + component.getPreferredSize().height );
//        return component;
//    }

}
