package chat.server;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ログを出力するクラス。
 */
public class ChatLogger {
	/**
	 * true ならログを出力する。
	 */
	public static final boolean DEBUG_MODE = true;

	/**
	 * true ならクライアントとの間で送受信した内容を出力する。
	 */
	public static final boolean VERBOSE_MODE = true;

	/**
	 * ログを出力する。
	 * @param message ログのメッセージ
	 */
	public static void log(String message) {
		if (DEBUG_MODE) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
			System.out.println(dateFormat.format(new Date()) + " " + message);
		}
	}

	/**
	 * クライアントとの間で送受信した内容を出力する。
	 * @param message クライアントとの間で送受信した内容
	 */
	public static void verboseLog(String message) {
		if (VERBOSE_MODE) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
			System.out.println(dateFormat.format(new Date()) + " " + message);
		}
	}
}
