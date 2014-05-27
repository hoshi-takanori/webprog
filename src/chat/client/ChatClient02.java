package chat.client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * チャットのクライアント、その 2 (接続しっぱなしで read および write を実行する)。
 */
public class ChatClient02 extends ChatClient01 {
	/**
	 * コマンドの送信先。
	 */
	protected PrintStream writer;

	/**
	 * 結果の受信元。
	 */
	protected BufferedReader reader;

	/**
	 * コマンドの入力を実行を繰り返す。
	 * @throws IOException 入出力に関する例外が発生
	 */
	@Override
	public void commandLoop() throws IOException {
		// サーバーに接続する。
		try (Socket socket = new Socket(host, port)) {
			writer = new PrintStream(socket.getOutputStream(), true, "UTF-8");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			// ログインする。
			writer.println("USER");
			writer.println(user);
			writer.println();

			// ログインの結果を受信する。
			if (! receiveResult(reader, "USER", "ログイン")) {
				return;
			}

			// コマンドループに入る。
			super.commandLoop();
		}
	}

	/**
	 * コマンドを実行する。
	 * @param line コマンドライン
	 * @throws IOException 入出力に関する例外が発生
	 */
	@Override
	public void handleCommand(String line) throws IOException {
		if (line.equals("read") || line.startsWith("read ")) {
			handleRead(line);
		} else {
			super.handleCommand(line);
		}
	}

	/**
	 * read コマンドを実行する。
	 * @param line コマンドライン
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void handleRead(String line) throws IOException {
		// 表示するメッセージの個数 (デフォルトは 10 個)。
		int num = 10;

		// read コマンドの引数を解析する。
		String[] array = line.split(" ", 2);
		if (array.length == 2) {
			if (array[1].equals("all")) {
				num = 0;
			} else {
				try {
					num = Integer.parseInt(array[1]);
				} catch (NumberFormatException e) {
					System.err.println("引数を整数に変換できません。");
					return;
				}
			}
		}

		// READ コマンドを送信する。
		writer.println("READ");
		if (num != 0) {
			writer.println(num);
		}
		writer.println();

		// 結果を受信し、表示する。
		receiveMessages(reader);
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

		// WRITE コマンドを送信する。
		writer.println("WRITE");
		writer.println(message);
		writer.println();

		// 結果を受信する。
		receiveResult(reader, "WRITE", "書き込み");
	}

	/**
	 * main メソッド。
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		try {
			new ChatClient02().start(args);
		} catch (NumberFormatException e) {
			System.err.println("コマンドライン引数を整数に変換できません。");
		} catch (IOException e) {
			System.err.println("入出力に関する例外が発生しました。");
			e.printStackTrace();
		}
	}
}
