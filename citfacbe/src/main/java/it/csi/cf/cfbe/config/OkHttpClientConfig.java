/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Configuration
@Slf4j
public class OkHttpClientConfig {

	@Value("${ok-http-client.connect-timeout-seconds}")
	long connectTimeoutSeconds;

	@Value("${ok-http-client.read-timeout-seconds}")
	long readTimeoutSeconds;

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public OkHttpClient httpClient() {
		log.debug(
				"----------------------------------------------------------CREO CLIENT HTTP------------------------------------");
		return new OkHttpClient().newBuilder().connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
				.readTimeout(readTimeoutSeconds, TimeUnit.SECONDS).build();
	}

}
