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
	 * ログを出力する。
	 * @param message ログのメッセージ
	 */
	public static void log(String message) {
		if (DEBUG_MODE) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
			System.out.println(dateFormat.format(new Date()) + " " + message);
		}
	}
}
