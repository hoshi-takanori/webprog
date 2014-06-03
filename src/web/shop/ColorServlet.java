package web.shop;

import web.example.BasicView;
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
	 * @return 色コード
	 */
	public int[] getRGBColor(Request request) {
		// random パラメーターがあれば、ランダムな色を返す。
		if (request.getParameter("random") != null) {
			int red = (int) (Math.random() * 256);
			int green = (int) (Math.random() * 256);
			int blue = (int) (Math.random() * 256);
			int[] color = { red, green, blue };
			return color;
		}

		// red, green, blue があれば、その色を返す。
		try {
			int red = Math.max(0, Math.min(255, request.getIntParameter("red")));
			int green = Math.max(0, Math.min(255, request.getIntParameter("green")));
			int blue = Math.max(0, Math.min(255, request.getIntParameter("blue")));
			int[] color = { red, green, blue };
			return color;
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
		// 色コードを取得する。
		int[] color = getRGBColor(request);

		// ページを表示する。
		BasicView view = new BasicView(response);
		view.printHead(getName());

		view.printTag("p", "色売り屋へようこそ。");

		// 色選択フォームを表示する。
		view.printTag("p", "色を選んでください。または、" +
				view.linkTag("ランダムな色", "/", "random") + "。");
		view.printOpenFormTag("GET", "/");
		response.println("あか：" + view.inputTag("text",
				"red", color != null ? "" + color[0] : null, "size", "5"));
		response.println("みどり：" + view.inputTag("text",
				"green", color != null ? "" + color[1] : null, "size", "5"));
		response.println("あお：" + view.inputTag("text",
				"blue", color != null ? "" + color[2] : null, "size", "5"));
		view.printInputTag("submit", null, "送信");
		view.printCloseTag("form");

		// 色が選択されていれば、その色を表示する。
		if (color != null) {
			String colorString = String.format("%02x%02x%02x", color[0], color[1], color[2]);
			view.printTag("p", "あなたの選んだ色は、#" + colorString + " です。");
			view.printTag("div", "", "style", "background: #" + colorString +
					"; border: solid 1px black; width: 200px; height: 50px;");
		}

		view.printTail();
	}
}
