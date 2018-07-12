package gr.blackswamp.awesorm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gr.blackswamp.awesorm.Annotations.Column;
import gr.blackswamp.awesorm.Annotations.PKey;
import gr.blackswamp.awesorm.Annotations.Table;

class TableMap {
    /**
     * The class that is represented by this table
     */
    private Class<?> _class;
    /**
     * The name of the table
     */
    String _name;
    /**
     * If row id will be used in creation
     */
    private boolean _without_row_id;
    /**
     * A list of all the columns of the table
     */
    List<TableColumn> _columns = new ArrayList<>();

    TableMap(Class<?> clazz) {
        _class = clazz;
        Table table = _class.getAnnotation(Table.class);
        if (table != null) {
            _name = table.name();
            _without_row_id = table.without_row_id();
        } else {
            _name = _class.getSimpleName().toLowerCase();
            _without_row_id = false;
        }

        Class checked = _class;
        while (checked != null && checked != Object.class) {
            Field[] fields = checked.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(Column.class) != null || field.getAnnotation(PKey.class) != null) {
                    _columns.add(new TableColumn(field));
                }
            }
            checked = checked.getSuperclass();
        }
        List<TableColumn> keys = new ArrayList<>();
        for (TableColumn column : _columns) {
            if (column.pk) {
                keys.add(column);
                if (!column.get_declaration().contains(" INTEGER "))
                    column.auto_increment = false;
            }else {
                column.auto_increment = false;
            }
        }
        if (keys.size() > 1) {
            for (TableColumn column : keys) {
                column.auto_increment = false;
            }
        }

    }

    List<TableColumn> get_indexed_columns(boolean unique) {
        List<TableColumn> indexed_columns = new ArrayList<>();
        for (TableColumn col : _columns) {
            if ((col.indexed && !unique) || (col.unique && unique)) {
                indexed_columns.add(col);
            }
        }
        if (!unique)
            Collections.sort(indexed_columns);
        return indexed_columns;
    }


}
