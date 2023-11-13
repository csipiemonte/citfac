/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.utility;

import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.exception.TokenException;
import it.csi.cf.cfbe.model.TokenApiManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Component
public class CittaFacileToken {
	
	private static final Logger LOG = LoggerFactory.getLogger(CittaFacileToken.class);
	
	@Autowired
	private OkHttpClient okHttpClient;
	
	@Value("${api-manager-url}")
	private String apiManagerUrl;
	
	@Value("${citta-facile-token.consumer-key}") 
	private String key;
	
	@Value("${citta-facile-token.consumer-secret}") 
	private String secret;
	
	public TokenApiManager token(ConfigComune comune) throws TokenException {
		Request request = newTokenRequest(comune);
		
		try (okhttp3.Response response = okHttpClient.newCall(request).execute()) {
			
			if(!response.isSuccessful()) {
				throw new TokenException("Impossibile ottenere il token. Risposta con stato: "+response.code());
			}
			
			
			String bodyString = response.body().string();
			
			ObjectMapper om = new ObjectMapper(); 
			
			return om.readValue(bodyString, TokenApiManager.class);
			
		} catch (IOException ioe) {
			LOG.error("IOException", ioe);
			throw new TokenException("Impossibile ottenere il token", ioe);
		}

	}

	private Request newTokenRequest(ConfigComune comune) {
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/x-www-form-urlencoded");

		RequestBody body = RequestBody.create("grant_type=client_credentials", mediaType);

		Request request = new Request.Builder().url(apiManagerUrl + "/token").post(body)
				.addHeader("Authorization", "Basic " + getAuthorization(comune))
				.addHeader("Content-Type", "application/x-www-form-urlencoded").build();
		return request;
	}
	
	private String getAuthorization(ConfigComune comune) {
		String keySecret;
		if(comune == null)
			keySecret = key + ":" + secret;
		else
			keySecret = comune.getApiManagerKey() + ":" + comune.getApiManagerSecret();
		
		return Base64.getEncoder().encodeToString(keySecret.getBytes());
	}
	
}
