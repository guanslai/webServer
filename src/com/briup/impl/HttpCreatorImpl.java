package com.briup.impl;

import java.net.Socket;

import com.briup.http.HttpAccessProcessor;
import com.briup.http.HttpCreator;
import com.briup.http.HttpRequest;
import com.briup.http.HttpResponse;

public class HttpCreatorImpl implements HttpCreator{

	private HttpRequest request;
	private HttpResponse response;
	private HttpAccessProcessor processor;
	
	public HttpCreatorImpl(Socket client){
		request =  new HttpRequestImpl(client);
		response = new HttpResponseImpl(client);
		processor = new HttpAccessProcessorImpl();
	}

	@Override
	public HttpRequest getHttpRequest() {
		return request;
	}

	@Override
	public HttpResponse getHttpResponse() {
		return response;
	}

	@Override
	public HttpAccessProcessor getHttpAccessProcessor() {
		return processor;
	}

}
