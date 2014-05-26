package web.servlet;

/**
 * サーブレットからのレスポンスのインターフェイス。
 */
public interface Response {
	/**
	 * リクエストの処理が成功した場合。
	 */
	public static final String STATUS_OK = "200 OK";

	/**
	 * 別の URL に転送する場合。
	 */
	public static final String STATUS_REDIRECT = "303 See Other";

	/**
	 * リクエストがおかしい場合。
	 */
	public static final String STATUS_BAD_REQUEST = "400 Bad Request";

	/**
	 * リクエストされた URI が存在しない場合。
	 */
	public static final String STATUS_NOT_FOUND = "404 Not Found";

	/**
	 * 内部エラー。サーブレットがステータスコードを設定しなかった場合はこれが返る。
	 */
	public static final String STATUS_ERROR = "500 Internal Server Error";

	/**
	 * レスポンスステータス (Response.STATUS_OK など) を設定する。
	 * @param status レスポンスステータス
	 */
	public void setStatus(String status);

	/**
	 * レスポンスステータスを一時的に転送 (Response.STATUS_REDIRECT) に設定し、
	 * "Location" ヘッダーに指定された転送先 URL を設定する。
	 * @param location 転送先 URL
	 */
	public void setRedirect(String location);

	/**
	 * レスポンスステータス (Response.STATUS_ERROR など) を設定し、 
	 * 本文にはエラーメッセージを設定する。
	 * @param status レスポンスステータス
	 * @param message エラーメッセージ
	 */
	public void setError(String status, String message);

	/**
	 * レスポンスステータス (Response.STATUS_ERROR など) を設定し、 
	 * 本文には例外に基づいたエラーメッセージを設定する。
	 * @param status レスポンスステータス
	 * @param exception 例外または null
	 */
	public void setError(String status, Exception exception);

	/**
	 * レスポンスヘッダーを設定する。指定されたヘッダー名が存在すれば値は上書きされる。
	 * @param name ヘッダー名
	 * @param value ヘッダーの値
	 */
	public void setHeader(String name, String value);

	/**
	 * クッキーを追加する。
	 * @param cookie クッキー文字列
	 */
	public void addCookie(String cookie);

	/**
	 * レスポンス本文に文字列を追加する。
	 * @param str 追加する文字列
	 */
	public void print(String str);

	/**
	 * レスポンス本文に文字列を追加し、改行する。
	 * @param str 追加する文字列
	 */
	public void println(String str);

	/**
	 * レスポンス本文を改行する。
	 */
	public void println();

	/**
	 * レスポンス本文をバイト列に設定する。
	 * @param bytes レスポンス本文となるバイト列
	 */
	public void setBytes(byte[] bytes);
}
