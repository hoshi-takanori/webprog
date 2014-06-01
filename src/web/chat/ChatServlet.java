package web.chat;

import web.example.BasicView;
import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * チャットサーブレット。
 */
public class ChatServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "Chat Servlet";
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	@Override
	public void service(Request request, Response response) {
		BasicView view = new BasicView(response);
		view.printHead(getName());
		response.println("<a href=\"/read\">よむ</a>");
		response.println("<a href=\"/write\">かく</a>");
		view.printTail();
	}
}
