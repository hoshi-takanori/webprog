package shop.view;

import java.text.SimpleDateFormat;
import java.util.List;

import shop.entity.Sale;
import shop.entity.SaleDetail;
import shop.entity.User;
import web.example.BasicView;
import web.servlet.Response;

/**
 * 色売り屋購入ビュー。
 */
public class SalesView extends BasicView {
	/**
	 * コンストラクタ。
	 * @param response レスポンス
	 * @param title タイトル
	 * @param user ユーザー
	 */
	public SalesView(Response response, String title, User user) {
		super(response);

		printHead(title);
		printTag("p", "色売り屋へようこそ。" +
				linkTag("トップ", "/") + " | " +
				linkTag(user.getDisplayName() + "さん", "/login"));
	}

	/**
	 * 売り上げのリストを表示する。
	 * @param saleList 売り上げのリスト
	 */
	public void printSaleList(List<Sale> saleList) {
		printTag("p", "購入履歴：");

		printOpenTag("ul");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (Sale sale : saleList) {
			response.println(openTag("li") + linkTag(
					dateFormat.format(sale.getDate()), "/sales", "id", "" + sale.getId()));
			for (SaleDetail detail : sale.getDetailList()) {
				printTag("div", "", "style", "background: #" + detail.getColor().getCode() + "; " +
						"border: solid 1px black; display: inline-block; height: 1em; width: 50px;");
			}
		}
		printCloseTag("ul");
	}

	/**
	 * 売り上げの詳細を表示する。
	 * @param sale 売り上げ
	 */
	public void printSaleDetail(Sale sale) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		printTag("p", dateFormat.format(sale.getDate()) + " " +
				sale.getUser().getDisplayName() + "さんの購入の詳細：");

		printOpenTag("table", "border", "1");
		printOpenTag("tr");
		printTag("th", "色", "colspan", "2");
		printCloseTag("tr");
		for (SaleDetail detail : sale.getDetailList()) {
			printOpenTag("tr");
			printTag("td", taggedStr("code", detail.getColor().getCode()));
			printTag("td", "&nbsp;", "bgcolor", "#" + detail.getColor().getCode(), "width", "100");
			printCloseTag("tr");
		}
		printCloseTag("table");
	}
}
