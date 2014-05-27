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
		BasicView view = new BasicView(response);
		view.printHead(getName());
		if (shutdown) {
			response.setStatus(WebResponse.STATUS_SHUTDOWN);
			view.printTag("p", "Shutting down...");
		} else {
			view.printTag("p", "Servlets are:");
			view.printOpenTag("ul");
			for (Servlet servlet : WebServer.getInstance().getServlets()) {
				view.printTag("li", servlet.getName());
			}
			view.printCloseTag("ul");
			view.printOpenTag("form", "method", "POST");
			view.printInputTag("hidden", "shutdown", "yes");
			view.printInputTag("submit", null, "Shutdown");
			view.printCloseTag("form");
		}
		view.printTail();
	}
}
