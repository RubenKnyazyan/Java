/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sbrf.sm.soap;

/**
 *
 * @author petrunin1-aa
 */
public class ResponseSOAPMessage {

    private String response;
    private String request;
    private int StatusCode;
    private String URL;
    private String UserName;
    private String Cookie;
    private String SOAPAction;
    private int cursor;

    @Override
    public String toString() {
        return "ResponseSOAPMessage{" + "response=" + response + ", request=" + request + ", StatusCode=" + StatusCode + ", URL=" + URL + ", UserName=" + UserName + ", Cookie=" + Cookie + ", SOAPAction=" + SOAPAction + ", cursor=" + cursor + '}';
    }

    public String getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public String getURL() {
        return URL;
    }

    public String getUserName() {
        return UserName;
    }

    public String getCookie() {
        return Cookie;
    }

    public String getSOAPAction() {
        return SOAPAction;
    }

    public int getCursor() {
        return cursor;
    }

 

    public ResponseSOAPMessage(String request, String response, int StatusCode, String URL, String UserName, String Cookie, String SOAPAction, int cursor) {
        this.request = request;
        this.response = response;
        this.StatusCode = StatusCode;
        this.URL = URL;
        this.UserName = UserName;
        this.Cookie = Cookie;
        this.SOAPAction = SOAPAction;
        this.cursor = cursor;
            
    }


    
}

