package shop.servlet;

import java.sql.SQLException;

import shop.dao.ShopDB;
import shop.entity.Color;
import shop.entity.Session;
import shop.view.ColorView;
import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * 色売り屋サーブレット。
 */
public class ColorServlet implements Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "色売り屋";
	}

	/**
	 * リクエストから色を取得する。
	 * @param request リクエスト (入力)
	 * @return 色
	 */
	public Color getRGBColor(Request request) {
		// random パラメーターがあれば、ランダムな色を返す。
		if (request.getParameter("random") != null) {
			return Color.randomColor();
		}

		// red, green, blue があれば、その色を返す。
		try {
			int red = request.getIntParameter("red");
			int green = request.getIntParameter("green");
			int blue = request.getIntParameter("blue");
			return new Color(red, green, blue);
		} catch (NullPointerException | NumberFormatException e) {
			// なければ、null を返す。
			return null;
		}
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	@Override
	public void service(Request request, Response response) {
		// 色を取得する。
		Color color = getRGBColor(request);

		// セッションを取得する。
		Session session = null;
		try (ShopDB db = new ShopDB()) {
			session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null) {
				session = db.getSessionDao().generateSession();
				response.addCookie("session_id=" + session.getId());
			}
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}

		// ページを表示する。
		ColorView view = new ColorView(response, getName(), session.getUser());

		// 色選択フォームを表示する。
		view.printColorForm(color);

		// 色が選択されていれば、その色を表示する。
		if (color != null) {
			view.printColorSample(color);
		}

		view.printTail();
	}
}
