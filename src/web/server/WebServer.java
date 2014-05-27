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
 * 単純な Web サーバー (らしきもの)。
 */
public class WebServer {
	/**
	 * 唯一のインスタンス (シングルトンパターン)。
	 */
	private static WebServer server;

	/**
	 * ポート番号。
	 */
	private int port;

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
		if (server == null) {
			server = new WebServer(settingName);
		}
		return server;
	}

	/**
	 * 唯一のインスタンスを返す。
	 * @return 唯一のインスタンス
	 */
	public static WebServer getInstance() {
		return server;
	}

	/**
	 * コンストラクタ。
	 * @param settingName 設定ファイルの名前
	 */
	private WebServer(String settingName) {
		ResourceBundle settings = ResourceBundle.getBundle(settingName);
		port = Integer.parseInt(settings.getString("SERVER_PORT"));
		try {
			WebLogger.debugMode = Boolean.parseBoolean(settings.getString("DEBUG_MODE"));
		} catch (MissingResourceException e) {
			WebLogger.debugMode = false;
		}
		servlets = new LinkedHashMap<String, Servlet>();
	}

	/**
	 * サーブレットを追加する。
	 * @param pattern サーブレットが処理するパスを表現する正規表現
	 * @param servlet サーブレット
	 */
	public void addServlet(String pattern, Servlet servlet) {
		WebLogger.log("adding " + servlet.getName());
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
	 * Web サーバーの処理を開始する。
	 */
	public void start() {
		WebLogger.log("opening port " + port);
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
