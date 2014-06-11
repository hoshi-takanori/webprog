package shop.entity;

/**
 * カートクラス。
 */
public class Cart {
	/**
	 * カート ID。
	 */
	private int id;

	/**
	 * セッション。
	 */
	private Session session;

	/**
	 * 色。
	 */
	private Color color;

	/**
	 * コンストラクタ。
	 * @param id カート ID
	 * @param session セッション
	 * @param color 色
	 */
	public Cart(int id, Session session, Color color) {
		this.id = id;
		this.session = session;
		this.color = color;
	}

	/**
	 * カート ID を返す。
	 * @return カート ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * セッションを返す。
	 * @return セッション
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * 色を返す。
	 * @return 色
	 */
	public Color getColor() {
		return color;
	}
}
