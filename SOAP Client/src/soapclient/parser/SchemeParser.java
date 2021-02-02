package soapclient.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SchemeParser {

    protected DocumentBuilder builder;
    protected Document document;
    private URL schemeURL;
    protected ArrayList<SchemeField> fields;
    protected ArrayList<String> methods;
    protected HashMap<String, String> actions ;

    public HashMap<String, String> getActions() {
        return actions;
    }

    public Boolean initParser(String schemeURL) {

        Boolean result = false;
        try {
            this.schemeURL = new URL(schemeURL);
            this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.document =  this.builder.parse(this.schemeURL.openStream());

            this.fields = new ArrayList<SchemeField>();
            this.methods = new ArrayList<String>();
            this.actions = new HashMap<>();

            result = true;
        } catch (ParserConfigurationException e) {
            result = false;
            e.printStackTrace();
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        } catch (SAXException e) {
            result = false;
            e.printStackTrace();
        }
        finally {
            return  result;
        }
    }
    public Boolean initParser(File inputFile) {

        Boolean result = false;
        try {
            this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.document =  this.builder.parse(inputFile);

            this.fields = new ArrayList<SchemeField>();
            this.methods = new ArrayList<String>();
            this.actions = new HashMap<>();

            result = true;
        } catch (ParserConfigurationException e) {
            result = false;
            e.printStackTrace();
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        } catch (SAXException e) {
            result = false;
            e.printStackTrace();
        }
        finally {
            return  result;
        }
    }

    public SchemeParser() {

        this.fields = new ArrayList<SchemeField>();
        this.methods = new ArrayList<String>();
        this.actions = new HashMap<>();
    }

    public SchemeParser(File inputFile) {

        try {

            this.fields = new ArrayList<SchemeField>();
            this.methods = new ArrayList<String>();
            this.actions = new HashMap<>();
        }
        catch (NullPointerException ex) {

            this.logger(ex.toString());
        }
    }

    public SchemeParser(String url) {

        try {
            this.schemeURL = new URL(url);
            this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.document =  this.builder.parse(this.schemeURL.openStream());
            this.fields = new ArrayList<SchemeField>();
            this.methods = new ArrayList<String>();
            this.actions = new HashMap<>();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    public void setFields(ArrayList<SchemeField> fields) {
        this.fields = fields;
    }

    public void setMethods(ArrayList<String> methods) {
        this.methods = methods;
    }

    public void setActions(HashMap<String, String> actions) {
        this.actions = actions;
    }

    public boolean startParse() {

        Boolean result = false;
        try {

            Node root = this.document.getDocumentElement();
            NodeList elements = root.getChildNodes();
            for (int i=0; i< elements.getLength(); i++) {

                Node element = elements.item(i);
                switch(element.getNodeName()) {

                    case "types" :
                        getFields(element);
                        break;
                    case "portType":
                        parseMethods(element);
                        break;
                    case "binding":
                        parseSOAPActions(element);
                        break;

                }
            }
            result = true;
        }
        catch(NullPointerException ex) {
            result = false;
            this.logger( ex.getMessage());
        }
        finally {
            if (this.schemeURL != null) {
                ;
            }
            return result;
        }
    }

    public String getActionByMethod(String method) {

        if (this.actions != null) {

            return this.actions.get(method);
        }
        else
            return "";
    }
    protected void parseSOAPActions(Node element) {

        try {

            NodeList chields = element.getChildNodes();
            for (int i=0; i < chields.getLength(); i++) {

                Node chieldItem = chields.item(i);
                if (chieldItem.getNodeName().equals("operation")) {

                    String method = chieldItem.getAttributes().getNamedItem("name").getNodeValue().toString();
                    if (!getAction(chieldItem).equals("") && getAction(chieldItem) != null) {
                        this.actions.put(method,getAction(chieldItem) );
                    }
                    else
                        throw new Exception("Нет действия для метода " +method );
                }
            }
        }
        catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

    protected String getAction(Node element) {

        String action = "";
        try {

            NodeList chields = element.getChildNodes();
            for (int i = 0; i <chields.getLength(); i++) {

                Node item = chields.item(i);
                if (item.getNodeName().equals("soap:operation")) {

                    action = item.getAttributes().getNamedItem("soapAction").getNodeValue().toString();
                    break;
                }
            }
        }
        catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
        finally {
            return action;
        }
    }
    public Boolean loadProject() {

        Boolean result = false;
        try {

            Node root = this.document.getDocumentElement();
            NodeList chieldItems = root.getChildNodes();
            for (int i=0; i < chieldItems.getLength(); i++) {

                Node item = chieldItems.item(i);
                if (item.getNodeName().equals("FieldList")) {

                    this.findFields(item);
                }
                if (item.getNodeName().equals("MethodList")) {

                    this.findMethods(item);
                }
                result = true;
            }
        }
        catch (Exception ex) {

            ex.printStackTrace();
            result = false;
        }
        finally {

            return result;
        }
    }

    private void findMethods(Node element) {

        NodeList chieldNodes = element.getChildNodes();
        String action = "";
        String method = "";
        for (int i=0; i< chieldNodes.getLength(); i++) {

            Node item = chieldNodes.item(i);
            if (item.getNodeName().equals("Method"))
                this.findMethods(item);
            else {
                if(item.getNodeName().equals("Action"))
                    action = item.getTextContent();
                if(item.getNodeName().equals("Name"))
                    method = item.getTextContent();
            }
        }
        if (element.getNodeName().equals("Method")) {

            if (
                    !action.equals("") && action != null &&
                            !method.equals("") && method != null
            ) {

                this.methods.add(method);
                this.actions.put(method, action);
            }
        }
    }

    private void findFields(Node element) {

        NodeList chieldNodes = element.getChildNodes();
        SchemeField schemeField = new SchemeField();
        for (int i=0; i< chieldNodes.getLength(); i++) {

            Node item = chieldNodes.item(i);
            if (item.getNodeName().equals("Field"))
                this.findFields(item);
            else {

                if(item.getNodeName().equals("Name"))
                    schemeField.setName(item.getTextContent());
               if(item.getNodeName().equals("Type"))
                   schemeField.setType(item.getTextContent());
               if(item.getNodeName().equals("Table"))
                   schemeField.setTable(item.getTextContent());
               if(item.getNodeName().equals("Array"))
                   schemeField.setArray(Boolean.valueOf(item.getTextContent()));
               if(item.getNodeName().equals("Key"))
                   schemeField.setKey(Boolean.valueOf(item.getTextContent()));
               if(item.getNodeName().equals("Value"))
                   schemeField.setValue(item.getTextContent());
            }
        }
        if (schemeField != null) {

            if (element.getNodeName().equals("Field")) {

                if (
                        !schemeField.getName().equals("") && schemeField.getName() != null &&
                        !schemeField.getType().equals("") && schemeField.getType() != null
                ) {
                    this.fields.add(schemeField);
                }
            }
        }
    }
    protected void parseMethods(Node element) {

        try {

            NodeList chieldElements = element.getChildNodes();
            for (int index = 0; index < chieldElements.getLength(); index++) {

                Node chield = chieldElements.item(index);
                if (chield.getNodeName().equals("operation")) {

                    this.methods.add(chield.getAttributes().getNamedItem("name").getNodeValue());
                }
            }
        }
        catch(NullPointerException ex) {

            this.logger( ex.getMessage());
        }
    }

    protected void getFields(Node element) {

        try {

            if (element != null) {

                NodeList chieldElements = element.getChildNodes();
                for (int index=0; index < chieldElements.getLength(); index ++) {

                    Node chield = chieldElements.item(index);
                    if (chield.getNodeName().equals("xs:schema") || chield.getNodeName().equals("xs:import")) {

                        getFields(chield);
                    }
                    else {

                        if (chield.getNodeName().equals("xs:complexType") && (
                                chield.getAttributes().getNamedItem("name").getNodeValue().toString().indexOf("KeysType") > -1
                                ))
                            parseFields(chield,true, "");
                        if (chield.getNodeName().equals("xs:complexType") && (
                                chield.getAttributes().getNamedItem("name").getNodeValue().toString().indexOf("InstanceType") > -1
                                ))
                            parseFields(chield,false, "");

                    }
                }
            }
            else
                throw new Exception("Root element is null");
        } catch (NullPointerException ex) {

            this.logger(ex.toString());
        } catch (Exception ex) {

            this.logger(ex.toString());
        }

    }
    protected void parseFields(Node parent, Boolean isKey, String table) {

        try {

            if (parent != null) {

                NodeList chieldElement = parent.getChildNodes();
                for (int index=0; index < chieldElement.getLength(); index++ ) {

                    Node chield = chieldElement.item(index);

                    if (!chield.getNodeName().equals("xs:element"))
                        this.parseFields(chield,isKey, table);
                    else {

                        if (chield.getNodeName().equals("xs:element")) {

                            String fieldName = chield.getAttributes().getNamedItem("name").getNodeValue();
                            String fieldType = "";
                            Boolean isArray = false;
                            if (fieldName.indexOf("file.") > -1) {

                                this.parseFields(chield,isKey,fieldName);
                            }
                            else {
                                if (chield.getChildNodes().getLength() > 0) {

                                    isArray = true;
                                    fieldType = this.getArrayField(chield);
                                } else {
                                    isArray = false;
                                    fieldType = chield.getAttributes().getNamedItem("type").getNodeValue();
                                }

                                this.addFieldToArray(fieldName, fieldType, isArray, isKey, table);
                            }
                        }
                    }
                }
            }
            else
                throw new Exception("Root element is null");
        }
        catch (NullPointerException ex) {

            this.logger(ex.getMessage());
        }
        catch (Exception ex) {
            this.logger(ex.toString());
        }
    }

    protected void addFieldToArray(String field, String type, Boolean isArray, Boolean isKey, String table) {

        String fieldType = "";
        if (type != "" && type != null)
            fieldType = type.replace("cmn:","");
        SchemeField schemeField = new SchemeField(field,fieldType,isArray,isKey);
        if (table != "" && table != null)
            schemeField.setTable(table);
        this.fields.add(schemeField);
    }

    protected String getArrayField(Node element) {
        String Type = "";
        if (element != null) {
            if (element.getChildNodes().getLength() > 0) {

                Type = this.getArrayField(element.getFirstChild());
            } else {
                if (element.getNodeName().equals("xs:element")) {

                    Type = element.getAttributes().getNamedItem("type").getNodeValue();
                }
            }
        }
        return Type;
    }
    public ArrayList<SchemeField> getFields() {
        return this.fields;
    }

    public ArrayList<String> getMethods() {
        return this.methods;
    }

    private void logger(String text) {

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date) + " : " + text);
    }
}
