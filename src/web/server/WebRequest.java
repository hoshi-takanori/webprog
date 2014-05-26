package web.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import web.servlet.Request;

/**
 * サーブレットへのリクエスト。
 */
public class WebRequest implements Request {
	/**
	 * HTTP メソッド。
	 */
	private String method;

	/**
	 * リクエストされたパス。
	 */
	private String path;

	/**
	 * 生のパラメータ。
	 */
	private String rawParams;

	/**
	 * パラメータ。
	 */
	private Map<String, String> params;

	/**
	 * リクエストヘッダー。
	 */
	private Map<String, String> headers;

	/**
	 * クッキー。
	 */
	private Map<String, String> cookies;

	/**
	 * コンストラクタ。
	 * @param method HTTP メソッド
	 * @param path リクエストされたパス
	 */
	public WebRequest(String method, String path) {
		this.method = method;
		this.path = path;
		params = new HashMap<String, String>();
		headers = new HashMap<String, String>();
		cookies = new HashMap<String, String>();

		int index = path.indexOf('?');
		if (index >= 0) {
			this.path = path.substring(0, index);
			parseParameters(path.substring(index + 1));
		}
	}

	/**
	 * クエリー文字列を解析してパラメータを設定する。
	 * @param query クエリー文字列
	 */
	public void parseParameters(String query) {
		rawParams = query;
		for (String param : query.split("&")) {
			String[] array = param.split("=", 2);
			if (array.length > 0 && array[0].length() > 0) {
				params.put(array[0], (array.length == 1) ? array[0] : array[1]);
			}
		}
	}

	/**
	 * リクエストヘッダーを設定する。
	 * @param name ヘッダー名
	 * @param value ヘッダーの値
	 */
	public void setHeader(String name, String value) {
		headers.put(name.toLowerCase(), value.trim());

		if (name.toLowerCase().equals("cookie")) {
			for (String cookie : value.trim().split("; *")) {
				String[] array = cookie.split("=", 2);
				if (array.length == 2) {
					cookies.put(array[0], array[1]);
				}
			}
		}
	}

	/**
	 * HTTP メソッド (Request.METHOD_GET など) を返す。
	 * @return HTTP メソッド
	 */
	@Override
	public String getMethod() {
		return method;
	}

	/**
	 * リクエストされたパス ("/foo/bar.cgi" など) を返す。
	 * クエリー文字列 (? 以降) は含まないので注意。
	 * @return リクエストされたパス
	 */
	@Override
	public String getPath() {
		return path;
	}

	/**
	 * 生のパラメータを返す。
	 * @return 生のパラメータ
	 */
	public String getRawParameters() {
		return rawParams;
	}

	/**
	 * クエリーに含まれるパラメータ名をすべて返す。
	 * @return パラメータ名の集合
	 */
	@Override
	public Set<String> getParameterNames() {
		return params.keySet();
	}

	/**
	 * 指定されたパラメータの値を返す。デコードされてないことに注意。
	 * @param name パラメータ名 (大文字小文字を区別する)
	 * @return パラメータの値、指定されたパラメータが存在しない場合は null
	 */
	@Override
	public String getParameter(String name) {
		return params.get(name);
	}

	/**
	 * 指定されたパラメータを URL デコードした値を返す。
	 * @param name パラメータ名 (大文字小文字を区別する)
	 * @return パラメータの値をデコードしたもの、指定されたパラメータが存在しない場合は null
	 */
	@Override
	public String getDecodedParameter(String name) {
		try {
			String value = getParameter(name);
			return value != null ? URLDecoder.decode(value, "UTF-8") : null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 指定されたパラメータの値を整数に変換して返す。
	 * @param name パラメータ名 (大文字小文字を区別する)
	 * @return パラメータの値を整数に変換したもの
	 * @throws NullPointerException 指定されたパラメータが存在しない場合
	 * @throws NumberFormatException パラメータの値を整数に変換できない場合
	 */
	@Override
	public int getIntParameter(String name) {
		String value = getParameter(name);
		return Integer.parseInt(value);
	}

	/**
	 * リクエストヘッダーに含まれるヘッダー名をすべて返す。
	 * ヘッダー名はすべて小文字に変換されることに注意。
	 * @return 小文字に変換したヘッダー名の集合
	 */
	@Override
	public Set<String> getHeaderNames() {
		return headers.keySet();
	}

	/**
	 * 指定されたヘッダーの値を返す。
	 * @param name ヘッダー名 (大文字小文字を区別しない)
	 * @return ヘッダーの値、指定されたヘッダーが存在しない場合は null
	 */
	@Override
	public String getHeader(String name) {
		return headers.get(name.toLowerCase());
	}

	/**
	 * 指定されたヘッダーの値を整数に変換して返す。
	 * @param name ヘッダー名 (大文字小文字を区別しない)
	 * @return ヘッダーの値を整数に変換したもの
	 * @throws NullPointerException 指定されたヘッダーが存在しない場合
	 * @throws NumberFormatException ヘッダーの値を整数に変換できない場合
	 */
	@Override
	public int getIntHeader(String name) {
		String value = getHeader(name);
		return Integer.parseInt(value);
	}

	/**
	 * リクエストヘッダーに含まれるクッキー名をすべて返す。
	 * @return クッキー名の集合
	 */
	@Override
	public Set<String> getCookieNames() {
		return cookies.keySet();
	}

	/**
	 * 指定されたクッキーの値を返す。
	 * @param name クッキー名 (大文字小文字を区別する)
	 * @return クッキーの値、指定されたクッキーが存在しない場合は null
	 */
	@Override
	public String getCookie(String name) {
		return cookies.get(name);
	}
}
