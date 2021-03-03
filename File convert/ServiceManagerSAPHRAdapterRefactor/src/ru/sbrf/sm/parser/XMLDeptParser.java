/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.sbrf.sm.parser;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONObject;
import ru.sbrf.sm.file.GenerateOutputFile;
import ru.sbrf.sm.kadrinfo.KadrInfo;

import java.io.StringReader;
import java.util.Iterator;
/**
 *
 * @author knyazyan-ra
 */
public class XMLDeptParser extends Thread {

private JSONObject dept;
    private String XMLText;
    private GenerateOutputFile outputFile;
    private KadrInfo kadrInfo;
    private JSONObject template;
    private SAXBuilder builder;

    public XMLDeptParser(String XMLText, GenerateOutputFile outputFile, KadrInfo kadrInfo, JSONObject parseTemplate) {
        this.XMLText = XMLText;
        //в качетсве возврата будет использоват JSON - как и в СМе при парсе
        this.dept = new JSONObject();
        //для сохранения в файл
        this.outputFile = outputFile;
        //parse template
        this.template = parseTemplate;
        //
        this.kadrInfo = kadrInfo;
        //XML SAX Builder
        this.builder = new SAXBuilder();
    }

    @Override
    public void run() {

        try {
            //разбираем документ
            Document document = this.builder.build(new StringReader(this.XMLText));//XMLUtils.loadXMLFromString(this.XMLText);
            Element ORG = document.getRootElement();
            for (Element item: ORG.getChildren()) {

               if (item.getName().equals("DYN_ATTR")) {

                   JSONObject dynAttrMap = (JSONObject) this.template.get("DYN_ATTR");
                   if (dynAttrMap != null) {
                       for (Element dynAttr : item.getChildren()) {

                          JSONObject dynAttrItemMap = (JSONObject) dynAttrMap.get(dynAttr.getName());
                          if (dynAttrItemMap !=null) {

                              for (Element dynAttrItem : dynAttr.getChildren("item")) {
                                  //получаем поля в шаблоне парса
                                  Iterator<String> fieldsItemsConfig = dynAttrItemMap.keySet().iterator();
                                    //пробегаем по полям
                                  while (fieldsItemsConfig.hasNext()) {

                                      String chemeField = fieldsItemsConfig.next();
                                      JSONObject field = (JSONObject) dynAttrItemMap.get(chemeField);
                                      if (field != null) {

                                          if (field.get("rule").equals("no")) {
                                              if (dynAttrItem.getChild(chemeField) != null) {
                                                  this.dept.put(field.get("field"), dynAttrItem.getChild(chemeField).getText());
                                              }
                                          }
                                          else {

                                              String[] rule = field.get("rule").toString().split(" ");
                                              if (rule[0].equals("between")) {

                                                  if (rule.length == 3) {
                                                      String dateStart = dynAttrItem.getChild(rule[1]).getText(),
                                                              dateEnd = dynAttrItem.getChild(rule[2]).getText();


                                                      if (dateStart != null && !dateStart.equals("") && dateEnd != null && !dateEnd.equals("")) {

                                                          if (XMLUtils.compareDateBegin(dateStart) && XMLUtils.compareDateEnd(dateEnd)) {
                                                              if (dynAttrItem.getChild(chemeField) != null) {
                                                                  this.dept.put(field.get("field"), dynAttrItem.getChild(chemeField).getText());
                                                              }
                                                          }
                                                      }
                                                  }
                                              }
                                          }
                                      }
                                  }
                              }
                          }
                       }
                   }
               }
               else {

                   JSONObject field = (JSONObject) this.template.get(item.getName());
                   if (field != null) {

                       if (field.get("rule").equals("no")) {
                           this.dept.put(field.get("field"), item.getText());
                       }
                   }
               }
            }
            if (!this.dept.isEmpty()) {

                if (this.outputFile.addORG(this.dept)) {

                    this.kadrInfo.incrementCountPassET_ORG();
                }
                else {
                    this.kadrInfo.incrementCountErrET_ORG();
                }
            }
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    public JSONObject getDept() {
        return dept;
    }
}
