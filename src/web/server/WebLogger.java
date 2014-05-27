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
	 * ログを出力する。
	 * @param message ログのメッセージ
	 */
	public static void log(String message) {
		if (debugMode) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
			System.out.println(dateFormat.format(new Date()) + " " + message);
		}
	}
}
