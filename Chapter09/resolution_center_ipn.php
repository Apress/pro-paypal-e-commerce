<?php
$req = 'cmd=_notify-validate';
foreach ($_POST as $key => $value) {
    $value = urlencode(stripslashes($value));
    $req .= "&$key=$value";
}
// post back to PayPal system to validate
$header .= "POST /cgi-bin/webscr HTTP/1.0\r\n";
$header .= "Host: www.paypal.com:80\r\n";
$header .= "Content-Type: application/x-www-form-urlencoded\r\n";
$header .= "Content-Length: " . strlen($req) . "\r\n\r\n";
$fp = fsockopen ('www.paypal.com', 80, $errno, $errstr, 30);
// get the transaction type
$txn_type = $_POST['txn_type']; // should be equal to "new_case" if disputed
if ($txn_type == 'new_case') {
    // assign posted variables to local variables
    $txn_id = $_POST['txn_id'];
    $case_id = $_POST['case_id'];
    $case_type = $_POST['case_type'];
    $case_creation_date = $_POST['case_creation_date'];
    $reason_code = $_POST['reason_code'];
    // set email variables
    $to = $_POST['business'];
    $from = "PayPal Resolution Center";
    $subject = "New dispute filed";
    $msg = "A new dispute has been filed against you in å
    the PayPal Resolution Center";
    $msg .= "\n\nThe details of the dispute are as follows:";
    $msg .= "\n" . "Transaction ID: " . $txn_id ;
    $msg .= "\n" . "Case ID: " . $case_id;
    $msg .= "\n" . "Case Type: " . $case_type;
    $msg .= "\n" . "Case Created: " . $case_creation_date;
    $msg .= "\n" . "Reason: " . $reason_code;
} else {
    // do normal IPN processing (see Chapter 5)
    // obtain all the POST variables
    // set the email $to, $from, $subject, and $msg variables
}
// send the email
if (!$fp) {
    // HTTP ERROR
} else {
    fputs ($fp, $header . $req);
    while (!feof($fp)) {
    $res = fgets ($fp, 1024);
    if (strcmp ($res, "VERIFIED") == 0) {
        $mail_From = $from;
        $mail_To = $to;
        $mail_Subject = $subject;
        $mail_Body = $msg;
        mail($mail_To, $mail_Subject, $mail_Body, $mail_From);
    }
    else if (strcmp ($res, "INVALID") == 0) {
        // log for manual investigation
    }
}
fclose ($fp);
}
?>

