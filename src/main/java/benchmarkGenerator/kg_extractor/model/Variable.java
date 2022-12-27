package benchmarkGenerator.kg_extractor.model;

import settings.Settings;

/**
 *
 * @author aorogat
 */
public class Variable {

    private String name;
    private String value;
    private String type;
    private String value_without_prefix;

    public Variable(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValueWithPrefix() {
        return value;
    }

    public String getValue() {
        if (value.equals(Settings.Number) || value.equals(Settings.Date) || value.equals(Settings.Literal)) {
            return value;
        }
        if (value_without_prefix != null) {
            return value_without_prefix;
        }
        return value_without_prefix = Settings.explorer.removePrefix(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.replace("\n", " - ");
    }
}
