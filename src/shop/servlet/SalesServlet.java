package shop.servlet;

import java.sql.SQLException;
import java.util.List;

import shop.dao.ShopDB;
import shop.entity.Cart;
import shop.entity.Sale;
import shop.entity.Session;
import shop.view.SalesView;
import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * 色売り屋購入サーブレット。
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
		// セッションと、売り上げのリストを取得する。
		Session session = null;
		List<Sale> saleList = null;
		try (ShopDB db = new ShopDB()) {
			session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null || session.getUser() == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "ログインしてください。");
				return;
			}
			saleList = db.getSaleDao().selectSales(session.getUser());
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}

		// ページを表示する。
		SalesView view = new SalesView(response, getName(), session.getUser());

		// 売り上げのリストを表示する。
		view.printSaleList(saleList);

		view.printTail();
	}

	/**
	 * GET メソッド (詳細) を処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processDetail(Request request, Response response) {
		// 購入 ID を取得する。
		int saleId;
		try {
			saleId = request.getIntParameter("id");
		} catch (NullPointerException | NumberFormatException e) {
			response.setError(Response.STATUS_BAD_REQUEST, "購入 ID を取得できません。");
			return;
		}

		// セッションと、売り上げを取得する。
		Session session = null;
		Sale sale = null;
		try (ShopDB db = new ShopDB()) {
			session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null || session.getUser() == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "ログインしてください。");
				return;
			}
			sale = db.getSaleDao().selectSale(session.getUser(), saleId);
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}

		// ページを表示する。
		SalesView view = new SalesView(response, getName() + " (詳細)", session.getUser());

		// 売り上げの詳細を表示する。
		view.printSaleDetail(sale);

		view.printTag("p", view.linkTag("購入履歴に戻る", "/sales"));

		view.printTail();
	}

	/**
	 * POST メソッドを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processPost(Request request, Response response) {
		try (ShopDB db = new ShopDB()) {
			// セッションを取得する。
			Session session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null || session.getUser() == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "ログインしてください。");
				return;
			}

			// カートのリストを取得する。
			List<Cart> cartList = db.getCartDao().selectCarts(session);

			// 売り上げを登録する。
			Sale sale = db.getSaleDao().insertSale(session.getUser(), cartList);

			// カートを削除する。
			for (Cart cart : cartList) {
				db.getCartDao().deleteCart(cart);
			}

			// リダイレクトを送信。
			response.setRedirect("/sales?id=" + sale.getId());
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}
}
