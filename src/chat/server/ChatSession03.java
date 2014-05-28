package chat.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * チャットの接続を処理するやつ、その 3 (書き込みがあれば、それをブロードキャストする)。
 */
public class ChatSession03 extends ChatSession02 implements Observer {
	/**
	 * コンストラクタ。
	 * @param server チャットサーバー
	 * @param socket 接続のソケット
	 */
	public ChatSession03(ChatServer server, Socket socket) {
		super(server, socket);
	}

	/**
	 * チャットの接続を処理する (終了時にストリーミングを停止する)。
	 * @throws IOException 入出力に関する例外が発生
	 */
	@Override
	public void handleSession() throws IOException {
		try {
			super.handleSession();
		} finally {
			sessionLog("delete observer");
			getServer().deleteObserver(this);
		}
	}

	/**
	 * コマンドを実行する。
	 * @param command コマンドとその引数
	 * @throws EOFException 入力終了
	 */
	@Override
	public void handleCommand(String[] command) throws EOFException {
		if (command[0].equals("STREAM")) {
			handleStream(command);
		} else {
			super.handleCommand(command);
		}
	}

	/**
	 * STREAM コマンドを実行する。
	 * その 3 では、引数が START ならばストリーミングを開始、STOP なら終了する。
	 * @param command コマンドとその引数
	 */
	public void handleStream(String[] command) {
		boolean ok = false;
		if (command.length == 2) {
			if (command[1].equals("START")) {
				sessionLog("add observer");
				getServer().addObserver(this);
				ok = true;
			} else if (command[1].equals("STOP")) {
				sessionLog("delete observer");
				getServer().deleteObserver(this);
				ok = true;
			}
		}
		if (ok) {
			sendLine("STREAM OK");
		} else {
			sendLine("STREAM ERROR");
		}
	}

	/**
	 * ストリーミングが開始された状態で、メッセージが追加された時に呼ばれる。
	 * @param server チャットサーバー
	 * @param message 追加されたメッセージ
	 */
	@Override
	public void update(Observable server, Object message) {
		sendLine("STREAM 1");
		sendLine(message.toString());
	}
}
