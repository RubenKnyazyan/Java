package soapclient.soap;


import org.apache.axiom.util.base64.Base64Utils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public  class SoapApacheHttpClient {

    private String Cookie = "";
    private Auth autorization;

    public Auth getAutorization() {
        return autorization;
    }

    public void setAutorization(Auth autorization) {
        this.autorization = autorization;
    }

    public SoapApacheHttpClient() {

    }

    public SoapApacheHttpClient(Auth autorization) {

        this.autorization = autorization;
    }

    public ResponseSOAPMessage sendPOST(String body, String SOAPAction) throws IOException {

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            Throwable throwable = null;
            try {

                HttpPost httpPost = new HttpPost(this.autorization.getHost());
                RequestConfig rqCfg = RequestConfig.custom().setSocketTimeout(120000).build();
                httpPost.setConfig(rqCfg);
                httpPost.addHeader("SOAPAction", "\"" + SOAPAction + "\"");

                httpPost.addHeader("Accept-Encoding", "gzip, deflate");
                httpPost.addHeader("Accept", "*/*");
                httpPost.addHeader("Cookie", this.Cookie);
                httpPost.addHeader("Cache-Control", "no-cache");
                httpPost.addHeader("Content-Type", "text/xml;charset=UTF-8");
                httpPost.addHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");

                String autorizationString = this.autorization.getLogin() + ":" + this.autorization.getPassword();
                httpPost.addHeader("Authorization", "Basic " + Base64Utils.encode(autorizationString.getBytes()));
//                httpPost.addHeader("Authorization", "Basic " + Base64.encodeBase64(autorizationString.getBytes()));
                String value = "Basic " + Base64.getEncoder().encodeToString((this.autorization.getLogin()  + ":" + this.autorization.getPassword()).getBytes());
//                httpPost.addHeader("Authorization", value);
//                httpPost.addHeader("Authorization", "Basic " + Base64Utils.encode(autorizationString.getBytes()));
//
                HttpEntity postParams = new StringEntity(body, ContentType.parse("UTF-8"));
                httpPost.setEntity(postParams);
                CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                Header[] headers = httpResponse.getAllHeaders();

                for (int i = 0; i < headers.length; ++i) {
                    if (headers[i].toString().contains("Set-Cookie")) {
                        String[] cookie = headers[i].toString().split(":");
                        String[] Set_Cookie = cookie[1].split(";");

                        for (int j = 0; j < Set_Cookie.length; ++j) {
                            if (Set_Cookie[j].contains("JSESSIONID")) {
                                this.Cookie = Set_Cookie[j];
                            }
                        }
                    }

                    System.out.println(headers[i].toString());
                }

                String response = IOUtils.toString(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8));
                ResponseSOAPMessage responseSOAPMessage = new ResponseSOAPMessage(body, response, statusCode, this.autorization.getHost(), this.autorization.getLogin(), this.Cookie, SOAPAction, 0);

                return responseSOAPMessage;
            } catch (Exception EX) {
                System.out.println(EX.getMessage());
                EX.printStackTrace();
            } finally {
                if (httpClient != null) {

                    if (throwable != null) {
                        try {
                            httpClient.close();
                        } catch (Throwable closeThrowable) {
                            throwable.addSuppressed(closeThrowable);
                        }
                    } else {
                        httpClient.close();
                    }
                }
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}