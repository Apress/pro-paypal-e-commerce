<?php
// {{{ constants
// {{{ error codes
define("PP_ERROR_OK", 0);
define("PP_ERROR_FILE", 1);
define("PP_ERROR_OPENSSL", 2);
define("PP_ERROR_DATA", 3);
define("PP_ERROR_PARAMS", 4);
define("PP_ERROR_NOTFOUND", 5);
define("PP_ERROR_UNKNOWN", 6);
// }}}
// }}}
require_once "Config.php";
class PayPal_EncryptedButtons {
    // {{{ properties
    /** Button Data Array */
    var $buttonData = array();
    /** Config Pointer */
    var $config;
    /** Internal Data Handler */
    var $_data;
    /** Random Transaction ID */
    var $_rnd;
    var $debug = true;
    // }}}
    // {{{ constructor
    /**
    * Constructs a new PayPal_EncryptedButtons object
    *
    * @access public
    */
    function PayPal_EncryptedButtons($config = array()) {
        $this->config = new PayPal_EncryptedButtons_Config;
        return;
    }
    // }}}
    // {{{ changeConfig()
    /**
    * Change a Configuration Directive after the class has been loaded
    *
    * @param string $name
    * @param string $value
    * @return bool
    * @access public
    *
    */
    function changeConfig($name, $value = "") {
        if ($this->config->$name) {
            $this->config->$name = $value;
            return true;
        }
        else {
            return false;
        }
    }
    // }}}
    // {{{ addButtonParam()
    /**
    * Add parameters to the button code
    *
    * @param mixed $names
    * @param mixed $values
    * @return int
    * @access public
    */
    function addButtonParam($names = "", $values = "") {
        if ((is_array($names) && !is_array($values)) || (!is_array($names) && is_array($values))) {
            return PP_ERROR_PARAMS;
        }
        elseif (is_array($names) && is_array($values)) {
            if (count($names) !== count($values)) {
                return PP_ERROR_PARAMS;
            }
            $i = 0;
            while ($i < count($names)) {
                $this->buttonData[$names[$i]] = $values[$i];
                $i++;
            }
            return PP_ERROR_OK;
        }
        else {
                if ($names == "" || $values == "") {
                return PP_ERROR_PARAMS;
            }
            else {
                $this->buttonData[$names] = $values;
                return PP_ERROR_OK;
            }
        }
    }
    // }}}
    // {{{ delButtonParam()
    /**
    * Deletes a Button Parameter
    *
    * @param string $name
    * @return int
    * @access public
    */
    function delButtonParam($name = "") {
        if ($name == "") {
            return PP_ERROR_PARAMS;
        }
        if ($this->buttonData[$name] !== null) {
            $this->buttonData[$name] = null;
            return PP_ERROR_OK;
        }
        return PP_ERROR_NOTFOUND + 10;
    }
    // }}}
    // {{{ encryptButtonData()
    /**
    * Encrypts the data in buttonData
    *
    * @return int
    * @access public
    */
    function encryptButtonData() {
        $this->encryptedButton = null;
        if (!is_dir($this->config->basedir)) {
            if (!mkdir($this->config->basedir)) {
                return PP_ERROR_NOTFOUND + 20;
            }
        }
        @chdir($this->config->basedir);
        $this->_data = "cmd=_xclick\n";
        $this->_data .= "business=".$this->config->business."\n";
        $this->_data .= "receiver_email=".$this->config->receiver_email."\n";
        foreach ($this->buttonData as $name => $val) {
        if ($val == null) { continue; }
            $this->_data .= $name."=".$val."\n";
        }
        $this->_data .= "cert_id=".$this->config->cert_id;
        $this->_rnd = rand(100000, 999999);
        if (!is_dir($this->config->tempdir)) {
            if (!@mkdir($this->config->tempdir)) {
                return PP_ERROR_NOTFOUND + 30;
            }
        }
        $f = @fopen($this->config->tempdir.$this->_rnd.".1", "w");
        //if (!$f) { return PP_ERROR_FILE; }
        if (!$f) { return 41; }
        fwrite($f, trim($this->_data), strlen(trim($this->_data)));
        fclose($f);
        if (!file_exists($this->config->my_private)) {
            return 145;
        }
        if (!file_exists($this->config->my_public)) {
            return 245;
        }
        if (!file_exists($this->config->paypal_public)) {
            return 345;
        }
        if (!file_exists($this->config->my_private) ||
        !file_exists($this->config->my_public) ||
        !file_exists($this->config->paypal_public)) {
            return PP_ERROR_NOTFOUND + 40;
        }
        $exec = $this->config->openssl." smime -sign -in ".$this->config->
        tempdir.$this->_rnd.".1 -signer ".$this->config->my_public." -inkey ".$this->
        config->my_private." -outform der -nodetach -binary > ".$this->config->
        tempdir.$this->_rnd.".2";
        $status1 = `$exec`;
        $exec = $this->config->openssl." smime -encrypt -des3 -binary -outform pem ".$this->config->paypal_public." < ".$this->config->
        tempdir.$this->_rnd.".2 > ".$this->config->tempdir.$this->_rnd.".3";
        $status2 = `$exec`;
        $this->encryptedButton = trim(file_get_contents($this->config->tempdir.$this->_rnd.".3"));
        @unlink($this->config->tempdir.$this->_rnd.".1");
        @unlink($this->config->tempdir.$this->_rnd.".2");
        @unlink($this->config->tempdir.$this->_rnd.".3");
        if (strpos($status1, "No such file or directory") !== false || strpos($status2, "No such file or directory") !== false) {
            return PP_ERROR_OPENSSL;
        }
        if (!$this->encryptedButton) {
            return PP_ERROR_DATA;
        }
        return PP_ERROR_OK;
    }
    // }}}
    // {{{ getButton()
    /**
    * Returns the Encrypted Button Contents
    *
    * @return string
    * @access public
    */
    function getButton() {
        if (!$this->encryptedButton) {
            return "";
        }
        return $this->encryptedButton;
    }
    // }}}
}
// }}}
?>

