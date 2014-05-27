package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
	private ChatServer server;

	/**
	 * 接続のソケット。
	 */
	private Socket socket;

	/**
	 * 接続の入力を読むやつ。
	 */
	private BufferedReader reader;

	/**
	 * 接続の出力に書くやつ。
	 */
	private PrintStream writer;

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
	 * チャットサーバーを取得する。
	 * @return チャットサーバー
	 */
	public ChatServer getServer() {
		return server;
	}

	/**
	 * セッション ID 付きでログを出力する。
	 * @param message ログのメッセージ
	 */
	public void sessionLog(String message) {
		ChatLogger.log("[" + sessionId + "] " + message);
	}

	/**
	 * クライアントに 1 行送信する。
	 * @param line 送信する内容
	 */
	public void sendLine(String line) {
		ChatLogger.verboseLog("[" + sessionId + "] -> \"" + line + "\"");
		writer.println(line);
	}

	/**
	 * クライアントからのコマンドを読み込み、各行を配列に入れて返す。
	 * なお、コマンドは複数行の引数を持つことができ、最後に空行が来る。
	 * @return コマンド (最初の要素) とその引数 (残りの要素)
	 * @throws IOException 入出力に関する例外が発生
	 */
	public String[] receiveCommand() throws IOException {
		List<String> list = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			ChatLogger.verboseLog("[" + sessionId + "] <- \"" + line + "\"");
			if (line.length() == 0) {
				break;
			}
			list.add(line);
		}
		return list.toArray(new String[0]);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		sessionLog("connection closed");
	}
}
