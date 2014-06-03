package web.shop;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * お買い物サイトの DB クラス。
 */
public class ShopDB extends DBCommon {
	/**
	 * コンストラクタ。
	 * @throws ClassNotFoundException JDBC ドライバが見つからない
	 * @throws SQLException DB への接続時にエラーが発生
	 */
	public ShopDB() throws ClassNotFoundException, SQLException {
		super();
	}

	/**
	 * セッション ID が有効かどうかチェックする。
	 * @param sessionId セッション ID
	 * @return セッション ID が有効なら true
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public boolean checkSessionId(String sessionId) throws SQLException {
		String sql = "select * from sessions where id = ?";
		try (PreparedStatement st = getConnection().prepareStatement(sql)) {
			st.setString(1, sessionId);
			try (ResultSet rs = st.executeQuery()) {
				if (rs.next()) {
					// セッションの有効期限をチェックすべき。
					return true;
				}
				return false;
			}
		}
	}

	/**
	 * セッション ID を生成し、DB に登録して返す。
	 * @return 生成したセッション ID
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public String generateSessionId() throws SQLException {
		int rand = (int) (Math.random() * 1000000000);
		long time = System.currentTimeMillis();
		String sessionId = String.format("%09d-%013d", rand, time);

		String sql = "insert into sessions (id) values (?)";
		try (PreparedStatement st = getConnection().prepareStatement(sql)) {
			st.setString(1, sessionId);
			int numRows = st.executeUpdate();
			if (numRows == 1) {
				return sessionId;
			}
			throw new SQLException("セッション ID の生成に失敗しました。");
		}
	}
}
