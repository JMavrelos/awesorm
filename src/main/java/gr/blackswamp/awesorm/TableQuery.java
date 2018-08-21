package gr.blackswamp.awesorm;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@SuppressWarnings({"unused", "SameParameterValue", "WeakerAccess", "UnusedReturnValue"})
public class TableQuery<T> {
    private static final String TAG = "TableQuery";
    private final TableMap _mapping;
    private final SQLiteConnection _con;
    private final Class<T> _class;
    private final List<TableCondition> _conditions = new ArrayList<>();
    private final List<TableOrder> _order = new ArrayList<>();
    private int _skip = 0;
    private int _take = -1;

    TableQuery(Class<T> cl, TableMap mapping, SQLiteConnection con) {
        _class = cl;
        _mapping = mapping;
        _con = con;
    }


    public TableQuery<T> and() {
        _conditions.add(new TableCondition(TableConditionType.and));
        return this;
    }


    public TableQuery<T> or() {
        _conditions.add(new TableCondition(TableConditionType.or));
        return this;
    }

    public TableQuery<T> in(String field, String query_field, TableQuery in_query) {
        StringBuilder sql = new StringBuilder("select `").append(query_field).append("` from `").append(in_query._mapping._name).append("` ");
        //from building
        in_query.apply_where(sql);
        //order by
        in_query.apply_order(sql);
        //limit
        in_query.apply_limit(sql);
        return in(field, sql.toString());
    }

    public TableQuery<T> in(String field, String query) {
        query = query.trim();
        if (!query.startsWith("(")) query = "(" + query;
        if (!query.endsWith(")")) query = query + ")";
        _conditions.add(new TableCondition(TableConditionType.in, field, query, false));
        return this;
    }

    public TableQuery<T> like(String field, String value) {
        _conditions.add(new TableCondition(TableConditionType.lk, field, value, true));
        return this;
    }

    public TableQuery<T> equals(String field, String value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value, true));
        return this;
    }

    public TableQuery<T> equals(String field, Byte value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> equals(String field, Short value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> equals(String field, Integer value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> equals(String field, Long value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> equals(String field, Double value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> equals(String field, Float value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> equals(String field, Boolean value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value ? "1" : "0", false));
        return this;
    }

    public TableQuery<T> equals(String field, Date value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, String.valueOf(value.getTime()), false));
        return this;
    }

    public TableQuery<T> equals(String field, UUID value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, value.toString(), true));
        return this;
    }

    public TableQuery<T> not_equals(String field, String value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value, true));
        return this;
    }

    public TableQuery<T> not_equals(String field, Byte value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> not_equals(String field, Short value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> not_equals(String field, Integer value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> not_equals(String field, Long value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> not_equals(String field, Double value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> not_equals(String field, Float value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> not_equals(String field, Boolean value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value ? "1" : "0", false));
        return this;
    }

    public TableQuery<T> not_equals(String field, Date value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, String.valueOf(value.getTime()), false));
        return this;
    }

    public TableQuery<T> not_equals(String field, UUID value) {
        _conditions.add(new TableCondition(TableConditionType.dt, field, value.toString(), true));
        return this;
    }

    public TableQuery<T> greater_than(String field, String value) {
        _conditions.add(new TableCondition(TableConditionType.gt, field, value, true));
        return this;
    }

    public TableQuery<T> greater_than(String field, Byte value) {
        _conditions.add(new TableCondition(TableConditionType.gt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than(String field, Short value) {
        _conditions.add(new TableCondition(TableConditionType.gt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than(String field, Integer value) {
        _conditions.add(new TableCondition(TableConditionType.gt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than(String field, Long value) {
        _conditions.add(new TableCondition(TableConditionType.gt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than(String field, Double value) {
        _conditions.add(new TableCondition(TableConditionType.gt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than(String field, Float value) {
        _conditions.add(new TableCondition(TableConditionType.gt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than(String field, Date value) {
        _conditions.add(new TableCondition(TableConditionType.gt, field, String.valueOf(value.getTime()), false));
        return this;
    }

    public TableQuery<T> less_than(String field, String value) {
        _conditions.add(new TableCondition(TableConditionType.lt, field, value, true));
        return this;
    }

    public TableQuery<T> less_than(String field, Byte value) {
        _conditions.add(new TableCondition(TableConditionType.lt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than(String field, Short value) {
        _conditions.add(new TableCondition(TableConditionType.lt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than(String field, Integer value) {
        _conditions.add(new TableCondition(TableConditionType.lt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than(String field, Long value) {
        _conditions.add(new TableCondition(TableConditionType.lt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than(String field, Double value) {
        _conditions.add(new TableCondition(TableConditionType.lt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than(String field, Float value) {
        _conditions.add(new TableCondition(TableConditionType.lt, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than(String field, Date value) {
        _conditions.add(new TableCondition(TableConditionType.lt, field, String.valueOf(value.getTime()), false));
        return this;
    }

    public TableQuery<T> greater_than_or_equal(String field, String value) {
        _conditions.add(new TableCondition(TableConditionType.gte, field, value, true));
        return this;
    }

    public TableQuery<T> greater_than_or_equal(String field, Byte value) {
        _conditions.add(new TableCondition(TableConditionType.gte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than_or_equal(String field, Short value) {
        _conditions.add(new TableCondition(TableConditionType.gte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than_or_equal(String field, Integer value) {
        _conditions.add(new TableCondition(TableConditionType.gte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than_or_equal(String field, Long value) {
        _conditions.add(new TableCondition(TableConditionType.gte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than_or_equal(String field, Double value) {
        _conditions.add(new TableCondition(TableConditionType.gte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than_or_equal(String field, Float value) {
        _conditions.add(new TableCondition(TableConditionType.gte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> greater_than_or_equal(String field, Date value) {
        _conditions.add(new TableCondition(TableConditionType.gte, field, String.valueOf(value.getTime()), false));
        return this;
    }

    public TableQuery<T> less_than_or_equal(String field, String value) {
        _conditions.add(new TableCondition(TableConditionType.lte, field, value, true));
        return this;
    }

    public TableQuery<T> less_than_or_equal(String field, Byte value) {
        _conditions.add(new TableCondition(TableConditionType.lte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than_or_equal(String field, Short value) {
        _conditions.add(new TableCondition(TableConditionType.lte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than_or_equal(String field, Integer value) {
        _conditions.add(new TableCondition(TableConditionType.lte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than_or_equal(String field, Long value) {
        _conditions.add(new TableCondition(TableConditionType.lte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than_or_equal(String field, Double value) {
        _conditions.add(new TableCondition(TableConditionType.lte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than_or_equal(String field, Float value) {
        _conditions.add(new TableCondition(TableConditionType.lte, field, value.toString(), false));
        return this;
    }

    public TableQuery<T> less_than_or_equal(String field, Date value) {
        _conditions.add(new TableCondition(TableConditionType.lte, field, String.valueOf(value.getTime()), false));
        return this;
    }

    public TableQuery<T> between(String field, String first_value, String second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, first_value, second_value, true));
        return this;
    }

    public TableQuery<T> between(String field, Byte first_value, Byte second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, first_value.toString(), second_value.toString(), false));
        return this;
    }

    public TableQuery<T> between(String field, Short first_value, Short second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, first_value.toString(), second_value.toString(), false));
        return this;
    }

    public TableQuery<T> between(String field, Integer first_value, Integer second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, first_value.toString(), second_value.toString(), false));
        return this;
    }

    public TableQuery<T> between(String field, Long first_value, Long second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, first_value.toString(), second_value.toString(), false));
        return this;
    }

    public TableQuery<T> between(String field, Double first_value, Double second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, first_value.toString(), second_value.toString(), false));
        return this;
    }

    public TableQuery<T> between(String field, Float first_value, Float second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, first_value.toString(), second_value.toString(), false));
        return this;
    }

    public TableQuery<T> between(String field, Date first_value, Date second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, String.valueOf(first_value.getTime()), String.valueOf(second_value.getTime()), false));
        return this;
    }

    public TableQuery<T> between(String field, UUID first_value, UUID second_value) {
        _conditions.add(new TableCondition(TableConditionType.eq, field, first_value.toString(), second_value.toString(), true));
        return this;
    }


    public TableQuery<T> order_by(String field) {
        _order.add(new TableOrder(field, true));
        return this;
    }

    public TableQuery<T> order_by(String field, boolean ascending) {
        _order.add(new TableOrder(field, ascending));
        return this;
    }

    public TableQuery<T> skip(int to_skip) {
        _skip = to_skip;
        return this;
    }

    public TableQuery<T> take(int count) {
        _take = count;
        return this;
    }

    public List<T> from_query(String query) {
        _con.reset_last_error();
        try (Cursor c = _con.connection.rawQuery(false, query, null)) {
            List<T> items = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    T item = create_item(c);
                    items.add(item);
                } while (c.moveToNext());
            }
            return items;
        } catch (Exception e) {
            _con._last_error = e;
            e.printStackTrace();
            return null;
        }
    }

    public List<T> find_all() {
        StringBuilder sql = build_select();
        //from building
        apply_where(sql);
        //order by
        apply_order(sql);
        //limit
        apply_limit(sql);
        //execute;
        Log.d(TAG, "query:" + sql.toString());
        return from_query(sql.toString());
    }

    public T first() {
        StringBuilder sql = build_select();
        apply_where(sql);
        apply_order(sql);
        _take = 1;
        apply_limit(sql);

        Log.d(TAG, "query:" + sql.toString());
        _con.reset_last_error();
        try (Cursor c = _con.connection.rawQuery(false, sql.toString(), null)) {
            if (!c.moveToFirst())
                return null;
            return create_item(c);
        } catch (Exception e) {
            _con._last_error = e;
            e.printStackTrace();
            return null;
        }
    }

    public int count() {
        StringBuilder sql = new StringBuilder("select count(*) from `").append(_mapping._name).append('`');
        apply_where(sql);
        _con.reset_last_error();
        try (Cursor c = _con.connection.rawQuery(false, sql.toString(), null)) {
            if (c.moveToFirst())
                return c.getInt(0);
            return -1;
        } catch (Exception e) {
            _con._last_error = e;
            e.printStackTrace();
            return -1;
        }
    }

    private T create_item(Cursor c) throws IllegalAccessException, InstantiationException {
        T item = _class.newInstance();
        for (TableColumn col : _mapping._columns) {
            col.field.set(item, col.get_value_from(c));
        }
        return item;
    }

    private void apply_where(StringBuilder sql) {
        if (_conditions.size() == 0)
            return;
        sql.append(" where ");
        int idx = 0;
        while (idx < _conditions.size()) {
            TableCondition condition = _conditions.get(idx);
            switch (condition._type) {
                case dt:
                case eq:
                case gte:
                case gt:
                case lte:
                case lt:
                case in:
                case lk:
                    sql.append(condition.build());
                    if (idx < _conditions.size() - 1) { //if this is not the last condition
                        TableCondition next = _conditions.get(idx + 1);
                        if (next._type != TableConditionType.or && next._type != TableConditionType.and) {
                            sql.append(" and ");
                        }
                    }
                    break;
                case and:
                    sql.append(" and ");
                    break;
                case or:
                    sql.append(" or ");
                    break;
                case bg:
                    sql.append("(");
                    break;
                case eg:
                    sql.append(")");
                    break;
            }
            idx++;
        }
    }

    private void apply_order(StringBuilder sql) {

        if (_order.size() == 0)  //if no ordering is given then do nothing
            return;
        sql.append(" order by ");
        for (TableOrder order : _order) {
            sql.append('`').append(order.field).append('`').append(order.ascending ? " asc " : " desc ").append(',');
        }
        sql.deleteCharAt(sql.length() - 1);
    }

    private void apply_limit(StringBuilder sql) {
        if (_skip == 0 && _take == -1)
            return;
        sql.append(" limit ").append(_take);
        if (_skip > 0)
            sql.append(" offset ").append(_skip);
    }

    private StringBuilder build_select() {
        StringBuilder sql = new StringBuilder(" select ");
        //build select
        for (TableColumn col : _mapping._columns) {
            sql.append('`').append(col.name).append("` ,");
        }
        sql.deleteCharAt(sql.length() - 1).append(" from `").append(_mapping._name).append('`');
        return sql;
    }

}
