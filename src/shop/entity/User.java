package shop.entity;

/**
 * ユーザークラス。
 */
public class User {
	/**
	 * ユーザー ID。
	 */
	private int id;

	/**
	 * ユーザーのアカウント。
	 */
	private String name;

	/**
	 * ユーザーの表示名。
	 */
	private String displayName;

	/**
	 * ユーザーのパスワード。
	 */
	private String password;

	/**
	 * コンストラクタ。
	 * @param id ユーザー ID
	 * @param name ユーザーのアカウント
	 * @param displayName ユーザーの表示名
	 * @param password ユーザーのパスワード
	 */
	public User(int id, String name, String displayName, String password) {
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.password = password;
	}

	/**
	 * ユーザー ID を返す。
	 * @return ユーザー ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * ユーザーのアカウントを返す。
	 * @return ユーザーのアカウント
	 */
	public String getName() {
		return name;
	}

	/**
	 * ユーザーの表示名を返す。
	 * @return ユーザーの表示名
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * ユーザーのパスワードを返す。
	 * @return ユーザーのパスワード
	 */
	public String getPassword() {
		return password;
	}
}
