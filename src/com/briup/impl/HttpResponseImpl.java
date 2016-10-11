package com.briup.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.briup.http.HttpResponse;
import com.briup.utils.ConfigUtil;
import com.briup.utils.StatusCodeUtil;

public class HttpResponseImpl implements HttpResponse{
	
	private OutputStream out;
	private PrintWriter pw;
	private StringBuffer sbuff;
	
	public HttpResponseImpl(Socket client){
		try {
			out = client.getOutputStream();
			pw = new PrintWriter(new OutputStreamWriter(out));
			sbuff = new StringBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public OutputStream getOutputStream() throws Exception {
		return this.out;
	}

	@Override
	public PrintWriter getPrintWriter() throws Exception {
		return this.pw;
	}

	@Override
	public void setStatusLine(String statusCode) {
		//获取响应状态行,状态码为String
		sbuff.append("HTTP/1.1"+statusCode+StatusCodeUtil.getStatusMsg(statusCode));
		setCRLF();
	}
	@Override
	public void setStatusLine(int statusCode) {
		//获取响应状态行,状态码为int
		setStatusLine(statusCode+"");
	}

	
	@Override
	public void setResponseHeader(String hName, String hValue) {
		sbuff.append(hName+": "+hValue);
		setCRLF();
		
	}

	@Override
	public void setContentType(String contentType) {
		//sbuff.append("contentType"+contentType);
		setResponseHeader("contentType", contentType);
	}

	@Override
	public void setContentType(String contentType, String charsetName) {
		setResponseHeader("contentType",contentType+"charsetName"+charsetName);
	}

	@Override
	public void setCRLF() {
		sbuff.append("\r\n");
	}

	@Override
	public void printResponseHeader() {
		//把拼接好的String内容写给浏览器
		//包括响应状态行 响应消息报头 CRLF
		String str = sbuff.toString();
		//不能使用换行，不然图片不能显示
		pw.print(str);
		pw.flush();
	}
	
	
	@Override
	public void printResponseContent(String requestPath) {
		FileInputStream fis=null;
		
		try {
			File file = new File(ConfigUtil.getConfigValue("rootPath"),requestPath);
			fis = new FileInputStream(file);
			byte[] buf = new byte[1024];
			int len = -1;
			while((len=fis.read(buf))!=-1){
				out.write(buf, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

}
