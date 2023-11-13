/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.csi.cf.cfbe.exception.TokenException;
import it.csi.cf.cfbe.filter.IrideIdAdapterFilter;
import it.csi.cf.cfbe.model.identity.Utente;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
public class MoonProxyService extends ProxyService {
	
	@Value("${api-manager-url}")
	private String apiManagerUrl;
	
	@Value("${moon-api-base-url}")
	private String moonApiBaseUrl;
	
	@Value("${moon-client-profile}")
	private String moonClientProfile;
	
	public Response getIdentitaMoon(HttpServletRequest req) {
		Utente utente = (Utente) req.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
		String nomePortale = ContextBFF.getTenant(req);
		String completePath = getBasePath() +"moon-identita?logon_mode=API_IDP_SHIB&nome_portale="+nomePortale+"&identificativo_utente=" +utente.getCodFisc() + "&nome=" + utente.getNome() + "&cognome=" + utente.getCognome();
		
		try {
			return doProxy(req, completePath, "POST");
		} catch (Exception e) {
			return ContextBFF.buildResponseError(400, e.getMessage());
		}
	}
	
	public Response getModuliWrapped(HttpServletRequest req) throws TokenException {
		Utente utente = (Utente) req.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
		String nomePortale = ContextBFF.getTenant(req);
		String completePath = getBasePath() +"moon-identita?logon_mode=API_IDP_SHIB&nome_portale="+nomePortale+"&identificativo_utente=" +utente.getCodFisc() + "&nome=" + utente.getNome() + "&cognome=" + utente.getCognome();
		String token = getApiManagerToken(null).getToken();
		
		RequestBody body = RequestBody.create("", okhttp3.MediaType.parse("application/json"));
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		Request request = new Request.Builder()
				.url(completePath)
				.method("POST", body)
				.addHeader("X-Request-Id", UUID.randomUUID().toString())
				.addHeader("Authorization", token)
				.addHeader("client-profile", moonClientProfile)
				.build();
		
		okhttp3.Response response;
		
		try {
			response = client.newCall(request).execute();
			
			if(response.isSuccessful()) {
				String bodyString = response.body().string();
				
				ObjectMapper mapper = new ObjectMapper();
				TypeReference<HashMap<String,Object>> typeRef 
			      = new TypeReference<HashMap<String,Object>>() {};
			      
				
				Map<String, Object> identitaMap = mapper.readValue(bodyString, typeRef);
	
				String identitaJwt = (String) identitaMap.get("idMoonToken");
				
				completePath = getBasePath() + "/moduli";
				
				request = new Request.Builder()
						.url(completePath)
						.method("GET", null)
						.addHeader("X-Request-Id", UUID.randomUUID().toString())
						.addHeader("Authorization", token)
						.addHeader("Moon-Identita-JWT", identitaJwt)
						.build();
				
				response = client.newCall(request).execute();
				
				if(response.isSuccessful()) {
					return convertResponseObject(response);
				} else {
					return ContextBFF.buildResponseError(response.code(), "Chiamata per moduli moon fallita");
				}
				
				
			} else {
				return ContextBFF.buildResponseError(response.code(), "Chiamata per identità moon fallita");
			}
			
		} catch (Exception e) {
			return ContextBFF.buildResponseError(500, "Impossibilie contattare API moon");
		}
	}

	@Override
	public Request createRequest(String url, String method, RequestBody requestBody, HttpServletRequest req) throws TokenException {
		String token = getApiManagerToken(null).getToken();
	
		Request request = new Request.Builder()
			.url(url)
			.method(method, requestBody)
			.addHeader("X-Request-Id", UUID.randomUUID().toString())
			.addHeader("Authorization", token)
			.build();
		
		String moonIdentitaJWT = req.getHeader("Moon-Identita-JWT");
		if(StringUtils.hasText(moonIdentitaJWT)) {
			request = request.newBuilder()
						.addHeader("Moon-Identita-JWT", moonIdentitaJWT)
						.build();
		} else {
			request = request.newBuilder()
						.addHeader("client-profile", moonClientProfile)
						.build();
		}
		
		return request;
	}


	@Override
	public String getBasePath() {
		return apiManagerUrl + moonApiBaseUrl;
	}

}
