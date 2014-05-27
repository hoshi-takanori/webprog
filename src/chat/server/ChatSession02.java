package chat.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * チャットの接続を処理するやつ、その 2 (接続を維持し、何度でもコマンドを実行できる)。
 */
public class ChatSession02 extends ChatSession01 {
	/**
	 * 接続したユーザー名。
	 */
	private String user;

	/**
	 * コンストラクタ。
	 * @param server チャットサーバー
	 * @param socket 接続のソケット
	 */
	public ChatSession02(ChatServer server, Socket socket) {
		super(server, socket);
	}

	/**
	 * チャットの接続を処理する (接続を維持し、何度でもコマンドを実行できる)。
	 * @throws IOException 入出力に関する例外が発生
	 */
	@Override
	public void handleSession() throws IOException {
		while (true) {
			super.handleSession();
		}
	}

	/**
	 * コマンドを実行する。
	 * @param command コマンドとその引数
	 * @throws EOFException 入力終了
	 */
	@Override
	public void handleCommand(String[] command) throws EOFException {
		if (command[0].equals("EXIT")) {
			throw new EOFException("exit");
		} else if (command[0].equals("USER")) {
			handleUser(command);
		} else {
			super.handleCommand(command);
		}
	}

	/**
	 * USER コマンドを実行する。
	 * その 2 では、引数をユーザー名としてログインする。
	 * @param command コマンドとその引数
	 */
	public void handleUser(String[] command) {
		if (command.length == 2) {
			user = command[1];
			sendLine("USER OK");
		} else {
			sendLine("USER ERROR");
		}
	}

	/**
	 * READ コマンドを実行する。
	 * その 2 では、引数があればその個数分、なければすべてのメッセージを表示する。
	 * @param command コマンドとその引数
	 */
	@Override
	public void handleRead(String[] command) {
		int num = 0;
		if (command.length == 2) {
			try {
				num = Integer.parseInt(command[1]);
			} catch (NumberFormatException e) {
				sendLine("READ ERROR");
				return;
			}
		}
		readMessages(num);
	}

	/**
	 * WRITE コマンドを実行する。
	 * その 2 では、ログイン済みならば引数はメッセージの内容のみでよい。
	 * @param command コマンドとその引数
	 */
	@Override
	public void handleWrite(String[] command) {
		if (command.length == 2 && user != null) {
			writeMessage(user, command[1]);
		} else {
			super.handleWrite(command);
		}
	}
}
