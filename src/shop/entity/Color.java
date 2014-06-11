package shop.entity;

/**
 * 色クラス。
 */
public class Color {
	/**
	 * 赤。
	 */
	private int red;

	/**
	 * 緑。
	 */
	private int green;

	/**
	 * 青。
	 */
	private int blue;

	/**
	 * 色コードから色を生成する。
	 * @param code 色コード
	 * @return 色
	 */
	public static Color fromCode(String code) {
		if (code == null || ! code.matches("^[0-9a-fA-F]{6}$")) {
			return null;
		}
		int red = Integer.parseInt(code.substring(0, 2), 16);
		int green = Integer.parseInt(code.substring(2, 4), 16);
		int blue = Integer.parseInt(code.substring(4, 6), 16);
		return new Color(red, green, blue);
	}

	/**
	 * ランダムな色を生成する。
	 * @return 色
	 */
	public static Color randomColor() {
		int red = (int) (Math.random() * 256);
		int green = (int) (Math.random() * 256);
		int blue = (int) (Math.random() * 256);
		return new Color(red, green, blue);
	}

	/**
	 * コンストラクタ。
	 * @param red 赤
	 * @param green 緑
	 * @param blue 青
	 */
	public Color(int red, int green, int blue) {
		this.red = Math.max(0, Math.min(255, red));
		this.green = Math.max(0, Math.min(255, green));
		this.blue = Math.max(0, Math.min(255, blue));
	}

	/**
	 * 赤を返す。
	 * @return 赤
	 */
	public int getRed() {
		return red;
	}

	/**
	 * 緑を返す。
	 * @return 緑
	 */
	public int getGreen() {
		return red;
	}

	/**
	 * 青を返す。
	 * @return 青
	 */
	public int getBlue() {
		return red;
	}

	/**
	 * 色コードを返す。
	 * @return 色コード
	 */
	public String getCode() {
		return String.format("%02x%02x%02x", red, green, blue);
	}
}
