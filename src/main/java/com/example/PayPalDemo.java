package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

public class PayPalDemo {
	public static void runPayPalDemo() {
		System.out.println("\n");
		System.out.println("-------------------------------------");
		System.out.println("Running Paypal integration.");
		System.out.println("-------------------------------------");
		try {
			System.out.println("Acess Token Returned from Paypal");
			String accessTokenResponse = getAccessToken();
			getPaymentGateway(accessTokenResponse);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method uses the sand box API credentials for demo purpose.
	 * 
	 * returns the Access Token  from Paypal
	 * @return
	 * @throws PayPalRESTException
	 */
	private static String getAccessToken() throws PayPalRESTException {
		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "sandbox");
		String accessToken = new OAuthTokenCredential("AQkquBDf1zctJOWGKWUEtKXm6qVhueUEMvXO_-MCI4DQQ4-LWvkDLIN2fGsd",
				"EL1tVxAjhT7cJimnz5-Nsx9k2reTKSVfErNQF-CmrwJgxRtylkGTKlU4RvrX", sdkConfig).getAccessToken();
		System.out.println(accessToken);
		return accessToken;
	}

	/**
	 * Creates the Payment and prints the Approval URL to be shown to user
	 * for payment approval
	 * 
	 * @param accessTokenResponse
	 * @throws PayPalRESTException
	 */
	private static void getPaymentGateway(String accessTokenResponse) throws PayPalRESTException {
		//JSONObject obj = new JSONObject(accessTokenResponse);
		//String accessToken = obj.getString("access_token");
		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "sandbox");
		String accessTokenHeader = accessTokenResponse;
		APIContext apiContext = new APIContext(accessTokenHeader);
		apiContext.setConfigurationMap(sdkConfig);
		Amount amount = new Amount();
		amount.setCurrency("USD");
		amount.setTotal("12");
		Transaction transaction = new Transaction();
		transaction.setDescription("creating a payment");
		transaction.setAmount(amount);
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("https://devtools-paypal.com/guide/pay_paypal?cancel=true");
		redirectUrls.setReturnUrl("https://devtools-paypal.com/guide/pay_paypal?success=true");
		payment.setRedirectUrls(redirectUrls);

		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId("paypal");
		
		Payment createdPayment = payment.create(apiContext);
		
		List<Links> links = createdPayment.getLinks();
		for(Links link : links){
			if(link.getRel().equals("approval_url")){
				System.out.println("\nRedirect User here for payment approval:");
				System.out.println(link.getHref());
			}
		}
	}
}
