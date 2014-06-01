package web.chat;

import web.example.BasicView;
import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * チャットを読むサーブレット。
 */
public class ChatReadServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "Chat Read Servlet";
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
		response.println("<p>この中身を何とかする。</p>");
		view.printTail();
	}
}
