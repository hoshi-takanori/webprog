package web.chat;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
		if (request.getMethod().equals(Request.METHOD_POST)) {
			handlePost(request, response);
		} else {
			handleGet(request, response);
		}
	}

	/**
	 * GET リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void handleGet(Request request, Response response) {
		BasicView view = new BasicView(response);
		view.printHead(getName(), view.cssLinkTag("/style.css"));

		response.println("<p>なんか書いてね。</p>");
		response.println("<form method=\"POST\" action=\"/write\">");
		response.println("なまえ：<input type=\"text\" name=\"username\"><br>");
		response.println("かきこみ：<input type=\"text\" name=\"message\"><br>");
		response.println("<input type=\"submit\" value=\"かきこむ\">");
		response.println("</form>");

		view.printTail();
	}

	/**
	 * POST リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void handlePost(Request request, Response response) {
		String username = request.getDecodedParameter("username");
		String message = request.getDecodedParameter("message");

		String sql = "insert into chat_log (user_name, message) values (?, ?)";
		try (DBCommon db = new DBCommon();
				PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setString(1, username);
			st.setString(2, message);
			int numRows = st.executeUpdate();
			if (numRows == 1) {
				BasicView view = new BasicView(response);
				view.printHead(getName(), view.cssLinkTag("/style.css"));
				response.println("<p>書き込みました。</p>");
				view.printTail();
			} else {
				response.setError(Response.STATUS_ERROR, "書き込み失敗しました。");
			}
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
		}
	}
}
