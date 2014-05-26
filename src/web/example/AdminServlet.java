package web.example;

import web.server.WebResponse;
import web.server.WebServer;
import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * 管理用サーブレット。
 */
public class AdminServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "Admin Servlet";
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	@Override
	public void service(Request request, Response response) {
		String str = request.getParameter("shutdown");
		boolean shutdown = str != null && str.equals("yes");
		response.setStatus(shutdown ? WebResponse.STATUS_SHUTDOWN : Response.STATUS_OK);
		response.setHeader("Content-Type", "text/html");
		response.println("<html>");
		response.println("<head>");
		response.println("<title>" + getName() + "</title>");
		response.println("</head>");
		response.println("<body>");
		response.println("<h1>" + getName() + "</h1>");
		if (shutdown) {
			response.println("<p>Shutting down...</p>");
		} else {
			response.println("<p>Servlets are:</p>");
			response.println("<ul>");
			for (Servlet servlet : WebServer.getInstance().getServlets()) {
				response.println("<li>" + servlet.getName());
			}
			response.println("</ul>");
			response.println("<form method=\"POST\" action=\"" + request.getPath() + "\">");
			response.println("<input type=\"hidden\" name=\"shutdown\" value=\"yes\">");
			response.println("<input type=\"submit\" value=\"Shutdown\">");
			response.println("</form>");
		}
		response.println("</body>");
		response.println("</html>");
	}
}
