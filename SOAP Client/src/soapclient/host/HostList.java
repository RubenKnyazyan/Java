package soapclient.host;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class HostList {

    private ArrayList<String> hostNameList;
    private ArrayList<Host> hostArrayList;

    public HostList() {

        this.hostArrayList = new ArrayList<Host>();
        this.hostNameList = new ArrayList<String>();
        this.initHostList();
    }

    public void initHostList() {

        try {

            File fXmlFile = new File("src\\soapclient\\hosts.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            Node root = doc.getDocumentElement();
            NodeList elements = root.getChildNodes();
            for (int i=0; i< elements.getLength(); i++) {

                Node element = elements.item(i);

                if (element.getNodeName().equals("item")) {

                    Element elementItem = (Element) element;
                    String name = elementItem.getElementsByTagName("name").item(0).getTextContent();
                    String value = elementItem.getElementsByTagName("value").item(0).getTextContent();
                    if (name != null && value != null) {

                        Host host = new Host(name, value);

                        this.hostArrayList.add(host);
                        this.hostNameList.add(name);
                    }
                }
            }

        }
        catch( Exception ex) {
//            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String getHostValue(String hostName) {

        String result = "";
        if (hostName != null && !hostName.equals("")) {

            for (int i=0; i < this.hostArrayList.size(); i++) {

                Host item = this.hostArrayList.get(i);
                if (item.getName().equals(hostName)) {

                    result = item.getValue();
                    break;
                }
            }
        }
        return result;
    }
    public ArrayList<String> getHostNameList() {
        return this.hostNameList;
    }

    public void setHostNameList(ArrayList<String> hostName) {
        this.hostNameList = hostName;
    }
}
