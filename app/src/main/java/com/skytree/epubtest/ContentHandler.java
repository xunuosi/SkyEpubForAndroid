package com.skytree.epubtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.util.Log;

import com.skytree.epub.ContentListener;

class ContentHandler implements ContentListener {
	public long getLength(String baseDirectory,String contentPath) {
		String path = baseDirectory + "/" + contentPath;
		File file = new File(path);
		if (file.exists()) {
			return file.length(); 
		}
		else {
			return 0;
		}
	}
	
	public boolean isExists(String baseDirectory,String contentPath) {		
		String path = baseDirectory +"/"+ contentPath;
		File file = new File(path);
		boolean res = false;
		Log.w("EPub",contentPath);
		if (file.exists()) {
			res =  true;
		}
		else {
			res =  false;
		}
		return res;		
	}
	
	public long getLastModified(String baseDirectory,String contentPath) {
		String path = baseDirectory + "/" + contentPath;
		File file = new File(path);
		if (file.exists()) {
			return file.lastModified();
		}
		else {
			return 0;		
		}
	}
	
	public InputStream getInputStream(String baseDirectory,String contentPath) {
		String path = baseDirectory + "/" + contentPath;
		File file = new File(path);
		try {
			FileInputStream fis = new FileInputStream(file);
			return fis;
		}catch(Exception e) {
			return null;
		}		
	}
}