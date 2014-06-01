package web.chat;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
			response.setError(Response.STATUS_ERROR, "GET には対応していません。");
		}
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
				response.setRedirect("/");
				response.addCookie("username=" + username);
			} else {
				response.setError(Response.STATUS_ERROR, "書き込み失敗しました。");
			}
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
		}
	}
}
