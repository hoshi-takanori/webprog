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
		} else if (line.equals("write") || line.startsWith("write ")) {
			handleWrite(line);
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
			writer.println();

			// 結果を受信し、表示する。
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			receiveMessages(reader);
		}
	}

	/**
	 * read コマンドの結果を受信し、表示する。
	 * @param reader 結果の受信元
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void receiveMessages(BufferedReader reader) throws IOException {
		// 結果の 1 行目 (メッセージの個数) を受信する。
		String result = reader.readLine();
		if (result == null) {
			throw new EOFException("メッセージの個数を取得できません。");
		}

		// 結果をスペースで区切る。
		String[] array = result.split(" ");
		if (array.length != 2 || ! array[0].equals("READ")) {
			System.err.println("メッセージの個数を取得できません。");
			return;
		}

		// メッセージの個数を整数に変換する。
		int num;
		try {
			num = Integer.parseInt(array[1]);
		} catch (NumberFormatException e) {
			System.err.println("メッセージの個数を整数に変換できません。");
			return;
		}

		// num 個のメッセージを受信し、出力する。
		for (int i = 0; i < num; i++) {
			String message = reader.readLine();
			if (message == null) {
				throw new EOFException("メッセージが途中で来なくなりました。");
			}
			System.out.println(message);
		}
	}

	/**
	 * write コマンドを実行する。
	 * @param line コマンドライン
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void handleWrite(String line) throws IOException {
		// メッセージの内容をコマンドラインから取得する。
		String[] array = line.split(" ", 2);
		String message;
		if (array.length == 2) {
			message = array[1];
		} else {
			// メッセージの内容を入力する。
			System.out.print("write> ");
			System.out.flush();
			message = stdinReader.readLine();
			if (message == null) {
				throw new EOFException("メッセージの入力中に終了します。");
			}
		}

		// メッセージを送信する。
		writeMessage(message);
	}

	/**
	 * メッセージを送信する。
	 * @param message メッセージの内容
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void writeMessage(String message) throws IOException {
		// サーバーに接続する。
		try (Socket socket = new Socket(host, port)) {
			// WRITE コマンドを送信する。
			PrintStream writer = new PrintStream(socket.getOutputStream(), true, "UTF-8");
			writer.println("WRITE");
			writer.println(user);
			writer.println(message);
			writer.println();

			// 結果を受信する。
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			receiveResult(reader, "WRITE", "書き込み");
		}
	}

	/**
	 * コマンドの実行結果を受信する。
	 * @param reader 結果の受信元
	 * @param command コマンド
	 * @param commandName コマンドの表示名
	 * @return 成功したら true、失敗したら false
	 * @throws IOException 入出力に関する例外が発生
	 */
	public boolean receiveResult(BufferedReader reader, String command, String commandName) throws IOException {
		String result = reader.readLine();
		if (result == null) {
			throw new EOFException(commandName + "のお返事が来ません。");
		} else if (result.equals(command + " OK")) {
			System.out.println(commandName + "成功しました。");
			return true;
		} else {
			System.err.println(commandName + "失敗しました。");
			return false;
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
