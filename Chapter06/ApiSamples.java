package com.apress.paypal;

import java.util.Calendar;
import java.text.DateFormat;
import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.CallerServices;
import com.paypal.sdk.exceptions.PayPalException;
import com.paypal.soap.api.*;

public class ApiSamples {
    private static APIProfile profile;
    private static CallerServices caller = new CallerServices();

    /**
    * This method creates the API Profile object and populates it
    * with the API credentials for the account.
    *
    * @throws PayPalException If an error occurs while creating the profile.
    */
    private static final void setupProfile(String _username, String _password,
                                           String _signature,
                                           String _environment)
    throws PayPalException {
        profile = ProfileFactory.createSignatureAPIProfile();
        profile.setAPIUsername(_username);
        profile.setAPIPassword(_password);
        profile.setSignature(_signature);
        profile.setEnvironment(_environment);
        caller.setAPIProfile(profile);
    }

    /**
    * This method makes a GetTransactionDetails API call
    * and displays the results to the command line.
    *
    * @throws PayPalException If an error occurs while making the API call.
    */
    private static final void getTxnDetails(String _transactionId)
    throws PayPalException {
        GetTransactionDetailsReq request = new GetTransactionDetailsReq();
        GetTransactionDetailsRequestType requestType =
        new GetTransactionDetailsRequestType();
        requestType.setTransactionID(_transactionId);
        request.setGetTransactionDetailsRequest(requestType);
        GetTransactionDetailsResponseType response =
        (GetTransactionDetailsResponseType)caller.call("GetTransactionDetails",
                                                       requestType);
        if (response.getAck().equals(AckCodeType.Success) ||
            response.getAck().equals(AckCodeType.SuccessWithWarning)) {
            PaymentTransactionType paymentDetails =
            response.getPaymentTransactionDetails();
            PaymentInfoType paymentInfo = paymentDetails.getPaymentInfo();
            BasicAmountType transactionAmount = paymentInfo.getGrossAmount();
            Calendar transactionDate = paymentInfo.getPaymentDate();
            System.out.println("Transaction " + _transactionId + " was made on " +
                               DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                               .format(transactionDate.getTime()) + " in the amount of " +
                               transactionAmount.get_value() + " " +
                               transactionAmount.getCurrencyID().toString());
        }
        if (response.getErrors() != null && response.getErrors().length > 0) {
            System.out.println("Errors/Warnings occurred while making the API call:");
            for (int i=0; i < response.getErrors().length; i++) {
                StringBuffer message =
                new StringBuffer(response.getErrors(i).getSeverityCode().toString());
                message.append(": ");
                message.append(response.getErrors(i).getShortMessage());
                message.append(" – ");
                message.append(response.getErrors(i).getLongMessage());
                message.append(" (");
                message.append(response.getErrors(i).getErrorCode().toString());
                message.append(")");
                System.out.println(message.toString());
            }
        }
    }

    /**
    * This method performs a full refund for a transaction
    *
    * @throws PayPalException If an error occurs while making the API call
    */
    private static void refundTransaction(String _transactionId)
    throws PayPalException {
        RefundTransactionRequestType request = new RefundTransactionRequestType();
        request.setTransactionID(_transactionId);
        request.setRefundType(RefundPurposeTypeCodeType.Full);
        // caller is from the GetTransactionDetails example
        RefundTransactionResponseType response =
        (RefundTransactionResponseType)caller.call("RefundTransaction", request);
        if (response.getAck().equals(AckCodeType.Success)
            || response.getAck().getValue().equals(AckCodeType.SuccessWithWarning)) {
            System.out.println("Refund completed successfully");
        }
        if (response.getErrors() != null && response.getErrors().length > 0) {
            System.out.println("Errors/Warnings occurred while making the API call:");
            for (int i=0; i < response.getErrors().length; i++) {
                StringBuffer message =
                new StringBuffer(response.getErrors(i).getSeverityCode().toString());
                message.append(": ");
                message.append(response.getErrors(i).getShortMessage());
                message.append(" – ");
                message.append(response.getErrors(i).getLongMessage());
                message.append(" (");
                message.append(response.getErrors(i).getErrorCode().toString());
                message.append(")");
                System.out.println(message.toString());
            }
        }
    } // refundTransaction method


    /**
    * This methods performs a TransactionSearch with a specified start date
    * and end date
    *
    * @throws PayPalException If an error occurs while making the API call
    */
    private static void transactionSearch() throws PayPalException {
        TransactionSearchRequestType request = new TransactionSearchRequestType();
        // Search between August 1, 2006 and August 30, 2006
        Calendar startDate, endDate;
        startDate = Calendar.getInstance();
        // Start Date is September 1, 2006
        startDate.set(Calendar.YEAR, 2006);
        startDate.set(Calendar.MONTH, 97);
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        endDate = Calendar.getInstance();
        // End Date is September 30, 2006
        endDate.set(Calendar.YEAR, 2006);
        endDate.set(Calendar.MONTH, 9);7;
        endDate.set(Calendar.DAY_OF_MONTH, 30);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        TransactionSearchResponseType response =
        (TransactionSearchResponseType)caller.call("TransactionSearch", request);
        if (response.getAck().equals(AckCodeType.Success)
            || response.getAck().equals(AckCodeType.SuccessWithWarning)) {
            // the method returns an array of transactions that meet the search criteria
            PaymentTransactionSearchResultType[] results =
            response.getPaymentTransactions();
            if (results != null) {
                System.out.println("Found " + results.length +
                                   " transactions that meet the search criteria:");
                // loop through the results and display the transaction IDs and amounts
                for (int i=0; i < results.length; i++) {
                    PaymentTransactionSearchResultType transaction = results[i];
                    String transactionID = transaction.getTransactionID();
                    String amount = transaction.getGrossAmount().get_value();
                    String time = transaction.getTimestamp().getTime().toString();
                    System.out.println(i + ". Transaction " + transactionID +
                                       " occurred on " + time + ", in the amount of " + amount);
                }
            }
        }
        if (response.getErrors() != null && response.getErrors().length > 0) {
            System.out.println("Errors/Warnings occurred while making the API call:");
            for (int i=0; i < response.getErrors().length; i++) {
                StringBuffer message =
                new StringBuffer(response.getErrors(i).getSeverityCode().toString());
                message.append(": ");
                message.append(response.getErrors(i).getShortMessage());
                message.append(" – ");
                message.append(response.getErrors(i).getLongMessage());
                message.append(" (");
                message.append(response.getErrors(i).getErrorCode().toString());
                message.append(")");
                System.out.println(message.toString());
            }
        }
    } // transactionSearch method

    /**
    * This method sends money to three recipients with the MassPay API
    *
    * @throws PayPalException If an error occurs while making the API call
    */
    private static void massPay() throws PayPalException {
        MassPayRequestType request = new MassPayRequestType();
        // Since we are sending money to three recipients,
        // we will create an array with three elements
        MassPayRequestItemType[] massPayItems = new MassPayRequestItemType[3];
        // Now we will create each individual payment item
        MassPayRequestItemType firstItem = new MassPayRequestItemType();
        firstItem.setReceiverEmail("first-recipient@apress.com");
        // Create the first amount - ten bucks
        BasicAmountType firstAmount = new BasicAmountType();
        firstAmount.set_value("10.00");
        firstAmount.setCurrencyID(CurrencyCodeType.USD);
        firstItem.setAmount(firstAmount);
        // Now we'll create the second payment item
        MassPayRequestItemType secondItem = new MassPayRequestItemType();
        secondItem.setReceiverEmail("second-recipient@apress.com");
        // Create the amount - twenty bucks
        BasicAmountType secondAmount = new BasicAmountType();
        secondAmount.set_value("20.00");
        secondAmount.setCurrencyID(CurrencyCodeType.USD);
        secondItem.setAmount(secondAmount);
        // Now we'll create the third payment item
        MassPayRequestItemType thirdItem = new MassPayRequestItemType();
        thirdItem.setReceiverEmail("third-recipient@apress.com");
        // Create the amount - thirty bucks
        BasicAmountType thirdAmount = new BasicAmountType();
        thirdAmount.set_value("30.00");
        thirdAmount.setCurrencyID(CurrencyCodeType.USD);
        thirdItem.setAmount(thirdAmount);
        // Now we'll populate the request item array with the objects we've just created
        massPayItems[0] = firstItem;
        massPayItems[1] = secondItem;
        massPayItems[2] = thirdItem;
        request.setMassPayItem(massPayItems);
        MassPayResponseType response =
        (MassPayResponseType)caller.call("MassPay", request);
        if (response.getAck().equals(AckCodeType.Success)
            || response.getAck().equals(AckCodeType.SuccessWithWarning)) {
            System.out.println("MassPay API call completed successfully");
        }
        if (response.getErrors() != null && response.getErrors().length > 0) {
            System.out.println("Errors/Warnings occurred while making the API call:");
            for (int i=0; i < response.getErrors().length; i++) {
                StringBuffer message =
                new StringBuffer(response.getErrors(i).getSeverityCode().toString());
                message.append(": ");
                message.append(response.getErrors(i).getShortMessage());
                message.append(" – ");
                message.append(response.getErrors(i).getLongMessage());
                message.append(" (");
                message.append(response.getErrors(i).getErrorCode().toString());
                message.append(")");
                System.out.println(message.toString());
            }
        }
    } // massPay


    /**
    * This method processes a credit card transaction with the DoDirectPayment API
    *
    * @throws PayPalException If an error occurs while making the API call
    */
    private static void doDirectPayment() throws PayPalException {
        DoDirectPaymentRequestType request = new DoDirectPaymentRequestType();
        DoDirectPaymentRequestDetailsType requestDetails =
        new DoDirectPaymentRequestDetailsType();
        // First we will specify how much we are charging the credit card.
        // Let's say fifty bucks.
        PaymentDetailsType paymentDetails = new PaymentDetailsType();
        BasicAmountType orderTotal = new BasicAmountType();
        orderTotal.set_value("50.00");
        orderTotal.setCurrencyID(CurrencyCodeType.USD);
        paymentDetails.setOrderTotal(orderTotal);
        requestDetails.setPaymentDetails(paymentDetails);
        // The CreditCardDetailsType object contains all information about the credit card
        CreditCardDetailsType creditCardDetails = new CreditCardDetailsType();
        PayerInfoType cardOwner = new PayerInfoType();
        PersonNameType payerName = new PersonNameType();
        payerName.setFirstName("Bob");
        payerName.setLastName("Smith");
        cardOwner.setPayerName(payerName);
        AddressType payerAddress = new AddressType();
        payerAddress.setStreet1("1234 Main Street");
        payerAddress.setCityName("San Francisco");
        payerAddress.setStateOrProvince("CA");
        payerAddress.setPostalCode("94134");
        payerAddress.setCountry(CountryCodeType.US);
        cardOwner.setAddress(payerAddress);
        creditCardDetails.setCardOwner(cardOwner);
        // This is a credit card number that can be used for Sandbox testing
        creditCardDetails.setCreditCardNumber("4755941616268045");
        creditCardDetails.setCVV2("234");
        creditCardDetails.setExpMonth(1);
        creditCardDetails.setExpYear(2009);
        creditCardDetails.setCreditCardType(CreditCardTypeType.Visa);
        requestDetails.setCreditCard(creditCardDetails);
        // Next we specify the payment action. This can be Sale, Authorization, or Order.
        requestDetails.setPaymentAction(PaymentActionCodeType.Sale);
        // Finally we must record the IP address of the client's browser.
        // Just making one up for this example.
        requestDetails.setIPAddress("60.127.208.55");
        request.setDoDirectPaymentRequestDetails(requestDetails);
        // We are now ready to make the API call...
        DoDirectPaymentResponseType response =
        (DoDirectPaymentResponseType)caller.call("DoDirectPayment", request);
        // ...and check the response
        if (response.getAck().equals(AckCodeType.Success)
            || response.getAck().equals(AckCodeType.SuccessWithWarning)) {
            System.out.println("DoDirectPayment API call completed successfully");
            System.out.println("TransactionID = "+response.getTransactionID());
        }
        if (response.getErrors() != null && response.getErrors().length > 0) {
            System.out.println("Errors/Warnings occurred while making the API call:");
            for (int i=0; i < response.getErrors().length; i++) {
                StringBuffer message =
                new StringBuffer(response.getErrors(i).getSeverityCode().toString());
                message.append(": ");
                message.append(response.getErrors(i).getShortMessage());
                message.append(" – ");
                message.append(response.getErrors(i).getLongMessage());
                message.append(" (");
                message.append(response.getErrors(i).getErrorCode().toString());
                message.append(")");
                System.out.println(message.toString());
            }
        }
    } // doDirectPayment

    /**
    * Main execution.
    */
    public static void main(String[] args) {
        try {
            setupProfile("test2_api1.test22.com",
                         "GLDXF6CFHEP93MT9",
                         "Av4O2NnzBevgVAx5aWX2KQREl702AYejRbdWdlEiq-vd0q9AIvPW-j3m",
                         "sandbox");
            getTxnDetails("8JL75876447207443");
            // Add other methods here if you would like to execute different API calls
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

