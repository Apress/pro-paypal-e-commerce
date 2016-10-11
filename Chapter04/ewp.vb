Private mRecipientPublicCertPath As String
Private mSignerCert As CAPICOM.Certificate
Private mRecipientCert As CAPICOM.Certificate
Public Sub Class_Terminate()
Set mSignerCert = Nothing
Set mRecipientCert = Nothing
End Sub
Property Get RecipientPublicCertPath() As String
RecipientPublicCertPath = mRecipientPublicCertPath
End Property
Property Let RecipientPublicCertPath(value As String)
mRecipientPublicCertPath = value
Set mRecipientCert = New CAPICOM.Certificate
mRecipientCert.Load value
End Property
Public Sub LoadSignerCredential(signerPfxCertPath As String, å
signerPfxCertPassword As String)
Set mSignerCert = New CAPICOM.Certificate
mSignerCert.Load signerPfxCertPath, signerPfxCertPassword
End Sub
' This function takes an unencrypted variable string as a parameter and returns å
a string that has been signed and encrypted
Public Function SignAndEncrypt(ustr As String) As String
Dim bstr As String
Dim signed As String
Dim enveloped As String
Dim result As String
bstr = UnicodeStringToBinaryString(ustr)
signed = Sign(bstr)
enveloped = Envelope(signed)
result = FormatForTransport(enveloped)
SignAndEncrypt = result
End Function
' This function performs the digital signature on the encrypted string
Private Function Sign(bstr As String) As String
Dim result As String
Dim signer As CAPICOM.ISigner
Dim signed As CAPICOM.SignedData
Set signer = New CAPICOM.signer
signer.Certificate = mSignerCert
Set signed = New CAPICOM.SignedData
signed.Content = bstr
result = signed.Sign(signer, False, CAPICOM_ENCODE_BINARY)
Sign = result
Set signed = Nothing
Set signer = Nothing
End Function
' This function encrypts the button variables
Private Function Envelope(bstr As String) As String
Dim result As String
Dim enveloped As CAPICOM.EnvelopedData
Set enveloped = New CAPICOM.EnvelopedData
enveloped.Content = bstr
enveloped.Recipients.Add mRecipientCert
result = enveloped.Encrypt(CAPICOM_ENCODE_BINARY)
Envelope = result
Set enveloped = Nothing
End Function
' This function creates the encrypted variable string that will be assigned å
to the encrypted hidden form variable in your button code
Private Function FormatForTransport(bstr As String) As String
Dim result As String
Dim util As CAPICOM.Utilities
Dim b64 As String
Set util = New CAPICOM.Utilities
'First we Base64-encode the variables
b64 = util.Base64Encode(bstr)
'Next we remove all of the line breaks
result = Replace(b64, vbCrLf, "")
'Finally we add the PKCS7 header and footer
result = "-----BEGIN PKCS7-----" & result & "-----END PKCS7-----"
FormatForTransport = result
Set util = Nothing
End Function
' Utility function to convert unicode to binary
Private Function UnicodeStringToBinaryString(ustr As String) As String
Dim bstr As String
Dim bytes() As Byte
Dim utils As CAPICOM.Utilities
bytes = StrConv(ustr, vbFromUnicode)
Set utils = New CAPICOM.Utilities
bstr = utils.ByteArrayToBinaryString(bytes)
UnicodeStringToBinaryString = bstr
Set utils = Nothing
End Function

