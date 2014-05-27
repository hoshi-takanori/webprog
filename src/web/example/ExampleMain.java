package web.example;

import web.server.WebServer;

/**
 * 例題のサーブレットを動かすためのメインクラス。
 */
public class ExampleMain {
	/**
	 * main メソッド。
	 * @param args コマンド行引数
	 */
	public static void main(String[] args) {
		WebServer server = WebServer.createInstance("web");
		server.addServlet("/", new AdminServlet());
		server.addServlet("/hello", new HelloServlet());
		server.addServlet("/.*", new FileServlet("public"));
		server.start();
	}
}
