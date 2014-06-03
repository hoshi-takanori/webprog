package web.shop;

import web.example.FileServlet;
import web.server.WebServer;

/**
 * お買い物サイトのメインクラス。
 */
public class ShopMain {
	/**
	 * main メソッド。
	 * @param args コマンド行引数
	 */
	public static void main(String[] args) {
		WebServer server = new WebServer("web");
		server.addServlet("/", new ColorServlet());
		server.addServlet("/cart", new CartServlet());
		server.addServlet("/login", new LoginServlet());
		server.addServlet("/.*", new FileServlet("public"));
		server.start();
	}
}
