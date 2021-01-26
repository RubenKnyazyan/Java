package soapclient.project;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import soapclient.parser.SchemeField;
import soapclient.parser.SchemeParser;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class SaveProject {

    private ArrayList<SchemeField> fieldArrayList;
    private String Directory;
    private HashMap<String, String> actions ;

    public SaveProject(ArrayList<SchemeField> fieldArrayList, HashMap<String,String> actionMap, String directory) {
        this.fieldArrayList = fieldArrayList;
        this.Directory = directory;
        this.actions = actionMap;
    }

    public SaveProject(ArrayList<SchemeField> fieldArrayList) {
        this.fieldArrayList = fieldArrayList;
    }

    public ArrayList<SchemeField> getFieldArrayList() {
        return fieldArrayList;
    }

    public void setFieldArrayList(ArrayList<SchemeField> fieldArrayList) {
        this.fieldArrayList = fieldArrayList;
    }

    public String getDirectory() {
        return Directory;
    }

    public void setDirectory(String directory) {
        Directory = directory;
    }

    public Boolean run() {

        Boolean result = false;

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = docFactory.newDocumentBuilder();
            //root element
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("Project");
            document.appendChild(rootElement);

            Element fieldsListElement = document.createElement("FieldList");
            Element methodsListElement = document.createElement("MethodList");

            if (!this.saveFields(fieldsListElement,document ) ) {

                throw new Exception("Ошибка при сохранении запроса. Не удалось сохранить поля");
            }
            rootElement.appendChild(fieldsListElement);

            if (!this.saveMethodAndAction(methodsListElement,document)) {

                throw new Exception("Ошибка при сохранении запроса. Не удалось сохранить методы");
            }
            rootElement.appendChild(methodsListElement);

            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(this.Directory));

            transformer.transform(source, streamResult);

            result = true;

        }
        catch (Exception ex) {

            result = false;
            System.out.println(ex.getMessage());
        }
        finally {

            return result;
        }
    }
    private Boolean saveFields(Element rootNode, Document document) {

        Boolean result = true;
        try {

            for (int i=0; i < this.fieldArrayList.size(); i++) {
                //получаем поле
                SchemeField field = this.fieldArrayList.get(i);;
                //готовим узел для сохранения поля
                Element fieldElement = document.createElement("Field");
                //готовим теги для записи значений
                Element fieldName = document.createElement("Name");
                Element fieldType = document.createElement("Type");
                Element fieldIsArray = document.createElement("Array");
                Element fieldIsKey = document.createElement("Key");
                Element fieldTable = document.createElement("Table");
                Element fieldValue = document.createElement("Value");
                //записываем значения
                fieldName.appendChild(document.createTextNode(field.getName()));
                fieldType.appendChild(document.createTextNode(field.getType()));
                fieldTable.appendChild(document.createTextNode(field.getTable()));
                fieldIsArray.appendChild(document.createTextNode(String.valueOf(field.isArray())));
                fieldIsKey.appendChild(document.createTextNode(String.valueOf(field.isKey())));
                fieldValue.appendChild(document.createTextNode(field.getFieldValue()));
                //добавим в родительский узел
                fieldElement.appendChild(fieldName);
                fieldElement.appendChild(fieldType);
                fieldElement.appendChild(fieldTable);
                fieldElement.appendChild(fieldIsArray);
                fieldElement.appendChild(fieldIsKey);
                fieldElement.appendChild(fieldValue);
                //добавим в родительский тег
                rootNode.appendChild(fieldElement);
                result = true;
            }
        }
        catch (Exception ex) {
            result = false;
        }
        finally {

            return result;
        }
    }
    private Boolean saveMethodAndAction(Element rootNode, Document document) {

        Boolean result = false;
        try {

            for (Map.Entry<String, String> entry : this.actions.entrySet()) {

                Element methodElement = document.createElement("Method");

                Element methodName = document.createElement("Name");
                Element methodAction = document.createElement("Action");

                methodName.appendChild(document.createTextNode(entry.getKey()));
                methodAction.appendChild(document.createTextNode(entry.getValue()));

                methodElement.appendChild(methodName);
                methodElement.appendChild(methodAction);

                rootNode.appendChild(methodElement);
            }
            result = true;
        }
        catch (Exception ex) {
            result = false;
        }
        finally {

            return result;
        }
    }
}
