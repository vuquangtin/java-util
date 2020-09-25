package com.util.project.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.util.project.utilities.Log4jUtils;

/*

 * @author EMAIL:vuquangtin@gmail.com , tel:0377443333
 * @version 1.0.0
 * @see <a href="https://github.com/vuquangtin/java-util">https://github.com/vuquangtin/java-util</a>
 *
 */
public class GetAllFilesInDirectory {
	static Logger logger = Logger.getLogger(GetAllFilesInDirectory.class
			.getName());
	public static final String URL = "https://onlinejudge.org/external/";
	public static final String SAVED = "/media/vuquangtin/Find/Documents/GitHub/UVA/pdf/";

	public static void main(String[] args) {
		logger = Log4jUtils.initLog4j();
		int index = 0;
		boolean isTest = false;
		if (!isTest) {
			try {
				Document doc = Jsoup.connect(URL).get();
				Elements links = doc.getElementsByTag("a");
				for (Element link : links) {
					logger.debug(link.attr("href") + " - " + link.text());
					String folder = link.text();
					if (folder.matches("[0-9]*/")) {
						processDirectory(URL + folder, folder, true);
						// if (index >= 3)
						// break;
						index++;
						logger.debug(index);
					}

				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			String urlTest = URL + "130/";
			processDirectory(urlTest, "130", true);
		}

	}

	public static final String PDF = ".pdf";
	public static final String HTML = ".html";
	public static final String MATCH_FILE = ".*\\.(html|pdf)";

	// ".*\\.pdf";
	// ".*\\.(html|pdf)"
	public static void processDirectory(String url, String folder,
			boolean showLog) {
		if (showLog)
			logger.debug("url:" + url + "\t" + folder);
		String savedFolder = SAVED + folder;
		if (showLog)
			logger.debug("savedFolder:" + savedFolder);
		if (!new File(savedFolder).exists()) {
			new File(savedFolder).mkdir();
		}
		try {
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.getElementsByTag("a");
			boolean exists = false;
			for (Element link : links) {
				// logger.debug(link.attr("href") + " - " + link.text());
				String currentFile = link.text();
				if (currentFile.matches(MATCH_FILE)) {
					exists = true;
					if (showLog)
						logger.debug(url + link.text());
					if (showLog)
						logger.debug("saved in " + savedFolder + "/"
								+ currentFile);
					try {
						downloadUsingStream(url + link.text(), savedFolder
								+ "/" + currentFile);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}
			if (!exists) {
				if (showLog)
					logger.debug("not exists url:" + url + "\t" + folder);

				// gen
				String fileName = "";
				String fileNameHTML = "";
				String pFileName = "";
				String nameFolder = folder.replace("/", "");
				for (int i = 0; i < 100; i++) {
					if (i < 10) {
						fileName = "0" + i;
					} else {
						fileName = i + "";
					}
					fileNameHTML = url + nameFolder + fileName + HTML;
					fileName = nameFolder + fileName + PDF;
					pFileName = url + "p" + fileName;
					fileName = url + fileName;
					if (showLog) {
						logger.debug(fileNameHTML);
						logger.debug(fileName);
						logger.debug(pFileName);
						logger.debug(FilenameUtils
								.getName(new URL(fileNameHTML).getPath()));
						try {
							downloadUsingStream(fileNameHTML, savedFolder,
									new URL(fileNameHTML));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						try {
							downloadUsingStream(fileName, savedFolder, new URL(
									fileName));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						try {
							downloadUsingStream(pFileName, savedFolder,
									new URL(pFileName));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				// System.exit(1);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void downloadUsingStream(String urlStr, String file, URL url) {
		String name = FilenameUtils.getName(url.getPath());
		file = file + name;
		logger.debug("file:" + file);
		try {
			downloadUsingStream(urlStr, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void downloadUsingStream(String urlStr, String file)
			throws IOException {
		file=file.replace("//", "/");
		if (new File(file).exists()) {
			logger.debug("exists--->" + file);
			return;
		}
		logger.debug("Download:---->"+file);
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = bis.read(buffer, 0, 1024)) != -1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
	}

	private static void downloadUsingNIO(String urlStr, String file)
			throws IOException {
		URL url = new URL(urlStr);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}
}
