package shop.view;

import shop.entity.User;
import web.example.BasicView;
import web.servlet.Response;

/**
 * 色売り屋ログインビュー。
 */
public class LoginView extends BasicView {
	/**
	 * コンストラクタ。
	 * @param response レスポンス
	 * @param title タイトル
	 * @param user ユーザー
	 */
	public LoginView(Response response, String title, User user) {
		super(response);

		printHead(title);
		printTag("p", "色売り屋へようこそ。" +
				linkTag("トップ", "/") + " | " +
				linkTag("カート", "/cart"));
	}

	/**
	 * ログインフォームを表示する。
	 */
	public void printLoginForm() {
		printTag("p", "ログインしてください。");

		printOpenFormTag("POST", "/login");
		response.println("ユーザー名：" + inputTag("text", "username", null) + "<br>");
		response.println("パスワード：" + inputTag("password", "password", null) + "<br>");
		printInputTag("submit", null, "ログイン");
		printCloseTag("form");
	}

	/**
	 * ログイン時の内容を表示する。
	 */
	public void printLoggedIn(User user) {
		printTag("p", "ようこそ、" + user.getDisplayName() + "さん。");

		printTag("p", linkTag("購入履歴を見る", "/sales"));

		printOpenFormTag("POST", "/login");
		printInputTag("hidden", "logout", user.getName());
		printInputTag("submit", null, "ログアウト");
		printCloseTag("form");
	}
}
