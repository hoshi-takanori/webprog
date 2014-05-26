package chat.client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * チャットのクライアント、その 1 (毎回接続し直して read および write を実行する)。
 */
public class ChatClient01 extends ChatClient00 {
	/**
	 * コマンドを実行する。
	 * @param line コマンドライン
	 * @throws IOException 入出力に関する例外が発生
	 */
	@Override
	public void handleCommand(String line) throws IOException {
		if (line.equals("read")) {
			handleRead();
		} else if (line.equals("write")) {
			handleWrite();
		} else {
			System.err.println("そんなコマンドは知りません。");
		}
	}

	/**
	 * read コマンドを実行する。
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void handleRead() throws IOException {
		// サーバーに接続する。
		try (Socket socket = new Socket(host, port)) {
			// READ コマンドを送信する。
			PrintStream writer = new PrintStream(socket.getOutputStream(), true, "UTF-8");
			writer.println("READ");

			// 結果を受信する。
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			// 結果の 1 行目 (メッセージの個数) を受信する。
			String result = reader.readLine();
			if (result == null) {
				System.err.println("メッセージの個数を取得できません。");
				return;
			}

			// メッセージの個数を整数に変換する。
			int num;
			try {
				num = Integer.parseInt(result);
			} catch (NumberFormatException e) {
				System.err.println("メッセージの個数を整数に変換できません。");
				return;
			}

			// num 個のメッセージを受信し、出力する。
			for (int i = 0; i < num; i++) {
				String message = reader.readLine();
				if (message == null) {
					System.err.println("メッセージが途中で来なくなりました。");
					return;
				}
				System.out.println(message);
			}
		}
	}

	/**
	 * write コマンドを実行する。
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void handleWrite() throws IOException {
		// メッセージの内容を入力する。
		System.out.print("write> ");
		System.out.flush();
		String message = stdinReader.readLine();
		if (message == null) {
			throw new EOFException("メッセージの入力中に終了します。");
		}

		// サーバーに接続する。
		try (Socket socket = new Socket(host, port)) {
			// WRITE コマンドを送信する。
			PrintStream writer = new PrintStream(socket.getOutputStream(), true, "UTF-8");
			writer.println("WRITE " + user + " " + message);

			// 結果を受信する。
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			String result = reader.readLine();
			if (result == null) {
				System.err.println("書き込みのお返事が来ません。");
				return;
			}
			if (result.equals("OK")) {
				System.out.println("書き込み成功しました。");
			} else {
				System.err.println("書き込み失敗しました。");
			}
		}
	}

	/**
	 * main メソッド。
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		try {
			new ChatClient01().start(args);
		} catch (NumberFormatException e) {
			System.err.println("コマンドライン引数を整数に変換できません。");
		} catch (IOException e) {
			System.err.println("入出力に関する例外が発生しました。");
			e.printStackTrace();
		}
	}
}
