package chat.client;

import java.io.EOFException;
import java.io.IOException;

/**
 * チャットのクライアント、その 2 (接続しっぱなしで read および write を実行する)。
 */
public class ChatClient03 extends ChatClient02 {
	/**
	 * コマンドを実行する。
	 * @param line コマンドライン
	 * @throws IOException 入出力に関する例外が発生
	 */
	@Override
	public void handleCommand(String line) throws IOException {
		if (line.equals("stream")) {
			handleStream();
		} else {
			super.handleCommand(line);
		}
	}

	/**
	 * stream コマンドを実行する。
	 * @throws IOException 入出力に関する例外が発生
	 */
	public void handleStream() throws IOException {
		// STREAM コマンドを送信する。
		writer.println("STREAM");
		writer.println("START");
		writer.println();

		// 結果を受信する。
		String result = reader.readLine();
		if (result == null) {
			throw new EOFException("ストリーミングのお返事が来ません。");
		} else if (! result.equals("STREAM OK")) {
			System.err.println("ストリーミング失敗しました。");
			return;
		}

		// ストリーミングを実行する。
		System.out.println("ストリーミングを開始します。終了は Ctrl-C または Ctrl-Z です。");
		while (true) {
			String stream = reader.readLine();
			if (stream != null && stream.equals("STREAM 1")) {
				String message = reader.readLine();
				if (message != null) {
					System.out.println(message);
				} else {
					break;
				}
			} else {
				break;
			}
		}
	}

	/**
	 * main メソッド。
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		try {
			new ChatClient03().start(args);
		} catch (NumberFormatException e) {
			System.err.println("コマンドライン引数を整数に変換できません。");
		} catch (IOException e) {
			System.err.println("入出力に関する例外が発生しました。");
			e.printStackTrace();
		}
	}
}
