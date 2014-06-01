package web.chat;

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
		String sql = "select * from chat_log";
		try (DBCommon db = new DBCommon();
				Statement st = db.getConnection().createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			BasicView view = new BasicView(response);
			view.printHead(getName(), view.cssLinkTag("/style.css"));

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

			view.printTail();
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
		}
	}
}