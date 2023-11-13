/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.proxy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Token;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
public class AppschedProxyService extends ProxyService{
	
	@Value("${api-manager-url}")
	private String apiManagerUrl;
	
	@Value("${appsched-api-base-url}")
	private String appschedApiBaseUrl;

	@Override
	public Request createRequest(String url, String method, RequestBody requestBody, HttpServletRequest req)
			throws Exception {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		Token token = getApiManagerToken(comune); 
		
		return new Request.Builder()
				.url(url)
				.method(method, requestBody)
				.addHeader("Authorization", token.getToken())
				.addHeader("Accept-Language", "it")
				.build();
	}

	@Override
	public String getBasePath() {
		return apiManagerUrl + appschedApiBaseUrl;
	}

}
