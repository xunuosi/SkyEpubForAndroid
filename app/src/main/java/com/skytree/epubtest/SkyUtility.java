package com.skytree.epubtest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.apache.http.util.ByteArrayBuffer;

import com.skytree.epubtest.HomeActivity.ImageButtonHighlighterOnTouchListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class SkyUtility {
	Context context;
	final String TAG = "EPub";
	public SkyUtility(Context context) {
		this.context = context;
	}	
	
	public void debug(String msg) {
//		if (Setting.isDebug()) {
			Log.d("EPub", msg);
//		}
	}
	
	public static  void setFrame(View view,int dx, int dy, int width, int height) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		param.leftMargin = dx;
		param.topMargin =  dy;
		param.width = width;
		param.height = height;
		view.setLayoutParams(param);	
	}
	
	public static  void setMargins(View view,int leftMargin, int topMargin, int rightMargin, int bottomMargin ) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		param.leftMargin = leftMargin;
		param.topMargin =  topMargin;
		param.rightMargin = rightMargin;
		param.bottomMargin = bottomMargin;
		view.setLayoutParams(param);	
	}
	
	public static void setMargins(View view, int margin) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		param.leftMargin = margin;
		param.topMargin =  margin;
		param.rightMargin = margin;
		param.bottomMargin = margin;
		view.setLayoutParams(param);		
	}
	
	public static  void setSize(View view,int rule,int width, int height) {		
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		param.addRule(rule);
		param.width = width;
		param.height = height;
		view.setLayoutParams(param);	
	}
	
	public static void setRule(View view, int rule) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
		if (params==null) params = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT); // width,height
		params.addRule(rule);
		view.setLayoutParams(params);
	}
	
	public static void setWeight(View view,float weight,int orientation) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
		if (params==null) {
			if (orientation!=LinearLayout.VERTICAL) {
				params = new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT);
			}else {
				params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,0);
			}
		}
		params.weight = weight;
		view.setLayoutParams(params);
	}
	
	public static ImageButton makeImageButton(Context context,int id,int resId,int width, int height,OnClickListener onClickListener,OnTouchListener onTouchListener) {
		Drawable icon;
		ImageButton button = new ImageButton(context);
		button.setId(id);
		if (onClickListener!=null) button.setOnClickListener(onClickListener);
		button.setBackgroundColor(Color.TRANSPARENT);
		icon = context.getResources().getDrawable(resId);
		icon.setBounds(0,0,width,height);
		
		Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, width, height, false);
		button.setImageBitmap(bitmapResized);
		button.setVisibility(View.VISIBLE);
		if (onTouchListener!=null) button.setOnTouchListener(onTouchListener);
		return button;		
	}
	
	public static void changeImageButton(Context context,ImageButton button,int resId,int width,int height) {
		Drawable icon;
		icon = context.getResources().getDrawable(resId);
		icon.setBounds(0,0,width,height);		
		Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, width, height, false);
		button.setImageBitmap(bitmapResized);
		
	}
	
	public static String getModelName() {		
		return Build.MODEL;
	}
	
	public static String getDeviceName() {
		return Build.DEVICE;
	}
	
	/*
	Mako      – Google Nexus 4 
	Flo       – Google Nexus 7 (2013)
	Grouper   – Google Nexus 7
	Manta     – Google Nexus 10
	Maguro    – Google Galaxy Nexus
	Crespo    – Google Nexus S
	Steelhead – Google Nexus Q
	*/
	
	public static boolean isNexus() {
		String models[]={"mako","flo","grouper","maguro","crespo","hammerhead"};
		String model = SkyUtility.getModelName();
		String device = SkyUtility.getDeviceName();
		for (int i=0; i<models.length; i++) {
			String name = models[i];
			if (name.equalsIgnoreCase(model) || name.equalsIgnoreCase(device)) {
				return true;
			}
		}
		return false;
	}

	
	public static LinearLayout.LayoutParams getWeightParams(float weight,int orientation) {
		LinearLayout.LayoutParams params = null;
		if (orientation!=LinearLayout.VERTICAL) {	// HORIZONTAL
			params = new LinearLayout.LayoutParams(0,LayoutParams.FILL_PARENT,weight);
		}else {
			params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,0,weight);
		}
		return params;
	}
	
	public static void hideSoftButtons(Activity activity) {		
//		activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//		activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
//		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		     WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		activity.getWindow().getDecorView()
//		    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}
	
	public int getOSVersion() {
		return Build.VERSION.SDK_INT;
	}
	
	@SuppressLint("InlinedApi")
	public static void makeFullscreen(Activity activity) {
//		if (!SkyUtility.isNexus()) return;
//		if (SkyUtility.isNexusTablet()) return;
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			     WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		if (Build.VERSION.SDK_INT>=19) {
			activity.getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_IMMERSIVE
				|	View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 
				|	View.SYSTEM_UI_FLAG_FULLSCREEN
				|	View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				|	View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				|	View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				|	View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			);			
		}else if (Build.VERSION.SDK_INT>=11) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
	}	
	
	public static void setGravity(View view,int gravity) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
		params.gravity = gravity;	
		view.setLayoutParams(params);
	}
	
	public static void maximizeView(View view) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.width =  LayoutParams.FILL_PARENT;	// 400;
        params.height = LayoutParams.FILL_PARENT;	// 600;        
        view.setLayoutParams(params);		
	}
	
	public static void show(View view) {
		view.setVisibility(View.VISIBLE);
	}
	
	public static void hide(View view) {
		view.setVisibility(View.INVISIBLE);
//		view.setVisibility(View.GONE);
	}
	
	
	public static void setHeight(View view,int height) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		param.height = height;
		view.setLayoutParams(param);
	}
	
	public static String removeExtention(String filePath) {
	    // These first few lines the same as Justin's
	    File f = new File(filePath);

	    // if it's a directory, don't remove the extention
	    if (f.isDirectory()) return filePath;

	    String name = f.getName();

	    // Now we know it's a file - don't need to do any special hidden
	    // checking or contains() checking because of:
	    final int lastPeriodPos = name.lastIndexOf('.');
	    if (lastPeriodPos <= 0)
	    {
	        // No period after first character - return name as it was passed in
	        return filePath;
	    }
	    else
	    {
	        // Remove the last period and everything after it
	        File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
	        return renamed.getPath();
	    }
	}
	
	public static boolean moveFile(String from, String to) {
		try {
		    int bytesum = 0;
		    int byteread = 0;
		    InputStream inStream = new FileInputStream(from);
		    FileOutputStream fs = new FileOutputStream(to);
		    byte[] buffer = new byte[1444];
		    while ((byteread = inStream.read(buffer)) != -1) {
		    	bytesum += byteread;
		    	fs.write(buffer, 0, byteread);
		    }
		    inStream.close();
		    fs.close();
		    File source = new File(from);
		    source.delete();
		    return true;
		} catch (Exception e) {
			e.printStackTrace();
		    return false;
		}
	}
	
	public static TextView makeLabel(Context context,int id, String text, int gravity,float textSize,int textColor,boolean isBold) {
		TextView label = new TextView(context);
		label.setId(id);
		label.setGravity(gravity);
		label.setBackgroundColor(Color.TRANSPARENT);
		label.setText(text);
		label.setTextColor(textColor);		
		label.setTextSize(textSize);
		if (isBold) {
			label.setPaintFlags(label.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
		}
		return label;
	}
	
	// 여기에 오류가 있었다. 형변환 오류로 1.5를 곱한 것이 1을 곱한 것과 같이 나왔다. 다행이 2인 경우는 올바르게 나옴  
	static public int getPX(Context context,float dp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int density = metrics.densityDpi;
		float factor = (float)density/160.f;
		int px = (int)(dp*factor);
		return px;		
	}
	

	
	public boolean makeDirectory(String dirName) {
		boolean res;		
		String filePath = new String(SkySetting.getStorageDirectory() + "/"+dirName);
		debug(filePath);
		File file = new File(filePath);
		if (!file.exists()) {
			res = file.mkdirs();
		}else {
			res = false;		
		}
		return res;	
	}
	
	public void copyImageToDevice(String fileName) {			      
		try
		{
			String path = SkySetting.getStorageDirectory() + "/images/"+fileName;
			File file = new File(path);
			if (file.exists()) return;
			InputStream localInputStream = context.getAssets().open("images/"+fileName);	        	  
			FileOutputStream localFileOutputStream = new FileOutputStream(SkySetting.getStorageDirectory() + "/images/"+fileName);

			byte[] arrayOfByte = new byte[1024];
			int offset;
			while ((offset = localInputStream.read(arrayOfByte))>0)
			{
				localFileOutputStream.write(arrayOfByte, 0, offset);	              
			}
			localFileOutputStream.close();
			localInputStream.close();
			Log.d(TAG, fileName+" copied to phone");	            
		}
		catch (IOException localIOException)
		{
			localIOException.printStackTrace();
			Log.d(TAG, "failed to copy");
			return;
		}
	}
	
	public void copyFontToDevice(String fileName) {			      
		try
		{
			String path = SkySetting.getStorageDirectory()+ "/books/fonts/"+fileName;
			File file = new File(path);
			if (file.exists()) return;
			InputStream localInputStream = context.getAssets().open("fonts/"+fileName);	        	  
			FileOutputStream localFileOutputStream = new FileOutputStream(SkySetting.getStorageDirectory() + "/books/fonts/"+fileName);

			byte[] arrayOfByte = new byte[1024];
			int offset;
			while ((offset = localInputStream.read(arrayOfByte))>0)
			{
				localFileOutputStream.write(arrayOfByte, 0, offset);	              
			}
			localFileOutputStream.close();
			localInputStream.close();
			Log.d(TAG, fileName+" copied to phone");	            
		}
		catch (IOException localIOException)
		{
			localIOException.printStackTrace();
			Log.d(TAG, "failed to copy");
			return;
		}
	}
	
    public static String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }
    
	public static String getFileExtension(String url) {		
		String extension = url.substring(url.lastIndexOf(".")+1);
		return extension;
	}
	
	public static String getFileName(String url) {
		String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
		return fileName;
	}
	
	public static String getPureName(String url) {
		String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
		String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
		return fileNameWithoutExtn;
	}
	
	
	private boolean isSetup() {
        SharedPreferences pref = this.context.getSharedPreferences("EPubTest",0);        
        return pref.getBoolean("isSetup",false);
	}

	
	public void makeSetup() {
		if (this.isSetup()) return;

		if (!this.makeDirectory("scripts")) {
        	debug("faild to make scripts directory");
        }
        
        if (!this.makeDirectory("images")) {
        	debug("faild to make images directory");
        }
        
        if (!this.makeDirectory("covers")) {
        	debug("faild to make covers directory");
        }
        
        if (!this.makeDirectory("caches")) {
        	debug("faild to make caches directory");
        }
        
        copyImageToDevice("PagesCenter.png");
        copyImageToDevice("PagesCenter1.png");
        copyImageToDevice("PagesCenter2.png");
        copyImageToDevice("PagesStack.png");

        copyImageToDevice("Phone-Landscape-White.png");
        copyImageToDevice("Phone-Landscape-Double-White.png");
        copyImageToDevice("Phone-Portrait-White.png");
        copyImageToDevice("phone_portrait.png");

        copyImageToDevice("Phone-Landscape-Brown.png");
        copyImageToDevice("Phone-Landscape-Double-Brown.png");
        copyImageToDevice("Phone-Portrait-Brown.png");

        copyImageToDevice("Phone-Landscape-Black.png");
        copyImageToDevice("Phone-Landscape-Double-Black.png");
        copyImageToDevice("Phone-Portrait-Black.png");

        copyImageToDevice("Phone-Landscape-Alpha.png");
        copyImageToDevice("Phone-Landscape-Double-Alpha.png");
        copyImageToDevice("Phone-Portrait-Alpha.png");

        
        if (!this.makeDirectory("downloads")) {
        	debug("faild to make downloads directory");
        }
//        
        if (!this.makeDirectory("books")) {
        	debug("faild to make books directory");
        }
        
        if (!this.makeDirectory("books/fonts")) {
        	debug("faild to make fonts directory");
        }

        SharedPreferences pref = this.context.getSharedPreferences("EPubTest",0);
        SharedPreferences.Editor edit = pref.edit();
        
        edit.putBoolean("isSetup", true);        
        edit.commit();
	}
	
	public static void DownloadFileFromUrl(String URL, String fileName) {
		try {
			URL url = new URL(URL);
			File file = new File(fileName);
			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
		} catch (IOException e) {
			Log.d("EPub", "Error: " + e);
		}
	}
	
	public static boolean isExists1(String URL){
	    try {
	      HttpURLConnection.setFollowRedirects(false);
	      HttpURLConnection con = (HttpURLConnection) new URL(URL).openConnection();
	      con.setRequestMethod("HEAD");
	      boolean ret = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	      return ret;
	    }catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	  }
	
	public static boolean isExists(String urlString) {
		try {
		    URL u = new URL(urlString); 
		    HttpURLConnection huc =  (HttpURLConnection)  u.openConnection(); 
		    huc.setRequestMethod("GET"); 
		    huc.connect(); 
		    int rc = huc.getResponseCode();
		    return (rc==HttpURLConnection.HTTP_OK);
		    // Handle response code here...
		} catch (UnknownHostException uhe) {
		    // Handle exceptions as necessary
			Log.w("EPub",uhe.getMessage());
		} catch (FileNotFoundException fnfe) {
		    // Handle exceptions as necessary
			Log.w("EPub",fnfe.getMessage());
		} catch (Exception e) {
		    // Handle exceptions as necessary
			Log.w("EPub",e.getMessage());
		}
		return false;
	}
	
	public static boolean isNexusTablet() {
		String device = SkyUtility.getDeviceName();
		if (device.equalsIgnoreCase("grouper") || device.equalsIgnoreCase("flo")) return true;
		else return false;
	}
}



class SkyDrawable extends ShapeDrawable {
    private final Paint fillpaint, strokepaint;	 
    public SkyDrawable(Shape s, int fillColor, int strokeColor, int strokeWidth) {
        super(s);
        fillpaint = new Paint(this.getPaint());
        fillpaint.setColor(fillColor);
        strokepaint = new Paint(fillpaint);
        strokepaint.setStyle(Paint.Style.STROKE);
        strokepaint.setStrokeWidth(strokeWidth);
        strokepaint.setColor(strokeColor);
    }
 
    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        shape.draw(canvas, fillpaint);
        shape.draw(canvas, strokepaint);
    }
}

class CustomFont {
	public String fontFaceName;
	public String fontFileName;

	CustomFont(String faceName,String fileName) {
		this.fontFaceName = faceName;
		this.fontFileName = fileName;
	}
	
	public String getFullName() {
		String fullName = "";
		if (fontFileName==null || fontFileName.isEmpty()) {
			fullName = this.fontFaceName;
		}else {
			fullName = this.fontFaceName+"!!!/fonts/"+this.fontFileName;
		}
		return fullName;	
	}
}

