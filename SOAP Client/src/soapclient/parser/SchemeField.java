package soapclient.parser;

import javax.swing.*;

public class SchemeField {

    private String name;
    private String type;
    private boolean isArray;
    private boolean isKey;
    private String table;
    private JComponent inputField;

    public SchemeField(String name, String type, boolean isArray, boolean isKey) {
        this.name = name;
        this.type = type;
        this.isArray = isArray;
        this.isKey = isKey;
    }

    public JComponent getInputField() {
        return inputField;
    }

    public void setInputField(JComponent inputField) {
        this.inputField = inputField;
    }

    public SchemeField(String name, String type, boolean isArray) {
        this.name = name;
        this.type = type;
        this.isArray = isArray;
        this.isKey = false;
    }

    public SchemeField(String name, String type) {
        this.name = name;
        this.type = type;
        this.isKey = false;
        this.isArray = false;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean key) {
        isKey = key;
    }
}
