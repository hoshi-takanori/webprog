package shop.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import shop.entity.Session;
import shop.entity.User;

/**
 * セッション DAO。
 */
public class SessionDao {
	/**
	 * データベース。
	 */
	private ShopDB db;

	/**
	 * コンストラクタ。
	 * @param db データベース
	 */
	public SessionDao(ShopDB db) {
		this.db = db;
	}

	/**
	 * DB からセッションを取得する。
	 * @param sessionId セッション ID
	 * @return セッション
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public Session selectSession(String sessionId) throws SQLException {
		if (sessionId == null) {
			return null;
		}

		String sql = "select user_id, date from sessions where id = ?";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setString(1, sessionId);
			try (ResultSet rs = st.executeQuery()) {
				if (rs.next()) {
					int userId = rs.getInt("user_id");
					User user = null;
					if (userId != 0) {
						user = db.getUserDao().selectUser(userId);
					}
					Timestamp date = rs.getTimestamp("date");
					return new Session(sessionId, user, date);
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * 新規セッションを生成し、DB に登録して返す。
	 * @return 生成したセッション
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public Session generateSession() throws SQLException {
		int rand = (int) (Math.random() * 1000000000);
		long time = System.currentTimeMillis();
		String sessionId = String.format("%09d-%013d", rand, time);

		String sql = "insert into sessions (id) values (?)";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setString(1, sessionId);
			int numRows = st.executeUpdate();
			if (numRows == 1) {
				return selectSession(sessionId);
			} else {
				throw new SQLException("セッションの生成に失敗しました。");
			}
		}
	}

	/**
	 * セッションのユーザー ID を更新する。
	 * @param session セッション
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public void updateSession(Session session) throws SQLException {
		String sql = "update sessions set user_id = ?, date = ? where id = ?";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			if (session.getUser() != null) {
				st.setInt(1, session.getUser().getId());
			} else {
				st.setNull(1, Types.INTEGER);
			}
			st.setTimestamp(2, session.getDate());
			st.setString(3, session.getId());
			int numRows = st.executeUpdate();
			if (numRows != 1) {
				throw new SQLException("セッションの更新に失敗しました。");
			}
		}
	}
}
