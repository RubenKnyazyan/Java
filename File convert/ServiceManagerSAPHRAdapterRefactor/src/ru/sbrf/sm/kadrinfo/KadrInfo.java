/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sbrf.sm.kadrinfo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author petrunin1-aa
 */
public class KadrInfo {

    private static final Logger LOGGER = Logger.getLogger(KadrInfo.class);
    private Document responseDocument;
    private LinkedList<String> listET_ORG = new LinkedList<>();
    private LinkedList<String> listET_PERSON = new LinkedList<>();
    private String orgNumber = "";
    private int countET_ORG = 0;
    private int countET_PERSON = 0;
    private Integer countPassET_ORG = 0;
    private Integer countPassET_PERSON = 0;
    private Integer countErrET_ORG = 0;
    private Integer countErrET_PERSON = 0;

    public int getCountErrET_ORG() {
        return countErrET_ORG;
    }

    public int getCountErrET_PERSON() {
        return countErrET_PERSON;
    }

    public void incrementCountErrET_ORG() {
        synchronized (countErrET_ORG){
            countErrET_ORG+=1;
        }
    }

    public void incrementCountErrET_PERSON() {
        synchronized (countErrET_PERSON){
            countErrET_PERSON+=1;
        }
    }
    
    public void incrementCountPassET_ORG() {
        synchronized (countPassET_ORG){
            countPassET_ORG+=1;
        }
    }

    public void incrementCountPassET_PERSON() {
        synchronized (countPassET_PERSON){
            countPassET_PERSON+=1;
        }
    }

    public int getCountET_ORG() {
        return countET_ORG;
    }

    public int getCountET_PERSON() {
        return countET_PERSON;
    }

    public int getCountPassET_ORG() {
        return countPassET_ORG;
    }

    public int getCountPassET_PERSON() {
        return countPassET_PERSON;
    }

    public String getOrgNumber() {
        return orgNumber;
    }

    public void setET_ORG(String str){
        listET_ORG.add(str);
    }
    
    public void setET_PERSON(String str){
        listET_PERSON.add(str);
    }
    
    public LinkedList<String> getListET_ORG() {
        return listET_ORG;
    }

    public LinkedList<String> getListET_PERSON() {
        return listET_PERSON;
    }

    public KadrInfo(File file) {
        
        Pattern pattern = Pattern.compile("[FU]{1,2}_(\\d+)_");
        Matcher matcher = pattern.matcher(file.getName());
        matcher.find();
        if(matcher.groupCount()==1){
            orgNumber = matcher.group(1);
            LOGGER.info("orgNumber="+orgNumber);
        } else {
            LOGGER.error("Не удалось определить номер ОРГ единицы из наименовани файла");
            System.exit(-1);
        }
        
        countPassET_ORG = 0;
        countPassET_PERSON = 0;
        countErrET_ORG = 0;
        countErrET_PERSON = 0;
        
        SAXBuilder builder = new SAXBuilder();
        try {
            responseDocument = builder.build(file);
            parseET_ORG();
            LOGGER.info("ET_ORG count=" + countET_ORG);
            parseET_PERSON();
            LOGGER.info("ET_PERSON count=" + countET_PERSON);
        } catch (JDOMException ex) {
            LOGGER.error(ex, ex);
        } catch (IOException ex) {
            LOGGER.error(ex, ex);
        } catch (OutOfMemoryError ex){
            LOGGER.error(ex, ex);
        }
    }

    void parseET_ORG() {
        int count = 0;
        Element rootElement = responseDocument.getRootElement();
        for (Element rootChild : rootElement.getChildren()) {
            if (rootChild.getName().equals("values")) {
                Element elemET_ORG = rootChild.getChild("ET_ORG");
                for (Element item : elemET_ORG.getChildren()) {
                    XMLOutputter xmOut = new XMLOutputter();
                    listET_ORG.addLast(xmOut.outputString(item));
                    count++;

                }
            }
        }
        countET_ORG = count;
    }

    void parseET_PERSON() {
        int count = 0;
        Element rootElement = responseDocument.getRootElement();
        for (Element rootChild : rootElement.getChildren()) {
            if (rootChild.getName().equals("values")) {
                Element elemET_PERSON = rootChild.getChild("ET_PERSON");
                for (Element item : elemET_PERSON.getChildren()) {
                    XMLOutputter xmOut = new XMLOutputter();
                    listET_PERSON.addLast(xmOut.outputString(item));
                    count++;
                }
            }
        }
        countET_PERSON = count;
    }

    public String getFerstET_ORG() {
        String value = null;
        try {
            synchronized (listET_ORG) {
                value = listET_ORG.getFirst();
                listET_ORG.removeFirst();
            }
        } catch (NoSuchElementException ex){
            return null;
        }
        return value;
    }

    public String getFerstET_PERSON() {
        String value = null;
        try {
            synchronized (listET_PERSON) {
                value = listET_PERSON.getFirst();
                listET_PERSON.removeFirst();
            }
        } catch (NoSuchElementException ex){
            return null;
        }
        return value;
    }

}
