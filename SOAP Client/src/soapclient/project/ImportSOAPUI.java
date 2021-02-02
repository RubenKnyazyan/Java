package soapclient.project;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import soapclient.parser.SchemeParser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ImportSOAPUI extends SchemeParser {

    private HashMap<String,String> requests;
    public ImportSOAPUI() {

        super();
    }

    public HashMap<String, String> getRequests() {
        return requests;
    }

    public Boolean startImport() {

        this.requests = new HashMap<>();

        Boolean result = false;
        try {

            NodeList chieldItems = this.document.getChildNodes();
            for (int index=0; index < chieldItems.getLength(); index++) {

                Node element =chieldItems.item(index);
                if (element.getNodeName().equals("con:soapui-project")) {
                    findParts(element);
                }
            }
            result = true;
        }
        catch (Exception ex) {

            ex.printStackTrace();
            result = false;
        }
        finally {

            return result;
        }
    }
    protected void findParts(Node element) throws IOException, SAXException {

        NodeList chieldItems = element.getChildNodes();
        for (int index=0; index < chieldItems.getLength(); index++) {

            Node chield =chieldItems.item(index);
            if (chield.getNodeName().equals("con:interface")) {

                this.findParts(chield);
            }
            else {

                switch(chield.getNodeName()) {

                    case "con:definitionCache":
                        this.findScheme(chield);
                        break;
                    case "con:operation":
                       this.findRequest(chield,chield.getAttributes().getNamedItem("name").getNodeValue());
                        break;

                }
            }
        }
    }
    protected void findScheme(Node element) throws IOException, SAXException {

        NodeList chieldItems = element.getChildNodes();
        for (int index = 0; index < chieldItems.getLength(); index++) {

            Node chield =chieldItems.item(index);
            if (chield.getNodeName().equals("con:part")) {

                this.findScheme(chield);
            }
            else {

                if(chield.getNodeName().equals("con:content")) {

                    //преобразуем текст для парса
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(chield.getTextContent()));

                    this.document = this.builder.parse(is);
                    this.startParse();

                    break;
                }
            }
        }
    }
    protected void findRequest(Node element,String action) {

        NodeList chieldElements = element.getChildNodes();
        for (int index=0; index < chieldElements.getLength(); index++) {

            Node chield = chieldElements.item(index);
            if (chield.getNodeName().equals("con:call")) {
                this.findRequest(chield,action);
            }
            else {
                if (chield.getNodeName().equals("con:request")) {

                    this.requests.put(action + " " + element.getAttributes().getNamedItem("name").getNodeValue(),chield.getTextContent());
                }
            }
        }
    }
}
