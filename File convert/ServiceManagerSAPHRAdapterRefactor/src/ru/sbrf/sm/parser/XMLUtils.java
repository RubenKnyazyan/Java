/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.sbrf.sm.parser;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author knyazyan-ra
 */
public class XMLUtils {

public static Document loadXMLFromString(String xml) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilder builderFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return  builderFactory.parse(is);
    }
    public static String nameCorrection(String text) {

        String InputString = text;
        if (InputString != null && !InputString.equals(""))
        {

            InputString = InputString.replaceAll("\"","'"); //Заменяем кавычки на одинарные
            InputString = InputString.replace("\\", "/"); //Заменяем левый слэш на правый
            InputString = InputString.trim(); //Удаляем пробелы в начале строки
            InputString = InputString.replace("*", ""); //Убираем звездочку из текста
            InputString = InputString.replace("+", ""); //Убираем плюсы из текста
            InputString = InputString.replace("[comma]", ","); //Заменяем [comma] на запятую - для интеграции с MARS.
            if (InputString.equals("")) //Если в результате исправления не осталось символов, используем дефолтное имя
            {
                InputString = "Без названия";
            }
            else
            {
                return InputString;
            }
        }
        else
        {
            return null;
        }
        return InputString;
    }
    public static Boolean compareDateBegin(String date) throws ParseException {

        String dateBeg = date+ " 00:00:00";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String nowStr = dateFormat.format(new Date());
        Date begin = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateBeg);
        Date now =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(nowStr);
        return begin.before(now);
    }
    public static Boolean compareDateEnd(String date) throws ParseException {

        String dateEnd = date+ " 23:59:59";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String nowStr = dateFormat.format(new Date());
        Date end = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateEnd);
        Date now =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(nowStr);
        return now.before(end);
    }

    public static String createShortName(String name) {

        String[] parts = name.split(" ");
        String shortName= "";
        for (String templ : parts) {
            shortName+=templ.substring(0,1);
        }
        return shortName.toUpperCase();
    }
    public static String parseInsiderDateTime(String date, String sTime) {

        //если дата и время пустые, обнуляем
        if (date == null && sTime == null) {
            return null;
        }
        String sHour = (sTime == null || sTime.equals("")) ? "00" :  sTime.substring( 0, 2);
        String sMinute = (sTime == null || sTime.equals("")) ? "00" : sTime.substring( 2, 4);
        String sSecond = (sTime == null || sTime.equals("")) ? "00" : sTime.substring( 4, sTime.length());
        if (date != null && !date.equals("")) {

            if (date.indexOf("9999") > 0)   date.replace("9999", "2100");
            try {
                return makeJSONFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date + " " + sHour+":"+ sMinute+":"+ sSecond));
            }
            catch (Exception  ex) {
                return null;
            }
        }
        else
            return null;
    }
    public static String makeJSONFormat(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return simpleDateFormat.format(date)+"Z";
    }
}
