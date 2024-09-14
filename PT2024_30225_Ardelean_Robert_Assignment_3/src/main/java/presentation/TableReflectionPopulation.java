package presentation;

import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.List;

/**
 *
 */
public class TableReflectionPopulation {

    public  static <T> void populateTableWithData(DefaultTableModel tableModel, List<T> objects) {
        if (objects.isEmpty()) {
            return;
        }

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        Field[] fields = objects.get(0).getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            tableModel.addColumn(field.getName());
        }

        for (T object : objects) {
            Object[] rowData = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                try {
                    rowData[i] = fields[i].get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            tableModel.addRow(rowData);
        }
    }

}
