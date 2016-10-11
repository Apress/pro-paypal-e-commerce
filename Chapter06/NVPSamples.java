package com.apress.paypal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.paypal.sdk.exceptions.FatalException;


public class NVPSamples {
    public static String call(String payload) throws Exception {
        StringBuffer request = new StringBuffer();
        request.append("USER=MyUser&PASSWORD=MyPassword&");
        request.append("SIGNATURE=9875lsjdf98734ljsdfks89l09kfkld&").append(payload);

        // Change this URL to the correct PayPal URL
        URL url = new URL("https://api.sandbox.paypal.com/nvp/");
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "text/namevalue");
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(request.toString().getBytes());
        out.close();

        // Read the gateway response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    } // call


    public static void getTxnDetails() throws Exception {
        NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "GetTransactionDetails");
        encoder.add("TRANSACTIONID", "8JL75876447207443");
        String strNVPString = encoder.encode();
        String ppresponse = call(strNVPString);
        NVPDecoder results = new NVPDecoder();
        results.decode(ppresponse);
        String transactionID = results.get("TRANSACTIONID");
        String amt = results.get("AMT");
        String paymentDate = results.get("PAYMENTDATE");
        if ("Success".equals(results.get("ACK"))) {
            System.out.println("Transaction " + transactionID + " was made on " +
                               paymentDate + " in the amount of " + amt);
        } else {
            showErrors(results);
        }
    }

    /**
    * This method performs a full refund for a transaction
    *
    * @throws PayPalException If an error occurs while making the API call
    */
    public static void refundTransaction(String _transactionId) throws Exception {
        NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "RefundTransaction");
        encoder.add("TRANSACTIONID", _transactionId);
        encoder.add("REFUNDTYPE", "Full");
        String nvpString = encoder.encode();
        String ppresponse = call(nvpString);
        NVPDecoder results = new NVPDecoder();
        results.decode(ppresponse);
        String refundTransactionId = results.get("REFUNDTRANSACTIONID");
        if ("Success".equals(results.get("ACK"))
            || "SuccessWithWarning".equals(results.get("AckACK"))) {
            System.out.println("Refund completed successfully");
            System.out.println("refundTransactionID = " + refundTransactionId);
        }
        showErrors(results);
    }


    /**
    * This methods performs a TransactionSearch
    * with a specified start date and end date
    *
    * @throws PayPalException If an error occurs while making the API call
    */
    public static void transactionSearch() throws Exception {
        NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "TransactionSearch");
        encoder.add("STARTDATE", "2006-8-10");
        encoder.add("ENDDATE", "2006-8-21");
        String strNVPRequest = encoder.encode();
        String strNVPResponse = call(strNVPRequest);
        NVPDecoder results = new NVPDecoder();
        results.decode(strNVPResponse);
        if ("Success".equals(results.get("ACK"))
            || "SuccessWithWarning".equals(results.get("ACK"))) {
            if (results.get("L_TRANSACTIONID0") != null
                && !results.get("L_TRANSACTIONID0").equals("")) {
                int intCount = 0;
                while (results.get("L_TRANSACTIONID" + intCount) != null
                       && results.get("L_TRANSACTIONID" + intCount).length() > 0) {
                    String timestamp = results.get("L_TIMESTAMP" + intCount);
                    String amt = results.get("L_AMT" + intCount);
                    intCount++;
                    System.out.println(intCount + ". Transaction " +
                                       results.get("L_TRANSACTIONID" + intCount) +
                                       " occurred on " + timestamp +
                                       ", in the amount of " + amt);
                }
            }
        } else {
            showErrors(results);
        }
    } // transactionSearch


    /**
    * This method sends money to three recipients with the MassPay API
    *
    * @throws Exception If an error occurs while making the API call
    */
    public static void massPay() throws Exception {
        NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "MassPay");
        encoder.add("CURRENCYCODE", "USD");
        encoder.add("L_EMAIL1", "tryme@ic.net");
        encoder.add("L_AMT1", "10.00");
        encoder.add("L_EMAIL1", "a@b.org");
        encoder.add("L_AMT2", "20.00");
        encoder.add("L_EMAIL2", "test33434@ic.net");
        encoder.add("L_AMT2", "30.00");
        String strNVPString = encoder.encode();
        String ppresponse = call(strNVPString);
        NVPDecoder results = new NVPDecoder();
        results.decode(ppresponse);
        if ("Success".equals(results.get("ACK"))) {
            System.out.println("MassPay API call completed successfully");
        } else {
            showErrors(results);
        }
    } // massPay


    /**
    * This method processes a credit card transaction with the DoDirectPayment API
    *
    * @throws PayPalException If an error occurs while making the API call
    */
    public static void doDirectPayment() throws Exception {
        NVPEncoder encoder = new NVPEncoder();
        encoder.add("METHOD", "DoDirectPayment");
        encoder.add("PAYMENTACTION", "Sale");
        encoder.add("AMT", "50.00");
        encoder.add("CREDITCARDTYPE", "Visa");
        encoder.add("ACCT", "4755941616268045");
        encoder.add("EXPDATE", "2009-10-02");
        encoder.add("CVV2", "234");
        encoder.add("FIRSTNAME", "Bob");
        encoder.add("LASTNAME", "Smith");
        encoder.add("STREET", "1234 Main Street");
        encoder.add("CITY", "San Francisco");
        encoder.add("STATE", "CA");
        encoder.add("ZIP", "94134");
        encoder.add("COUNTRYCODE", "US");
        encoder.add("CURRENCYCODE", "USD");
        String nvpString = encoder.encode();
        String ppresponse = call(nvpString);
        NVPDecoder results = new NVPDecoder();
        results.decode(ppresponse);
        String transactionId = results.get("TRANSACTIONID");
        if ("Success".equals(results.get("ACK"))) {
            System.out.println("DoDirectPayment API call completed successfully");
            System.out.println("TransactionID = " + transactionId);
        } else {
            showErrors(results);
        }
    }


    // displays error messages
    private static void showErrors(NVPDecoder results) {
        int i = 0;
        while (results.get("L_LONGMESSAGE" + i) != null
               && results.get("L_LONGMESSAGE" + i).length() > 0) {
            System,out,println("Severity: " + results.get("L_SEVERITYCODE" + i));
            System.out.println("Error Number: " + results.get("L_ERRORCODE" + i));
            System.out.println("Short Message: " + results.get("L_SHORTMESSAGE" + i));
            System.out.println("Long Message: " + results.get("L_LONGMESSAGE" + i));
            i++;
        }
    }
    public static void main(String[] args) {
        try {
            getTxnDetails();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
} // NVPSamples
