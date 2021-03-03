/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.sbrf.sm.cfg;

/**
 *
 * @author knyazyan-ra
 */

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.simple.JSONObject;
import ru.sbrf.sm.file.SaveFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ParseConfig {

    private static final Logger LOGGER = Logger.getLogger(ParseConfig.class);
    private String userSM;
    private String passwordSM;
    private String hostSM;
    private int maxIteration;
    private int servletPort;
    private int servletThread;
    private String servletToken;
    private String path;
    private JSONObject ET_ORG_config;
    private JSONObject ET_PERSON_config;

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public String getUserSM() {
        return userSM;
    }

    public String getPasswordSM() {
        return passwordSM;
    }

    public String getHostSM() {
        return hostSM;
    }

    public int getMaxIteration() {
        return maxIteration;
    }

    public int getServletPort() {
        return servletPort;
    }

    public int getServletThread() {
        return servletThread;
    }

    public ParseConfig(String cfgPath) {

        this.ET_ORG_config    = new JSONObject();
        this.ET_PERSON_config = new JSONObject();

        SAXBuilder builder = new SAXBuilder();
        File file = new File(cfgPath);
        if(!file.exists()){
            LOGGER.error("Файл не найден! " + cfgPath + "\r\nУказать путь можно параметром -config=%path%");
            System.exit(-1);
        }

        else {

            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(cfgPath), StandardCharsets.UTF_8);
                Document document;
                document = builder.build(isr);
                XMLOutputter xmOut=new XMLOutputter();
                //LOGGER.debug(xmOut.outputString(document));

                Element SMSAPHRAdapter = document.getRootElement();
                String encodeHex = "encodeHex";
                Element Path = SMSAPHRAdapter.getChild("Path");
                this.path = Path.getText();
                Element USER_SMIT = SMSAPHRAdapter.getChild("USER_SMIT");
                Element UserSM = USER_SMIT.getChild("UserSM");
                Element PasswordSM = USER_SMIT.getChild("PasswordSM");
                Element Host = USER_SMIT.getChild("Host");
                this.hostSM = Host.getText();
                this.userSM = UserSM.getText();
                if(UserSM.getAttribute("maxIteration")!=null)
                    maxIteration = UserSM.getAttribute("maxIteration").getIntValue();
                else
                    maxIteration = 10000;
                if(PasswordSM.getAttribute(encodeHex).getValue().equals("f")){
                    this.passwordSM = PasswordSM.getText();
                    PasswordSM.setText(encodeHexString(PasswordSM.getText()));
                    PasswordSM.setAttribute(encodeHex, "t");
                }
                if(PasswordSM.getAttribute(encodeHex).getValue().equals("t"))
                    this.passwordSM = decodeHexString(PasswordSM.getText());

                //create parse org config
                this.setORGConfig(SMSAPHRAdapter.getChild("ET_ORG"));
                //create parse person config
                this.setPERSONConfig(SMSAPHRAdapter.getChild("ET_PERSON"));

                Element Servlet = SMSAPHRAdapter.getChild("Servlet");
                Element ServletPort = Servlet.getChild("port");
                this.servletPort = Integer.parseInt(ServletPort.getText());
                Element ServletThread = Servlet.getChild("thread");
                this.servletThread = Integer.parseInt(ServletThread.getText());
                Element ServletToken = Servlet.getChild("token");
                if(ServletToken.getAttribute(encodeHex).getValue().equals("f")){
                    this.servletToken = ServletToken.getText();
                    ServletToken.setText(encodeHexString(ServletToken.getText()));
                    ServletToken.setAttribute(encodeHex, "t");
                }
                if(ServletToken.getAttribute(encodeHex).getValue().equals("t"))
                    this.servletToken = decodeHexString(ServletToken.getText());

                if(!SaveFile.doSave(cfgPath, xmOut.outputString(document)))
                {LOGGER.error("Не удалось сохранить файл!");System.exit(-1);}


            } catch (JDOMException | IOException | NullPointerException | NumberFormatException ex) {
                ex.printStackTrace();
                LOGGER.error(ex, ex);
                System.exit(-1);
            }
        }
    }

    private void setORGConfig(Element element) {

        for (Element item: element.getChildren("item")) {

            try {
                JSONObject field = new JSONObject();
                field.put("field", item.getChild("FIELD").getText());
                field.put("rule",  item.getChild("RULE").getText());

                this.ET_ORG_config.put(item.getChild("TAG").getText(), field);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (element.getChild("DYN_ATTR") != null) {
            JSONObject dynAttr = new JSONObject();
            for (Element item : element.getChild("DYN_ATTR").getChildren()) {
                String key = item.getName();
                JSONObject fieldBlock = new JSONObject();
                for (Element dynItem : item.getChildren("item")) {

                    JSONObject field = new JSONObject();
                    field.put("field", dynItem.getChild("FIELD").getText());
                    field.put("rule", dynItem.getChild("RULE").getText());
                    fieldBlock.put(dynItem.getChild("TAG").getText(), field);
                }
                dynAttr.put(key, fieldBlock);
            }
            this.ET_ORG_config.put("DYN_ATTR", dynAttr);
        }
    }
    private void setPERSONConfig(Element element) {

        for (Element item: element.getChildren("item")) {

            try {
                JSONObject field = new JSONObject();
                field.put("field", item.getChild("FIELD").getText());
                field.put("rule",  item.getChild("RULE").getText());

                this.ET_PERSON_config.put(item.getChild("TAG").getText(), field);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (element.getChild("DYN_ATTR") != null) {
            JSONObject dynAttr = new JSONObject();
            for (Element item : element.getChild("DYN_ATTR").getChildren()) {
                String key = item.getName();
                JSONObject fieldBlock = new JSONObject();
                for (Element dynItem : item.getChildren("item")) {

                    JSONObject field = new JSONObject();
                    field.put("field", dynItem.getChild("FIELD").getText());
                    field.put("rule", dynItem.getChild("RULE").getText());
                    fieldBlock.put(dynItem.getChild("TAG").getText(), field);
                }
                dynAttr.put(key, fieldBlock);
            }
            this.ET_PERSON_config.put("DYN_ATTR", dynAttr);
        }

    }
    public JSONObject getET_ORG_config() {
        return ET_ORG_config;
    }

    public JSONObject getET_PERSON_config() {
        return ET_PERSON_config;
    }

    public String getServletToken() {
        return servletToken;
    }
    public String getPath() {
        return path;
    }
    String decodeHexString(String Text){
        byte[] b;
        try {
            b = Hex.decodeHex(Text.toCharArray());
            return new String(b, StandardCharsets.UTF_8);

        } catch (DecoderException ex) {
            LOGGER.error(ex,ex);
        }
        return "";
    }

    String encodeHexString(String Text){
        return Hex.encodeHexString(Text.getBytes(StandardCharsets.UTF_8));
    }
}
