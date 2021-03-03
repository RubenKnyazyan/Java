/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.sbrf.sm.file;

/**
 *
 * @author knyazyan-ra
 */

import org.json.simple.JSONObject;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.ArrayList;

public class GenerateOutputFile {

    private ArrayList<JSONObject> personList = new ArrayList<>();
    private ArrayList<JSONObject> deptList = new ArrayList();

    private String filePath;

    public GenerateOutputFile(String Path)  {

        this.filePath = Path;
    }

    public Boolean addPerson(JSONObject person) {

        try {
            //так как несколько потоков будет писать в один узел
            synchronized (this.personList) {

                this.personList.add(person);
            }
            return true;
        }
        catch (Exception ex) {

            return false;
        }
    }
    public Boolean addORG(JSONObject dept) {

        try {
            //так как несколько потоков будет писать в один узел
            synchronized (this.deptList) {

                this.deptList.add(dept);
            }
            return true;
        }
        catch (Exception ex) {

            return false;
        }
    }

    public Boolean writeFile() {

        try {

             Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.filePath),"UTF-8"));
            
//            FileWriter fileWriter = new FileWriter(this.filePath, StandardCharsets.UTF_8);
//            //записываем dept
            for (JSONObject object : this.deptList) {

                out.write(object.toJSONString()+"™\n");
//                fileWriter.append(object.toJSONString());
//                fileWriter.append("™\n");
            }
//            //записываем person
            for (JSONObject object : this.personList) {

                out.write(object.toJSONString()+"™\n");//
//                fileWriter.append(object.toJSONString());
//                fileWriter.append("™\n");
            }
            out.close();
//            //save
//            fileWriter.flush();
//            fileWriter.close();

            return true;
        }
        catch(Exception ex) {

            ex.printStackTrace();
            return false;
        }
    }
}
