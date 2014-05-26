package web.server;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import web.servlet.Response;

/**
 * サーブレットからのレスポンス。
 */
public class WebResponse implements Response {
	/**
	 * WebServer を終了させるための特別なステータス。
	 */
	public static final String STATUS_SHUTDOWN = "XXX Shutdown";

	/**
	 * レスポンスステータス。
	 */
	private String status;

	/**
	 * レスポンスヘッダー。
	 */
	private Map<String, String> headers;

	/**
	 * クッキー。
	 */
	private List<String> cookies;

	/**
	 * レスポンス本文 (print 用)。
	 */
	private StringBuilder buffer;

	/**
	 * レスポンス本文 (バイト列)。
	 */
	private byte[] bytes;

	/**
	 * コンストラクタ。
	 */
	public WebResponse() {
		headers = new HashMap<String, String>();
		cookies = new ArrayList<String>();
		buffer = new StringBuilder();
	}

	/**
	 * レスポンスステータスを返す。
	 * @return レスポンスステータス
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 指定されたレスポンスヘッダーの値を返す。
	 * @param name ヘッダー名
	 * @return ヘッダーの値、指定されたヘッダーが存在しない場合は null
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}

	/**
	 * クッキーのリストを返す。
	 * @return クッキーのリスト
	 */
	public List<String> getCookies() {
		return cookies;
	}

	/**
	 * レスポンスステータス (Response.STATUS_OK など) を設定する。
	 * @param status レスポンスステータス
	 */
	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * レスポンスステータスを一時的に転送 (Response.STATUS_REDIRECT) に設定し、
	 * "Location" ヘッダーに指定された転送先 URL を設定する。
	 * @param location 転送先 URL
	 */
	public void setRedirect(String location) {
		status = STATUS_REDIRECT;
		setHeader("Location", location);
	}

	/**
	 * レスポンスステータス (Response.STATUS_ERROR など) を設定し、 
	 * 本文にはエラーメッセージを設定する。
	 * @param status レスポンスステータス
	 * @param message エラーメッセージ
	 */
	@Override
	public void setError(String status, String message) {
		this.status = status;
		headers.clear();
		buffer.delete(0, buffer.length());
		bytes = null;

		setHeader("Content-Type", "text/html");
		println("<html><head>");
		println("<title>" + status + "</title>");
		println("</head><body>");
		println("<h1>" + status + "</h1>");
		println("<pre>" + message + "</pre>");
		println("</body></html>");
	}

	/**
	 * レスポンスステータス (Response.STATUS_ERROR など) を設定し、 
	 * 本文には例外に基づいたエラーメッセージを設定する。
	 * @param status レスポンスステータス
	 * @param exception 例外または null
	 */
	@Override
	public void setError(String status, Exception exception) {
		if (exception == null) {
			setError(status, "An unknown error has occurred.");
		} else {
			StringWriter writer = new StringWriter();
			exception.printStackTrace(new PrintWriter(writer));
			setError(status, writer.toString());
		}
	}

	/**
	 * レスポンスヘッダーを設定する。指定されたヘッダー名が存在すれば値は上書きされる。
	 * @param name ヘッダー名
	 * @param value ヘッダーの値
	 */
	@Override
	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	/**
	 * クッキーを追加する。
	 * @param cookie クッキー文字列
	 */
	@Override
	public void addCookie(String cookie) {
		cookies.add(cookie);
	}

	/**
	 * レスポンス本文に文字列を追加する。
	 * @param str 追加する文字列
	 */
	@Override
	public void print(String str) {
		buffer.append(str);
	}

	/**
	 * レスポンス本文に文字列を追加し、改行する。
	 * @param str 追加する文字列
	 */
	@Override
	public void println(String str) {
		buffer.append(str).append('\n');
	}

	/**
	 * レスポンス本文を改行する。
	 */
	@Override
	public void println() {
		buffer.append('\n');
	}

	/**
	 * レスポンス本文をバイト列に設定する。
	 * @param bytes レスポンス本文となるバイト列
	 */
	@Override
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * レスポンスをストリームに書き出す。
	 * @param stream 出力先のストリーム
	 * @param headerOnly true ならヘッダーのみを書き出す
	 * @throws IOException 入出力に関する問題が発生した場合
	 */
	public void writeTo(PrintStream stream, boolean headerOnly) throws IOException {
		stream.println("HTTP/1.0 " + status);
		for (String name : headers.keySet()) {
			stream.println(name + ": " + headers.get(name));
		}
		for (String cookie : cookies) {
			stream.println("Set-Cookie: " + cookie);
		}
		stream.println();

		if (! headerOnly) {
			if (bytes == null) {
				stream.print(buffer.toString());
			} else {
				stream.write(bytes);
			}
		}
	}
}
