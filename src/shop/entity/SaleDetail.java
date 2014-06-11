package shop.entity;

/**
 * 売り上げ詳細クラス。
 */
public class SaleDetail {
	/**
	 * 売り上げ詳細 ID。
	 */
	private int id;

	/**
	 * 色。
	 */
	private Color color;

	/**
	 * コンストラクタ。
	 * @param id 売り上げ詳細 ID
	 * @param color 色
	 */
	public SaleDetail(int id, Color color) {
		this.id = id;
		this.color = color;
	}

	/**
	 * 売り上げ詳細 ID を返す。
	 * @return 売り上げ詳細 ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * 色を返す。
	 * @return 色
	 */
	public Color getColor() {
		return color;
	}
}
