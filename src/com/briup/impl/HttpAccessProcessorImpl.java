package com.briup.impl;


import com.briup.http.HttpAccessProcessor;
import com.briup.http.HttpRequest;
import com.briup.http.HttpResponse;
import com.briup.http.Servlet;
import com.briup.utils.ErrorPageUtil;
import com.briup.utils.MIMEUtil;
import com.briup.utils.ServletMappingUtil;

public class HttpAccessProcessorImpl implements HttpAccessProcessor{
	
	
	
	
	@Override
	public void processStaticResource(HttpRequest request, HttpResponse response) {
		String requestPath = request.getRequestPath();
		String[] str = requestPath.split("[.]");
		response.setStatusLine(200);
		response.setContentType(MIMEUtil.getContentType(str[str.length-1]));
		response.setCRLF();
		response.printResponseHeader();
		response.printResponseContent(requestPath);
		
	}

	@Override
	public void processDynamicResource(HttpRequest request, HttpResponse response) {
		/*
		response.setStatusLine(200);
		response.setContentType("html","utf-8");
		response.setCRLF();
		response.printResponseHeader();
		response.printResponseContent(request.getRequestPath());
		*/
		
		String requestPath = request.getRequestPath();
		String className = ServletMappingUtil.getServletClass(requestPath);
		response.setStatusLine(200);
		response.setContentType("html");
		response.setCRLF();
		response.printResponseHeader();
		
		try {
			Object o = Class.forName(className).newInstance();
			if(o instanceof Servlet){
				((Servlet) o).service(request, response);
			}else{
				throw new Exception("只有servlet类型对象才能让浏览器访问");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void sendError(int statusCode, HttpRequest request, HttpResponse response) {
		response.setStatusLine(statusCode);
		response.setContentType(MIMEUtil.getContentType("html"),"utf-8");
		response.setCRLF();
		response.printResponseHeader();
		response.printResponseContent(ErrorPageUtil.getErrorPagePath(statusCode));
	}

}
