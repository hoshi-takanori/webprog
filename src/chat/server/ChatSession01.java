package chat.server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * チャットの接続を処理するやつ、その 1 (READ または WRITE コマンドを 1 回実行したらおしまい)。
 */
public class ChatSession01 extends ChatSession00 {
	/**
	 * コンストラクタ。
	 * @param server チャットサーバー
	 * @param socket 接続のソケット
	 */
	public ChatSession01(ChatServer server, Socket socket) {
		super(server, socket);
	}

	/**
	 * チャットの接続を処理する (READ または WRITE コマンドを 1 回実行したらおしまい)。
	 * @throws IOException 入出力に関する例外が発生
	 */
	@Override
	public void handleSession() throws IOException {
		String line = reader.readLine();
		if (line.equals("READ")) {
			handleRead();
		} else if (line.startsWith("WRITE")) {
			handleWrite(line);
		} else {
			writer.println("UNKNOWN COMMAND");
		}
	}

	/**
	 * READ コマンドを実行する。
	 */
	public void handleRead() {
		List<ChatMessage> messages = server.getMessages(0);
		ChatLogger.log("read " + messages.size() + " messages");
		writer.println(messages.size());
		for (ChatMessage message : messages) {
			writer.println(message);
		}
	}

	/**
	 * WRITE コマンドを実行する。
	 */
	public void handleWrite(String line) {
		String[] array = line.split(" ", 3);
		if (array.length == 3 && array[1].length() > 0 && array[2].length() > 0) {
			ChatMessage message = new ChatMessage(array[1], array[2]);
			server.addMessage(message);
			ChatLogger.log("write " + message);
			writer.println("OK");
		} else {
			writer.println("ERROR");
		}
	}
}
