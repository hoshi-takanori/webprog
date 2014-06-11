package shop.dao;

import java.sql.SQLException;

/**
 * お買い物サイトの DB クラス。
 */
public class ShopDB extends DBCommon {
	/**
	 * セッション DAO。
	 */
	private SessionDao sessionDao;

	/**
	 * ユーザー DAO。
	 */
	private UserDao userDao;

	/**
	 * カート DAO。
	 */
	private CartDao cartDao;

	/**
	 * 売り上げ DAO。
	 */
	private SaleDao saleDao;

	/**
	 * コンストラクタ。
	 * @throws ClassNotFoundException JDBC ドライバが見つからない
	 * @throws SQLException DB への接続時にエラーが発生
	 */
	public ShopDB() throws ClassNotFoundException, SQLException {
		super();
	}

	/**
	 * セッション DAO を、なければ生成して、返す。
	 * @return セッション DAO
	 */
	public SessionDao getSessionDao() {
		if (sessionDao == null) {
			sessionDao = new SessionDao(this);
		}
		return sessionDao;
	}

	/**
	 * ユーザー DAO を、なければ生成して、返す。
	 * @return ユーザー DAO
	 */
	public UserDao getUserDao() {
		if (userDao == null) {
			userDao = new UserDao(this);
		}
		return userDao;
	}

	/**
	 * カート DAO を、なければ生成して、返す。
	 * @return カート DAO
	 */
	public CartDao getCartDao() {
		if (cartDao == null) {
			cartDao = new CartDao(this);
		}
		return cartDao;
	}

	/**
	 * 売り上げ DAO を、なければ生成して、返す。
	 * @return 売り上げ DAO
	 */
	public SaleDao getSaleDao() {
		if (saleDao == null) {
			saleDao = new SaleDao(this);
		}
		return saleDao;
	}
}
