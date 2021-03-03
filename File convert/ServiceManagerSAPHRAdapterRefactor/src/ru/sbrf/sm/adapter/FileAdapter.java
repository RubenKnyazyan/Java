/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.sbrf.sm.adapter;

/**
 *
 * @author knyazyan-ra
 */

import ru.sbrf.sm.cfg.ParseConfig;
import ru.sbrf.sm.file.GenerateOutputFile;
import ru.sbrf.sm.kadrinfo.KadrInfo;
import ru.sbrf.sm.parser.XMLDeptParser;
import ru.sbrf.sm.parser.XMLPersonParser;
import ru.sbrf.sm.soap.SenderRezult;
import ru.sbrf.sm.soap.SoapApacheHttpClient;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileAdapter extends Thread {

    private String fileName;
    private String sbmqexchenge;
    private ParseConfig config;

    private static final Logger LOGGER = Logger.getLogger(FileAdapter.class);

    private String sourcePath = "C:\\Users\\knyazyan-ra\\Documents\\Локальные документы\\SAP\\";
    public FileAdapter(ParseConfig config, String fileName, String sbmqexchenge) {

        this.sbmqexchenge = sbmqexchenge;
        this.config = config;
        this.sourcePath = config.getPath() + fileName;
    }
    @Override
    public void run() {
        try {

            KadrInfo kadrInfo = new KadrInfo(new File(this.sourcePath));
            //для создание переделанного файла
            GenerateOutputFile outputFile = new GenerateOutputFile(config.getPath()+"\\TRANSFORMED_"+kadrInfo.getOrgNumber()+".csv");
            //устанавливаем количство потоков
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            for (String xmlDept : kadrInfo.getListET_ORG()) {

                XMLDeptParser deptParser = new XMLDeptParser(xmlDept,outputFile,kadrInfo,this.config.getET_ORG_config());
                executorService.execute(deptParser);
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) {  }

            LOGGER.info("DEPT transformed " + kadrInfo.getListET_ORG().size());
            executorService = Executors.newFixedThreadPool(10);
            for (String xmlPerson : kadrInfo.getListET_PERSON()) {

                XMLPersonParser personParser = new XMLPersonParser(xmlPerson,outputFile,kadrInfo,this.config.getET_PERSON_config());
                executorService.execute(personParser);
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) {  }
            LOGGER.info("PERSON transformed " + kadrInfo.getListET_PERSON().size());

            if (outputFile.writeFile()) {

                String rezultText = "";
                rezultText += "Общее кол-во item ET_ORG = " + kadrInfo.getCountET_ORG()+"\r\n";
                rezultText += "Общее кол-во item ET_PERSON = " + kadrInfo.getCountET_PERSON()+"\r\n";
                rezultText += "Кол-во не удачных трансформированных item ET_ORG = " + kadrInfo.getCountErrET_ORG() +"\r\n";
                rezultText += "Кол-во не удачных трансформированных item ET_PERSON = " + kadrInfo.getCountErrET_PERSON() +"\r\n";

                LOGGER.info("File created");
                LOGGER.info(rezultText);

                SenderRezult senderRezult = new SenderRezult(kadrInfo, new SoapApacheHttpClient(config.getHostSM(), config.getUserSM(),config.getPasswordSM(),0), sbmqexchenge, rezultText, this.config);
                senderRezult.run();

            }
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}

    