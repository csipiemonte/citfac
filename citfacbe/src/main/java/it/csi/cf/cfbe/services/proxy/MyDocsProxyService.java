/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.proxy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.exception.TokenException;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
public class MyDocsProxyService extends ProxyService{
	
	@Value("${api-manager-url}")
	private String apiManagerUrl;
	
	@Value("${mydocs-frontend-base-url}")
	private String mydocasApiBaseUrl;

	@Override
	public Request createRequest(String url, String method, RequestBody requestBody, HttpServletRequest req) throws TokenException {
		String token = getApiManagerToken(null).getToken();
		String codiceEnte = ContextBFF.getConfigComune(req).getCodiceComune();
		
		return new Request.Builder()
				.url(url)
				.method(method, requestBody)
				.addHeader("Authorization", token)
				.addHeader("Content-type", "application/json")
				.addHeader("ente", codiceEnte)
				.build();
	}

	@Override
	public String getBasePath() {
		return mydocasApiBaseUrl;
		//return apiManagerUrl + mydocasApiBaseUrl;
	}


}
