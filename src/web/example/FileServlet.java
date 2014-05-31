package web.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import web.servlet.Request;
import web.servlet.Response;
import web.servlet.Servlet;

/**
 * 静的なファイルの内容を返すサーブレット。
 */
public class FileServlet implements Servlet {
	/**
	 * ファイルの置き場所。
	 */
	private File basePath;

	/**
	 * 拡張子とファイルタイプの対応表。
	 */
	private Map<String, String> fileTypes;

	/**
	 * コンストラクタ。
	 */
	public FileServlet(String basePath) {
		this.basePath = new File(basePath);

		fileTypes = new HashMap<String, String>();
		fileTypes.put("txt", "text/plain");
		fileTypes.put("htm", "text/html");
		fileTypes.put("html", "text/html");
		fileTypes.put("css", "text/css");
		fileTypes.put("js", "text/javascript");
		fileTypes.put("xml", "text/xml");
		fileTypes.put("java", "text/plain");
		fileTypes.put("sql", "text/plain");
		fileTypes.put("bmp", "image/bmp");
		fileTypes.put("gif", "image/gif");
		fileTypes.put("jpg", "image/jpeg");
		fileTypes.put("jpeg", "image/jpeg");
		fileTypes.put("png", "image/png");
		fileTypes.put("ico", "image/x-icon");
	}

	/**
	 * サーブレットの名前を返す。
	 * @return サーブレットの名前
	 */
	@Override
	public String getName() {
		return "File Servlet";
	}

	/**
	 * 指定したファイルのファイルタイプを返す。
	 * @param file 目的のファイル
	 * @return ファイルタイプ
	 */
	public String getFileType(File file) {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index >= 0) {
			String ext = name.substring(index + 1);
			String type = fileTypes.get(ext);
			if (type != null) {
				return type;
			}
		}
		return "application/octet-stream";
	}

	/**
	 * リクエストを処理する。
	 * @param request リクエスト (入力)
	 * @param response レスポンス (出力)
	 */
	@Override
	public void service(Request request, Response response) {
		File file = new File(basePath, request.getPath());
		if (file.isDirectory()) {
			File index = new File(file, "index.html");
			if (index.isFile()) {
				file = index;
			}
		}
		if (file.isDirectory()) {
			String[] list = file.list();
			response.setStatus(Response.STATUS_OK);
			response.setHeader("Content-Type", "text/plain");
			response.println(list.length + " items:");
			for (String item : list) {
				response.println(item);
			}
		} else if (file.isFile()) {
			int size = (int) file.length();
			try (FileInputStream stream = new FileInputStream(file)) {
				byte[] buffer = new byte[size];
				int read = stream.read(buffer);
				if (read == size) {
					response.setStatus(Response.STATUS_OK);
					response.setHeader("Content-Type", getFileType(file));
					response.setBytes(buffer);
				} else {
					response.setError(Response.STATUS_ERROR, "read < size");
				}
			} catch (IOException exception) {
				response.setError(Response.STATUS_ERROR, exception);
			}
		} else {
			response.setError(Response.STATUS_ERROR, "no such file or directory");
		}
	}
}
