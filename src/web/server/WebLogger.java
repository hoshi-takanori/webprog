package web.server;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ログを出力するクラス。
 */
public class WebLogger {
	/**
	 * true ならログを出力する。
	 */
	public static boolean debugMode;

	/**
	 * true ならクライアントとの間で送受信した内容を出力する。
	 */
	public static boolean verboseMode;

	/**
	 * ログを出力する。
	 * @param message ログのメッセージ
	 */
	public static void log(String message) {
		if (debugMode) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
			System.out.println(dateFormat.format(new Date()) + " " + message);
		}
	}

	/**
	 * クライアントとの間で送受信した内容を出力する。
	 * @param message クライアントとの間で送受信した内容
	 */
	public static void verboseLog(String message) {
		if (verboseMode) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
			System.out.println(dateFormat.format(new Date()) + " " + message);
		}
	}
}
