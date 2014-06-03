package web.shop;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import web.example.BasicView;
import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * 色売り屋ログインサーブレット。
 */
public class LoginServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "色売り屋 - ログイン";
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	@Override
	public void service(Request request, Response response) {
		if (request.getMethod().equals(Request.METHOD_POST)) {
			processPost(request, response);
		} else {
			processGet(request, response);
		}
	}

	/**
	 * GET メソッドを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processGet(Request request, Response response) {
		// セッション ID とユーザー名を取得する。
		String sessionId = request.getCookie("session_id");
		try (ShopDB db = new ShopDB()) {
			if (sessionId == null || ! db.checkSessionId(sessionId)) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッション ID を取得できません。");
				return;
			}
			String[] names = db.getUserNames(sessionId);

			// ページを表示する。
			BasicView view = new BasicView(response);
			view.printHead(getName());

			view.printTag("p", "色売り屋へようこそ。" +
					view.linkTag("トップ", "/") + " | " +
					view.linkTag("カート", "/cart"));

			if (names == null) {
				// ログインフォームを表示する。
				view.printTag("p", "ログインしてください。");
				view.printOpenFormTag("POST", "/login");
				response.println("ユーザー名：" + view.inputTag("text", "username", null) + "<br>");
				response.println("パスワード：" + view.inputTag("password", "password", null) + "<br>");
				view.printInputTag("submit", null, "ログイン");
				view.printCloseTag("form");
			} else {
				// ログアウトボタンを表示する。
				view.printTag("p", "ようこそ、" + names[2] + "さん。");
				view.printTag("p", view.linkTag("購入履歴を見る", "/sales"));
				view.printOpenFormTag("POST", "/login");
				view.printInputTag("hidden", "logout", names[1]);
				view.printInputTag("submit", null, "ログアウト");
				view.printCloseTag("form");
			}

			view.printTail();
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}

	/**
	 * POST メソッドを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processPost(Request request, Response response) {
		// ログイン情報を取得する。
		String username = request.getDecodedParameter("username");
		String password = request.getDecodedParameter("password");
		String logout = request.getParameter("logout");
		if ((username == null || password == null) && logout == null) {
			response.setError(Response.STATUS_BAD_REQUEST, "ログイン情報を取得できません。");
			return;
		}

		// セッション ID を取得する。
		String sessionId = request.getCookie("session_id");
		try (ShopDB db = new ShopDB()) {
			if (sessionId == null || ! db.checkSessionId(sessionId)) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッション ID を取得できません。");
				return;
			}

			// ユーザー ID を取得する。ログアウトの場合は 0。
			int userId = 0;
			if (logout == null) {
				String sql = "select id from users where name = ? and password = ?";
				try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
					st.setString(1, username);
					st.setString(2, password);
					try (ResultSet rs = st.executeQuery()) {
						if (rs.next()) {
							userId = rs.getInt("id");
						} else {
							response.setError(Response.STATUS_BAD_REQUEST,
									"ユーザー名またはパスワードが間違ってます。");
							return;
						}
					}
				}
			}

			// セッションのユーザー ID を更新する。
			String sql = "update sessions set user_id = ? where id = ?";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
				if (userId != 0) {
					st.setInt(1, userId);
				} else {
					st.setNull(1, Types.INTEGER);
				}
				st.setString(2, sessionId);
				int numRows = st.executeUpdate();
				if (numRows == 1) {
					response.setRedirect("/login");
				} else {
					response.setError(Response.STATUS_BAD_REQUEST, "セッションを更新できません。");
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}
}
