package com.apress.paypal;

import java.util.Properties;
import java.util.Enumeration;
import com.paypal.sdk.profiles.EWPProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.EWPServices;
import com.paypal.sdk.exceptions.PayPalException;

public class CreateButton {

    // the button properties
    public static final String PROPERTIES_FILE = "button.params.properties";
    // path to your PKCS12 file
    public static final String PKCS12 = "./my_pkcs12.p12";
    // path to PayPal's public certificate
    public static final String PAYPAL_CERT = "./paypal_cert_pem.txt";
    // use https://www.sandbox.paypal.com if testing
    public static final String URL = "https://www.paypal.com";


    public static void main (String args[]) {
        // Check to see if the user provided a password
        if (args.length != 1) {
            System.out.println("You must provide a password.");
            System.exit(0);
        }
        // password used to encrypt your PKCS12 files
        // obtained from the command line
        String USER_PASSWORD = args[0];
        // Read properties file with a custom loader
        PropertiesLoader loader = new PropertiesLoader(); // custom loader
        Properties properties = loader.loadProperties(PROPERTIES_FILE);
        // First we will create the EWPProfile object
        try {
            EWPProfile ewpProfile = ProfileFactory.createEWPProfile();
            ewpProfile.setCertificateFile(PKCS12);
            ewpProfile.setPayPalCertificateFile(PAYPAL_CERT);
            ewpProfile.setPrivateKeyPassword(USER_PASSWORD);
            ewpProfile.setUrl(URL);
            StringBuilder buttonParameters = new StringBuilder();
            // Now we will define the button parameters for our payment button
            for (Enumeration e = properties.keys(); e.hasMoreElements();) {
                String key = (String)e.nextElement();
                buttonParameters.append(key + "=" + properties.getProperty(key) + "\n");
            }
            // Next we will create the EWPServices object
            // and tell it which EWPProfile object to use
            EWPServices ewpServices = new EWPServices();
            ewpServices.setEWPProfile(ewpProfile);
            // Finally we are ready to call the method to perform the button encryption
            String encryptedButton =
            ewpServices.encryptButton(buttonParameters.toString().getBytes());
            System.out.println(encryptedButton);
        }
        catch (PayPalException ppe) {
            System.out.println("An exception occurred when creating the button.");
            ppe.printStackTrace();
        }
    }
}

