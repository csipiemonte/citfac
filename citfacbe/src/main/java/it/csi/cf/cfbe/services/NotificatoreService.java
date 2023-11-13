/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.JwtCredentials;
import it.csi.cf.cfbe.model.notificatore.Contatti;
import it.csi.cf.cfbe.repository.JwtRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

@Slf4j
@Service
public class NotificatoreService {
	
	@Value("${notificatore-api-base-url}")
	private String apiBaseUrl;
	
	@Value("${notificatore-api-preferences}")
	private String UriPreferenze;
	
	@Value("${notificatore-api-message-store}")
	private String messageStoreUrl;
	
	@Autowired
	private JwtRepository jwtRepository;
	
	public Response acceptTerms(HttpServletRequest req, String cfParam) {
		String cf = ContextBFF.getCodiceFiscaleShib(req);
		
		String url = getUriPreferenze() + "terms";
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		Request request = new Request.Builder()
				.url(url)
				.method("GET", null)
				.addHeader("X-Request-Id", UUID.randomUUID().toString())
				.addHeader("x-authentication",getJwt(req))
				.addHeader("Shib-Iride-IdentitaDigitale", cf)
				.build();

		try {
			okhttp3.Response response = client.newCall(request).execute();
			
			if(response.isSuccessful()) {
				String responseBodyString = response.body().string();
				
				MessageDigest md = MessageDigest.getInstance("MD5");
			    md.update(responseBodyString.getBytes());
			    byte[] digest = md.digest();
			    
			    String termHash = DatatypeConverter
			      .printHexBinary(digest).toLowerCase();
			    
			    String requestBodyString = "{ \"hash\": \"" + termHash + "\" }";
			    
			    log.debug(requestBodyString);
	
				RequestBody body = RequestBody.create(requestBodyString, MediaType.parse("application/json"));
				
				url = getUriPreferenze() + "users/" + cfParam + "/terms";
				
				log.debug("Url: {}", url);
				
				request = new Request.Builder()
						.url(url)
						.method("PUT", body)
						.addHeader("X-Request-Id", UUID.randomUUID().toString())
						.addHeader("x-authentication", getJwt(req))
						.addHeader("Shib-Iride-IdentitaDigitale", cf)
						.build();
				log.debug("provo ad accettare i termini");
				response = client.newCall(request).execute();
			}
			
			return convertResponseObject(response);		
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public Contatti getCustomerInfo(HttpServletRequest req) {
		
		String cf = ContextBFF.getCodiceFiscaleShib(req);
		
		String url = getUriPreferenze() + "users/"+ cf + "/contacts";
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		Request request = new Request.Builder()
				.url(url)
				.method("GET", null)
				.addHeader("X-Request-Id", UUID.randomUUID().toString())
				.addHeader("x-authentication", getJwt(req))
				.addHeader("Shib-Iride-IdentitaDigitale", cf)
				.build();

		try (okhttp3.Response response = client.newCall(request).execute()) {
			log.debug("------------------------------------------------------------");
			if(response.isSuccessful()) {
				String bodyString = response.body().string();
				
				ObjectMapper mapper = new ObjectMapper();
				TypeReference<HashMap<String,Object>> typeRef 
			      = new TypeReference<HashMap<String,Object>>() {};
			      
				
				Map<String, Object> customerMap = mapper.readValue(bodyString, typeRef);
				
				log.debug("------------------------------------------------------------",customerMap.toString());
	
				return new Contatti((String) customerMap.get("sms"),(String) customerMap.get("email"), customerMap.get("push"));
			}
			
			return null;
			
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
    public Response doGet(HttpServletRequest req, String baseUrl, String jwtToken) throws IOException {
    	if(jwtToken == null) {
    		return ContextBFF.buildResponseError(500, "Jwt notificatore non presente");
    	}
    	
		String codiceFiscale = ContextBFF.getCodiceFiscaleShib(req);
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		Request request = new Request.Builder()
				.url(baseUrl)
				.method("GET", null)
				.addHeader("X-Request-Id", UUID.randomUUID().toString())
				.addHeader("x-authentication", jwtToken)
				.addHeader("Shib-Iride-IdentitaDigitale", codiceFiscale)
				.build();
		
		log.debug("request {}", request);

		try (okhttp3.Response response = client.newCall(request).execute()) {

			return convertResponseObject(response);

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
    }
    
    public Response doPut(HttpServletRequest req, String baseUrl, String jwtToken) throws IOException {
    	if(jwtToken == null) {
    		return ContextBFF.buildResponseError(500, "Jwt notificatore non presente");
    	}
    	
    	String codiceFiscale = ContextBFF.getCodiceFiscaleShib(req);

        String bodyString = req.getReader().lines().collect(Collectors.joining()); 
        
        RequestBody body =  RequestBody.create(bodyString, okhttp3.MediaType.parse("application/json"));
	
		log.debug("url {}", baseUrl);
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		Request request = new Request.Builder()
				.url(baseUrl)
				.method("PUT", body)
				.addHeader("X-Request-Id", UUID.randomUUID().toString())
				.addHeader("x-authentication", jwtToken)
				.addHeader("Shib-Iride-IdentitaDigitale", codiceFiscale)
				.build();

		try (okhttp3.Response response = client.newCall(request).execute()) {

			return convertResponseObject(response);

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
    }
    
    public Response doDelete(HttpServletRequest req, String baseUrl, String jwtToken) throws IOException {
    	if(jwtToken == null) {
    		return ContextBFF.buildResponseError(500, "Jwt notificatore non presente");
    	}
    	
    	String codiceFiscale = ContextBFF.getCodiceFiscaleShib(req);
       
		log.debug("url {}", baseUrl);
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		Request request = new Request.Builder()
				.url(baseUrl)
				.method("DELETE", null)
				.addHeader("X-Request-Id", UUID.randomUUID().toString())
				.addHeader("x-authentication", jwtToken)
				.addHeader("Shib-Iride-IdentitaDigitale", codiceFiscale)
				.build();

		try (okhttp3.Response response = client.newCall(request).execute()) {

			return convertResponseObject(response);

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
    }
    
    public String getUriPreferenze() {
    	return apiBaseUrl + UriPreferenze;
    }
    
    public String getUriMEssageStore() {
    	return apiBaseUrl + messageStoreUrl;
    }
    
    protected Response convertResponseObject(okhttp3.Response response) throws IOException {
		ResponseBuilder responseBuilder = Response.noContent();
		
		copyStatus(response, responseBuilder);
		copyBody(response, responseBuilder);
		copyHeader(response, responseBuilder);
		
		return responseBuilder.build();
	}

	protected void copyStatus(okhttp3.Response response, ResponseBuilder responseBuilder) {
		int responseCode = response.code();
		log.info("responseCode: '{}'", responseCode);
		responseBuilder.status(responseCode);
	}
	
	protected void copyHeader(okhttp3.Response response, ResponseBuilder responseBuilder) {
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

	protected void copyBody(okhttp3.Response response, ResponseBuilder responseBuilder) throws IOException {
		ResponseBody responseBody = response.body();
		if(responseBody!=null) {
			byte[] bytes = responseBody.bytes();
			responseBuilder.entity(bytes);
		}
	}
	
	private String getJwt(HttpServletRequest req) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, "NOTIFICATIONS");
		
		return jwt.getJwt();
	}
}
