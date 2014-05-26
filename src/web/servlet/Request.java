package web.servlet;

import java.util.Set;

/**
 * サーブレットへのリクエストのインターフェイス。
 */
public interface Request {
	/**
	 * HTTP HEAD メソッド。
	 */
	public static final String METHOD_HEAD = "HEAD";

	/**
	 * HTTP GET メソッド。
	 */
	public static final String METHOD_GET = "GET";

	/**
	 * HTTP POST メソッド。
	 */
	public static final String METHOD_POST = "POST";

	/**
	 * HTTP メソッド (Request.METHOD_GET など) を返す。
	 * @return HTTP メソッド
	 */
	public String getMethod();

	/**
	 * リクエストされたパス ("/foo/bar.cgi" など) を返す。
	 * クエリー文字列 (? 以降) は含まないので注意。
	 * @return リクエストされたパス
	 */
	public String getPath();

	/**
	 * クエリーに含まれるパラメータ名をすべて返す。
	 * @return パラメータ名の集合
	 */
	public Set<String> getParameterNames();

	/**
	 * 指定されたパラメータの値を返す。デコードされてないことに注意。
	 * @param name パラメータ名 (大文字小文字を区別する)
	 * @return パラメータの値、指定されたパラメータが存在しない場合は null
	 */
	public String getParameter(String name);

	/**
	 * 指定されたパラメータを URL デコードした値を返す。
	 * @param name パラメータ名 (大文字小文字を区別する)
	 * @return パラメータの値をデコードしたもの、指定されたパラメータが存在しない場合は null
	 */
	public String getDecodedParameter(String name);

	/**
	 * 指定されたパラメータの値を整数に変換して返す。
	 * @param name パラメータ名 (大文字小文字を区別する)
	 * @return パラメータの値を整数に変換したもの
	 * @throws NullPointerException 指定されたパラメータが存在しない場合
	 * @throws NumberFormatException パラメータの値を整数に変換できない場合
	 */
	public int getIntParameter(String name);

	/**
	 * リクエストヘッダーに含まれるヘッダー名をすべて返す。
	 * ヘッダー名はすべて小文字に変換されることに注意。
	 * @return 小文字に変換したヘッダー名の集合
	 */
	public Set<String> getHeaderNames();

	/**
	 * 指定されたヘッダーの値を返す。
	 * @param name ヘッダー名 (大文字小文字を区別しない)
	 * @return ヘッダーの値、指定されたヘッダーが存在しない場合は null
	 */
	public String getHeader(String name);

	/**
	 * 指定されたヘッダーの値を整数に変換して返す。
	 * @param name ヘッダー名 (大文字小文字を区別しない)
	 * @return ヘッダーの値を整数に変換したもの
	 * @throws NullPointerException 指定されたヘッダーが存在しない場合
	 * @throws NumberFormatException ヘッダーの値を整数に変換できない場合
	 */
	public int getIntHeader(String name);

	/**
	 * リクエストヘッダーに含まれるクッキー名をすべて返す。
	 * @return クッキー名の集合
	 */
	public Set<String> getCookieNames();

	/**
	 * 指定されたクッキーの値を返す。
	 * @param name クッキー名 (大文字小文字を区別する)
	 * @return クッキーの値、指定されたクッキーが存在しない場合は null
	 */
	public String getCookie(String name);
}
