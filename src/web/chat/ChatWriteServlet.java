package web.chat;

import web.example.BasicView;
import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * チャットを書くサーブレット。
 */
public class ChatWriteServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "Chat Write Servlet";
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	@Override
	public void service(Request request, Response response) {
		BasicView view = new BasicView(response);
		view.printHead(getName(), view.cssLinkTag("/style.css"));
		response.println("<p>この中身を何とかする。</p>");
		view.printTail();
	}
}
