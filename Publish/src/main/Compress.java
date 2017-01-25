package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;


public class Compress {
	
/*	static String folderPath="";
	static String targzipFilePath="";*/
	
/*	
	public static void main(String[] args){
		try{
	          List<File> files = getFiles("c:\\logs");
	          List<File> sources = new ArrayList<File>();
	            for(File f : files){
	                System.out.println(f.getName());
	                System.out.println(f.toURI());
	                sources.add(f);
	         }
			  File target = new File("c:\\logs.tar");
			  compress(pack(sources, target));
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
/*	
	public static void main(String[] args){
		Shell s=new Shell("192.168.0.102",22,"root","homeend724");
		String[] commands={"mkdir /nimabi"};
		boolean b = s.executeCommands(commands);
		System.out.println(b);
	}
	*/
	public static void generateTarGzFIle(String sourcePath,String targetNameWithPath) throws Exception{
          List<File> sources = getFiles(sourcePath);
/*        List<File> sources = new ArrayList<File>();*/
		  File tarfile = new File(targetNameWithPath+".tar");
		  pack(sources, tarfile);  
		  compress(tarfile,targetNameWithPath+".tar.gz");
	}

	private static void pack(List<File> sources , File target) throws Exception{
		  FileOutputStream out = null;

		   out = new FileOutputStream(target);

		  TarArchiveOutputStream os = new TarArchiveOutputStream(out);
		  os.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		  for (File file : sources) {

		    os.putArchiveEntry(new TarArchiveEntry(file));
		    IOUtils.copy(new FileInputStream(file), os);
		    os.closeArchiveEntry();

		  }
		  if(os != null) {
		    os.flush();
		    os.close();
		  }		  
		 }
	private static void compress(File tarfile,String targzfile) throws Exception {
		  File target = new File(targzfile);
		  FileInputStream in = null;
		  GZIPOutputStream out = null;

		   in = new FileInputStream(tarfile);
		   out = new GZIPOutputStream(new FileOutputStream(target));
		   byte[] array = new byte[1024];
		   int number = -1;
		   while((number = in.read(array, 0, array.length)) != -1) {
		    out.write(array, 0, number);
		   }
		   if(in != null) {		   
			     in.close();
		   }
		   if(out != null) {
			     out.close();
			  }
		 }
    private static List<File> getFiles(String path){
        File root = new File(path);
        List<File> files = new ArrayList<File>();
        if(!root.isDirectory()){
            files.add(root);
        }else{
            File[] subFiles = root.listFiles();
            for(File f : subFiles){
                files.addAll(getFiles(f.getAbsolutePath()));
            }    
        }
        return files;
    }
}
