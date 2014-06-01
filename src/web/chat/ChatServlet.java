package web.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
		try {
			BasicView view = new BasicView(response);
			view.printHead(getName(), view.cssLinkTag("/style.css"));

			printWriteForm(request, response);
			printChatLog(request, response);

			view.printTail();
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
		}
	}

	/**
	 * 書き込みフォームを表示する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void printWriteForm(Request request, Response response) {
		String username = request.getCookie("username");

		response.println("<p>なんか書いてね。</p>");
		response.println("<form method=\"POST\" action=\"/write\">");
		if (username != null) {
			response.println("なまえ：<input type=\"text\" name=\"username\" value=\"" + username + "\"><br>");
		} else {
			response.println("なまえ：<input type=\"text\" name=\"username\"><br>");
		}
		response.println("かきこみ：<input type=\"text\" name=\"message\"><br>");
		response.println("<input type=\"submit\" value=\"かきこむ\">");
		response.println("</form>");
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 * @throws ClassNotFoundException JDBC ドライバが見つからない
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public void printChatLog(Request request, Response response)
			throws ClassNotFoundException, SQLException {
		int offset = 0;
		try {
			offset = request.getIntParameter("offset");
		} catch (NullPointerException | NumberFormatException e) {
		}

		try (DBCommon db = new DBCommon()) {
			String sql1 = "select count(*) as count from chat_log";
			int count = 0;
			try (Statement st = db.getConnection().createStatement();
					ResultSet rs = st.executeQuery(sql1)) {
				if (rs.next()) {
					count = rs.getInt("count");
				}
			}

			response.print("<p>");
			if (offset >= 10) {
				response.print("<a href=\"/?offset=" + (offset - 10) + "\">前の10件</a>");
			}
			if (offset + 10 < count) {
				if (offset >= 10) {
					response.print(" | ");
				}
				response.print("<a href=\"/?offset=" + (offset + 10) + "\">次の10件</a>");
			}
			response.println("</p>");

			String sql2 = "select * from chat_log order by date desc limit 10 offset ?";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql2)) {
				st.setInt(1, offset);
				try (ResultSet rs = st.executeQuery()) {
					response.println("<table border=1>");
					response.println("<tr><th>日付</th><th>名前</th><th>メッセージ</th></tr>");
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
					while (rs.next()) {
						Timestamp date = rs.getTimestamp("date");
						String dateString = dateFormat.format(date);
						String user = rs.getString("user_name");
						String message = rs.getString("message");

						response.println("<tr>");
						response.println("<td>" + dateString + "</td>");
						response.println("<td>" + user + "</td>");
						response.println("<td>" + message + "</td>");
						response.println("</tr>");
					}
					response.println("</table>");
				}
			}
		}
	}
}
