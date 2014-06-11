package shop.servlet;

import java.sql.SQLException;

import shop.dao.ShopDB;
import shop.entity.Session;
import shop.entity.User;
import shop.view.LoginView;
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
			if (request.getParameter("logout") != null) {
				processLogout(request, response);
			} else {
				processLogin(request, response);
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
		// セッションを取得する。
		Session session = null;
		try (ShopDB db = new ShopDB()) {
			session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッションを取得できません。");
				return;
			}
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}

		// ページを表示する。
		LoginView view = new LoginView(response, getName(), session.getUser());

		if (session.getUser() == null) {
			// ログインフォームを表示する。
			view.printLoginForm();
		} else {
			// ログイン時の内容を表示する。
			view.printLoggedIn(session.getUser());
		}

		view.printTail();
	}

	/**
	 * POST メソッド (ログイン) を処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processLogin(Request request, Response response) {
		// ログイン情報を取得する。
		String username = request.getDecodedParameter("username");
		String password = request.getDecodedParameter("password");
		if (username == null || password == null) {
			response.setError(Response.STATUS_BAD_REQUEST, "ログイン情報を取得できません。");
			return;
		}

		try (ShopDB db = new ShopDB()) {
			// セッションを取得する。
			Session session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッションを取得できません。");
				return;
			}
			if (session.getUser() != null) {
				response.setError(Response.STATUS_BAD_REQUEST, "すでにログイン済みです。");
				return;
			}

			// ユーザーを取得する。
			User user = db.getUserDao().findUser(username, password);
			if (user == null) {
				response.setError(Response.STATUS_BAD_REQUEST,
						"ユーザー名またはパスワードが間違ってます。");
				return;
			}

			// セッションを更新する。
			session.setUser(user);
			db.getSessionDao().updateSession(session);

			// リダイレクトを送信。
			response.setRedirect("/login");
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}

	/**
	 * POST メソッド (ログアウト) を処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void processLogout(Request request, Response response) {
		try (ShopDB db = new ShopDB()) {
			// セッションを取得する。
			Session session = db.getSessionDao().selectSession(request.getCookie("session_id"));
			if (session == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "セッションを取得できません。");
				return;
			}
			if (session.getUser() == null) {
				response.setError(Response.STATUS_BAD_REQUEST, "すでにログアウト済みです。");
				return;
			}

			// セッションを更新する。
			session.setUser(null);
			db.getSessionDao().updateSession(session);

			// リダイレクトを送信。
			response.setRedirect("/login");
		} catch (ClassNotFoundException | SQLException e) {
			response.setError(Response.STATUS_ERROR, e);
			return;
		}
	}
}
