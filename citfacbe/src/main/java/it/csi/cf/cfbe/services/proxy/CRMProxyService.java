/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.proxy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Token;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
public class CRMProxyService extends ProxyService{
	
	@Value("${api-manager-url}")
	private String apiManagerUrl;
	
	@Value("${crm-api-base-url}")
	private String crmApiBaseUrl;
	
	public Response ticketSearch(HttpServletRequest req) {
		String codiceFiscale = ContextBFF.getCodiceFiscaleShib(req);
		String completePath = getBasePath() + "tickets/search?filter={\"customer.codice_fiscale\":{\"eq\":\"" + codiceFiscale + "\"}}";
		
		try {
			return doProxy(req, completePath, "GET");
		} catch (Exception e) {
			e.printStackTrace();
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
	}

	@Override
	public Request createRequest(String url, String method, RequestBody requestBody, HttpServletRequest req) throws Exception {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		Token token = getApiManagerToken(comune); 
		
		return new Request.Builder()
				.url(url)
				.method(method, requestBody)
				.addHeader("Authorization", token.getToken()).build();
	}

	@Override
	public String getBasePath() {
		return apiManagerUrl + crmApiBaseUrl;
	}
}
