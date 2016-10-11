package com.briup.servlet;

import java.io.PrintWriter;

import com.briup.http.HttpRequest;
import com.briup.http.HttpResponse;
import com.briup.http.Servlet;

public class HelloWorldServlet implements Servlet{
	
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		String name = request.getParameter("name");
		try {
			PrintWriter pw = response.getPrintWriter();
			pw.println("<html>");
				pw.println("<head>");
					pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
				pw.println("</head>");
				pw.println("<body>");
					pw.println("<h1>我是动态资源:hello world name = "+name+"</h1>");
				pw.println("</body>");
			pw.println("</html>");
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
