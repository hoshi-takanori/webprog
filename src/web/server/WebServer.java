package web.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import web.servlet.Request;
import web.servlet.Servlet;

/**
 * 単純な Web サーバー (らしきもの)。
 */
public class WebServer {
	/**
	 * ポート番号。
	 */
	private int port;

	/**
	 * サーブレットたち。
	 */
	private Map<String, Servlet> servlets;

	/**
	 * コンストラクタ。
	 * @param settingName 設定ファイルの名前
	 */
	public WebServer(String settingName) {
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
			Executor executor = Executors.newCachedThreadPool();
			while (true) {
				Socket socket = listener.accept();
				WebConnection connection = new WebConnection(this, socket);
				executor.execute(connection);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
