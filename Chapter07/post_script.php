<?php
if (!empty($_POST)) {
    extract($_POST);
}
// specify the name for the output file
$filename = "trxlog.txt";
// open the file for output
$fp = fopen($filename,"a");
if ($_POST) {
    fputs($fp,"Post received.\n");
    foreach ($_POST as $key => $value) {
        fputs($fp,"$key: $value\n");
    }
    fputs($fp, "--------------------\n\n");
}
if (empty($_POST)) {
    fputs($fp,"No post received!");
}
fclose ($fp);
?>
