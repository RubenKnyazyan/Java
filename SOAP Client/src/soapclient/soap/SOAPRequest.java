package soapclient.soap;

import soapclient.parser.SchemeField;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SOAPRequest {

    private  String soapMessage = "";
    private ArrayList<SchemeField> fieldList;
    private String method;

    private String body;

    public SOAPRequest(ArrayList<SchemeField> fieldList, String method) {
        this.fieldList = fieldList;
        this.method = method;
    }

    public String getSoapMessage() {
        return soapMessage;
    }

    public ArrayList<SchemeField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(ArrayList<SchemeField> fieldList) {
        this.fieldList = fieldList;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public SOAPRequest(ArrayList<SchemeField> fieldList) {
        this.fieldList = fieldList;
    }

    public  String generateMessage(String requestMethod) {

        this.body = "";
        this.soapMessage = "";
        return this.getHeader() +
                this.getBody(this.fieldList, requestMethod) +
                this.getFooter();
    }

    private String getBodyStruct(ArrayList<SchemeField> fieldList) {

            return  "\t\t\t<pws:model query=\" \">"+"\n"+
                    this.getBodyKey(fieldList)+"\n"+
                    this.getBodyInstance(fieldList)+"\n"+
                    "\t\t\t</pws:model>";
    }
    private String getBodyKey(ArrayList<SchemeField> fieldList) {

        String result = "\t\t\t\t<pws:keys query=\" \" updatecounter=\" \">\n";
        for (int index=0; index < fieldList.size(); index++) {

            String value = getFieldValue(fieldList.get(index));
            if (fieldList.get(index).isKey()) {
                result+="\t\t\t\t\t<pws:"+fieldList.get(index).getName()+" type=\""+fieldList.get(index).getType().replace("Type","")+"\" mandatory=\" \" readonly=\" \">"+value+"</pws:"+fieldList.get(index).getName()+">\n";
            }
        }
        result+="\t\t\t\t</pws:keys>";
        return result;
    };
    private   String getBodyInstance(ArrayList<SchemeField> fieldList) {

        if (fieldList.size() > 0) {

            this.body +="\t\t\t\t<pws:instance query=\"\" uniquequery=\"\" recordid=\"\" updatecounter=\"\">\n";
            for (int index = 0; index < fieldList.size(); index ++ ) {

                SchemeField field = fieldList.get(index);
                if ( ! field.isKey()) {
                    String value = getFieldValue(field);
                    if (!value.equals("") && value != null) {

                        if (field.getTable() != null && !field.getTable().equals("")) {

                            index = this.createStruct(fieldList,field.getTable(),index);
                        }
                        else {

                            this.body += this.createFieldTAG(field, value, 5);
                        }
                    }
                }
            }
            this.body+="\t\t\t\t</pws:instance>";
        }
        return this.body;
    }
    private String createFieldTAG(SchemeField field,String value, int tabCount) {

        String result = "";
        if (!value.equals("") && value != null) {

            if (field.isArray()) {
                for (int i =0; i<tabCount; i++) {result+="\t";}
                result+=  "<pws:" +
                        field.getName() +
                        " type=\"Array\">";
                        for (int i =0; i<tabCount; i++) {result+="\t";}
                result+="\t<pws:" +
                        field.getName() +
                        " type=\"" + field.getType().replace("Type", "") + "\"" +
                        " mandatory=\"\" readonly=\"\">" +
                        value +
                        "</pws:" + field.getName() + ">\n" +
                        "</pws:" + field.getName() + ">\n";
            }
            else {
                for (int i =0; i<tabCount; i++) {result+="\t";}
                result += "<pws:" +
                        field.getName() +
                        " type=\"" + field.getType().replace("Type", "") + "\"" +
                        " mandatory=\"\" readonly=\"\">" +
                        value +
                        "</pws:" + field.getName() + ">\n";
            }
        }
        return result;
    }
    private int createStruct(ArrayList<SchemeField> fieldList, String fileName, int start) {

        if (start > 0) {
            int index = start;
            this.body+="\t\t\t\t\t<pws:"+fileName+" type=\"Structure\">\n";
            for ( ;index < fieldList.size(); index ++) {

                if (fieldList.get(index).getTable() != null && !fieldList.get(index).getTable().equals(fileName)) {

                    break;
                }
                else {

                    String value = getFieldValue(fieldList.get(index));
                    this.body += this.createFieldTAG(fieldList.get(index), value,6);
                }
            }
            this.body +="\t\t\t\t\t</pws:"+fileName+">\n";
            return index--;
        }
        else
            return start;
    }
    private String getBody(ArrayList<SchemeField> fields, String method) {

        return "\t<soapenv:Body>\n" +
                "\t\t<pws:"+method+"Request attachmentInfo=\" \" attachmentData=\" \" ignoreEmptyElements=\"true\" updateconstraint=\"-1\">"+"\n"+
                this.getBodyStruct(fields)+"\n"+
                "\t\t</pws:"+method+"Request>"+"\n"+
                "\t</soapenv:Body>\n";
    }
    private  String getHeader() {

        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                "xmlns:pws=\"http://servicecenter.peregrine.com/PWS\" xmlns:com=\"http://servicecenter.peregrine.com/PWS/Common\">\n" +
                "\t<soapenv:Header/>";
    }
    private   String getFooter() {

        return  "</soapenv:Envelope>";
    }
    private String getFieldValue (SchemeField field) {

        String value = "";
        switch (field.getType()) {

            case "BooleanType":
                JCheckBox inputCheckBox = (JCheckBox) field.getInputField();
                value = String.valueOf(inputCheckBox.isSelected());
                break;
            case "DurationType":
            case "DateTimeType":
                JSpinner inputDate = (JSpinner) field.getInputField();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                value = simpleDateFormat.format(inputDate.getValue());
                break;
            case "DecimalType":
            case "StringType":
                JTextField inputText = (JTextField) field.getInputField();
                value = inputText.getText();
                break;
        }
        return value;
    }
}
