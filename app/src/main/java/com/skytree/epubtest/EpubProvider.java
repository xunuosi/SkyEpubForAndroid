package com.skytree.epubtest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.util.Log;

import com.skytree.epub.ContentListener;
import com.skytree.epub.ContentProvider;
import com.skytree.epub.ContentData;

class EpubProvider implements ContentProvider {
	ZipFile zipFile = null;
	private void debug(String msg) {
		Log.w("EPub",msg);
	}
	
	private boolean isCustomFont(String contentPath) {
		if (contentPath.startsWith("/fonts")) {
			return true;
		}
		return false;
	}
	
	
	private void setupZipFile(String baseDirectory,String contentPath) {
		if (zipFile!=null) return;
		String[] subDirs = contentPath.split(Pattern.quote(File.separator));
		String fileName = subDirs[1]+".epub";
		String filePath = baseDirectory+"/"+subDirs[1]+"/"+fileName;		
		try {
			File file = new File(filePath);
			zipFile = new ZipFile(file);			
		}catch(Exception e) {
			zipFile=null;
		}		
	}
	
	// Entry name should start without / like META-INF/container.xml 
	private ZipEntry getZipEntry(String contentPath) {
		if (zipFile==null) return null;
		String[] subDirs = contentPath.split(Pattern.quote(File.separator));
		String corePath = contentPath.replace(subDirs[1], "");
		corePath=corePath.replace("//", "");
		ZipEntry entry = zipFile.getEntry(corePath.replace(File.separatorChar, '/'));
		return entry;
	}

	public boolean isExists(String baseDirectory,String contentPath) {
		setupZipFile(baseDirectory,contentPath);
		if (this.isCustomFont(contentPath)) {
			String path = baseDirectory +"/"+ contentPath;
			File file = new File(path);
			return file.exists();
		}
		
		ZipEntry entry = this.getZipEntry(contentPath);
		if (entry==null) {
//			Log.w("EPub",contentPath+" not exist");
		}
		if (contentPath.contains("mp4")) {
//			Log.w("EPub",contentPath);
		}
		if (entry==null) return false;
		else return true;		
	}
	
	
	public ContentData getContentData(String baseDirectory,String contentPath) {
		debug("getInputStream "+contentPath);
		ContentData data = new ContentData();
		data.contentPath = contentPath;		
		if (this.isCustomFont(contentPath)) {
			String path = baseDirectory + "/" + contentPath;
			FileInputStream fis = null;
			File file = new File(path);
			long fileLength = file.length(); 
			try {
				fis = new FileInputStream(file);
			}catch(Exception e) {}
			data.contentLength = fileLength;
			data.inputStream = fis;
			return data;
		}
		InputStream is = null;
		try {
			ZipEntry entry = this.getZipEntry(contentPath);
			if (entry==null) return null;
			is = zipFile.getInputStream(entry);
			long length = entry.getSize();
			// in some zip format, zipEntry can't generates proper inputStream, 
			// to fix this, byteArrayInputStream is used instead of zipEntry.getInputStream. 
			if (is.available()==1) {
				BufferedInputStream bis = new BufferedInputStream(is);  
				int file_size  = (int) entry.getCompressedSize();  
				byte[] blob = new byte[(int) entry.getCompressedSize()];  
				int bytes_read = 0;  
				int offset = 0;  
				while((bytes_read = bis.read(blob, 0, file_size)) != -1) {  
					offset += bytes_read;  
				} 
				bis.close();
				ByteArrayInputStream bas = new ByteArrayInputStream(blob);
				is = bas;
				length = is.available();
			}			
			data.contentLength = length;
			data.inputStream = is;
			data.lastModified = entry.getTime();
			return data;
		}catch(Exception e) {
			debug(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
}