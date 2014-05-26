package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * チャットのクライアント、その 0 (抽象クラス)。
 */
public abstract class ChatClient00 {
	/**
	 * デフォルトの接続先ホスト名。
	 */
	public static final String DEFAULT_HOST = "localhost";

	/**
	 * デフォルトの接続先ポート番号。
	 */
	public static final int DEFAULT_PORT = 1234;

	/**
	 * 接続先のホスト名。
	 */
	protected String host;

	/**
	 * 接続先のポート番号。
	 */
	protected int port;

	/**
	 * ユーザー名。
	 */
	protected String user;

	/**
	 * 標準入力を読むやつ。
	 */
	protected BufferedReader stdinReader;

	/**
	 * コマンドを実行する (抽象メソッド)。
	 * @param line コマンドライン
	 */
	public abstract void handleCommand(String line) throws IOException;

	/**
	 * コマンドの入力を実行を繰り返す。
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void commandLoop() throws IOException {
		stdinReader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("chat> ");
			System.out.flush();
			String line = stdinReader.readLine();
			if (line == null || line.equals("exit")) {
				break;
			} else {
				handleCommand(line);
			}
		}
	}

	/**
	 * コマンドライン引数からホスト名、ポート番号、ユーザー名を取得し、コマンドループに入る。
	 * @param args コマンドライン引数
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void start(String[] args) throws IOException {
		// 1 番目のコマンドライン引数が存在すれば、それをホスト名とする。
		if (args.length > 0) {
			host = args[0];
		} else {
			host = DEFAULT_HOST;
		}

		// 2 番目のコマンドライン引数が存在すれば、それをポート番号とする。
		if (args.length > 1) {
			port = Integer.parseInt(args[1]);
		} else {
			port = DEFAULT_PORT;
		}

		// 3 番目のコマンドライン引数が存在すれば、それをユーザー名とする。
		if (args.length > 2) {
			user = args[2];
		} else {
			user = System.getProperty("user.name");
		}

		// コマンドループに入る。
		commandLoop();
	}
}
