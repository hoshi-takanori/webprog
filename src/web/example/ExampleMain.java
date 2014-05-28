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
		WebServer server = new WebServer("web");
		server.addServlet("/", new AdminServlet(server));
		server.addServlet("/hello", new HelloServlet());
		server.addServlet("/.*", new FileServlet("public"));
		server.start();
	}
}
