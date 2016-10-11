Private Function Encrypt() As String
Dim paypalCertPath As String
Dim myCertPath As String
Dim myCertPassword As String
Dim paymentData As String
Dim ewp As clsEWP
Dim encrypted As String
paypalCertPath = "C:\paypal_cert_pem.txt"
myCertPath = "C:\ewp_cert.p12"
myCertPassword = GetCertPassword() ' retrieve your password securely here
paymentData = "cmd=_xclick" & vbLf & _
"business=your@email.com" & vbLf & _
"currency_code=GBP" & vbLf & _
"item_name=Tennis Balls ßü (£12 umlot OK)" & vbLf & _
"amount=15.00" & vbLf & _
"return=https://www.yourdomain.com/return" & vbLf & _
"cancel_return=https://www.yourdomain.com/cancel" & vbLf & _
"cert_id=C2XRTSNRF7E2S"
Set ewp = New clsEWP
ewp.RecipientPublicCertPath = paypalCertPath
ewp.LoadSignerCredential myCertPath, myCertPassword
encrypted = ewp.SignAndEncrypt(paymentData)
Encrypt = encrypted
End Function

