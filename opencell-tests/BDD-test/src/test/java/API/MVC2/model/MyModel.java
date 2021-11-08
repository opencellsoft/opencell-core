package API.MVC2.model;

import API.MVC2.constants.TableConstants;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedHashMap;

public class MyModel extends AbstractTableModel {

    private final LinkedHashMap<Object,Object> map;
    final Object[] keys;
    final Object[] values;

    public MyModel(LinkedHashMap<Object,Object> map) {
        this.map = map;
        this.keys = map.keySet().toArray();
        this.values = map.values().toArray();
    }

    @Override
    public int getRowCount() {
        return map.size();
    }

    @Override
    public String getColumnName(int column) {
        return TableConstants.TABLE_HEADER[column];
    }

    @Override
    public int getColumnCount() {
        return TableConstants.TABLE_HEADER.length;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if ( columnIndex == 0 )
            return keys[rowIndex];
        else {
            return values[rowIndex];
        }
    }

    @Override
    public void setValueAt(Object changedValue, int rowIndex, int columnIndex) {
        if ( columnIndex == 0 ) {
            Object aValue = values[rowIndex];
            map.remove(keys[rowIndex]);
            keys[rowIndex] = changedValue;
            map.put(changedValue, aValue);
        }
        else {
            values[rowIndex] = changedValue;
            map.replace(keys[rowIndex], changedValue);
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

}
