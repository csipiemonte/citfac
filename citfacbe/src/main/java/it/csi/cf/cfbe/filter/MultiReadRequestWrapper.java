/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.filter;

import org.apache.commons.io.input.BoundedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class MultiReadRequestWrapper extends HttpServletRequestWrapper {
	
	private static final Logger LOG = LoggerFactory.getLogger(MultiReadRequestWrapper.class);
	
	public static final Integer MAX_BYTE_SIZE = 1_048_576; // 1 MB 
	private StringBuilder body;
	
	public MultiReadRequestWrapper(HttpServletRequest request) throws IOException {
	    super(request);
	    body = new StringBuilder("");
	    try (
	        InputStream bounded = new BoundedInputStream(request.getInputStream(), MAX_BYTE_SIZE);
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bounded));){
	        String line;
	        while ((line = bufferedReader.readLine()) != null) {
	            body.append(line);
	        }
	    } catch(Exception e) {
	        LOG.error(e.getMessage());
	    }
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
	    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.toString().getBytes());
	    
	    return new ServletInputStream() {
	    	
	        public int read() throws IOException {
	            return byteArrayInputStream.read();
	        }
	        
	        @Override
	        public boolean isFinished() {
	            return byteArrayInputStream.available() == 0;
	        }
	        
	        @Override
	        public boolean isReady() {
	            return true;
	        }
	        
	        @Override
	        public void setReadListener(ReadListener readListener) {
	        }
	        
	    };
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
	    return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
	
}