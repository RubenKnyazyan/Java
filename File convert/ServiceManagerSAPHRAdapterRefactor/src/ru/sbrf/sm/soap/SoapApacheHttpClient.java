/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sbrf.sm.soap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
/**
 *
 * @author petrunin1-aa
 */
public class SoapApacheHttpClient {

    private final Logger LOGGER = Logger.getLogger(SoapApacheHttpClient.class);
    private  ArrayList <String> HostSMArr = new ArrayList();
    private  String POST_URL = "";
    private   String Cookie = "";
    private   String UserName = "";
    private   String UserPassword = "";
    //private static  String SOAPAction = "";
    private int cursor = 0;
    private int currentIteration = 1;
    private int maxIteration = 10000;

    @Override
    public String toString() {
        return "SoapApacheHttpClient{" +"UserName: " + UserName+ "; POST_URL: "+POST_URL+'}';
    }

    public String getUserName() {
        return UserName;
    }

    public void nextURL (){

        if(HostSMArr.size()>=cursor)
            cursor+=1;
        else
            cursor = 0;
    }
    public SoapApacheHttpClient(ArrayList <String> HostSMArr, String UserName, String UserPassowrd, int maxIteration) {
        this.HostSMArr = HostSMArr;
        this.UserName = UserName;
        this.UserPassword = UserPassowrd;
        this.POST_URL = this.HostSMArr.get(cursor);
        currentIteration = 1;
        this.maxIteration=maxIteration;
    }

    public SoapApacheHttpClient(String HostSM, String UserName, String UserPassowrd, int maxIteration) {
        ArrayList <String> HostSMArr = new ArrayList();
        HostSMArr.add(HostSM);
        this.HostSMArr = HostSMArr;
        this.UserName = UserName;
        this.UserPassword = UserPassowrd;
        this.POST_URL = this.HostSMArr.get(cursor);
        currentIteration = 1;
        this.maxIteration=maxIteration;
    }

    public  ResponseSOAPMessage sendPOST(String Body, String SOAPAction) throws IOException {
        int statusCode;
        String response;
        try ( //org.apache.commons.logging.LogFactory
              CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(POST_URL);

            RequestConfig rqCfg = RequestConfig.custom()
                    .setSocketTimeout(120*1000)
                    .build();
            httpPost.setConfig(rqCfg);

            httpPost.addHeader("SOAPAction", "\""+SOAPAction+"\"");
            if(currentIteration>maxIteration){
                httpPost.addHeader("Connection", "Close");
                currentIteration=1;
                //LOGGER.debug("Connection:=Close Cookie:="+Cookie);
            }
            else{
                httpPost.addHeader("Connection", "Keep-Alive");
                currentIteration+=1;
            }
            httpPost.addHeader("Accept-Encoding", "gzip, deflate");
            httpPost.addHeader("Accept", "*/*");
            //httpPost.addHeader("Host", "tv-sm-12r2-007.ca.sbrf.ru:13091");
            httpPost.addHeader("Cookie", Cookie);
            httpPost.addHeader("Cache-Control", "no-cache");
            httpPost.addHeader("Content-Type", "text/xml;charset=UTF-8");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
            String autorization = UserName+":"+UserPassword;
            httpPost.addHeader("Authorization", "Basic " + Base64Utils.encode(autorization.getBytes()));
            HttpEntity postParams = new StringEntity(Body,"UTF-8");
            httpPost.setEntity(postParams);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            //LOGGER.debug("POST Response Status:: " + statusCode);
            Header [] headers = httpResponse.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {

                if (headers[i].toString().contains("Set-Cookie")){
                    String[] cookie = headers[i].toString().split(":");
                    String[] Set_Cookie = cookie[1].split(";");
                    for (int j = 0; j < Set_Cookie.length; j++)
                        if(Set_Cookie[j].contains("JSESSIONID"))
                            Cookie = Set_Cookie[j];
                }
                LOGGER.debug( headers[i].toString());

            }
            //LOGGER.debug("Cookie:\r\n"+Cookie);
            response = IOUtils.toString(new InputStreamReader(httpResponse.getEntity().getContent(),StandardCharsets.UTF_8));

            return new ResponseSOAPMessage (Body, response, statusCode, POST_URL,UserName,Cookie,SOAPAction,cursor);

        } catch (UnsupportedOperationException er){
            LOGGER.error(er, er);
        } catch (IOException er){
            LOGGER.error(er, er);
        } catch (UnsupportedCharsetException er){
            LOGGER.error(er, er);
        }


        return null;

    }

}


