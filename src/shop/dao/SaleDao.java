package shop.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import shop.entity.Cart;
import shop.entity.Color;
import shop.entity.Sale;
import shop.entity.SaleDetail;
import shop.entity.User;

/**
 * 売り上げ DAO。
 */
public class SaleDao {
	/**
	 * データベース。
	 */
	private ShopDB db;

	/**
	 * コンストラクタ。
	 * @param db データベース
	 */
	public SaleDao(ShopDB db) {
		this.db = db;
	}

	/**
	 * DB から売り上げを検索する。
	 * @param user ユーザー
	 * @return 売り上げのリスト
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public List<Sale> selectSales(User user) throws SQLException {
		String sql = "select id, date from sales where user_id = ? order by date desc";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setInt(1, user.getId());
			try (ResultSet rs = st.executeQuery()) {
				List<Sale> saleList = new ArrayList<Sale>();
				while (rs.next()) {
					int saleId = rs.getInt("id");
					Timestamp date = rs.getTimestamp("date");
					Sale sale = new Sale(saleId, user, date);
					sale.setDetailList(selectSaleDetails(sale));
					saleList.add(sale);
				}
				return saleList;
			}
		}
	}

	/**
	 * DB から売り上げを取得する。
	 * @param user ユーザー
	 * @param saleId 売り上げ ID
	 * @return 売り上げ
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public Sale selectSale(User user, int saleId) throws SQLException {
		String sql = "select date from sales where user_id = ? and id = ?";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setInt(1, user.getId());
			st.setInt(2, saleId);
			try (ResultSet rs = st.executeQuery()) {
				if (rs.next()) {
					Timestamp date = rs.getTimestamp("date");
					Sale sale = new Sale(saleId, user, date);
					sale.setDetailList(selectSaleDetails(sale));
					return sale;
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * DB から売り上げ詳細を検索する。
	 * @param sale 売り上げ
	 * @return 売り上げ詳細のリスト
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public List<SaleDetail> selectSaleDetails(Sale sale) throws SQLException {
		String sql = "select detail_id, color from sales_detail where sales_id = ? order by detail_id";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql)) {
			st.setInt(1, sale.getId());
			try (ResultSet rs = st.executeQuery()) {
				List<SaleDetail> detailList = new ArrayList<SaleDetail>();
				while (rs.next()) {
					int detailId = rs.getInt("detail_id");
					Color color = Color.fromCode(rs.getString("color"));
					detailList.add(new SaleDetail(detailId, color));
				}
				return detailList;
			}
		}
	}

	/**
	 * 売り上げを追加する。
	 * @param user ユーザー
	 * @param cartList カートのリスト
	 * @return 追加した売り上げ
	 * @throws SQLException DB の処理時にエラーが発生
	 */
	public Sale insertSale(User user, List<Cart> cartList) throws SQLException {
		// トランザクションを開始する。
		try (Statement st = db.getConnection().createStatement()) {
			st.executeUpdate("begin transaction");
		}

		// 購入テーブルに追加する。
		String sql1 = "insert into sales (user_id) values (?)";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql1)) {
			st.setInt(1, user.getId());
			int numRows = st.executeUpdate();
			if (numRows != 1) {
				throw new SQLException("売り上げの追加に失敗しました。");
			}
		}

		// 追加した購入 ID を取得する。
		String sql2 = "select currval('sales_id_seq') as sales_id";
		int saleId;
		try (Statement st = db.getConnection().createStatement();
				ResultSet rs = st.executeQuery(sql2)) {
			if (rs.next()) {
				saleId = rs.getInt("sales_id");
			} else {
				throw new SQLException("売り上げ ID の追加に失敗しました。");
			}
		}

		// 購入詳細テーブルに追加する。
		String sql3 = "insert into sales_detail (sales_id, detail_id, color) values (?, ?, ?)";
		try (PreparedStatement st = db.getConnection().prepareStatement(sql3)) {
			for (int i = 0; i < cartList.size(); i++) {
				st.setInt(1, saleId);
				st.setInt(2, i + 1);
				st.setString(3, cartList.get(i).getColor().getCode());
				int numRows = st.executeUpdate();
				if (numRows != 1) {
					throw new SQLException("売り上げ明細の追加に失敗しました。");
				}
			}
		}

		// トランザクションをコミットする。
		try (Statement st = db.getConnection().createStatement()) {
			st.executeUpdate("commit transaction");
		}

		// 追加した売り上げを返す。
		return selectSale(user, saleId);
	}
}
