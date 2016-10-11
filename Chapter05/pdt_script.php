<?php
// PayPal Payment Data Transfer (PDT) Script
// Begin to form the HTTP request that will be sent to PayPal
$req = 'cmd=_notify-synch';
// Read the Transaction ID from the 'tx' variable sent by PayPal
$transaction_id = $_GET['tx'];
// Define your identify token
$identity_token = "o97T9kcQ5skO847fk7CLyjTmIDqh4nG9vAonIS1IbQxnhG5gPUgBFMv8Pw8";
// Append the Transaction ID and Identify Token to the request string
$req .= "&tx=$transaction_id&at=$identity_token";
// Post back to PayPal system to validate the transaction å
// and retrieve transaction details
// First, form the HTTP Header
$header .= "POST /cgi-bin/webscr HTTP/1.0\r\n";
$header .= "Content-Type: application/x-www-form-urlencoded\r\n";
$header .= "Content-Length: " . strlen($req) . "\r\n\r\n";
// This code posts to the PayPal Sandbox testing environment, and not the live site
// If your server is SSL enabled, use 443 instead of 80 to make a secure post
$fp = fsockopen ('www.sandbox.paypal.com', 80, $errno, $errstr, 30);
if (!$fp) {
// HTTP ERROR
}
else {
    // Make the POST
    fputs ($fp, $header . $req);
    // Read the body data
    $res = '';
    $headerdone = false;
    while (!feof($fp)) {
        $line = fgets ($fp, 1024);
        if (strcmp($line, "\r\n") == 0) {
            // Read the header
            $headerdone = true;
        }
        else if ($headerdone) {
            // Header has been read, now read the contents
            $res .= $line;
        }
    }
    // Parse the response from PayPal. Create a string array of values.
    $lines = explode("\n", $res);
    $keyarray = array();
    // Check the first line to see if it reads SUCCESS or FAIL
    if (strcmp ($lines[0], "SUCCESS") == 0) {
        for ($i=1; $i<count($lines);$i++) {
            list($key,$val) = explode("=", $lines[$i]);
            $keyarray[urldecode($key)] = urldecode($val);
        }
        // At this point, you have an array of values
        // that tell you a lot about the transaction
        // You may want to add logic to perform the following checks:
        // * the payment_status is Completed before processing an order
        // * the transaction ID has not been previously processed
        // * the receiver_email is your primary PayPal email
        // * the payment_amount and payment_currency are correct
        $firstname = $keyarray['first_name'];
        $lastname = $keyarray['last_name'];
        $itemname = $keyarray['item_name'];
        $amount = $keyarray['mc_gross'];
        // Display a customized thank you message to your customer
        echo ("<b> Dear $firstname $lastname ,</b>\n");
        echo ("Thank you for your purchase of $itemname \n");
        echo ("Your order has been processed for the amount of $amount \n");
        echo ("");
    }
    else if (strcmp ($lines[0], "FAIL") == 0) {
    // PayPal could not validate the transaction. Log and investigate!
    }
}
fclose ($fp);
?>
<p>
Your transaction has been completed, and a receipt for your purchase has been
emailed to you. You may log into your account at www.paypal.com to view details of
this transaction.</p><br/>

