package shop.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import shop.entity.Cart;
import shop.entity.Color;
import shop.entity.Session;

/**
 * カート DAO。
 */
public class CartDao {
	/**
	 * データベース。
	 */
	private ShopDB db;

	/**
	 * コンストラクタ。
	 * @param db データベース
	 */
	public CartDao(ShopDB db) {
		this.db = db;
	}

	/**
	 * DB からカートを検索する。
	 * @param session セッション
	 * @return カートのリスト
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public List<Cart> selectCarts(Session session) throws SQLException {
		String sql = "select id, color from cart where session_id = ? order by id";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setString(1, session.getId());
			try (ResultSet rs = st.executeQuery()) {
				List<Cart> cartList = new ArrayList<Cart>();
				while (rs.next()) {
					int cartId = rs.getInt("id");
					Color color = Color.fromCode(rs.getString("color"));
					cartList.add(new Cart(cartId, session, color));
				}
				return cartList;
			}
		}
	}

	/**
	 * DB からカートを取得する。
	 * @param session セッション
	 * @param cartId カート ID
	 * @return カート
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public Cart selectCart(Session session, int cartId) throws SQLException {
		String sql = "select color from cart where session_id = ? and id = ?";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setString(1, session.getId());
			st.setInt(2, cartId);
			try (ResultSet rs = st.executeQuery()) {
				if (rs.next()) {
					Color color = Color.fromCode(rs.getString("color"));
					return new Cart(cartId, session, color);
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * カートを追加する。
	 * @param cart カート
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public void insertCart(Cart cart) throws SQLException {
		String sql = "insert into cart (session_id, color) values (?, ?)";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setString(1, cart.getSession().getId());
			st.setString(2, cart.getColor().getCode());
			int numRows = st.executeUpdate();
			if (numRows != 1) {
				throw new SQLException("カートの追加に失敗しました。");
			}
		}
	}

	/**
	 * カートを削除する。
	 * @param cart カート
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public void deleteCart(Cart cart) throws SQLException {
		String sql = "delete from cart where id = ? and session_id = ?";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setInt(1, cart.getId());
			st.setString(2, cart.getSession().getId());
			int numRows = st.executeUpdate();
			if (numRows != 1) {
				throw new SQLException("カートの削除に失敗しました。");
			}
		}
	}
}
