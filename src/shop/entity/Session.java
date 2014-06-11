package shop.entity;

import java.sql.Timestamp;

/**
 * セッションクラス。
 */
public class Session {
	/**
	 * セッション ID。
	 */
	private String id;

	/**
	 * ユーザー。
	 */
	private User user;

	/**
	 * セッションの日付。
	 */
	private Timestamp date;

	/**
	 * コンストラクタ。
	 * @param id セッション ID
	 */
	public Session(String id, User user, Timestamp date) {
		this.id = id;
		this.user = user;
		this.date = date;
	}

	/**
	 * セッション ID を返す。
	 * @return セッション ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * ユーザーを返す。
	 * @return ユーザー
	 */
	public User getUser() {
		return user;
	}

	/**
	 * ユーザーを設定する。
	 * @param user ユーザー
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * セッションの日付を返す。
	 * @param date セッションの日付
	 */
	public Timestamp getDate() {
		return date;
	}
}
