/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.proxy;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Token;
import it.csi.cf.cfbe.exception.TokenException;
import it.csi.cf.cfbe.model.TokenApiManager;
import it.csi.cf.cfbe.repository.TokenRepository;
import it.csi.cf.cfbe.services.utility.CittaFacileToken;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

@Slf4j
public abstract class ProxyService {
	
	@Autowired
	private OkHttpClient okHttpClient;
	
	@Autowired
	private TokenRepository repositoryToken;
	
	@Autowired
	private CittaFacileToken cittaFacileToken;
	
	public Response proxyGet(HttpServletRequest req, String path) {
		
		String completePath = getBasePath() + path;
		
		try {
			return doProxy(req, completePath, "GET");
		} catch (Exception e) {
			e.printStackTrace();
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
		
	}
	
	public Response proxyPut(HttpServletRequest req, String path) {
		
		String completePath = getBasePath() + path;
		
		try {
			return doProxy(req, completePath, "PUT");
		} catch (Exception e) {
			e.printStackTrace();
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
		
	}
	
	public Response proxyPost(HttpServletRequest req, String path, boolean removeQuery) {
		
		String completePath = getBasePath() + path;
		
		try {
			return doProxy(req, completePath, "POST", removeQuery);
		} catch (Exception e) {
			e.printStackTrace();
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
		
	}
	
	public Response proxyPost(HttpServletRequest req, String path) {
		
		String completePath = getBasePath() + path;
		
		try {
			return doProxy(req, completePath, "POST");
		} catch (Exception e) {
			e.printStackTrace();
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
		
	}
	
	public Response proxyDelete(HttpServletRequest req, String path) {
		
		String completePath = getBasePath() + path;
		
		try {
			return doProxy(req, completePath, "DELETE");
		} catch (Exception e) {
			e.printStackTrace();
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
		
	}
	
	public Response doProxy(HttpServletRequest req, String url, String method, boolean removeQuery) throws Exception {

		String completePath = url;
		
		RequestBody requestBody = createRequestBody(req, method);
		
		Request request = createRequest(completePath, method, requestBody, req);
		
		log.debug("effettuo chiamata verso: {}", completePath);
		
		okhttp3.Response response = okHttpClient.newCall(request).execute();

		return  convertResponseObject(response);
		
	}
	
	public Response doProxy(HttpServletRequest req, String url, String method) throws Exception {
		String queryString = req.getQueryString() != null ? "?" +req.getQueryString() : "";
		String completePath = url + queryString;
		
		RequestBody requestBody = createRequestBody(req, method);
		
		Request request = createRequest(completePath, method, requestBody, req);
		
		log.debug("effettuo chiamata verso: {}", completePath);
		
		okhttp3.Response response = okHttpClient.newCall(request).execute();

		return  convertResponseObject(response);
		
	}
	
	private RequestBody createRequestBody(HttpServletRequest req, String method) throws IOException {
		String bodyString=req.getReader().lines().collect(Collectors.joining());
		
		if(bodyString.length() == 0) 
			return method == "GET" ? null : RequestBody.create("", okhttp3.MediaType.parse("application/json"));;
		
		return RequestBody.create(bodyString, MediaType.parse("application/json"));
	}
	
	public abstract Request createRequest(String url, String method, RequestBody requestBody, HttpServletRequest req) throws Exception ;
	
	public abstract String getBasePath(); 
	
	protected Token getApiManagerToken(ConfigComune comune) throws TokenException {
		String codiceComune = comune == null ? null : comune.getCodiceComune();
		Token token = repositoryToken.findByTypeAndCodiceComune("API_MANAGER", codiceComune);
		
		if(token == null || token.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
			TokenApiManager tokenApiManager = cittaFacileToken.token(comune);
			token = Token.builder()
					.id(token != null ? token.getId() : null)
					.token( tokenApiManager.getToken_type() + " " + tokenApiManager.getAccess_token())
					.type("API_MANAGER")
					.expiryDate(new Timestamp(System.currentTimeMillis() + (tokenApiManager.getExpires_in() * 1000)))
					.codiceComune(codiceComune)
					.build();
			
			repositoryToken.save(token);
		}
		
		return token;	
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
		String contentType = response.header("Content-Type");
		responseBuilder.header("Content-Type", contentType);
	}

	protected void copyBody(okhttp3.Response response, ResponseBuilder responseBuilder) throws IOException {
		ResponseBody responseBody = response.body();
		if(responseBody!=null) {
			byte[] bytes = responseBody.bytes();
			responseBuilder.entity(bytes);
		}
	}
}
