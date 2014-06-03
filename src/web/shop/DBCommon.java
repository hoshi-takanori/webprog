package web.shop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * DB 共通クラス。
 */
public class DBCommon implements AutoCloseable {
	/**
	 * デフォルトの設定ファイル名。
	 */
	public static final String DEFAULT_SETTINGS_NAME = "shopping";

	/**
	 * DB 接続。
	 */
	private Connection conn;

	/**
	 * コンストラクタ。デフォルトの設定ファイルに基づいて
	 * DB に接続し、インスタンス変数 conn に DB 接続を格納する。
	 *
	 * @throws ClassNotFoundException JDBC ドライバが見つからない
	 * @throws SQLException DB への接続時にエラーが発生
	 */
	public DBCommon() throws ClassNotFoundException, SQLException {
		this(DEFAULT_SETTINGS_NAME);
	}

	/**
	 * コンストラクタ。引数で指定された設定ファイルに基づいて
	 * DB に接続し、インスタンス変数 conn に DB 接続を格納する。
	 *
	 * @param settingsName 設定ファイルの名前
	 * @throws ClassNotFoundException JDBC ドライバが見つからない
	 * @throws SQLException DB への接続時にエラーが発生
	 */
	public DBCommon(String settingsName)
			throws ClassNotFoundException, SQLException {
		// JDBC ドライバを読み込む。
		Class.forName("org.postgresql.Driver");

		// データベースの設定を取得する。
		ResourceBundle settings = ResourceBundle.getBundle(settingsName);
		String host = settings.getString("DB_HOST");
		String name = settings.getString("DB_NAME");
		String user = settings.getString("DB_USER");
		String password = settings.getString("DB_PASSWORD");

		// DB に接続する。
		String url = "jdbc:postgresql://" + host + "/" + name;
		conn = DriverManager.getConnection(url, user, password);
	}

	/**
	 * DB 接続を閉じる。
	 *
	 * @throws SQLException DB 接続を閉じる時にエラーが発生
	 */
	@Override
	public void close() throws SQLException {
		if (conn != null) {
			try {
				conn.close();
			} finally {
				conn = null;
			}
		}
	}

	/**
	 * DB 接続を返す (ゲッター)。
	 *
	 * @return DB 接続
	 */
	public Connection getConnection() {
		return conn;
	}
}
