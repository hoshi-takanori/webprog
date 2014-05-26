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
		WebServer container = WebServer.getInstance();
		container.addServlet("/", new AdminServlet());
		container.addServlet("/hello", new HelloServlet());
		container.addServlet("/files/.*", new FileServlet());
		container.start();
	}
}
