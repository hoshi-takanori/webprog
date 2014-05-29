package web.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * WebServer への接続を処理する。
 */
public class WebConnection implements Runnable {
	 /**
	  * Web サーバー。
	  */
	 private WebServer server;

	/**
	 * 接続のソケット。
	 */
	private Socket socket;

	/**
	 * コンストラクタ。
	 * @param server Web サーバー
	 * @param socket 接続のソケット
	 */
	public WebConnection(WebServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	/**
	 * リクエストの内容を読み込み、Request オブジェクトを生成する。
	 * @return Request オブジェクト
	 * @throws IOException 入出力に関する問題が発生した場合
	 */
	public Request getRequest() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line = reader.readLine();
		if (line == null) {
			return null;
		}
		WebLogger.verboseLog("<- \"" + line + "\"");
		String[] array = line.split(" ");
		if (array.length < 2 || array.length > 3) {
			return null;
		}

		WebRequest request = new WebRequest(array[0], array[1]);
		if (array.length == 3) {
			while (true) {
				line = reader.readLine();
				if (line == null) {
					return null;
				}
				WebLogger.verboseLog("<- \"" + line + "\"");
				if (line.length() == 0) {
					break;
				} else {
					array = line.split(":", 2);
					if (array.length == 2) {
						request.setHeader(array[0], array[1]);
					}
				}
			}

			int length;
			try {
				length = request.getIntHeader("Content-Length");
			} catch (NumberFormatException e) {
				length = 0;
			}
			if (length > 0) {
				char[] buffer = new char[length];
				length = reader.read(buffer);
				if (length > 0 && request.getMethod().equals(Request.METHOD_POST)) {
					String body = new String(buffer, 0, length);
					WebLogger.verboseLog("<- \"" + body + "\"");
					request.parseParameters(body);
				}
			}
		}

		return request;
	}

	/**
	 * 接続を処理する。
	 */
	@Override
	public void run() {
		boolean shutdown = false;
		try (Socket socket = this.socket) {
			Request request = getRequest();
			WebResponse response = new WebResponse();
			if (request == null) {
				WebLogger.log("request = null");
				response.setError(Response.STATUS_BAD_REQUEST, "request == null");
			} else {
				WebLogger.log("request = " + request.getMethod() + " " + request.getPath());
				Servlet servlet = server.findServlet(request);
				if (servlet == null) {
					WebLogger.log("servlet = null");
					response.setError(Response.STATUS_NOT_FOUND, "servlet not found");
				} else {
					WebLogger.log("servlet = " + servlet.getName());
					try {
						servlet.service(request, response);
					} catch (Exception e) {
						response.setError(Response.STATUS_ERROR, e);
					}
				}
			}
			WebLogger.log("response = " + response.getStatus());
			if (response.getStatus() == null) {
				response.setError(Response.STATUS_ERROR, "response status == null");
			} else if (response.getStatus().equals(WebResponse.STATUS_SHUTDOWN)) {
				shutdown = true;
				response.setStatus(Response.STATUS_OK);
			}
			PrintStream stream = new PrintStream(socket.getOutputStream(), false, "UTF-8");
			if (request != null && request.getMethod().equals(Request.METHOD_HEAD)) {
				response.writeTo(stream, true);
			} else {
				response.writeTo(stream, false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (shutdown) {
			System.exit(0);
		}
	}
}
