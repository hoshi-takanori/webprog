package shop.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 売り上げクラス。
 */
public class Sale {
	/**
	 * 売り上げ ID。
	 */
	private int id;

	/**
	 * ユーザー。
	 */
	private User user;

	/**
	 * 売り上げた日付。
	 */
	private Timestamp date;

	/**
	 * 売り上げ詳細のリスト。
	 */
	private List<SaleDetail> detailList;

	/**
	 * コンストラクタ。
	 * @param id 売り上げ ID
	 * @param user ユーザー
	 * @param date 売り上げた日付
	 */
	public Sale(int id, User user, Timestamp date) {
		this.id = id;
		this.user = user;
		this.date = date;
		detailList = new ArrayList<SaleDetail>();
	}

	/**
	 * 売り上げ ID を返す。
	 * @return 売り上げ ID
	 */
	public int getId() {
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
	 * 売り上げた日付を返す。
	 * @return 売り上げた日付
	 */
	public Timestamp getDate() {
		return date;
	}

	/**
	 * 売り上げ詳細のリストを返す。
	 * @return 売り上げ詳細のリスト
	 */
	public List<SaleDetail> getDetailList() {
		return detailList;
	}

	/**
	 * 売り上げ詳細のリストを設定する。
	 * @param detailList 売り上げ詳細のリスト
	 */
	public void setDetailList(List<SaleDetail> detailList) {
		this.detailList = detailList;
	}
}
