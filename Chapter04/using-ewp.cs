string paypalCertPath = Server.MapPath("App_Data/paypal_cert_pem.txt");
string signerPfxPath = Server.MapPath("App_Data/my_cert.p12");
string signerPfxPassword = GetSignerPfxPassword(); // retrieve your password
string clearText = "cmd=_xclick\n" +
                   "business=your@domain.com\n" +
                   "currency_code=GBP\n" +
                   "item_name=Tennis Balls ßü (£12 umlot OK)\n" +
                   "amount=15.00\n" +
                   "return=https://www.yourdomain.com/return\n" +
                   "cancel_return=https://www.yourdomain.com/cancel\n" +
                   "cert_id=C2XRTSNRF7E2S";
ButtonEncryption ewp = new ButtonEncryption();
ewp.LoadSignerCredential(signerPfxPath, signerPfxPassword);
ewp.RecipientPublicCertPath = paypalCertPath;
string result = ewp.SignAndEncrypt(clearText);
