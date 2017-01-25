package main;

import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class FileUploadByFtp {

	public static void fileUpload() throws Exception {
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		
		Project project=Windows.clickedProject;

			ftpClient.connect(ParameterReader.getPropertiesByKey("serviceIP"));
			ftpClient.login(ParameterReader.getPropertiesByKey("serviceUser"),
					ParameterReader.getPropertiesByKey("servicePassword"));

			File srcFile = new File(project.getProjectLocalTarget()+project.getProjectName()+".tar.gz");
			fis = new FileInputStream(srcFile);
			// 设置上传目录
			ftpClient.changeWorkingDirectory(project.getProjectTarget());
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding("GBK");
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			Boolean b=ftpClient.storeFile(project.getProjectName()+".tar.gz", fis);
			System.out.println(b);
			
		//	ftpClient.appendFile("logs.tar.gz", fis);
			//ftpClient.
			
/*			FileOutputStream os = ftpClient.put("/xxx/logs.tar.gz");
            //获取本地文件的输入流
            File file_in = new File(this.localfilename);
            is = new FileInputStream(file_in);
            //创建一个缓冲区
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
            }*/
			fis.close();
			ftpClient.disconnect();
	}

	/**
	 * FTP下载单个文件测试
	 * @throws IOException 
	 */
	public static void fileDownloadByFtp() throws IOException {
		FTPClient ftpClient = new FTPClient();
		FileOutputStream fos = null;

		try {
			ftpClient.connect("192.85.1.9");
			ftpClient.login("zhangzhenmin", "62672000");

			String remoteFileName = "/home/zhangzhenmin/test_back_081901.sql";
			// fos = new FileOutputStream("E:/test/test_back_081901.sql");
			fos = new FileOutputStream("H:/test/test_back_081901.sql");
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileName, fos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			fos.close();
			//IOUtils.closeQuietly(fos);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}
/*
	public static void main(String[] args) {
		try {
			fileUploadByFtp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//fileDownloadByFtp();
	}*/
}
