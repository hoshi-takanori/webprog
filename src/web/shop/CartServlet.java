package web.shop;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import web.example.BasicView;
import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * 色売り屋カートサーブレット。
 */
public class CartServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "色売り屋 - カート";
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	@Override
	public void service(Request request, Response response) {
		if (request.getMethod().equals(Request.METHOD_POST)) {
			if (request.getParameter("delete") != null) {
				processDelete(request, response);
			} else {
				processPost(request, response);
			}
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

			// カートから色を取得する。
			String sql = "select * from cart where session_id = ? order by id";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
				st.setString(1, sessionId);
				try (ResultSet rs = st.executeQuery()) {
					BasicView view = new BasicView(response);
					view.printHead(getName());

					view.printTag("p", "色売り屋へようこそ。" +
							view.linkTag("トップ", "/") + " | " +
							view.linkTag(names != null ? names[2] + "さん" : "ログイン", "/login"));

					String colors = null;
					while (rs.next()) {
						if (colors == null) {
							view.printTag("p", "カートの中身：");
							view.printOpenTag("table", "border", "1");
							view.printOpenTag("tr");
							view.printTag("th", "色", "colspan", "2");
							view.printTag("th", "削除");
							view.printCloseTag("tr");
						}

						int id = rs.getInt("id");
						String color = rs.getString("color");
						view.printOpenTag("tr");
						view.printTag("td", view.taggedStr("code", color));
						view.printTag("td", "&nbsp;", "bgcolor", "#" + color, "width", "100");
						view.printOpenTag("td");
						view.printOpenFormTag("POST", "/cart");
						view.printInputTag("hidden", "delete", "" + id);
						view.printInputTag("submit", null, "削除");
						view.printCloseTag("form");
						view.printCloseTag("td");
						view.printCloseTag("tr");

						if (colors == null) {
							colors = color;
						} else {
							colors += ":" + color;
						}
					}

					if (colors != null) {
						view.printCloseTag("table");
					} else {
						view.printTag("p", "カートは空っぽです。");
					}

					view.printTail();
				}
			}
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
		// 色コードを取得する。
		String color = request.getParameter("color");
		if (color == null || ! color.matches("^[0-9a-fA-F]{6}$")) {
			response.setError(Response.STATUS_BAD_REQUEST, "色コードを取得できません。");
			return;
		}

		// セッション ID を取得する。
		String sessionId = request.getCookie("session_id");
		try (ShopDB db = new ShopDB()) {
			if (sessionId == null || ! db.checkSessionId(sessionId)) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッション ID を取得できません。");
				return;
			}

			// カートに色を登録する。
			String sql = "insert into cart (session_id, color) values (?, ?)";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
				st.setString(1, sessionId);
				st.setString(2, color);
				int numRows = st.executeUpdate();
				if (numRows == 1) {
					response.setRedirect("/cart");
				} else {
					response.setError(Response.STATUS_BAD_REQUEST, "カートに色を登録できません。");
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}

	/**
	 * POST メソッド (delete) を処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processDelete(Request request, Response response) {
		// カート ID を取得する。
		int id;
		try {
			id = request.getIntParameter("delete");
		} catch (NullPointerException | NumberFormatException e) {
			response.setError(Response.STATUS_BAD_REQUEST, "カート ID を取得できません。");
			return;
		}

		// セッション ID を取得する。
		String sessionId = request.getCookie("session_id");
		try (ShopDB db = new ShopDB()) {
			if (sessionId == null || ! db.checkSessionId(sessionId)) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッション ID を取得できません。");
				return;
			}

			// カートから削除する。
			String sql = "delete from cart where id = ? and session_id = ?";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
				st.setInt(1, id);
				st.setString(2, sessionId);
				int numRows = st.executeUpdate();
				if (numRows == 1) {
					response.setRedirect("/cart");
				} else {
					response.setError(Response.STATUS_BAD_REQUEST, "カートから削除できません。");
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}
}
