package web.servlet;

/**
 * サーブレットのインターフェイス。
 */
public interface Servlet {
	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	public String getName();

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	public void service(Request request, Response response);
}
