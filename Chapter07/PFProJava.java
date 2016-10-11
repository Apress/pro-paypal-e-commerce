import com.Verisign.payment.PFProAPI;

class PFProJava {
    public static void main(String[] args) {
        PFProAPI pn = new PFProAPI();
        String parmList = "";
        // Set the certificate path since the Java client
        // is unable to read the PFPRO_CERT_PATH environment variable.
        // This example assumes a directory called "certs" resides in the
        // current working directory.
        pn.SetCertPath("certs");
        // Create the context for this transaction by supplying:
        // CreateContext(endpoint, port, timeout, proxy, proxy_port,
        // proxy_logon, proxy_pwd)
        pn.CreateContext("test-payflow.verisign.com", 443, 30, "", 0, "", "");

        // Build the NAME=VALUE pair parameter string
        // With Auth Credentials
        parmList =
        // We begin the parameter string with the Auth Credentials specific to your å
        account "PARTNER=VeriSign&VENDOR=MyOnlineStore&USER=MyOnlineStore å
        &PWD=MyPassword";
        // We will now specify transaction-specific parameters
        parmList += "&TRXTYPE=S"; // Indicates a Sale transaction
        parmList += "&TENDER=C"; // Indicates a Credit Card is being processed
        parmList += "&ACCT=5105105105105100&EXPDATE=0909"; // Add the cc num and exp date
        parmList += "&CVV2=123"; // Add the card security code
        parmList += "&STREET=123 Test Ave&ZIP=12345"; // Add the AVS info
        parmList += "&AMT=100.00"; // Add the amount

        // Submit the transaction and store the response
        String resp = pn.SubmitTransaction(parmList);
        // Display the response received
        System.out.println(resp);
        // Take appropriate action dependant on the RESULT code
        // First, parse the response variables into a Map
        Map responseVariables = new HashMap();
        StringTokenizer tokenizer = new StringTokenizer(resp, "&");
        // get each response value and put it into the responseVariables map
        while (tokenizer.hasMoreElements()) {
            String responseElement = tokenizer.nextToken();
            String variable = responseElement.substring(0, responseElement.indexOf("="));
            String value = responseElement.substring(responseElement.indexOf("=")+1);
            responseVariables.put(variable, value);
        }
        // now check RESULT
        String resultString = (String)responseVariables.get("RESULT");
        if (resultString != null) {
            byte result = Byte.parseByte(resultString);
            System.out.println("result = "+result);
            if (result == 0) {
                // Take appropriate action for a successful transaction
                System.out.println("Success");
            } else if (result < 0) {
                // Take appropriate action for a denied transaction
                System.out.println("Denied");
            } else if (result > 0) {
                // Take appropriate action for a communication error
                System.out.println("Error");
            }
        }
        // Destroy the context created for this transaction
        pn.DestroyContext();
    }
}

