package web.shop;

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
 * 色売り購入サーブレット。
 */
public class SalesServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "色売り屋 - 購入";
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
			if (request.getParameter("id") == null) {
				processGet(request, response);
			} else {
				processDetail(request, response);
			}
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
			if (names == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "ログインしてください。");
				return;
			}

			// 購入履歴を取得する。
			String sql = "select * from sales where user_id = ? order by date desc";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
				st.setInt(1, Integer.parseInt(names[0]));
				try (ResultSet rs = st.executeQuery()) {
					BasicView view = new BasicView(response);
					view.printHead(getName());

					view.printTag("p", "色売り屋へようこそ。" +
							view.linkTag("トップ", "/") + " | " +
							view.linkTag(names != null ? names[2] + "さん" : "ログイン", "/login"));

					view.printTag("p", "購入履歴：");
					view.printOpenTag("ul");
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					while (rs.next()) {
						int id = rs.getInt("id");
						Timestamp date = rs.getTimestamp("date");
						view.printTag("li", view.linkTag(dateFormat.format(date),
								"/sales", "id", "" + id));
					}
					view.printCloseTag("ul");

					view.printTail();
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}

	/**
	 * GET メソッド (詳細) を処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processDetail(Request request, Response response) {
		// 購入 ID を取得する。
		int salesId;
		try {
			salesId = request.getIntParameter("id");
		} catch (NullPointerException | NumberFormatException e) {
			response.setError(Response.STATUS_BAD_REQUEST, "購入 ID を取得できません。");
			return;
		}

		// セッション ID とユーザー名を取得する。
		String sessionId = request.getCookie("session_id");
		try (ShopDB db = new ShopDB()) {
			if (sessionId == null || ! db.checkSessionId(sessionId)) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッション ID を取得できません。");
				return;
			}
			String[] names = db.getUserNames(sessionId);

			// 購入履歴を取得する。
			String sql1 = "select * from sales, users where user_id = users.id and sales.id = ?";
			Timestamp date;
			String kanjiName;
			try (PreparedStatement st = db.getConnection().prepareStatement(sql1)) {
				st.setInt(1, salesId);
				try (ResultSet rs = st.executeQuery()) {
					if (rs.next()) {
						date = rs.getTimestamp("date");
						kanjiName = rs.getString("k_name");
					} else {
						response.setError(Response.STATUS_BAD_REQUEST, "購入情報を取得できません。");
						return;
					}
				}
			}

			// 購入詳細を取得する。
			String sql2 = "select * from sales_detail where sales_id = ? order by detail_id";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql2)) {
				st.setInt(1, salesId);
				try (ResultSet rs = st.executeQuery()) {
					BasicView view = new BasicView(response);
					view.printHead(getName());

					view.printTag("p", "色売り屋へようこそ。" +
							view.linkTag("トップ", "/") + " | " +
							view.linkTag(names != null ? names[2] + "さん" : "ログイン", "/login"));

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					view.printTag("p", dateFormat.format(date) + " " + kanjiName + "さんの購入の詳細：");
					view.printOpenTag("table", "border", "1");
					view.printOpenTag("tr");
					view.printTag("th", "色", "colspan", "2");
					view.printCloseTag("tr");
					while (rs.next()) {
						String color = rs.getString("color");
						view.printOpenTag("tr");
						view.printTag("td", view.taggedStr("code", color));
						view.printTag("td", "&nbsp;", "bgcolor", "#" + color, "width", "100");
						view.printCloseTag("tr");
					}
					view.printCloseTag("table");

					view.printTag("p", view.linkTag("購入履歴に戻る", "/sales"));

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
		String colors = request.getDecodedParameter("colors");
		String[] colorArray = null;
		if (colors != null) {
			colorArray = colors.split(":");
			for (String color : colorArray) {
				if (! color.matches("^[0-9a-fA-F]{6}$")) {
					colorArray = null;
					break;
				}
			}
		}
		if (colorArray == null) {
			response.setError(Response.STATUS_BAD_REQUEST, "色コードを取得できません。");
			return;
		}

		// セッション ID とユーザー ID を取得する。
		String sessionId = request.getCookie("session_id");
		try (ShopDB db = new ShopDB()) {
			if (sessionId == null || ! db.checkSessionId(sessionId)) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッション ID を取得できません。");
				return;
			}
			String[] names = db.getUserNames(sessionId);
			if (names == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "ログインしてください。");
				return;
			}

			// トランザクションを開始する。
			try (Statement st = db.getConnection().createStatement()) {
				st.executeUpdate("begin transaction");
			}

			// 購入テーブルに追加する。
			String sql1 = "insert into sales (user_id) values (?)";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql1)) {
				st.setInt(1, Integer.parseInt(names[0]));
				int numRows = st.executeUpdate();
				if (numRows != 1) {
					response.setError(Response.STATUS_BAD_REQUEST, "購入テーブルに追加できません。");
					return;
				}
			}

			// 追加した購入 ID を取得する。
			String sql2 = "select currval('sales_id_seq') as sales_id";
			int salesId;
			try (Statement st = db.getConnection().createStatement();
					ResultSet rs = st.executeQuery(sql2)) {
				if (rs.next()) {
					salesId = rs.getInt("sales_id");
				} else {
					response.setError(Response.STATUS_BAD_REQUEST, "購入.ID を取得できません。");
					return;
				}
			}

			// 購入詳細テーブルに追加する。
			String sql3 = "insert into sales_detail (sales_id, detail_id, color) values (?, ?, ?)";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql3)) {
				for (int i = 0; i < colorArray.length; i++) {
					st.setInt(1, salesId);
					st.setInt(2, i + 1);
					st.setString(3, colorArray[i]);
					int numRows = st.executeUpdate();
					if (numRows != 1) {
						response.setError(Response.STATUS_BAD_REQUEST, "購入明細テーブルに追加できません。");
						return;
					}
				}
			}

			// トランザクションをコミットする。
			try (Statement st = db.getConnection().createStatement()) {
				st.executeUpdate("commit transaction");
			}

			// カートの色を削除する。
			String sql4 = "delete from cart where session_id = ?";
			try (PreparedStatement st = db.getConnection().prepareStatement(sql4)) {
				st.setString(1, sessionId);
				st.executeUpdate();
			}

			// リダイレクトを送信。
			response.setRedirect("/sales?id=" + salesId);
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}
}
