package soapclient.parser;

import javax.swing.*;
import java.text.SimpleDateFormat;

public class SchemeField {

    private String name;
    private String type;
    private boolean isArray;
    private boolean isKey;
    private String table;
    private JComponent inputField;
    private String value;

    public SchemeField() {
        this.value = "";
    }

    public SchemeField(String name, String type, boolean isArray, boolean isKey) {
        this.name = name;
        this.type = type;
        this.isArray = isArray;
        this.isKey = isKey;
        this.value = "";
    }

    public SchemeField(String name, String type, boolean isArray, boolean isKey, String table, String value) {
        this.name = name;
        this.type = type;
        this.isArray = isArray;
        this.isKey = isKey;
        this.table = table;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        this.value = "";
    }

    public SchemeField(String name, String type) {
        this.name = name;
        this.type = type;
        this.isKey = false;
        this.isArray = false;
        this.value = "";
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

    public String getFieldValue () {

        String value = "";
        switch (this.getType()) {

            case "BooleanType":
                JCheckBox inputCheckBox = (JCheckBox) this.getInputField();
                value = String.valueOf(inputCheckBox.isSelected());
                break;
            case "DurationType":
                JSpinner inputDateDuration = (JSpinner) this.getInputField();
                SimpleDateFormat simpleDateFormatDuration = new SimpleDateFormat("HH:mm:ss");
                value = simpleDateFormatDuration.format(inputDateDuration.getValue());
                break;
            case "DateTimeType":
                JSpinner inputDate = (JSpinner) this.getInputField();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                value = simpleDateFormat.format(inputDate.getValue());
                break;
            case "DecimalType":
            case "StringType":
                JTextField inputText = (JTextField) this.getInputField();
                value = inputText.getText();
                break;
        }
        return value;
    }
}
