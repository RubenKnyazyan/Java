package soapclient.soap;

public class ResponseSOAPMessage {

        private String response;
        private String request;
        private int StatusCode;
        private String URL;
        private String UserName;
        private String Cookie;
        private String SOAPAction;
        private int cursor;

        public String toString() {
            return "ResponseSOAPMessage{response=" + this.response + ", request=" + this.request + ", StatusCode=" + this.StatusCode + ", URL=" + this.URL + ", UserName=" + this.UserName + ", Cookie=" + this.Cookie + ", SOAPAction=" + this.SOAPAction + ", cursor=" + this.cursor + '}';
        }

        public String getRequest() {
            return this.request;
        }

        public String getResponse() {
            return this.response;
        }

        public int getStatusCode() {
            return this.StatusCode;
        }

        public String getURL() {
            return this.URL;
        }

        public String getUserName() {
            return this.UserName;
        }

        public String getCookie() {
            return this.Cookie;
        }

        public String getSOAPAction() {
            return this.SOAPAction;
        }

        public int getCursor() {
            return this.cursor;
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


