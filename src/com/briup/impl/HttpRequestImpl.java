package com.briup.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.briup.http.HttpRequest;
import com.briup.utils.ConfigUtil;
import com.briup.utils.ServletMappingUtil;

public class HttpRequestImpl implements HttpRequest {

	private Socket client;
	private String protocol;// 请求协议
	private String requestMethod; // 请求方法
	private String requestPath; // 请求路径
	private Map<String, String> requestHeader; // 请求报头
	private Map<String, String> paramters; // 请求的传的参数值
	private BufferedReader br;
	private boolean isNullrequest;

	public HttpRequestImpl(Socket client) {
		this.client = client;
		requestHeader = new HashMap<String, String>();
		paramters = new HashMap<String, String>();
		getInfos();
	}

	private void getInfos() {
		try {
			br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String requestLine = br.readLine();

			if (requestLine == null) {
				isNullrequest = true;
				return;
			}

			parseRequestLine(requestLine);

			String rh = "";
			// 因为报头后面有/r/t，因此可以用“”判断是否是报头的值
			while (!"".equals(rh = br.readLine())) {
				// 解析报头
				parseRequestHeader(rh);
			}

			if (br.ready()) {
				// ？？？为什么不能用readLine()？
				// readLine()只能读取文本 返回String
				// read() 返回 int
				char[] buf = new char[1024];
				int len = br.read(buf);
				String parameters = new String(buf, 0, len);
				parseParameterByPost(parameters);
			} else {
				// 如果用get方式提交的请求 Path的形式为 /text.html?id=2&name=tom
				// 调用方法，将requestPath作为参数传值
				parseParameterByGet(requestPath);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseRequestLine(String requestLine) throws Exception {
		// 用“ ”来分割
		String[] str = requestLine.split(" ");
		// 判断请求行的格式，如果不能分割成三行就不符合协议
		if (str.length != 3) {
			throw new Exception("请求行的格式不符合HTTP协议");
		}
		// 将分割的第一个值赋给请求方法requestMethod
		this.requestMethod = str[0];
		// 路径
		this.requestPath = str[1];
		// 端口号
		this.protocol = str[2];
	}

	// 解析请求报头
	private void parseRequestHeader(String rh) throws Exception {

		String[] str = rh.split(": ");
		// 如果报头分割的值个数不是2个 则不符合要求
		if (str.length != 2) {
			throw new Exception("报头不符合要求");
		}
		requestHeader.put(str[0], str[1]);
	}

	// 解析请求正文
	private void parseParameterByPost(String strParameter) {
		// id=2&name=tom
		// 用&符号分割开， id=2 和 name=tom，分别传给字符串数组
		String[] str = strParameter.split("[&]");
		// 遍历字符串数组
		for (String kv : str) {
			// 用"="分割 得到 id 2或者name tom
			String[] kvStr = kv.split("=");
			// 赋值
			String parameterName = kvStr[0];
			String parameterValue = null;
			if (kvStr.length == 2) {
				parameterValue = kvStr[1];
			}

			// 将值添加到Map中
			paramters.put(parameterName, parameterValue);

		}
	}

	// 解析get形式提交的请求，需要传requestPah作为参数
	private void parseParameterByGet(String requestPath) {
		// 用？分割，因为？是特殊字符，加上[]
		String[] str = requestPath.split("[?]");

		// 如果没有传值，直接返回
		if (str.length <= 1) {
			return;
		}
		// 获取？后面的值 id=2&name=tom
		// 并且调用post请求提交的解析方法
		parseParameterByPost(str[1]);
		// 将获取的？前面的值/text.html 作为Path
		this.requestPath = str[0];

	}

	@Override
	public String getProtocol() {

		return this.protocol;
	}

	@Override
	public String getRequestMethod() {

		return this.requestMethod;
	}

	@Override
	public String getRequestPath() {
		//System.out.println("test...."+requestPath);
		return this.requestPath;
	}

	@Override
	public Map<String, String> getRequestHeader() {
		return this.requestHeader;
	}

	@Override
	public String getParameter(String parameterName) {
		String str = paramters.get(parameterName);
		return str;
	}

	@Override
	public boolean isStaticResource() {
		// 判断请求的路径是否在根目录下，如果是，表示该请求内容为静态文件
		if(requestPath!=null){
			File file = new File(ConfigUtil.getConfigValue("rootPath"), requestPath);
			//文件一定要在根目录下，而且还得是文件
			return file.exists()&&file.isFile();
		}
		return false;
	}

	@Override
	public boolean isDynamicResource() {
		// 判断请求的路径是否是在servle的配置文件中
		if(requestPath!=null){
		return ServletMappingUtil.isContainsKey(requestPath);
		}
		return false;
	}
	@Override
	public boolean isNullRequest() {
		return isNullrequest;
	}

}
