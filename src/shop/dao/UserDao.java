package shop.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import shop.entity.User;

/**
 * ユーザー DAO。
 */
public class UserDao {
	/**
	 * データベース。
	 */
	private ShopDB db;

	/**
	 * コンストラクタ。
	 * @param db データベース
	 */
	public UserDao(ShopDB db) {
		this.db = db;
	}

	/**
	 * DB からユーザーを取得する。
	 * @param userId ユーザー ID
	 * @return ユーザー
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public User selectUser(int userId) throws SQLException {
		String sql = "select name, k_name, password from users where id = ?";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setInt(1, userId);
			try (ResultSet rs = st.executeQuery()) {
				if (rs.next()) {
					String name = rs.getString("name");
					String displayName = rs.getString("k_name");
					String password = rs.getString("password");
					return new User(userId, name, displayName, password);
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * ユーザー名とパスワードが一致するユーザーを検索する。
	 * @param username ユーザー名
	 * @param password パスワード
	 * @return ユーザー
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public User findUser(String username, String password) throws SQLException {
		String sql = "select id, k_name from users where name = ? and password = ?";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setString(1, username);
			st.setString(2, password);
			try (ResultSet rs = st.executeQuery()) {
				if (rs.next()) {
					int userId = rs.getInt("id");
					String displayName = rs.getString("k_name");
					return new User(userId, username, displayName, password);
				} else {
					return null;
				}
			}
		}
	}
}
