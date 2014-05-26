package web.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import web.servlet.Request;
import web.servlet.Servlet;

/**
 * 単純なサーブレットコンテナ (らしきもの)。
 */
public class WebServer {
	/**
	 * 唯一のインスタンス (シングルトンパターン)。
	 */
	private static WebServer container;

	/**
	 * ポート番号。
	 */
	private int port;

	/**
	 * デバッグモードなら true。
	 */
	private boolean debugMode;

	/**
	 * サーブレットたち。
	 */
	private Map<String, Servlet> servlets;

	/**
	 * 唯一のインスタンスを生成する。
	 * @param settingName 設定ファイルの名前
	 * @return 唯一のインスタンス
	 */
	public static WebServer createInstance(String settingName) {
		if (container == null) {
			container = new WebServer(settingName);
		}
		return container;
	}

	/**
	 * 唯一のインスタンスを返す。
	 * @return 唯一のインスタンス
	 */
	public static WebServer getInstance() {
		return container;
	}

	/**
	 * コンストラクタ。
	 * @param settingName 設定ファイルの名前
	 */
	private WebServer(String settingName) {
		ResourceBundle settings = ResourceBundle.getBundle(settingName);
		port = Integer.parseInt(settings.getString("CONTAINER_PORT"));
		try {
			debugMode = Boolean.parseBoolean(settings.getString("DEBUG_MODE"));
		} catch (MissingResourceException e) {
			debugMode = false;
		}
		servlets = new LinkedHashMap<String, Servlet>();
	}

	/**
	 * デバッグモードなら、ログメッセージを標準出力に出力する。
	 * @param message ログメッセージ
	 */
	public void debugLog(String message) {
		if (debugMode) {
			System.out.println(message);
		}
	}

	/**
	 * サーブレットを追加する。
	 * @param pattern サーブレットが処理するパスを表現する正規表現
	 * @param servlet サーブレット
	 */
	public void addServlet(String pattern, Servlet servlet) {
		debugLog("adding " + servlet.getName());
		servlets.put(pattern, servlet);
	}

	/**
	 * サーブレットたちを返す。
	 * @return サーブレットたち
	 */
	public Collection<Servlet> getServlets() {
		return servlets.values();
	}

	/**
	 * 指定されたリクエストを処理するサーブレットを返す。
	 * @param request リクエスト
	 * @return 指定されたリクエストを処理するサーブレット
	 */
	public Servlet findServlet(Request request) {
		for (String pattern : servlets.keySet()) {
			if (request.getPath().matches(pattern)) {
				return servlets.get(pattern);
			}
		}
		return null;
	}

	/**
	 * サーブレットコンテナの処理を開始する。
	 */
	public void start() {
		debugLog("opening port " + port);
		try (ServerSocket listener = new ServerSocket(port)) {
			while (true) {
				Socket socket = listener.accept();
				WebConnection connection = new WebConnection(socket);
				new Thread(connection).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
