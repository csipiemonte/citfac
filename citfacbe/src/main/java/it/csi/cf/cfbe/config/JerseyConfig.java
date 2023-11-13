/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.config;

import javax.ws.rs.Path;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

@Configuration
public class JerseyConfig extends ResourceConfig {

	Logger log = LoggerFactory.getLogger(JerseyConfig.class);

	public JerseyConfig() {
		// register(TestService.class);

		// packages("it.csi.cf.cfbe.services"); //-> non funge in java -jar, non trova
		// le risorse Caused by: java.io.FileNotFoundException:

		registerServices();
	}

	private void registerServices() {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Path.class));
		provider.findCandidateComponents("it.csi.cf.cfbe.controller").forEach(beanDefinition -> {
			try {
				log.info("registering {} to jersey config", beanDefinition.getBeanClassName());
				register(Class.forName(beanDefinition.getBeanClassName()));
			} catch (ClassNotFoundException e) {
				log.warn("Failed to register: {}", beanDefinition.getBeanClassName());
			}
		});
	}

}