package MVC2.view;

import MVC2.model.MyModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NestedTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JTable masterTable;

    Object value = null;

    public NestedTableCellEditor(JTable masterTable) {
        this.masterTable = masterTable;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.value = value;
        if ( this.value instanceof Map ) {
            final LinkedHashMap<Object,Object> passed = (LinkedHashMap<Object,Object>) value;
            MyModel innerModel = new MyModel(passed);

            ComplexTable innerTable = new ComplexTable(innerModel);
//            JTable innerTable = new JTable();
//            innerTable.setModel(innerModel);
            innerTable.setDefaultRenderer(Object.class, new ColoredInnerTableCellRenderer());
            innerTable.setComponentPopupMenu(masterTable.getComponentPopupMenu());

            return new JScrollPane(innerTable);

//            JTable innerTable = new JTable();
//
//            AbstractTableModel innerTableModel = new AbstractTableModel() {
//                final Object[] keys = passed.keySet().toArray();
//                final Object[] values = passed.values().toArray();
//
//                public int getColumnCount() {
//                    return 2;
//                }
//
//                public int getRowCount() {
//                    return passed.size();
//                }
//
//                public Object getValueAt(int rowIndex, int columnIndex) {
//                    if ( columnIndex == 0 )
//                        return keys[rowIndex];
//                    else
//                        return values[rowIndex];
//                }
//
//                public boolean isCellEditable(int row, int col) {
//                    return true;
//                }
//
//                @Override
//                public String getColumnName(int column) {
//                    return TableConstants.TABLE_HEADER[column];
//                }
//
//                public void setValueAt(Object changedValue, int rowIndex, int columnIndex) {
//                    if ( columnIndex == 0 ) {
//                        Object aValue = values[rowIndex];
//                        passed.remove(keys[rowIndex]);
//                        keys[rowIndex] = changedValue;
//                        passed.put(changedValue, aValue);
//                    }
//                    else {
//System.out.println("changedValue 1 NEW : " + changedValue);
//System.out.println("keys.length : " + keys.length);
//System.out.println("values.length : " + values.length);
//                        values[rowIndex] = changedValue;
//                        passed.replace(keys[rowIndex], changedValue);
//                    }
//
//                    fireTableCellUpdated(rowIndex, columnIndex);
//                }
//            };
//
//            innerTable.setModel(innerTableModel);
//
//            return new JScrollPane(innerTable);
        }
        else if ( this.value instanceof java.util.List) {
            final java.util.List<Object> dataList = (List<Object>) value;
            final JTable innerTable = new JTable();
            innerTable.setTableHeader(null);

            AbstractTableModel innerTableModel = new AbstractTableModel() {
                public int getColumnCount() {
                    return 1;
                }

                public int getRowCount() {
                    return dataList.size();
                }

                public Object getValueAt(int rowIndex, int columnIndex) {
                    return dataList.get(rowIndex);
                }

                public boolean isCellEditable(int row, int col) {
                    return true;
                }

                public void setValueAt(Object value, int row, int col) {
                    dataList.set(row, value);
                    fireTableCellUpdated(row, col);
                }
            };

            innerTable.setModel(innerTableModel);
            innerTable.setDefaultRenderer(Object.class, new ColoredInnerTableCellRenderer());
            innerTable.setComponentPopupMenu(masterTable.getComponentPopupMenu());

            return new JScrollPane(innerTable);
        }
        else {
            return table.getDefaultEditor(value.getClass())
                    .getTableCellEditorComponent(table, value, isSelected, row, column);
        }
    }

    @Override
    public Object getCellEditorValue() {
        return this.value;
    }

}
