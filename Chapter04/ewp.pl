#!/usr/bin/perl
#
# Script which uses openssl to encrypt Paypal payment buttons
#
# More details: http://256.com/gray/docs/paypal_encrypt/
#
use FileHandle;
use IPC::Open2;
use strict;
# private key file to use
my $MY_KEY_FILE = "sample_key.pem";
# public certificate file to use - should correspond to the $cert_id
my $MY_CERT_FILE = "sample_cert.pem";
# Paypal's public certificate
my $PAYPAL_CERT_FILE = "paypal_public_cert.pem";
# File that holds extra parameters for the paypal transaction.
my $MY_PARAM_FILE = "params.txt";
# path to the openssl binary
#my $OPENSSL = "/usr/bin/openssl";
#my $OPENSSL = "C:\\OpenSSL\\Bin\\openssl.exe";
my $OPENSSL = "/usr/local/bin/openssl";
# make sure we can execute the openssl utility
die "Could not execute $OPENSSL: $!\n" unless -x $OPENSSL;
##############
# Send arguments into the openssl commands needed to do the sign,
# encrypt, s/mime magic commands.
my $pid = open2(*READER, *WRITER,
"$OPENSSL smime -sign -signer $MY_CERT_FILE " .
"-inkey $MY_KEY_FILE -outform der -nodetach -binary " .
"| $OPENSSL smime -encrypt -des3 -binary -outform pem " .
"$PAYPAL_CERT_FILE")
|| die "Could not run open2 on $OPENSSL: $!\n";
# Write our parameters that we need to be encrypted to the openssl process.
open(PARAMS, "< $MY_PARAM_FILE")
|| die "Could not open '$MY_PARAM_FILE': $!\n";
while (<PARAMS>) {
chomp;
next if (m/^\#/ || m/^$/);
print WRITER "$_\n";
}
close(PARAMS);
# close the writer file-handle
close(WRITER);
# read in the lines from openssl
my @lines = <READER>;
# close the reader file-handle which probably closes the openssl processes
close(READER);
# combine them into one variable
my $encrypted = join('', @lines);
####################
# print our encrypted HTML button code
print qq[
<html>
<head><title> Sample.html </title></head>
<body>
<h1>Donate</h1>
<form action="https://www. paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_s-xclick">
<input type="hidden" name="encrypted" value="
$encrypted" />
<input type="submit" value="Buy Now" />
</form>
</body>
</html>
];

