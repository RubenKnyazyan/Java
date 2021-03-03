/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sbrf.sm.soap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import ru.sbrf.sm.kadrinfo.KadrInfo;

import ru.sbrf.sm.cfg.ParseConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 * @author petrunin1-aa
 */
public class SenderRezult {
    private static final Logger LOGGER = Logger.getLogger(SenderRezult.class);
    private SoapApacheHttpClient SoapClient;
    private KadrInfo kadrInfo;
    private String sbmqexchenge;
    private String message;
    private ParseConfig config;

     public SenderRezult(KadrInfo kadrInfo,SoapApacheHttpClient SoapClient, String sbmqexchenge, String message,ParseConfig config) {
        this.kadrInfo = kadrInfo;
        this.SoapClient = SoapClient;
        this.sbmqexchenge = sbmqexchenge;
        this.message = message;
        this.config = config;
    }

    void Processid (ResponseSOAPMessage responseSOAPMessage) throws IOException{
        SAXBuilder builder = new SAXBuilder();
        try {
            Document responseDocument = builder.build(new ByteArrayInputStream(
                    StringUtils.substringBetween(responseSOAPMessage.getResponse(), "<SOAP-ENV:Body>", "</SOAP-ENV:Body>").getBytes("UTF-8")));
            Element rootElement = responseDocument.getRootElement();
            Namespace nsRoot = rootElement.getNamespace();

            int returnCode = -1;
            if(rootElement.getAttribute("returnCode")!=null)
                returnCode = Integer.parseInt(rootElement.getAttributeValue("returnCode"));
            switch (returnCode){
                case (0):
                    if(rootElement.getAttribute("message")!=null)
                        LOGGER.info("Результаты успешно отправлены в Service Manager");
                    else
                        LOGGER.error(responseSOAPMessage.getResponse());
                    break;
                case (9):
                    LOGGER.error(responseSOAPMessage.getResponse()); break;
                default:
                    LOGGER.error(responseSOAPMessage.getResponse()); break;
            }

        } catch (JDOMException|NullPointerException|NumberFormatException ex) {
            LOGGER.error(responseSOAPMessage.toString());
            LOGGER.error(ex,ex);
        }
    }


    public void run() {


        //LOGGER.info("stringET_PERSON="+stringET_PERSON);

        String SOAPAction = "ReadCSV";
        String XMLSOAPstr= XMLSBAPI.getXMLSOAPstr("ReadCSVSBAPI_FileParserRequest");
        XMLSOAPstr = XMLSOAPstr.replace("%message%", "<![CDATA["+message+"]]>");        
        XMLSOAPstr = XMLSOAPstr.replace("%object%", this.config.getPath() + "\\TRANSFORMED_"+kadrInfo.getOrgNumber()+".csv");
        XMLSOAPstr = XMLSOAPstr.replace("%orgID%", kadrInfo.getOrgNumber());
        XMLSOAPstr = XMLSOAPstr.replace("%sbmqexchange%", sbmqexchenge);

        try {
            try {
                ResponseSOAPMessage responseSOAPMessage = SoapClient.sendPOST(XMLSOAPstr,SOAPAction);

                if(responseSOAPMessage.getStatusCode() == 200){
                    Processid(responseSOAPMessage);
                }else{
                    LOGGER.error("Ошибка отправки сообщения в Service Manager");
                    LOGGER.error(responseSOAPMessage.toString());
                    SoapClient.nextURL();
                    Thread.sleep(30000);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                SoapClient.nextURL();
                LOGGER.error(ex,ex);
                Thread.sleep(30000);
            }
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            LOGGER.error(ex,ex);
        }


    }

}
