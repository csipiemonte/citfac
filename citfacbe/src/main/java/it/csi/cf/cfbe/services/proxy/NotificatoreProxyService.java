/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.proxy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.JwtCredentials;
import it.csi.cf.cfbe.exception.NotificatoreException;
import it.csi.cf.cfbe.repository.JwtRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class NotificatoreProxyService extends ProxyService {
	
	@Autowired
	private JwtRepository jwtRepository;

	@Override
	public Request createRequest(String url, String method, RequestBody requestBody, HttpServletRequest req)
			throws Exception {
		String cf = ContextBFF.getCodiceFiscaleShib(req);
		ConfigComune comune = ContextBFF.getConfigComune(req);
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, "NOTIFICATIONS");
		
		if(jwt == null) {
			throw new NotificatoreException("Jwt notificatore non presente");
    	}
		
		return new Request.Builder()
				.url(url)
				.method("GET", null)
				.addHeader("X-Request-Id", UUID.randomUUID().toString())
				.addHeader("x-authentication", jwt.getJwt())
				.addHeader("Shib-Iride-IdentitaDigitale", cf)
				.build();
	}

	@Override
	public String getBasePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void copyHeader(Response response, ResponseBuilder responseBuilder) {
		Headers headers = response.headers();
		Map<String, List<String>> headersMultiMap = headers.toMultimap();
		for(Entry<String, List<String>> entry: headersMultiMap.entrySet()) {
			String name = entry.getKey();
			for(String value : entry.getValue()) {
				responseBuilder.header(name, value);
				log.debug("response header: {} {}", name, value);
			}
		}
	}
	
}
