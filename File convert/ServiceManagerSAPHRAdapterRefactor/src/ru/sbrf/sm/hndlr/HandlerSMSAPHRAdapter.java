/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sbrf.sm.hndlr;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
import ru.sbrf.sm.adapter.FileAdapter;
import ru.sbrf.sm.cfg.ParseConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HandlerSMSAPHRAdapter implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(HandlerSMSAPHRAdapter.class);
    private ParseConfig cfg;
    private Thread thread = new Thread();

    public HandlerSMSAPHRAdapter(ParseConfig cfg) {
        this.cfg = cfg;
    }


    @Override
    public void handle(HttpExchange he) throws IOException {
        int statusCode = 200;
        boolean isBinary = false;
        byte[] resp = null;
        StringBuilder bodyResponse = new StringBuilder();

        Headers requestHeaders = he.getRequestHeaders();
        Headers responseHeaders = he.getResponseHeaders();
        String rqToken = "";

        LOGGER.info("called " + he.getRequestURI().toString());

        for (Map.Entry<String, List<String>> header : requestHeaders.entrySet()) {
            if(header.getKey().equalsIgnoreCase("Token"))
                rqToken = header.getValue().toString();
            LOGGER.debug("Request Header = "+header.getKey() + ":" + header.getValue().toString());
        }

        responseHeaders.add("Content-Type", "application/text; charset=utf-8");

        //[34jk5kljbkjb345i]
        if(rqToken.equals("["+cfg.getServletToken()+"]")){
            if ("POST".equalsIgnoreCase(he.getRequestMethod()) && statusCode != 400) {
                String requestBody  = IOUtils.toString(new InputStreamReader(he.getRequestBody(),StandardCharsets.UTF_8));
                LOGGER.debug("requestBody = "+requestBody);

                String filename = "";
                String sbmqexchange = "";
                for (String str : requestBody.split("&")){
                    if(str.indexOf("filename=")>=0)
                        filename=URLDecoder.decode(str.substring(str.indexOf("filename=")+"filename=".length()),StandardCharsets.UTF_8.name());
                    if(str.indexOf("sbmqexchange=")>=0)
                        sbmqexchange=URLDecoder.decode(str.substring(str.indexOf("sbmqexchange=")+"sbmqexchange=".length()),StandardCharsets.UTF_8.name());
                }

                if(filename.equals("")){
                    statusCode = 500;
                    bodyResponse.append("в BODY не указан filename");
                } else if (sbmqexchange.equals("")){
                    statusCode = 500;
                    bodyResponse.append("в BODY не указан filename");
                } else {
                    try {
                        if (thread.isAlive()){
                            statusCode = 500;
                            bodyResponse.append("Обработка уже ведется, повторите поппытку позже");
                        }else{
                            thread = new FileAdapter(cfg, filename, sbmqexchange);
                            thread.setDaemon(true);
                            thread.start();
                            bodyResponse.append("обработка начата");
                        }
                    } catch (RuntimeException | ThreadDeath  ex){
                        LOGGER.error(ex, ex);
                        statusCode = 500;
                    }
                }


            }
            if ("GET".equalsIgnoreCase(he.getRequestMethod()) && statusCode != 400) {
                bodyResponse.append("Unsupported request method with this context");
                statusCode = 405;
            }

        }else{
            LOGGER.error("requst Token = "+rqToken+" не совпадает с конфигом адаптера");
            statusCode = 401;
        }

        // sending response and aborting connection
        he.getRequestBody().close();
        if (!isBinary) {
            resp = bodyResponse.toString().getBytes(StandardCharsets.UTF_8);
        }
        for (Map.Entry<String, List<String>> header : responseHeaders.entrySet()) {
            LOGGER.debug("Response Header = "+header.getKey() + ":" + header.getValue().toString());
        }
        LOGGER.debug("responseBody = "+resp);
        he.sendResponseHeaders(statusCode, resp.length == 0 ? -1 : resp.length);
        if (resp.length > 0) {
            try (OutputStream out = he.getResponseBody()) {
                out.write(resp);
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
        }
        he.close();
    }
}
