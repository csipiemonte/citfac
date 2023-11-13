/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.proxy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Credenziali;
import it.csi.cf.cfbe.repository.CredenzialiRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
public class PPayProxyService extends ProxyService{
	
	@Value("${ppay-api-base-url}")
	private String ppayApiUrl;
	
	@Autowired
	private CredenzialiRepository credenzialiRepository;

	@Override
	public Request createRequest(String url, String method, RequestBody requestBody, HttpServletRequest req)
			throws Exception {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		String completePath = url.replace("{CF_COMUNE}", comune.getCf());
		
		String auth = getBasicAuth(comune);

		return new Request.Builder()
				.url(completePath)
				.addHeader("Authorization", auth)
				.method(method, requestBody)
				.build();
	}

	@Override
	public String getBasePath() {

		return ppayApiUrl + "organizations/{CF_COMUNE}/";
	}
	
	private String getBasicAuth(ConfigComune comune) throws Exception {		
		Credenziali credenziali = credenzialiRepository.findByComuneAndApi(comune, "PPAY");
		
		return Credentials.basic(credenziali.getUsername(), credenziali.getPassword());
	}

}
