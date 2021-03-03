/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.sbrf.sm.soap;

/**
 *
 * @author knyazyan-ra
 */

public class XMLSBAPI {

    public static String getXMLSOAPstr(String key) {

        if(key.equals("ReadCSVSBAPI_FileParserRequest"))
            return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:pws=\"http://servicecenter.peregrine.com/PWS\" xmlns:com=\"http://servicecenter.peregrine.com/PWS/Common\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <pws:ReadCSVSBAPI_FileParserRequest attachmentInfo=\"\" attachmentData=\"\" ignoreEmptyElements=\"true\" updateconstraint=\"-1\">\n" +
                    "         <pws:model query=\"\">\n" +
                    "            <pws:keys query=\"\" updatecounter=\"\">\n" +//
                    "               <pws:id type=\"Decimal\" mandatory=\"\" readonly=\"\"></pws:id>\n" +
                    "            </pws:keys>\n" +
                    "            <pws:instance query=\"\" uniquequery=\"\" recordid=\"\" updatecounter=\"\">\n" +
                    "               <pws:id type=\"Decimal\" mandatory=\"\" readonly=\"\">%sbmqexchange%</pws:id>\n" +
                    "               <pws:orgID type=\"String\" mandatory=\"\" readonly=\"\">%orgID%</pws:orgID>\n" +
                    "               <pws:message type=\"String\" mandatory=\"\" readonly=\"\">%message%</pws:message>\n" +
                    "               <pws:object type=\"String\" mandatory=\"\" readonly=\"\">%object%</pws:object>\n" +
                    "            </pws:instance>\n" +
                    "         </pws:model>\n" +
                    "      </pws:ReadCSVSBAPI_FileParserRequest>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

        return null;

    }
}

