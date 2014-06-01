package web.chat;

import web.example.FileServlet;
import web.server.WebServer;

/**
 * チャットサーブレットのメインクラス。
 */
public class ChatMain {
	/**
	 * main メソッド。
	 * @param args コマンド行引数
	 */
	public static void main(String[] args) {
		WebServer server = new WebServer("web");
		server.addServlet("/", new ChatServlet());
		server.addServlet("/write", new ChatWriteServlet());
		server.addServlet("/.*", new FileServlet("public"));
		server.start();
	}
}
