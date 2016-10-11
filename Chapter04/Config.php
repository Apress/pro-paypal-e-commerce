<?php

class PayPal_EncryptedButtons_Config {
    // {{{ properties
    /****************************************
    *
    * Please edit the options below to reflect
    * your system configuration. If they are
    * incorrect, this program may not work as
    * expected.
    *
    ****************************************/
    /* Certificate ID */
    var $cert_id = "PZ9MJJTPW9NP8";
    /* PayPal E-mail Address */
    var $business = "seller@testaccount.com";
    /* Receiver E-Mail - E-Mail Address Payment will be sent to */
    /* Leave blank if the same as above */
    var $receiver = "";
    /* Base Directory - Base directory where all files will be stored */
    /* This should be outside the website root, and only readable by you */
    /* The trailing slash is REQUIRED */
    var $basedir = "/var/www/php_paypal/";
    /* Certificate Store - Directory in which all certificates are stored */
    /* Can be the name of a subdirectory under basedir, or another path */
    /* The trailing slash is REQUIRED */
    var $certstore = "certificates/";
    /* Temporary Directory - Where temporary files are stored regarding å
    the transaction. This should be under the base directory OR outside å
    the webroot, and only readable by you/the web server. Files from this å
    directory are automatically removed after use. The trailing slash is REQUIRED */
    var $tempdir = "/var/www/php_paypal/temp/";
    /* OpenSSL Path - Path to the OpenSSL Binary */
    /* If openssl isn't in your PATH, then change this to where å
    it's located, otherwise leave it as it is */
    /* No trailing slash */
    var $openssl = "openssl";
    /* Certificate Names - Names of all the certificates required */
    /* Your Private Key Filename */
    var $my_private = "/var/www/php_paypal/certificates/cert_key.pem";
    /* Your Public Certificate Filename */
    var $my_public = "/var/www/php_paypal/certificates/cert_key.pem";
    /* PayPal's Public Certificate Filename */
    var $paypal_public = "/var/www/php_paypal/certificates/paypal_public_cert.pem";
    // }}}
}
?>

