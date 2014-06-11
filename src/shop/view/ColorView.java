package shop.view;

import shop.entity.Color;
import shop.entity.User;
import web.example.BasicView;
import web.servlet.Response;

/**
 * 色売り屋ビュー。
 */
public class ColorView extends BasicView {
	/**
	 * コンストラクタ。
	 * @param response レスポンス
	 * @param title タイトル
	 * @param user ユーザー
	 */
	public ColorView(Response response, String title, User user) {
		super(response);

		printHead(title);
		printTag("p", "色売り屋へようこそ。" +
				linkTag("カート", "/cart") + " | " +
				linkTag(user != null ? user.getDisplayName() + "さん" : "ログイン", "/login"));
	}

	/**
	 * 色選択フォームを表示する。
	 * @param color 色
	 */
	public void printColorForm(Color color) {
		printTag("p", "色を選んでください。または、" +
				linkTag("ランダムな色", "/", "random") + "。");

		printOpenFormTag("GET", "/");
		response.println("あか：" + inputTag("text",
				"red", color != null ? "" + color.getRed() : null, "size", "5"));
		response.println("みどり：" + inputTag("text",
				"green", color != null ? "" + color.getGreen() : null, "size", "5"));
		response.println("あお：" + inputTag("text",
				"blue", color != null ? "" + color.getBlue() : null, "size", "5"));
		printInputTag("submit", null, "送信");
		printCloseTag("form");
	}

	/**
	 * 色のサンプルを表示する。
	 * @param color 色
	 */
	public void printColorSample(Color color) {
		printTag("p", "あなたの選んだ色は、#" + color.getCode() + " です。");
		printTag("div", "", "style", "background: #" + color.getCode() +
				"; border: solid 1px black; width: 200px; height: 50px;");

		printOpenFormTag("POST", "/cart");
		printInputTag("hidden", "color", color.getCode());
		printInputTag("submit", null, "カートに追加");
		printCloseTag("form");
	}
}
