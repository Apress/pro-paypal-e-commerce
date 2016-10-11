package com.apress.paypal;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.net.URLDecoder;

class NVPDecoder {
    private HashMap nvps = null;

    /**
    * This method returns the value for the given Name key.
    * @param pName The name for which the value is required
    * @return String The value for the given name, URL decoded
    */
    public String get(String pName) {
        return(String)nvps.get(pName);
    }

    /**
    * This method parses the string in the NVP format passed as the parameter
    * and stores them in a collection.
    * @param pPayload The string in the NVP format.
    */
    public void decode(String pPayload) throws Exception {
        StringTokenizer stTok = new StringTokenizer(pPayload,"&");
        nvps = new HashMap();
        while (stTok.hasMoreTokens()) {
            StringTokenizer stInternalTokenizer =
            new StringTokenizer(stTok.nextToken(),"=");
            nvps.put(URLDecoder.decode(stInternalTokenizer.nextToken(), "UTF-8"),
                     stInternalTokenizer.hasMoreTokens() ?
                     URLDecoder.decode(stInternalTokenizer.nextToken(),
                                       "UTF-8") : "");
        }
    }
}
