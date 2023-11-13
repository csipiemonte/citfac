/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.Policy;
import it.csi.cf.cfbe.repository.PolicyRepository;

@Service
@Path("/api-public/policy")
@Produces(MediaType.APPLICATION_JSON)
public class PolicyController {
	
	@Autowired
	private PolicyRepository repository;
	
	@GET
	public String getPolicy(@Context HttpServletRequest request ) {
		Policy policy = repository.findByType("CRM");
		
		if(policy == null) {
			return "POLICY TEST";
		}
		
		return policy.getBody();
	}
	
	@GET
	@Path("/ppay")
	public String getPolicyPpay(@Context HttpServletRequest request ) {
		Policy policy = repository.findByType("PPAY");
		
		if(policy == null) {
			return "POLICY TEST";
		}
		
		return policy.getBody();
	}
					
}
