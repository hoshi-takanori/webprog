package chat.server;

import java.io.EOFException;
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
		String[] command = receiveCommand();
		if (command.length == 0) {
			sessionLog("empty command");
		} else {
			handleCommand(command);
		}
	}

	/**
	 * コマンドを実行する。
	 * @param command コマンドとその引数
	 * @throws EOFException 入力終了
	 */
	public void handleCommand(String[] command) throws EOFException {
		if (command[0].equals("READ")) {
			handleRead(command);
		} else if (command[0].equals("WRITE")) {
			handleWrite(command);
		} else {
			sessionLog("unknown command");
			sendLine("UNKNOWN COMMAND");
		}
	}

	/**
	 * READ コマンドを実行する。
	 * その 1 では、引数は無視して、すべてのメッセージを読む。
	 * @param command コマンドとその引数
	 */
	public void handleRead(String[] command) {
		readMessages(0);
	}

	/**
	 * メッセージを読む。
	 * @param num メッセージの個数
	 */
	public void readMessages(int num) {
		List<ChatMessage> messages = getServer().getMessages(num);
		sessionLog("read " + messages.size() + " messages");
		sendLine("READ " + messages.size());
		for (ChatMessage message : messages) {
			sendLine(message.toString());
		}
	}

	/**
	 * WRITE コマンドを実行する。
	 * その 1 では、引数はユーザー名とメッセージの内容。
	 * @param command コマンドとその引数
	 */
	public void handleWrite(String[] command) {
		if (command.length == 3) {
			writeMessage(command[1], command[2]);
		} else {
			sendLine("WRITE ERROR");
		}
	}

	/**
	 * メッセージを書き込む。
	 * @param user ユーザー名
	 * @param text メッセージの内容
	 */
	public void writeMessage(String user, String text) {
		ChatMessage message = new ChatMessage(user, text);
		getServer().addMessage(message);
		sessionLog("write " + message);
		sendLine("WRITE OK");
	}
}
