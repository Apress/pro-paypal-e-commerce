package com.apress.paypal;

import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URLEncoder;
import com.paypal.sdk.exceptions.FatalException;

class NVPEncoder {
    private HashMap nvps = new HashMap();

    /**
    * This method adds the given name and value as a pair as a new entity
    * @param pstrName The String containing the name.
    * @param pstrValue The String containing the value for the given name.
    */
    public void add(String pstrName, String pstrValue)
    {
        nvps.put(pstrName, pstrValue);
    }

    /**
    * This method removes the given name along with the value for that name
    * @param pstrName The String containing the name.
    */
    public void remove(String pstrName)
    {
        if (nvps.containsKey(pstrName)) {
            nvps.remove(pstrName);
        }
    }

    /**
    * This method clears all the name value pair data
    *
    */
    public void clear() {
        nvps.clear();
    }

    /**
    * This method forms an URL encoded string in the NVP format. To form the encoded
    * string it takes all the name and values added in this object.
    * @return String The URL encoded string in the NVP format.
    */
    public String encode() throws FatalException {
        String nvp = "";
        try {
            Set setKeysSet = nvps.keySet();
            Iterator iteKeys = setKeysSet.iterator();
            for (int i = 0 ; i < nvps.size(); i++) {
                if (iteKeys.hasNext()) {
                    String lCurrentNameValue = "";
                    String key = (String)iteKeys.next();
                    if (nvps.get(key) == null || nvps.get(key).toString().equals("")) {
                        continue;
                    }
                    lCurrentNameValue += URLEncoder.encode(key,"UTF-8") + "=" +
                                         URLEncoder.encode(nvps.get(key)+"","UTF-8") ;
                    nvp += lCurrentNameValue;
                    if (iteKeys.hasNext()) {
                        nvp += "&";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new FatalException(e.getMessage());
        }
        return nvp;
    }
}
