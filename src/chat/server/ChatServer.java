package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 簡単なチャットサーバー。
 */
public class ChatServer {
	/**
	 * デフォルトの待ち受けポート番号。
	 */
	public static final int DEFAULT_PORT = 1234;

	/**
	 * メッセージ一覧。
	 */
	private List<ChatMessage> messages;

	/**
	 * コンストラクタ。
	 */
	public ChatServer() {
		messages = new ArrayList<ChatMessage>();
	}

	/**
	 * メッセージの一覧を、新しいものから num 個取得する。
	 * @param num 取得するメッセージの個数、0 ならすべて
	 * @return メッセージの一覧
	 */
	public List<ChatMessage> getMessages(int num) {
		List<ChatMessage> result = new ArrayList<ChatMessage>();
		synchronized (messages) {
			if (num == 0) {
				// num == 0 の場合、すべてのメッセージを返す。
				result.addAll(messages);
			} else {
				// それ以外なら、最新 num 個のメッセージを返す。
				int start = Math.max(messages.size() - num, 0);
				result.addAll(messages.subList(start, messages.size()));
			}
		}
		return result;
	}

	/**
	 * メッセージを追加する。
	 * @param message 追加するメッセージ
	 */
	public void addMessage(ChatMessage message) {
		synchronized (messages) {
			messages.add(message);
		}
	}

	/**
	 * チャットサーバーの処理を実行する。
	 * @param args コマンドライン引数
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void start(String[] args) throws IOException {
		// コマンドライン引数が存在すれば、それをポート番号とする。
		int port = DEFAULT_PORT;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}
		ChatLogger.log("opening port " + port);

		// ポートを開き、接続を待ち受ける。
		try (ServerSocket listener = new ServerSocket(port)) {
			while (true) {
				// 接続されたら、セッションを生成・実行する。
				Socket socket = listener.accept();
				ChatLogger.log("connected from " + socket.getInetAddress());
				ChatSession01 session = new ChatSession01(this, socket);
				session.run();
			}
		}
	}

	/**
	 * main メソッド。
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		try {
			new ChatServer().start(args);
		} catch (NumberFormatException e) {
			System.err.println("コマンドライン引数を整数に変換できません。");
		} catch (IOException e) {
			System.err.println("入出力に関する例外が発生しました。");
			e.printStackTrace();
		}
	}
}
