/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sbrf.sm;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import ru.sbrf.sm.cfg.ParseConfig;
import ru.sbrf.sm.hndlr.HandlerSMSAPHRAdapter;

/**
 *
 * @author petrunin1-aa
 */
public class MainApps {

    private static final Logger LOGGER = Logger.getLogger(MainApps.class);
    
    public static void main(String[] args) {
        
          String log4jPatch = "log4j.properties";
        String SettingsPatch = "settings.xml";

        for(String arg:args){
            try {
                if(arg.startsWith("-config="))
                    SettingsPatch = arg.split("=")[1];
                if(arg.startsWith("-log4j="))
                    log4jPatch = arg.split("=")[1];
            } catch (NumberFormatException e) {
                LOGGER.error("Неверные аргументы!",e);
            }
        }
        org.apache.log4j.PropertyConfigurator.configure(log4jPatch);
        LOGGER.info("Start application ServiceManagerSAPHRAdapter");

        ParseConfig parseConfig = new ParseConfig(SettingsPatch);
        LOGGER.debug(parseConfig.toString());

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(parseConfig.getServletPort()), 0);
            server.createContext("/v1/SMSAPHRAdapter/run", new HandlerSMSAPHRAdapter(parseConfig));
            Executor exec = Executors.newFixedThreadPool(parseConfig.getServletThread());
            server.setExecutor(exec);
            server.start();

            LOGGER.info("Порт "+parseConfig.getServletPort()+" успешно поднят");

        } catch (IOException ex) {
            LOGGER.error(ex, ex);
        }
        
    }
    
}
