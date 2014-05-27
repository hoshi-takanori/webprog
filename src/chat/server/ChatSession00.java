package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * チャットの接続を処理するやつ、その 0 (抽象クラス)。
 */
public abstract class ChatSession00 implements Runnable {
	/**
	 * 現在のセッション ID。
	 */
	private static int currentSessionId;

	/**
	 * セッション ID。
	 */
	private int sessionId;

	/**
	 * チャットサーバー。
	 */
	protected ChatServer server;

	/**
	 * 接続のソケット。
	 */
	protected Socket socket;

	/**
	 * 接続の入力を読むやつ。
	 */
	protected BufferedReader reader;

	/**
	 * 接続の出力に書くやつ。
	 */
	protected PrintStream writer;

	/**
	 * コンストラクタ。
	 * @param server チャットサーバー
	 * @param socket 接続のソケット
	 */
	public ChatSession00(ChatServer server, Socket socket) {
		sessionId = ++currentSessionId;
		this.server = server;
		this.socket = socket;
	}

	/**
	 * チャットの接続を処理する (抽象メソッド)。
	 * @throws IOException 入出力に関する例外が発生
	 */
	public abstract void handleSession() throws IOException;

	/**
	 * セッション ID 付きでログを出力する。
	 * @param message ログのメッセージ
	 */
	public void sessionLog(String message) {
		ChatLogger.log("[" + sessionId + "] " + message);
	}

	/**
	 * チャットの接続の処理を実行する (スレッドの入り口)。
	 */
	@Override
	public void run() {
		sessionLog("connected from " + socket.getInetAddress());
		try (Socket socket = this.socket) {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writer = new PrintStream(socket.getOutputStream(), true, "UTF-8");
			handleSession();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sessionLog("connection closed");
	}
}
