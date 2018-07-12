package gr.blackswamp.awesorm;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import gr.blackswamp.awesorm.Annotations.AutoIncrement;
import gr.blackswamp.awesorm.Annotations.Column;
import gr.blackswamp.awesorm.Annotations.Indexed;
import gr.blackswamp.awesorm.Annotations.NotNull;
import gr.blackswamp.awesorm.Annotations.PKey;
import gr.blackswamp.awesorm.Annotations.Unique;

class TableColumn implements Comparable<TableColumn> {
    /**
     * The _field this column represents
     */
    final Field field;
    /**
     * If the column is a primary key
     */
    final boolean pk;
    /**
     * The name of the column in the database
     */
    final String name;
    /**
     * If the column is unique
     */
    final boolean unique;
    /**
     * If the column is indexed
     */
    final boolean indexed;
    /**
     * The order in which this column is indexed
     */
    final int index_order;
    /**
     * if we allow nulls
     */
    final boolean allow_null;
    /**
     * The maximum size of the column
     */
    private final int max_length;
    /**
     * If the column is auto_incremented
     */
    boolean auto_increment;

    TableColumn(Field field) {
        this.field = field;
        if (!this.field.isAccessible())
            this.field.setAccessible(true);
        PKey key = this.field.getAnnotation(PKey.class);
        pk = key != null;
        Column cl = this.field.getAnnotation(Column.class);
        if ((key != null && key.value().equals("")) || cl.value().equals(""))
            name = fix_column_name(this.field.getName());
        else if (key != null)
            name = fix_column_name(key.value());
        else
            name = fix_column_name(cl.value());

        unique = this.field.getAnnotation(Unique.class) != null;
        Indexed idx = this.field.getAnnotation(Indexed.class);
        if (idx != null) {
            indexed = true;
            index_order = idx.order();
        } else {
            indexed = false;
            index_order = -1;
        }
        max_length = (key == null) ? Math.max(0, cl.length()) : Math.max(0, key.length());
        allow_null = !pk && this.field.getAnnotation(NotNull.class) == null;
        auto_increment = this.field.getAnnotation(AutoIncrement.class) != null;
    }

    private String fix_column_name(String value) {
        return (value.startsWith("_") ? value.substring(1) : value).toLowerCase();
    }

    String get_declaration() {
        StringBuilder type = new StringBuilder(" `").append(name).append("` ");
        Class<?> cl = field.getType();
        if (cl.equals(int.class) || cl.equals(short.class) || cl.equals(long.class) || cl.equals(Date.class) || cl.equals(byte.class)) {
            type.append(" INTEGER ");
            if (auto_increment && pk)
                type.append(" PRIMARY KEY AUTOINCREMENT ");
        } else if (cl.equals(UUID.class)) {
            type.append(" VARCHAR(36) ");
        } else if (cl.equals(String.class)) {
            type.append(" VARCHAR");
            if (max_length > 0)
                type.append("(").append(max_length).append(")");
            else
                type.append(" ");
        } else if (cl.equals(float.class) || cl.equals(BigDecimal.class) || cl.equals(double.class)) {
            type.append(" REAL ");
        } else if (cl.equals(boolean.class)) {
            type.append(" INTEGER ");
        } else {
            throw new IllegalArgumentException(String.format("_type %1$s not supported", cl.getName()));
        }
        if (!pk && !allow_null)
            type.append(" not null ");
        return type.toString();
    }

    Object get_value_from(Cursor c) {
        Class<?> cl = field.getType();
        int idx = c.getColumnIndex(name);
        if (c.isNull(idx))
            return null;
        else if (cl.equals(int.class))
            return c.getInt(idx);
        else if (cl.equals(short.class))
            return c.getShort(idx);
        else if (cl.equals(long.class))
            return c.getLong(idx);
        else if (cl.equals(byte.class))
            return (byte) c.getInt(idx);
        else if (cl.equals(Date.class))
            return new Date(c.getLong(idx));
        else if (cl.equals(UUID.class))
            return UUID.fromString(c.getString(idx));
        else if (cl.equals(String.class))
            return c.getString(idx);
        else if (cl.equals(float.class))
            return (float) c.getDouble(idx);
        else if (cl.equals(BigDecimal.class))
            return new BigDecimal(c.getDouble(idx));
        else if (cl.equals(double.class))
            return c.getDouble(idx);
        else if (cl.equals(boolean.class))
            return c.getInt(idx) == 1;
        return null;
    }

    @Override
    public int compareTo(TableColumn other) {
        if (index_order == -1 && other.index_order == -1)
            return 0;
        else if (index_order == -1)
            return 1;
        else if (other.index_order == -1)
            return -1;
        else
            return index_order - other.index_order;
    }

    <T extends DataObject> String get_sql_val(T item) throws IllegalAccessException {
        Type t = field.getType();
        Object val = field.get(item);

        if (val == null) {
            return "null";
        } else if (t.equals(String.class)) {
            return "'" + ((String) val).replace("'", "''") + "'";
        } else if (t.equals(boolean.class)) {
            return ((boolean) val) ? "1" : "0";
        } else if (t.equals(UUID.class)) {
            return "'" + val.toString() + "'";
        } else if (t.equals(Date.class)) {
            return String.valueOf(((Date) val).getTime());
        } else {
            return val.toString();
        }
    }
}