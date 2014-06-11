package shop.servlet;

import java.sql.SQLException;
import java.util.List;

import shop.dao.ShopDB;
import shop.entity.Cart;
import shop.entity.Color;
import shop.entity.Session;
import shop.view.CartView;
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
				processAdd(request, response);
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
		// セッションと、カートのリストを取得する。
		Session session = null;
		List<Cart> cartList = null;
		try (ShopDB db = new ShopDB()) {
			session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッションを取得できません。");
				return;
			}
			cartList = db.getCartDao().selectCarts(session);
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}

		// ページを表示する。
		CartView view = new CartView(response, getName(), session.getUser());

		if (cartList.isEmpty()) {
			view.printTag("p", "カートは空っぽです。");
		} else {
			view.printCartList(cartList);
			view.printBuyOrLogin(session.getUser());
		}

		view.printTail();
	}

	/**
	 * POST メソッド (追加) を処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processAdd(Request request, Response response) {
		// 色コードを取得する。
		Color color = Color.fromCode(request.getParameter("color"));
		if (color == null) {
			response.setError(Response.STATUS_BAD_REQUEST, "色コードを取得できません。");
			return;
		}

		try (ShopDB db = new ShopDB()) {
			// セッションを取得する。
			Session session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッションを取得できません。");
				return;
			}

			// カートに色を登録する。
			Cart cart = new Cart(0, session, color);
			db.getCartDao().insertCart(cart);

			// リダイレクトを送信。
			response.setRedirect("/cart");
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}

	/**
	 * POST メソッド (削除) を処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processDelete(Request request, Response response) {
		// カート ID を取得する。
		int cartId;
		try {
			cartId = request.getIntParameter("delete");
		} catch (NullPointerException | NumberFormatException e) {
			response.setError(Response.STATUS_BAD_REQUEST, "カート ID を取得できません。");
			return;
		}

		try (ShopDB db = new ShopDB()) {
			// セッションを取得する。
			Session session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッションを取得できません。");
				return;
			}

			// カートを取得する。
			Cart cart = db.getCartDao().selectCart(session, cartId);
			if (cart == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "カートを取得できません。");
				return;
			}

			// カートから削除する。
			db.getCartDao().deleteCart(cart);

			// リダイレクトを送信。
			response.setRedirect("/cart");
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}
}
