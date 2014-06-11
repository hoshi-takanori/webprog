package shop.view;

import java.util.List;

import shop.entity.Cart;
import shop.entity.User;
import web.example.BasicView;
import web.servlet.Response;

/**
 * 色売り屋カートビュー。
 */
public class CartView extends BasicView {
	/**
	 * コンストラクタ。
	 * @param response レスポンス
	 * @param title タイトル
	 * @param user ユーザー
	 */
	public CartView(Response response, String title, User user) {
		super(response);

		printHead(title);
		printTag("p", "色売り屋へようこそ。" +
				linkTag("トップ", "/") + " | " +
				linkTag(user != null ? user.getDisplayName() + "さん" : "ログイン", "/login"));
	}

	/**
	 * カートのリストを表示する。
	 * @param cartList カートのリスト
	 */
	public void printCartList(List<Cart> cartList) {
		printTag("p", "カートの中身：");

		printOpenTag("table", "border", "1");
		printOpenTag("tr");
		printTag("th", "色", "colspan", "2");
		printTag("th", "削除");
		printCloseTag("tr");

		for (Cart cart : cartList) {
			printOpenTag("tr");
			printTag("td", taggedStr("code", cart.getColor().getCode()));
			printTag("td", "&nbsp;", "bgcolor", "#" + cart.getColor().getCode(), "width", "100");
			printOpenTag("td");
			printOpenFormTag("POST", "/cart");
			printInputTag("hidden", "delete", "" + cart.getId());
			printInputTag("submit", null, "削除");
			printCloseTag("form");
			printCloseTag("td");
			printCloseTag("tr");
		}

		printCloseTag("table");
	}

	/**
	 * 購入ボタンまたはログインを表示する。
	 * @param user ユーザー
	 */
	public void printBuyOrLogin(User user) {
		if (user != null) {
			printOpenFormTag("POST", "/sales");
			printInputTag("submit", null, "購入する");
			printCloseTag("form");
		} else {
			printTag("p", "購入するには" + linkTag("ログイン", "/login") + "してください。");
		}
	}
}
