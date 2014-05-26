package chat.server;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * チャットのメッセージ。
 */
public class ChatMessage {
	/**
	 * 日付と時刻のフォーマット。
	 */
	public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

	/**
	 * メッセージの日付と時刻。
	 */
	private Date date;

	/**
	 * メッセージのユーザー名。
	 */
	private String user;

	/**
	 * メッセージの内容。
	 */
	private String text;

	/**
	 * コンストラクタ。
	 * @param user ユーザー名
	 * @param text メッセージの内容
	 */
	public ChatMessage(String user, String text) {
		date = new Date();
		this.user = user;
		this.text = text;
	}

	/**
	 * メッセージの日付と時刻を取得する。
	 * @return メッセージの日付と時刻
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * メッセージの日付と時刻を文字列に変換する。
	 * @return 日付と時刻を文字列に変換したもの
	 */
	public String getDateString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.format(date);
	}

	/**
	 * メッセージのユーザー名を取得する。
	 * @return メッセージのユーザー名
	 */
	public String getUser() {
		return user;
	}

	/**
	 * メッセージの内容を取得する。
	 * @return メッセージの内容
	 */
	public String getText() {
		return text;
	}

	/**
	 * メッセージを文字列に変換する。
	 * @return メッセージを文字列に変換したもの
	 */
	public String toString() {
		return String.format("%s %-8s %s", getDateString(), user, text);
	}
}
