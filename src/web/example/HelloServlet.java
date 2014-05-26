package web.example;

import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * 単純なサーブレット。
 */
public class HelloServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "Hello Servlet";
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	@Override
	public void service(Request request, Response response) {
		response.setStatus(Response.STATUS_OK);
		response.setHeader("Content-Type", "text/plain");
		response.println("Hello, World.");
		response.println();
		response.println("method = " + request.getMethod());
		response.println("path = " + request.getPath());
		for (String name : request.getParameterNames()) {
			response.println("param[" + name + "] = " + request.getParameter(name));
		}
		for (String name : request.getHeaderNames()) {
			response.println("header[" + name + "] = " + request.getHeader(name));
		}
	}
}
