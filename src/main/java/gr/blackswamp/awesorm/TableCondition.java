package gr.blackswamp.awesorm;

class TableCondition {
    TableConditionType _type;
    private String _field;
    private Object _value;
    private Object _second_value;

    TableCondition(TableConditionType type) {
        this(type, null, null, null, false);
    }

    TableCondition(TableConditionType type, String field, String value, boolean is_string) {
        this(type, field, value, null, is_string);
    }

    TableCondition(TableConditionType type, String field, String value, String second_value, boolean is_string) {
        this._type = type;
        if (param_count(type) > 0) {
            this._field = (field.startsWith("_") ? field.substring(1) : field).toLowerCase();
            if (is_string) {
                this._value = "'" + value.replace("'", "''") + "'";
                this._second_value = second_value == null || param_count(type) != 2 ? null : "'" + second_value.replace("'", "''") + "'";
            } else {
                this._value = value;
                this._second_value = param_count(type) != 2 ? null : second_value;
            }
        }
    }

    public String build() {
        String built = "`" + _field + "`";
        switch (_type) {
            case lk:
                built += " like ";
                break;
            case bt:
                return built + " between  " + _value.toString() + " and " + _second_value.toString();
            case dt:
                built += " <> ";
                break;
            case eq:
                built += " = ";
                break;
            case gte:
                built += " >= ";
                break;
            case gt:
                built += " > ";
                break;
            case lte:
                built += " <= ";
                break;
            case lt:
                built += " < ";
                break;
            case in:
                built += " in ";
        }
        return built + " " + _value.toString();
    }

    private int param_count(TableConditionType type) {
        switch (type) {
            case and:
            case or:
                return 0;
            case eq:
            case dt:
            case lt:
            case lte:
            case gt:
            case gte:
            case bg:
            case eg:
            case lk:
            case in:
                return 1;
            case bt:
                return 2;
            default:
                return -1;
        }
    }
}
