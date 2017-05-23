package com.skytree.epubtest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.skytree.epub.Book;
import com.skytree.epub.BookInformation;
import com.skytree.epub.BookmarkListener;
import com.skytree.epub.Caret;
import com.skytree.epub.ClickListener;
import com.skytree.epub.ContentListener;
import com.skytree.epub.Highlight;
import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;
import com.skytree.epub.KeyListener;
import com.skytree.epub.MediaOverlayListener;
import com.skytree.epub.NavPoint;
import com.skytree.epub.NavPoints;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.skytree.epub.PageTransition;
import com.skytree.epub.PagingInformation;
import com.skytree.epub.PagingListener;
import com.skytree.epub.Parallel;
import com.skytree.epub.ScriptListener;
import com.skytree.epub.SearchListener;
import com.skytree.epub.SearchResult;
import com.skytree.epub.SelectionListener;
import com.skytree.epub.Setting;
import com.skytree.epub.SkyProvider;
import com.skytree.epub.StateListener;
import com.skytree.epub.State;
import com.skytree.epub.VideoListener;

import android.annotation.SuppressLint;
import com.skytree.epub.ReflowableControl;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.hardware.Camera.Size;
import android.media.audiofx.BassBoost.Settings;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import com.skytree.epub.Parallel;
import com.skytree.epub.VideoListener;
import android.graphics.PorterDuff;

public class BookViewActivity extends Activity {
	ReflowableControl rv;
	RelativeLayout ePubView;
	Button debugButton0;
	Button debugButton1;
	Button debugButton2;
	Button debugButton3;
 	
	ImageButton	rotationButton;
	ImageButton listButton;
	ImageButton fontButton;
	ImageButton searchButton;	
 	Rect bookmarkRect;
 	Rect bookmarkedRect;
 	
 	boolean isRotationLocked;
 	
 	TextView titleLabel;
 	TextView authorLabel;
 	TextView pageIndexLabel;
 	TextView secondaryIndexLabel;
 	
 	SkySeekBar seekBar;
 	OnSeekBarChangeListener seekListener;
 	SkyBox seekBox;
 	TextView seekLabel;
 	
 	SkyBox menuBox;
 	Button highlightMenuButton;
 	Button noteMenuButton; 
 	Rect boxFrame;
 	
 	SkyBox highlightBox;
 	ImageButton colorButtonInHighlightBox;
 	ImageButton trashButtonInHighlightBox;
 	ImageButton noteButtonInHighlightBox;
 	ImageButton shareButtonInHighlightBox;
 	
 	SkyBox colorBox;
 	
 	SkyBox noteBox;
 	EditText noteEditor;
 	int noteBoxWidth;
 	int noteBoxHeight;
 	
 	SkyBox searchBox;
 	EditText searchEditor;
 	ScrollView searchScrollView;
 	LinearLayout searchResultView;
 	
 	SkyBox fontBox;
 	SeekBar brightBar;
 	Button increaseButton;
 	Button decreaseButton;
 	ImageButton increaseLineSpaceButton;
 	ImageButton decreaseLineSpaceButton;
 	
 	String fontNames[] = {"Book Fonts","Sans Serif","Serif","Monospace"};
 	LinearLayout fontListView;
 	
 	SkyLayout listBox;
 	Button contentListButton;
 	Button bookmarkListButton;
 	Button highlightListButton;
 	ScrollView listScrollView;
 	LinearLayout listView;
 	Button listTopButton;
 	
 	SkyLayout mediaBox;
 	ImageButton playAndPauseButton;
 	ImageButton stopButton;
 	ImageButton prevButton;
 	ImageButton nextButton;
 	
 	ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
 	
 	boolean isBoxesShown;
 	
 	SkySetting setting;
 	SkyDatabase sd;
 	
 	Button outsideButton;
 	
 	String fileName;
 	String author;
 	String title; 	
 	
 	ProgressBar progressBar;
 	View pagingView;
 	
 	int currentColor = this.getColorByIndex(0);
 	Highlight currentHighlight; 	
 	
 	boolean isControlsShown = true;
 	double pagePositionInBook = -1;
 	int bookCode;
	
	Parallel currentParallel;
	boolean autoStartPlayingWhenNewPagesLoaded = false;   
	boolean autoMoveChapterWhenParallesFinished = true;
	boolean isAutoPlaying = true;
	boolean isPageTurnedByMediaOverlay = true;
	
	boolean isDoublePagedForLandscape;
	boolean isGlobalPagination;
	
	boolean isRTL =false;
	boolean isVerticalWriting = false;

	final private String TAG = "EPub";
	Highlights highlights;
	ArrayList <PagingInformation> pagings = new ArrayList<PagingInformation>();
	int temp = 20;
	
	Bitmap pagesStack,pagesCenter;
	BitmapDrawable pgsDrawable,pgcDrawable;
	
	boolean isFullScreenForNexus = true;
	
	ArrayList<Theme> themes = new ArrayList<Theme>();
	int themeIndex = -1;
	
	SkyApplication app;
	View videoView = null;
	
	LinearLayout themesView;
	
	ArrayList <CustomFont> fonts = new ArrayList <CustomFont>();
	
	PageInformation currentPageInformation;

	public int getDensityDPI() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int density = metrics.densityDpi;
		return density;
	}
	
	public void reportMetrics() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		log("densityDPI"+metrics.densityDpi);
		log("density"+metrics.density);
		log("real width  pixels"+metrics.widthPixels);
		log("real height pixels"+metrics.heightPixels);
		log("inch for width "+metrics.widthPixels /metrics.densityDpi);
		log("inch for height"+metrics.heightPixels/metrics.densityDpi);
	}
	
	public void reportFiles(String path) {		
		Log.d("EPub", "Path: " + path);
		File f = new File(path);        
		File file[] = f.listFiles();
		Log.d("EPub", "Size: "+ file.length);
		for (int i=0; i < file.length; i++)
		{
		    Log.d("EPub", "FileName:" + file[i].getName());
		}
	}
	
	public boolean isHighDensityPhone() {	// if HIGH density (not XHIGH) phone like Galaxy S2, retuns true;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int p0 = metrics.heightPixels;
		int p1 = metrics.widthPixels;
		int max = Math.max(p0, p1);
		if (metrics.densityDpi==240 && max==800) {
			return true;
		}else {
			return false;
		}		
	}
	
	// We use 240 base to meet the webview coodinate system instead of 160.  
	public int getPS(float dip) {
		float density = this.getDensityDPI();
		float factor = (float)density/240.f;
		int px = (int)(dip*factor);
		return px;		
	}
	
	public int getPSFromDP(float dps) {
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
	    float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, metrics);
	    return (int)pixels;
	}
	
	public int getPXFromLeft(float dip) {
		int ps = this.getPS(dip);
		return ps;		
	}
	
	public int getPXFromRight(float dip) {
		int ps = this.getPS(dip);
		int ms = this.getWidth()-ps;
		return ms;
	}
	
	public int getPYFromTop(float dip) {
		int ps = this.getPS(dip);
		return ps;
	}
	
	public int getPYFromBottom(float dip) {
		int ps = this.getPS(dip);
		int ms = this.getHeight()-ps;
		return ms;
	}
	
	public int pxl(float dp) {
		return this.getPXFromLeft(dp);		
	}
	
	public int pxr(float dp) {
		return this.getPXFromRight(dp);
	}
	
	public int pyt(float dp) {
		return this.getPYFromTop(dp);
	}
	
	public int pyb(float dp) {
		return this.getPYFromBottom(dp);
	}
	
	public int ps(float dp) {
		return this.getPS(dp);
	}
	
	public int pw(float sdp) {
		int ps = this.getPS(sdp*2);
		int ms = this.getWidth()-ps;
		return ms;
	}
	
	public int cx(float dp) {
		int ps = this.getPS(dp);
		int ms = this.getWidth()/2-ps/2;
		return ms;		
	}
	
	// in double paged and landscape mode,get the center of view(its width is dpWidth) on left page 
	public int lcx(float dpWidth) {
		int ps = this.getPS(dpWidth);
		int ms = this.getWidth()/4-ps/2;
		return ms;
	}
	
	// in double paged and landscape mode,get the center of view(its width is dpWidth) on right page
	public int rcx(float dpWidth) {
		int ps = this.getPS(dpWidth);
		int ms = this.getWidth()/2+this.getWidth()/4-ps/2;
		return ms;		
	}
	

	public float getDIP(float px) {
		float densityDPI = this.getDensityDPI();
		float dip = px/(densityDPI/240);
		return dip;		
	}
	
	
	public int getDarkerColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		int darker = Color.HSVToColor(hsv);
		return darker;
	}
	
	public Bitmap getBackgroundForLandscape() {
		Bitmap backgroundForLandscape=null;
		Theme theme = getCurrentTheme();
		Options options = new BitmapFactory.Options();   
		options.inScaled = false;
		if (this.isDoublePagedForLandscape) {
			backgroundForLandscape 	= 	BitmapFactory.decodeFile(SkySetting.getStorageDirectory()+"/images/"+theme.doublePagedName,options);
		}else {
			backgroundForLandscape 	= 	BitmapFactory.decodeFile(SkySetting.getStorageDirectory()+"/images/"+theme.landscapeName,options);
		}
		return backgroundForLandscape;
	}
	
	public Bitmap getBackgroundForPortrait() {
		Bitmap backgroundForPortrait;
		Theme theme = getCurrentTheme();
		Options options = new BitmapFactory.Options();   
		options.inScaled = true;
		backgroundForPortrait 	= 	BitmapFactory.decodeFile(SkySetting.getStorageDirectory()+"/images/"+theme.portraitName,options);		
		return backgroundForPortrait;		
	}
	
	public Bitmap getBitmap(String filename) {
		Bitmap bitmap;
		bitmap 	= 	BitmapFactory.decodeFile(SkySetting.getStorageDirectory()+"/images/"+filename);		
		return bitmap;
	}
	
	public int getMaxSize() {
		int width = this.getRawWidth();
		int height= this.getRawHeight();
		return Math.max(width,height); 
	}
	
	public Theme getCurrentTheme() {
		Theme theme = themes.get(themeIndex);
		return theme;
	}
	
	public void setThemeIndex(int index) {
		themeIndex = index;
	}
	
	//  	String fontNames[] = {"Book Fonts","Sans Serif","Serif","Monospace"};

	public void makeFonts() {
		fonts.clear();
		fonts.add(0,new CustomFont("Monospace",""));
		fonts.add(0,new CustomFont("Serif",""));
		fonts.add(0,new CustomFont("Sans Serif",""));
		fonts.add(0,new CustomFont("Book Fonts",""));
		for (int i=0; i<app.customFonts.size();i++) {
			this.fonts.add(app.customFonts.get(i));
		}
	}
	
	public int getOSVersion() {
		return Build.VERSION.SDK_INT;
	}
	

	public void onDestroy() {
		// Stop loading the ad.
		this.unregisterSkyReceiver(); // New in SkyEpub sdk 7.x
		super.onDestroy();
	}

	boolean isInitialized = false;
	
	
	public void makeLayout() {
		// make fonts
		this.makeFonts();
		// clear the existing themes. 
		themes.clear();
		// add themes 
		// String name,int foregroundColor,int backgroundColor,int controlColor,int controlHighlightColor,int seekBarColor,int seekThumbColor,int selectorColor,int selectionColor,String portraitName,String landscapeName,String doublePagedName,int bookmarkId
		themes.add(new Theme("white",Color.BLACK, 0xffffffff,Color.argb(240, 94,61,35),Color.LTGRAY,Color.argb(240, 94,61,35),Color.argb(120, 160, 124, 95),Color.DKGRAY,0x22222222,"Phone-Portrait-White.png","Phone-Landscape-White.png","Phone-Landscape-Double-White.png",R.drawable.bookmark2x));
		themes.add(new Theme("brown",Color.BLACK, 0xffece3c7,Color.argb(240, 94,61,35),Color.argb(255,255,255,255),Color.argb(240, 94,61,35),Color.argb(120, 160, 124, 95),Color.DKGRAY,0x22222222,"Phone-Portrait-Brown.png","Phone-Landscape-Brown.png","Phone-Landscape-Double-Brown.png",R.drawable.bookmark2x));
		themes.add(new Theme("black",Color.LTGRAY,0xff323230,Color.LTGRAY,Color.LTGRAY,Color.LTGRAY,Color.LTGRAY,Color.LTGRAY,0x77777777,null,null,"Phone-Landscape-Double-Black.png",R.drawable.bookmarkgray2x));
		themes.add(new Theme("Leaf",0xFF1F7F0E,0xffF8F7EA,0xFF186D08,Color.LTGRAY,0xFF186D08,0xFF186D08,Color.DKGRAY,0x22222222,null,null,null,R.drawable.bookmarkgray2x));
		themes.add(new Theme("夕陽",0xFFA13A0A,0xFFF6DFD9,0xFFA13A0A,0xFFDC4F0E,0xFFA13A0A,0xFFA13A0A,Color.DKGRAY,0x22222222,null,null,null,R.drawable.bookmarkgray2x));		
		this.setBrightness((float)setting.brightness);		
		// create highlights object to contains highlights of this book. 
		highlights = new Highlights();		
		Bundle bundle = getIntent().getExtras();
		fileName = bundle.getString("BOOKNAME");
		author = bundle.getString("AUTHOR");
		title = bundle.getString("TITLE");
		bookCode = bundle.getInt("BOOKCODE");
		if (pagePositionInBook==-1) pagePositionInBook = bundle.getDouble("POSITION");
		themeIndex = setting.theme;
		this.isGlobalPagination = bundle.getBoolean("GLOBALPAGINATION");
		this.isRTL = bundle.getBoolean("RTL");
		this.isVerticalWriting = bundle.getBoolean("VERTICALWRITING");
		this.isDoublePagedForLandscape = bundle.getBoolean("DOUBLEPAGED");
//		if (this.isRTL) this.isDoublePagedForLandscape = false; // In RTL mode, SDK does not support double paged.
		
		autoStartPlayingWhenNewPagesLoaded = this.setting.autoStartPlaying; 
		autoMoveChapterWhenParallesFinished = this.setting.autoLoadNewChapter;
	
		ePubView = new RelativeLayout(this);

		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		ePubView.setLayoutParams(rlp);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		if (this.getOSVersion()>=11) {
			rv = new ReflowableControl(this);						// in case that device supports transparent webkit, the background image under the content can be shown. in some devices, content may be overlapped.
		}else {
			rv = new ReflowableControl(this,getCurrentTheme().backgroundColor);			// in case that device can not support transparent webkit, the background color will be set in one color.
		}

		// if false highlight will be drawed on the back of text - this is default. 
		// for the very old devices of which GPU does not support transparent webView background, set the value to true.  
		rv.setDrawingHighlightOnFront(false);		
 
		// set the bookCode to identify the book file. 
		rv.bookCode = this.bookCode;
		
		// set bitmaps for engine. 
		rv.setPagesStackImage(this.getBitmap("PagesStack.png"));
		rv.setPagesCenterImage(this.getBitmap("PagesCenter.png"));
		// for epub3 which has page-progression-direction="rtl", rv.isRTL() will return true.
		// for old RTL epub which does not have <spine toc="ncx" page-progression-direction="rtl"> in opf file. 
		// you can enforce RTL mode.  
		
		
/*		
		// delay times for proper operations. 
		// !! DO NOT SET these values if there's no issue on your epub reader. !!
		// !! if delayTime is decresed, performance will be increase
		// !! if delayTime is set to too low value, a lot of problem can be occurred. 
		// bringDelayTime(default 500 ms) is for curlView and mainView transition - if the value is too short, blink may happen.
		rv.setBringDelayTime(500);
		// reloadDelayTime(default 100) is used for delay before reload (eg. changeFont, loadChapter or etc) 
		rv.setReloadDelayTime(100);
		// reloadDelayTimeForRotation(default 1000) is used for delay before rotation
		rv.setReloadDelayTimeForRotation(1000);
		// retotaionDelayTime(default 1500) is used for delay after rotation.
		rv.setRotationDelayTime(1500);
		// finalDelayTime(default 500) is used for the delay after loading chapter. 
		rv.setFinalDelayTime(500);
		// rotationFactor affects the delayTime before Rotation. default value 1.0f
		rv.setRotationFactor(1.0f);		
		// If recalcDelayTime is too short, setContentBackground function failed to work properly.  
		rv.setRecalcDelayTime(2500);
*/
		
		// set the max width or height for background. 
		rv.setMaxSizeForBackground(1024);
//		rv.setBaseDirectory(SkySetting.getStorageDirectory() + "/books");
//		rv.setBookName(fileName);
		// set the file path of epub to open
		// Be sure that the file exists before setting.
		rv.setBookPath(SkySetting.getStorageDirectory() + "/books/"+fileName);
		// if true, double pages will be displayed on landscape mode. 
		rv.setDoublePagedForLandscape(this.isDoublePagedForLandscape);
		// set the initial font style for book. 
		rv.setFont(setting.fontName,this.getRealFontSize(setting.fontSize));
		// set the initial line space for book. 
		rv.setLineSpacing(this.getRealLineSpace(setting.lineSpacing)); // the value is supposed to be percent(%).
		// set the horizontal gap(margin) on both left and right side of each page.  
		rv.setHorizontalGapRatio(0.30);
		// set the vertical gap(margin) on both top and bottom side of each page. 
		rv.setVerticalGapRatio(0.22);
		// set the HighlightListener to handle text highlighting. 
		rv.setHighlightListener(new HighlightDelegate());		
		// set the PageMovedListener which is called whenever page is moved. 
		rv.setPageMovedListener(new PageMovedDelegate());
		// set the SelectionListener to handle text selection. 
		rv.setSelectionListener(new SelectionDelegate());
		// set the pagingListener which is called when GlobalPagination is true. this enables the calculation for the total number of pages in book, not in chapter.   
		rv.setPagingListener(new PagingDelegate());
		// set the searchListener to search keyword.
		rv.setSearchListener(new SearchDelegate());
		// set the stateListener to monitor the state of sdk engine. 
		rv.setStateListener(new StateDelegate());
		// set the clickListener which is called when user clicks
		rv.setClickListener(new ClickDelegate());
		// set the bookmarkListener to toggle bookmark
		rv.setBookmarkListener(new BookmarkDelegate());
		// set the scriptListener to set custom javascript. 
		rv.setScriptListener(new ScriptDelegate());
		
		// enable/disable scroll mode
		rv.setScrollMode(false);
		
		
		// for some anroid device, when rendering issues are occurred, use "useSoftwareLayer"
//		rv.useSoftwareLayer();
		// In search keyword, if true, sdk will return search result with the full information such as position, pageIndex. 
		rv.setFullSearch(true);
		// if true, sdk will return raw text for search result, highlight text or body text without character escaping.  
		rv.setRawTextRequired(false);
		
		// if true, sdk will read the content of book directry from file system, not via Internal server. 
//		rv.setDirectRead(true);
		
		// If you want to make your own provider, please look into EpubProvider.java in Advanced demo.
//		EpubProvider epubProvider = new EpubProvider();
//		rv.setContentProvider(epubProvider);		
		
		// SkyProvider is the default ContentProvider which is presented with SDK. 
		// SkyProvider can read the content of epub file without unzipping. 
		// SkyProvider is also fully integrated with SkyDRM solution.  
		SkyProvider skyProvider = new SkyProvider();
		skyProvider.setKeyListener(new KeyDelegate());
		rv.setContentProvider(skyProvider);
		
		// set the start positon to open the book. 
		rv.setStartPositionInBook(pagePositionInBook);
		// DO NOT USE BELOW, if true , sdk will use DOM to highlight text.  
//		rv.useDOMForHighlight(false);
		// if true, globalPagination will be activated. 
		// this enables the calculation of page number based on entire book ,not on each chapter.
		// this globalPagination consumes huge computing power. 
		// AVOID GLOBAL PAGINATION FOR LOW SPEC DEVICES.
		rv.setGlobalPagination(this.isGlobalPagination);
		// set the navigation area on both left and right side to go to the previous or next page when the area is clicked. 
		rv.setNavigationAreaWidthRatio(0.1f); // both left and right side.
		// set the device locked to prevent Rotation. 
		rv.setRotationLocked(setting.lockRotation);
		isRotationLocked = setting.lockRotation;
		// set the mediaOverlayListener for MediaOverlay.
		rv.setMediaOverlayListener(new MediaOverlayDelegate());
		
		// set the audio playing based on Sequence. 
		rv.setSequenceBasedForMediaOverlay(false);
		
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;

		rv.setLayoutParams(params);
		this.applyThemeToRV(themeIndex);
		
		if (this.isFullScreenForNexus && SkyUtility.isNexus() && Build.VERSION.SDK_INT>=19) {
			rv.setImmersiveMode(true);
		}
		// If you want to get the license key for commercial use, please email us (skytree21@gmail.com). 
		// Without the license key, watermark message will be shown in background. 
		rv.setLicenseKey("0000-0000-0000-0000");
		
		// set PageTransition Effect 
		int transitionType = bundle.getInt("transitionType");
		if (transitionType==0) {
			rv.setPageTransition(PageTransition.None);
		}else if (transitionType==1) {
			rv.setPageTransition(PageTransition.Slide);
		}else if (transitionType==2) {
			rv.setPageTransition(PageTransition.Curl);
		}
		
		// setCurlQuality effects the image quality when tuning page in Curl Transition Mode. 
		// If "Out of Memory" occurs in high resolution devices with big screen, 
		// this value should be decreased like 0.25f or below.
		if (this.getMaxSize()>1280) {
			rv.setCurlQuality(1.0f);
		}
		
		// set the color of text selector. 
		rv.setSelectorColor(getCurrentTheme().selectorColor);
		// set the color of text selection area. 
		rv.setSelectionColor(getCurrentTheme().selectionColor);

		// setCustomDrawHighlight & setCustomDrawCaret work only if SDK >= 11
		// if true, sdk will ask you how to draw the highlighted text
		rv.setCustomDrawHighlight(true);
		// if true, sdk will require you to draw the custom selector.
		rv.setCustomDrawCaret(true); 

		rv.setFontUnit("px");

		rv.setFingerTractionForSlide(true);
		rv.setVideoListener(new VideoDelegate());
		
		// make engine not to send any event to iframe
		// if iframe clicked, onIFrameClicked will be fired with source of iframe
		// By Using that source of iframe, you can load the content of iframe in your own webView or another browser. 
		rv.setSendingEventsToIFrameEnabled(false);

		// make engine send any event to video(tag) or not
		// if video tag is clicked, onVideoClicked will be fired with source of iframe
		// By Using that source of video, you can load the content of video in your own media controller or another browser. 
		rv.setSendingEventsToVideoEnabled(true);

		// make engine send any event to video(tag) or not
		// if video tag is clicked, onVideoClicked will be fired with source of iframe
		// By Using that source of video, you can load the content of video in your own media controller or another browser.
		rv.setSendingEventsToAudioEnabled(true);
		
		// if true, sdk will return the character offset from the chapter beginning , not from element index.
		// then startIndex, endIndex of highlight will be 0 (zero) 
		rv.setGlobalOffset(true);
		// if true, sdk will return the text of each page in the PageInformation object which is passed in onPageMoved event. 
		rv.setExtractText(true);
		
		
		// if true, TextToSpeech will be enabled
		rv.setTTSEnabled(this.setting.tts);			// if true, TextToSpeech will be enabled. 
//		rv.setTTSLanguage(Locale.US); 	// change Locale according to the language of book. if not set, skyepub sdk tries to dectect the locale for this book. 
		rv.setTTSPitch(1.0f);	  		// if value is 2.0f, the pitch of voice is double times higher than normal, 1.0f is normal pitch.
		rv.setTTSSpeedRate(1.0f); 		// if value is 2.0f , the speed is double times faster than normal. 1.0f is normal speed;

		// Add ReflowableView into Main View.
		ePubView.addView(rv);
		
		this.makeControls();
		this.makeBoxes();
		this.makeIndicator();
		this.recalcFrames();
		if (this.isRTL) {
			this.seekBar.setReversed(true);
		}
		setContentView(ePubView);		
		this.isInitialized = true;	
	}

	// if the current theme should be changed while book is opened, 
	// use this function. 
	private void changeTheme(int newIndex) {
		if (newIndex>themes.size()-1 || newIndex<0) return;
		this.setThemeIndex(newIndex);
		this.applyThemeToRV(newIndex);
		this.applyThemeToUI(newIndex);		
		this.recalcFrames();
		this.processPageMoved(rv.getPageInformation());		
	}	
	
	// if the current theme should be changed while book is opened, 
	// use this function. (it takes some time because this reconstructs every user interface.) 
	private void changeTheme2(int newIndex) {
		if (newIndex>themes.size()-1 || newIndex<0) return;
		this.setThemeIndex(newIndex);	
		this.ePubView.removeAllViews();		
		this.makeLayout();
	}
	
	public void applyThemeToRV(int themeIndex) {
		this.themeIndex = themeIndex;	
		// set BackgroundImage
		// the first  Rect should be the rect of background image itself
		// the second Rect is used to define the inner client area which the real contentView will reside. 
		if (this.isDoublePagedForLandscape) {
			rv.setBackgroundForLandscape(this.getBackgroundForLandscape(), 	new Rect(0,0,2004,1506),	new Rect(32	,0,2004-32,1506)); 			// Android Rect - left,top,right,bottom
		}else {
			rv.setBackgroundForLandscape(this.getBackgroundForLandscape(), 	new Rect(0,0,2004,1506),	new Rect(0	,0,2004-32,1506)); 			// Android Rect - left,top,right,bottom
		}		
		rv.setBackgroundForPortrait(this.getBackgroundForPortrait(), 		new Rect(0,0,1002,1506),	new Rect(0	,0,1002-32,1506)); 			// Android Rect - left,top,right,bottom
		
		// setBackgroundColor is used to set the background color in initial time.
		// changeBackgroundColor is used to set the background color in run time.
		// both are effective only when background image is not set or null.		
		if (!this.isInitialized) {
			rv.setBackgroundColor(getCurrentTheme().backgroundColor);
			rv.setForegroundColor(getCurrentTheme().foregroundColor);
		}else {
			rv.changeBackgroundColor(getCurrentTheme().backgroundColor);
			rv.changeForegroundColor(getCurrentTheme().foregroundColor);			
			rv.recalcLayout();
			
			new Handler().postDelayed(new Runnable() { 
	            public void run() {
	            	rv.repaint();
	            } 
			}, 1000);			
		}			
	}
	
	public void applyThemeToUI(int themeIndex) {
		this.makeControls();		
	}
	
	public void enableHaptic() {
		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED, 1);
	}
	
	public void disableHaptic() {
		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED, 0);
	}

	
	public int getColorWithAlpha(int color,int alpha) {
		int red,green,blue;
		red 	= Color.red(color);
		green 	= Color.green(color);
		blue 	= Color.blue(color);
		int newColor = Color.argb(alpha, red, green, blue);
		return newColor;
	}
	
	public int getBrighterColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 1.2f; // value component
		int darker = Color.HSVToColor(hsv);
		return darker;
	}

	public void makeIndicator() {
		progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);		
		ePubView.addView(progressBar);		
		this.hideIndicator();
	}
	
	public void showIndicator() {
		RelativeLayout.LayoutParams params = 
			    (RelativeLayout.LayoutParams)progressBar.getLayoutParams();		
		params.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
		progressBar.setLayoutParams(params);
		progressBar.setVisibility(View.VISIBLE);
	}
	
	public void hideIndicator() {
		if (progressBar!=null) {
			progressBar.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}		
	
	public void removeBoxes() {		
		this.ePubView.removeView(seekBox);
		this.ePubView.removeView(menuBox);
		this.ePubView.removeView(highlightBox);
		this.ePubView.removeView(colorBox);
		this.ePubView.removeView(noteBox);
		this.ePubView.removeView(searchBox);
		this.ePubView.removeView(listBox);
		this.ePubView.removeView(mediaBox);
		this.ePubView.removeView(pagingView);		
	}
	
	public void makeBoxes() {
		this.removeBoxes();
		this.makeOutsideButton();
		this.makeSeekBox();
		this.makeMenuBox();
		this.makeHighlightBox();
		this.makeColorBox();
		this.makeNoteBox();
		this.makeSearchBox();
		this.makeFontBox();
		this.makeListBox();
		this.makeMediaBox();
		this.makePagingView();
	}
	
	public void makeOutsideButton() {
		outsideButton = new Button(this);
		outsideButton.setId(9999);
		outsideButton.setBackgroundColor(Color.TRANSPARENT);
		outsideButton.setOnClickListener(listener);
//		rv.customView.addView(outsideButton);
		ePubView.addView(outsideButton);
		hideOutsideButton();
	}
	
	public void showOutsideButton() {
		this.setFrame(outsideButton, 0,0, this.getWidth(),this.getHeight());
		outsideButton.setVisibility(View.VISIBLE);		
	}
	
	public void hideOutsideButton() {
		outsideButton.setVisibility(View.INVISIBLE);
		outsideButton.setVisibility(View.GONE);
	}
	
	public void makeSeekBox() {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		seekBox = new SkyBox(this);
		seekBox.setBoxColor(Color.DKGRAY);
		seekBox.setArrowDirection(true); // to Down Arrow
		seekBox.setArrowHeight(ps(25));
		param.leftMargin = ps(0);
		param.topMargin =  ps(0);
		param.width = 	   ps(300);
		param.height =     ps(65);
		seekBox.setLayoutParams(param);	
		// public TextView makeLabel(int id, String text, int gravity,float textSize,int textColor, int width, int height) {
		seekLabel = this.makeLabel(2000, "", Gravity.CENTER_HORIZONTAL, 13, Color.WHITE);
		this.setLocation(seekLabel, ps(10), ps(6));
		seekBox.addView(seekLabel);		
//		rv.customView.addView(seekBox);
		ePubView.addView(seekBox);
		this.hideSeekBox();
	}
	
	public void showSeekBox() {
		seekBox.setVisibility(View.VISIBLE);
	}
	public void hideSeekBox() {
		seekBox.setVisibility(View.INVISIBLE);
		seekBox.setVisibility(View.GONE);
	}
	
	int op = 0;
	int targetPageIndexInBook = 0;
	public void moveSeekBox(PageInformation pi) {
		int position = seekBar.getProgress();
		targetPageIndexInBook = position;
		if (Math.abs(op-position)<10) {
			return;
		}
		if (pi==null) return;
		String chapterTitle = null;

		chapterTitle = pi.chapterTitle;
		if (pi.chapterTitle==null || pi.chapterTitle.isEmpty()) {
			chapterTitle = "Chapter "+pi.chapterIndex;
		}
			
			
		if (rv.isGlobalPagination()) {
//			seekLabel.setText(String.format("%s %d",chapterTitle,position+1));
			seekLabel.setText(chapterTitle);
		}else {
			seekLabel.setText(chapterTitle);
		}
		int slw = this.getLabelWidth(seekLabel);	
		int max = seekBar.getMax();
		if (this.isRTL) {
			position = max - position;
		}
		float cx = (float)((this.getWidth()-ps(50)*2) * (float)((float)position/max));
		float sx = cx -slw/2+ps(50);
		if (sx<ps(50)) sx = ps(50);
		if (sx+slw>this.getWidth()-ps(50)) sx = this.getWidth()-slw-ps(50);
		this.setFrame(seekBox, (int)sx, pyb(200), slw+ps(20),ps(65));
		seekBox.setArrowPosition((int)cx+ps(46), (int)sx, slw);
		op = position;
	}
	
	class ButtonHighlighterOnTouchListener implements OnTouchListener {
		  final Button button;

		  public ButtonHighlighterOnTouchListener(final Button button) {
		    super();
		    this.button = button;
		  }
		  
		  @Override
		  public boolean onTouch(final View view, final MotionEvent motionEvent) {
		    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
		      //grey color filter, you can change the color as you like
		    	if (button.getId()==5000) {
		    		button.setTextColor(Color.BLUE);
		    		button.setTextSize(16);
		    	}else if (button.getId()==5001) {
		    		button.setTextColor(Color.BLUE);
		    		button.setTextSize(20);		    		
		    	}else if (button.getId()==6000 || button.getId()==6001) {
		    		button.setTextSize(17);
		    		button.setTextColor(Color.YELLOW);
		    	}else if (button.getId()==3001){
		    		button.setTextColor(Color.BLACK);
		    	}
		    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
		    	if (button.getId()==5000) {
		    		button.setTextColor(Color.BLACK);
		    		button.setTextSize(14);
		    	}else if (button.getId()==5001) {
		    		button.setTextColor(Color.BLACK);
		    		button.setTextSize(18);		    		
		    	}else if (button.getId()==6000 || button.getId()==6001) {
		    		button.setTextSize(15);
		    		button.setTextColor(Color.WHITE);
		    	}else if (button.getId()==3001) {
		    		button.setTextColor(Color.DKGRAY);
		    	}
		    }
		    return false;
		  }
	}
	
		
	class ImageButtonHighlighterOnTouchListener implements OnTouchListener {
		  final ImageButton button;
		  int highlightColor;
		  int controlColor;

		  public ImageButtonHighlighterOnTouchListener(final ImageButton button) {
			  super();
			  Theme theme = getCurrentTheme();
			  highlightColor = theme.controlHighlightColor;
			  controlColor = theme.controlColor;
			  this.button = button;
		  }

		  public ImageButtonHighlighterOnTouchListener(final ImageButton button,int controlColor,int highlightColor) {
			  super();
			  this.highlightColor = highlightColor;
			  this.controlColor = controlColor;
			  this.button = button;
		  }

		  
		  @Override
		  public boolean onTouch(final View view, final MotionEvent motionEvent) {
			  
			  if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				  beep(10);
				  button.setColorFilter(highlightColor);
				  
			  } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				  button.setColorFilter(controlColor);
			  }
		    return false;
		  }
	}
	
	public void makeMenuBox() {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		menuBox = new SkyBox(this);
		menuBox.setBoxColor(Color.DKGRAY);
		menuBox.setArrowHeight(ps(25));
		menuBox.setArrowDirection(true);
		param.leftMargin = ps(100);
		param.topMargin =  ps(100);
		param.width = 	   ps(280);
		param.height =     ps(85);
		menuBox.setLayoutParams(param);	
		menuBox.setArrowDirection(false);
		highlightMenuButton = new Button(this);
		highlightMenuButton.setText("Highlight");
		highlightMenuButton.setId(6000);
		highlightMenuButton.setBackgroundColor(Color.TRANSPARENT);
		highlightMenuButton.setTextColor(Color.LTGRAY);
		highlightMenuButton.setTextSize(15);
		highlightMenuButton.setOnClickListener(listener);
		highlightMenuButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(highlightMenuButton));
		this.setFrame(highlightMenuButton, ps(20),ps(0), ps(130), ps(65));
		menuBox.contentView.addView(highlightMenuButton);
		noteMenuButton = new Button(this);
		noteMenuButton.setText("Note");
		noteMenuButton.setId(6001);
		noteMenuButton.setBackgroundColor(Color.TRANSPARENT);
		noteMenuButton.setTextColor(Color.LTGRAY);
		noteMenuButton.setTextSize(15);
		noteMenuButton.setOnClickListener(listener);
		noteMenuButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(noteMenuButton));
		this.setFrame(noteMenuButton, ps(150),ps(0), ps(130), ps(65));
		menuBox.contentView.addView(noteMenuButton);
//		rv.customView.addView(menuBox);
		ePubView.addView(menuBox);
		this.hideMenuBox();		
	}
	
	public void makeHighlightBox() {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		highlightBox = new SkyBox(this);
		highlightBox.setBoxColor(currentColor);
		highlightBox.setArrowHeight(ps(25));
		highlightBox.setArrowDirection(true);
		param.leftMargin = ps(100);
		param.topMargin =  ps(100);
		param.width = 	   ps(280);
		param.height =     ps(85);
		highlightBox.setLayoutParams(param);	
		highlightBox.setArrowDirection(false);
		
		int bs = ps(38);		
		colorButtonInHighlightBox = this.makeImageButton(6002, R.drawable.colorchooser2x, bs, bs);
		trashButtonInHighlightBox = this.makeImageButton(6003, R.drawable.trash2x, bs,bs);
		noteButtonInHighlightBox  = this.makeImageButton(6004, R.drawable.memo2x, bs, bs);
		shareButtonInHighlightBox = this.makeImageButton(6005, R.drawable.save2x,bs,bs);
		
		int ds = 60;
		this.setLocation(colorButtonInHighlightBox, ps(10)+ps(ds)*0, 	ps(4));
		this.setLocation(trashButtonInHighlightBox, ps(10)+ps(ds)*1, 	ps(4));
		this.setLocation(noteButtonInHighlightBox, 	ps(10)+ps(ds)*2, 	ps(8));
		this.setLocation(shareButtonInHighlightBox, ps(10)+ps(ds)*3, 	ps(4));
		
		highlightBox.contentView.addView(colorButtonInHighlightBox);
		highlightBox.contentView.addView(trashButtonInHighlightBox);
		highlightBox.contentView.addView(noteButtonInHighlightBox);
		highlightBox.contentView.addView(shareButtonInHighlightBox);
		
//		rv.customView.addView(highlightBox);
		ePubView.addView(highlightBox);
		this.hideHighlightBox();			
	}
	
	public void showHighlightBox() {
		this.showOutsideButton();
		this.setFrame(highlightBox,boxFrame.left, boxFrame.top,boxFrame.width(),boxFrame.height());
		highlightBox.setArrowDirection(menuBox.isArrowDown);
		highlightBox.arrowPosition = menuBox.arrowPosition;
		highlightBox.arrowHeight = menuBox.arrowHeight;
		highlightBox.boxColor = currentColor;
		highlightBox.setVisibility(View.VISIBLE);		
		isBoxesShown = true;
	}
	
	public void showHighlightBox(Rect startRect,Rect endRect) {		
		this.showOutsideButton();
		highlightBox.setVisibility(View.VISIBLE);
		this.moveSkyBox(highlightBox,ps(280),ps(85), startRect, endRect);
		highlightBox.boxColor = currentHighlight.color;
		isBoxesShown = true;
	}	
	
	public void hideHighlightBox() {
		highlightBox.setVisibility(View.INVISIBLE);
		highlightBox.setVisibility(View.GONE);		
		isBoxesShown = false;
		hideOutsideButton();
	}
	
	public void makeColorBox() {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		colorBox = new SkyBox(this);
		colorBox.setBoxColor(currentColor);
		colorBox.setArrowHeight(ps(25));
		colorBox.setArrowDirection(true);
		param.leftMargin = ps(100);
		param.topMargin =  ps(100);
		param.width = 	   ps(280);
		param.height =     ps(85);
		colorBox.setLayoutParams(param);	
		colorBox.setArrowDirection(false);
		
		int bs = ps(38);		
		ImageButton yellowButton 	= this.makeImageButton(6010, R.drawable.yellowbox2x, bs, bs);
		ImageButton greenButton 	= this.makeImageButton(6011, R.drawable.greenbox2x, bs,bs);
		ImageButton blueButton  	= this.makeImageButton(6012, R.drawable.bluebox2x, bs, bs);
		ImageButton redButton 		= this.makeImageButton(6013, R.drawable.redbox2x,bs,bs);
		
		int ds = 60;
		int oy = 3;
		this.setLocation(yellowButton, 	ps(10)+ps(ds)*0, ps(oy));
		this.setLocation(greenButton, 	ps(10)+ps(ds)*1, ps(oy));
		this.setLocation(blueButton, 	ps(10)+ps(ds)*2, ps(oy));
		this.setLocation(redButton, 	ps(10)+ps(ds)*3, ps(oy));
		
		colorBox.contentView.addView(yellowButton);
		colorBox.contentView.addView(greenButton);
		colorBox.contentView.addView(blueButton);
		colorBox.contentView.addView(redButton);
		
//		rv.customView.addView(colorBox);
		ePubView.addView(colorBox);
		this.hideColorBox();
	}
	
	public void showColorBox() {
		this.showOutsideButton();
		this.setFrame(colorBox,boxFrame.left, boxFrame.top,boxFrame.width(),boxFrame.height());
		colorBox.setArrowDirection(highlightBox.isArrowDown);
		colorBox.arrowPosition = highlightBox.arrowPosition;
		colorBox.arrowHeight = highlightBox.arrowHeight;
		colorBox.boxColor = currentColor;
		colorBox.setVisibility(View.VISIBLE);
		isBoxesShown = true;
	}
	
	public void hideColorBox() {
		colorBox.setVisibility(View.INVISIBLE);
		colorBox.setVisibility(View.GONE);	
		isBoxesShown = false;
		hideOutsideButton();
	}
	
	public void showMenuBox(Rect startRect, Rect endRect) {
		menuBox.setVisibility(View.VISIBLE);
		this.moveSkyBox(menuBox,ps(280),ps(85), startRect, endRect);
		isBoxesShown = true;	
	}
	public void hideMenuBox() {
		if (menuBox.getVisibility()!=View.VISIBLE) return;
		menuBox.setVisibility(View.INVISIBLE);
		menuBox.setVisibility(View.GONE);
		isBoxesShown = false;
		hideOutsideButton();
	}	
	
	public void dismissKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(noteEditor.getWindowToken(), 0);		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		makeFullScreen();
	}
	
	public void showKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(noteEditor, 0);		
		noteEditor.requestFocus();
	}
	
	OnFocusChangeListener focusListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				processForKeyboard(true);
			}else {
				processForKeyboard(false);
			}
		}
	};	
	
	public void processForKeyboard(boolean isShown) {
		if(isShown){
			if (this.keyboardHidesNote()) {			
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						dismissKeyboard();	
						moveNoteBoxPositionForKeyboard();
						showKeyboard();						
					}
				}, 100);
			}			
		}else {
			if (isNoteMoved) {
				this.restoreNoteBoxPosition();
			}
 		}		
	}
	
	boolean keyboardHidesNote() {
		if (!this.isPortrait() && !this.isTablet()) return false;
		if (this.noteBox.VISIBLE!=View.VISIBLE) return false;
		RelativeLayout.LayoutParams params = 
			    (RelativeLayout.LayoutParams)noteBox.getLayoutParams();
		int bottomY = params.topMargin + params.height;
		int keyboardTop = (int)(this.getHeight()*0.6f);
		
		if (bottomY>=keyboardTop) return true;
		else return false;
	}
	
	int oldNoteTop;
	int oldNoteLeft;
	boolean isNoteMoved = false;
	
	void moveNoteBoxPositionForKeyboard() {
		RelativeLayout.LayoutParams params = 
			    (RelativeLayout.LayoutParams)noteBox.getLayoutParams();		
		int keyboardTop = (int)(this.getHeight()*0.6f);
		int noteHeight = ps(300);
		oldNoteTop = params.topMargin;
		oldNoteLeft = params.leftMargin;
		isNoteMoved = true;
		int noteTop = keyboardTop - noteHeight - ps(80);	
		this.setFrame(noteBox, params.leftMargin, noteTop, noteBoxWidth,  noteHeight);
	}
	
	void restoreNoteBoxPosition() {
		int noteHeight = ps(300);
		isNoteMoved = false;			
		this.setFrame(noteBox, oldNoteLeft, oldNoteTop, noteBoxWidth,  noteHeight);		
	}
	
	// NoteBox Coodinate is always based on Highlight Area.  
	public void makeNoteBox() {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		noteBox = new SkyBox(this);
		noteBox.setBoxColor(currentColor);
		noteBox.setArrowHeight(ps(25));
		noteBox.setArrowDirection(false);
		param.leftMargin = ps(50);
		param.topMargin =  ps(400);
		int minWidth = Math.min(this.getWidth(),this.getHeight());		
		noteBoxWidth = 	   (int)(minWidth * 0.8);
		param.width = noteBoxWidth;
		param.height =     ps(300);
		noteBox.setLayoutParams(param);	
		noteBox.setArrowDirection(false);
		
		noteEditor = new EditText(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.width =  LayoutParams.FILL_PARENT;	
        params.height = LayoutParams.FILL_PARENT;	        
        noteEditor.setLayoutParams(params);	
        noteEditor.setBackgroundColor(Color.TRANSPARENT);
        noteEditor.setMaxLines(1000);
        noteEditor.setGravity(Gravity.TOP|Gravity.LEFT);
        noteEditor.setOnFocusChangeListener(focusListener);
        noteBox.contentView.addView(noteEditor);
		
		ePubView.addView(noteBox);
		this.hideNoteBox();
	}
	
	
	public void showNoteBox() {
		if (currentHighlight==null) return;
		isBoxesShown = true;
		this.showOutsideButton();
		Rect startRect = rv.getStartRect(currentHighlight);
		Rect endRect   = rv.getEndRect(currentHighlight);	
		int minWidth = Math.min(this.getWidth(),this.getHeight());		
		noteBoxWidth = 	   (int)(minWidth * 0.7);
		noteBoxHeight = 	ps(300);
		noteEditor.setText(currentHighlight.note);
		noteBox.setBoxColor(currentColor);
		this.moveSkyBox(noteBox,noteBoxWidth,noteBoxHeight,startRect,endRect);	
		noteBox.setVisibility(View.VISIBLE);
		lockRotation();
	}
	
	public void hideNoteBox() {
		if (currentHighlight!=null && noteEditor!=null && noteBox.getVisibility()==View.VISIBLE) saveNoteBox();		
		this.noteBox.setVisibility(View.INVISIBLE);
		this.noteBox.setVisibility(View.GONE);
		this.dismissKeyboard();
		this.noteEditor.clearFocus();
		isBoxesShown = false;		
		this.hideOutsideButton();	
		unlockRotation();
	}
	
	public void saveNoteBox() {
		if (currentHighlight==null || noteEditor==null) return;
		if (noteBox.getVisibility()!=View.VISIBLE) return;
		boolean isNote;
		String note = noteEditor.getText().toString();
		if (note==null || note.length()==0) isNote = false;
		else isNote = true;		
		currentHighlight.isNote=isNote;
		currentHighlight.note = note;
		currentHighlight.style = 27;
		if (currentHighlight.color==0) currentHighlight.color = currentColor;
		rv.changeHighlightNote(currentHighlight, note);
	}
	
	
	public void makeSearchBox() {
		int boxColor           	= Color.rgb(241,238,229);
		int innerBoxColor		= Color.rgb(246,244,239);
        int inlineColor      	= Color.rgb(133,105,75);
		
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		searchBox = new SkyBox(this);
		searchBox.setBoxColor(boxColor);
		searchBox.setArrowHeight(ps(25));
		searchBox.setArrowDirection(false);
		param.leftMargin = ps(50);
		param.topMargin =  ps(400);
		param.width 	=  ps(400);
		param.height =     ps(300);
		searchBox.setLayoutParams(param);	
		searchBox.setArrowDirection(false);
		
		searchEditor = new EditText(this);
		this.setFrame(searchEditor, ps(20), ps(20),ps(400-140),ps(50));
		searchEditor.setTextSize(15f);
		searchEditor.setEllipsize(TruncateAt.END);
		searchEditor.setBackgroundColor(innerBoxColor);
		Drawable icon = getResources().getDrawable(R.drawable.search2x);
		
		Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
		Drawable fd = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, ps(28),ps(28), true));
		searchEditor.setCompoundDrawablesWithIntrinsicBounds(fd,null,null,null);
		RoundRectShape rrs = new RoundRectShape(new float[] { ps(15),ps(15),ps(15),ps(15),ps(15),ps(15),ps(15),ps(15)}, null, null);
		SkyDrawable sd = new SkyDrawable(rrs,innerBoxColor,inlineColor,2);
		searchEditor.setBackgroundDrawable(sd);
		searchEditor.setHint(getString(R.string.searchhint));
		searchEditor.setPadding(ps(20), ps(5), ps(10), ps(5));
		searchEditor.setLines(1);
		searchEditor.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		searchEditor.setSingleLine();
		searchEditor.setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_NEXT){
					String key = searchEditor.getText().toString();
					if (key!=null && key.length()>1) {
						showIndicator();
						clearSearchBox(1);
						makeFullScreen();
						rv.searchKey(key);
					}
				}
				return false;
			}
		});
		searchBox.contentView.addView(searchEditor);
		
		Button cancelButton = new Button(this);
		this.setFrame(cancelButton, ps(290),ps(20), ps(90),ps(50));
		cancelButton.setText(getString(R.string.cancel));
		cancelButton.setId(3001);
		RoundRectShape crs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5)}, null, null);
		SkyDrawable cd = new SkyDrawable(crs,innerBoxColor,inlineColor,2);
		cancelButton.setBackgroundDrawable(cd);
		cancelButton.setTextSize(12);
		cancelButton.setOnClickListener(listener);
		cancelButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(cancelButton));

		searchBox.contentView.addView(cancelButton);
		
		searchScrollView = new ScrollView(this);
		RoundRectShape rvs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5)}, null, null);
		SkyDrawable rd = new SkyDrawable(rvs,innerBoxColor,inlineColor,2);
		searchScrollView.setBackgroundDrawable(rd);
		this.setFrame(searchScrollView, ps(20),ps(100),ps(360), ps(200));
		this.searchBox.contentView.addView(searchScrollView);
		
		searchResultView = new LinearLayout(this);
		searchResultView.setOrientation(LinearLayout.VERTICAL);
		searchScrollView.addView(searchResultView,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));	
		

		this.ePubView.addView(searchBox);
		this.hideSearchBox();
	}
	
	public void hideSearchBox() {
		searchBox.setVisibility(View.INVISIBLE);
		searchBox.setVisibility(View.GONE);	
		isBoxesShown = false;
		this.hideOutsideButton();
		rv.stopSearch();
	}
	
	public void showSearchBox() {
		isBoxesShown = true;
		this.showOutsideButton();
		int width = 400; 
		int left,top;
		top = ps(65);
		if (!this.isTablet()) {  // in case of phone
			if (this.isHighDensityPhone()) {
				left =  pxr(width+40);
				if (!isPortrait()) top = ps(40); 
			}else {
				left =  pxr(width+60);
			}
		}else { // in case of tablet
			left = pxr(width+140);
			top = ps(120);
		}
		
		searchBox.setVisibility(View.VISIBLE);		
		int sh;
		if (this.isPortrait()) {			
			if (this.isTablet()) {
				sh = this.getHeight()-ps(400);
			}else {
				sh = this.getHeight()-ps(240);
			}
		}else {
			if (this.isTablet()) {
				sh = this.getHeight()-ps(350);
			}else {
				sh = this.getHeight()-ps(140);
			}			
		}		
		int rh = sh- ps(150);
		this.setFrame(searchBox, left,top,ps(width),sh);
		this.setFrame(searchScrollView, ps(20), ps(100), ps(360), rh);
		searchBox.setArrowHeight(ps(25));
		searchBox.setArrowPosition(pxr(100),left,ps(width));		
	}
	
	public void clearSearchBox(int mode) {
		if (mode==0) {
			this.dismissKeyboard();
			searchEditor.setText("");
			searchResultView.removeAllViews();		
			searchResults.clear();
		}else {
			searchResultView.removeAllViews();		
			searchResults.clear();			
		}
	}
	
	public void cancelPressed() {
		this.clearSearchBox(0);
		this.hideSearchBox();
	}
	
	public void addSearchResult(SearchResult sr,int mode) {
		View view = this.makeResultItem(sr,mode);
		this.searchResultView.addView(view);		 
		if (mode==0) {
			this.searchResults.add(sr);
		}else {
			this.moveSearchScrollViewToEnd();
		}
	}
	
	public void removeLastResult() {
		this.searchResultView.removeViewAt(searchResultView.getChildCount()-1);
	}

	public View makeResultItem(SearchResult sr,int mode) {
        int inlineColor      	= Color.rgb(133,105,75);
	    int headColor    		= Color.rgb(94,61,34); 
	    int textColor    		= Color.rgb(50, 40, 40);

		SkyLayout view = new SkyLayout(this);		
		int itemWidth = ps(370);
		int itemHeight = ps(190);
		
		this.setFrame(view, 0,0,itemWidth,itemHeight);
		
		TextView chapterLabel=null; 
		TextView positionLabel=null;
		TextView textLabel=null;
		Button itemButton = null;
		itemButton = new Button(this);
		itemButton.setBackgroundColor(Color.TRANSPARENT);
		itemButton.setOnClickListener(listener);
		this.setFrame(itemButton, 0, 0, itemWidth, itemHeight);
	
		if (mode==0) {		// Normal case
			int ci = sr.chapterIndex;
			String chapterText = "";
			chapterText = sr.chapterTitle;
			String positionText = String.format("%d/%d",sr.pageIndex+1,sr.numberOfPagesInChapter);
			if (chapterText==null ||chapterText.isEmpty()) {
				chapterText = "Chapter "+ci;
			}
			if (sr.pageIndex<0 || sr.numberOfPagesInChapter<0) {
				positionText = "";
			}
			chapterLabel 	= this.makeLabel(3090,chapterText, Gravity.LEFT, 15, headColor);
			positionLabel 	= this.makeLabel(3091,positionText , Gravity.LEFT, 15, headColor);
			textLabel		= this.makeLabel(3092, sr.text, Gravity.LEFT, 15, textColor);
			itemButton.setId(100000+searchResults.size());
		}else if (mode==1) { // Paused
			chapterLabel 	= this.makeLabel(3090, getString(R.string.searchmore)+ "....", Gravity.CENTER, 18, headColor);
//			positionLabel 	= this.makeLabel(3091, String.format("%d/%d",sr.pageIndex+1,sr.numberOfPagesInChapter), Gravity.LEFT, 15, headColor);
			textLabel		= this.makeLabel(3092, sr.numberOfSearched+" "+getString(R.string.searchfound)+".", Gravity.CENTER, 16, textColor);
			itemButton.setId(3093);
		}else if (mode==2) { // finished
			chapterLabel 	= this.makeLabel(3090, getString(R.string.searchfinished), Gravity.CENTER, 18, headColor);
//			positionLabel 	= this.makeLabel(3091, String.format("%d/%d",sr.pageIndex+1,sr.numberOfPagesInChapter), Gravity.LEFT, 15, headColor);
			textLabel		= this.makeLabel(3092, sr.numberOfSearched+" "+getString(R.string.searchfound)+".", Gravity.CENTER, 16, textColor);
			itemButton.setId(3094);
		}

		textLabel.setMaxLines(3);
		
		if (mode==0) {
			this.setFrame(chapterLabel, ps(20), ps(20),ps(270),ps(30));
			this.setFrame(positionLabel, itemWidth-ps(80), ps(20),ps(70),ps(30));
			this.setFrame(textLabel, ps(20),ps(80),itemWidth-ps(40),itemHeight-ps(80+20));			
		}else {
			this.setFrame(chapterLabel, ps(20), ps(20),ps(350),ps(40));
//			this.setFrame(positionLabel, itemWidth-ps(80), ps(20),ps(70),ps(30));
			this.setFrame(textLabel, ps(20),ps(80),itemWidth-ps(40),itemHeight-ps(80+20));
		}
		
		view.addView(chapterLabel);
		if (mode==0) view.addView(positionLabel);
		view.addView(textLabel);
		view.addView(itemButton);		
        
		RectShape crs = new RectShape();
		SkyDrawable cd = new SkyDrawable(crs,Color.TRANSPARENT,inlineColor,1);
		view.setBackgroundDrawable(cd);
		
		return view;
	}
	
	public void moveSearchScrollViewToEnd(){
		searchScrollView.post(new Runnable() {       
			@Override
			public void run() {
				searchScrollView.fullScroll(View.FOCUS_DOWN);              
			}

		});
	}
	
	public void makeFontBox() {
		int boxColor           	= Color.rgb(241,238,229);
		int innerBoxColor		= Color.rgb(246,244,239);
        int inlineColor      	= Color.rgb(133,105,75);

        int width = 450;
        int height = 500;
		fontBox = new SkyBox(this);
		fontBox.setBoxColor(boxColor);
		fontBox.setArrowHeight(ps(25));
		fontBox.setArrowDirection(false);
		setFrame(fontBox,ps(50),ps(200),ps(width),ps(height));
		
		ScrollView fontBoxScrollView = new ScrollView(this);
		this.setFrame(fontBoxScrollView, ps(5),ps(10),ps(440), ps(height-50));
		fontBox.contentView.addView(fontBoxScrollView);	// NEW
		
		SkyLayout contentLayout = new SkyLayout(this);		
		fontBoxScrollView.addView(contentLayout,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		// #1 first make brightness controller
		// brView is the box containing the bright slider. 
		int FY = 10;
		View brView = new View(this);
		RoundRectShape rrs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5)}, null, null);
		SkyDrawable srd = new SkyDrawable(rrs,innerBoxColor,inlineColor,1);
		brView.setBackgroundDrawable(srd);		
		setFrame(brView,ps(20),ps(FY),ps(width-40),ps(53));
		contentLayout.addView(brView);
		// darker and brighter icons
		int SBS = 60;		 
		ImageButton sbb = this.makeImageButton(9005,R.drawable.brightness2x,ps(SBS), ps(SBS));
		setFrame(sbb, ps(50),ps(FY), ps(SBS), ps(SBS));
		sbb.setAlpha(200);
		int BBS = 70;
		ImageButton bbb = this.makeImageButton(9006,R.drawable.brightness2x,ps(BBS), ps(BBS));
		setFrame(bbb, ps(width-110),ps(FY-5), ps(BBS), ps(BBS));
		bbb.setAlpha(200);
		contentLayout.addView(sbb);
		contentLayout.addView(bbb);		
		// making bright slider		
		brightBar = new SeekBar(this);
		brightBar.setMax(999);
		brightBar.setId(997);
        brightBar.setBackgroundColor(Color.TRANSPARENT);
        brightBar.setOnSeekBarChangeListener(new SeekBarDelegate());	
        brightBar.setProgressDrawable(new LineDrawable(Color.rgb(160, 160, 160),ps(10)));
        brightBar.setThumbOffset(-1);
        setFrame(brightBar,ps(100),ps(FY+4),ps(width-210),ps(50));
        contentLayout.addView(brightBar);
        
        
        // #2 second make decrese/increse font size buttons
        // decrease font size Button
        int FBY = 80;
        decreaseButton = new Button(this);        
        setFrame(decreaseButton,ps(20),ps(FBY),ps(width-40-20)/2,ps(60));
        decreaseButton.setText(getString(R.string.chara));
        decreaseButton.setGravity(Gravity.CENTER);
        decreaseButton.setTextSize(14);
        decreaseButton.setId(5000);
        RoundRectShape drs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5) }, null, null);
        SkyDrawable drd = new SkyDrawable(drs,innerBoxColor,inlineColor,1);
        decreaseButton.setBackgroundDrawable(drd);
        decreaseButton.setOnClickListener(listener);        
        decreaseButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(decreaseButton));
        contentLayout.addView(decreaseButton);
        // inccrease font size Button
        increaseButton = new Button(this);
        setFrame(increaseButton,ps(10+width/2),ps(FBY),ps(width-40-20)/2,ps(60));
        increaseButton.setText(getString(R.string.chara));
        increaseButton.setTextSize(18);
        increaseButton.setGravity(Gravity.CENTER);
        increaseButton.setId(5001);
        RoundRectShape irs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5)}, null, null);
        SkyDrawable ird = new SkyDrawable(irs,innerBoxColor,inlineColor,1);
        increaseButton.setBackgroundDrawable(ird);
        increaseButton.setOnClickListener(listener);
        increaseButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(increaseButton));
        contentLayout.addView(increaseButton);
        
        // # 3 make the button to increase/decrese line spacing. 
        int LBY = 145;
     // deccrease line space Button
        decreaseLineSpaceButton = this.makeImageButton(9005,R.drawable.decline2x,ps(30), ps(30));
        setFrame(decreaseLineSpaceButton,ps(20),ps(LBY),ps(width-40-20)/2,ps(60));
        decreaseLineSpaceButton.setId(4000);
        drs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5) }, null, null);
        drd = new SkyDrawable(drs,innerBoxColor,inlineColor,1);
        decreaseLineSpaceButton.setBackgroundDrawable(drd);
        decreaseLineSpaceButton.setOnClickListener(listener);        
        decreaseLineSpaceButton.setOnTouchListener(new ImageButtonHighlighterOnTouchListener(decreaseLineSpaceButton));
        contentLayout.addView(decreaseLineSpaceButton);
        // inccrease line space Button        
        increaseLineSpaceButton = this.makeImageButton(9005,R.drawable.incline2x,ps(30), ps(30));
        setFrame(increaseLineSpaceButton,ps(10+width/2),ps(LBY),ps(width-40-20)/2,ps(60));
        increaseLineSpaceButton.setId(4001);
        irs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5)}, null, null);
        ird = new SkyDrawable(irs,innerBoxColor,inlineColor,1);
        increaseLineSpaceButton.setBackgroundDrawable(ird);
        increaseLineSpaceButton.setOnClickListener(listener);
        increaseLineSpaceButton.setOnTouchListener(new ImageButtonHighlighterOnTouchListener(increaseLineSpaceButton));
        contentLayout.addView(increaseLineSpaceButton);

        
        // #4 make themes selector.  
        int TY = 220;
        int TH = 70;
        int TW = (width-40-20)/3;

        HorizontalScrollView themeScrollView = new HorizontalScrollView(this);
        themesView = new LinearLayout(this);
        themesView.setOrientation(LinearLayout.HORIZONTAL);
        
        themeScrollView.addView(themesView,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        
        for (int i=0; i<this.themes.size();i++) {
        	Theme theme = themes.get(i);
        	Button themeButton = new Button(this);
        	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ps(TW),ps(TH));
            layoutParams.setMargins(0,0,24,0);
            themesView.addView(themeButton,layoutParams);
			RoundRectShape rs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5) }, null, null);
			SkyDrawable brd = new SkyDrawable(rs,theme.backgroundColor,Color.BLACK,1);
			themeButton.setBackgroundDrawable(brd);
			themeButton.setText(theme.name);
			themeButton.setTextColor(theme.foregroundColor);
			themeButton.setId(7000+i);			
			themeButton.setOnClickListener(listener);
        }   
        
        this.setFrame(themeScrollView, ps(20),ps(TY),ps(width-40), ps(90));
        contentLayout.addView(themeScrollView);
        
        // #5 font list box
        int SY = 310;
        int fontButtonHeight = 80;
        int fontListHeight;
        fontListHeight = fontButtonHeight*fonts.size();		
		fontListView = new LinearLayout(this);
		fontListView.setOrientation(LinearLayout.VERTICAL);
		contentLayout.addView(fontListView);
		this.setFrame(fontListView, ps(20),ps(SY),ps(width-40), ps(fontListHeight));		
		int inlineColor2      	= Color.argb(140,133,105,75);
		
		for (int i=0; i<fonts.size(); i++) {
			CustomFont customFont = fonts.get(i);
			Button fontButton = new Button(this);			
			fontButton.setText(customFont.fontFaceName);
			fontButton.setTextSize(20);
			Typeface tf = null;
			if (customFont.fontFileName==null || customFont.fontFileName.isEmpty()) {
				tf = this.getTypeface(customFont.fontFaceName,Typeface.BOLD);				
			}else {
				tf = Typeface.createFromAsset(getAssets(), "fonts/"+customFont.fontFileName);				
			}			
			if (tf!=null) fontButton.setTypeface(tf);
			fontButton.setId(5100+i);
			RoundRectShape rs = new RoundRectShape(new float[] {ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5),ps(5) }, null, null);
			SkyDrawable brd = new SkyDrawable(rs,innerBoxColor,inlineColor2,1);
			fontButton.setBackgroundDrawable(brd);
			this.setFrame(fontButton, ps(0),ps(0),ps(width-40), ps(fontButtonHeight));
			fontListView.addView(fontButton);
			fontButton.setOnClickListener(listener);
			fontButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(fontButton));
		}
		
		this.ePubView.addView(fontBox);
		this.hideFontBox();
	}
	
	public Typeface getTypeface(String fontName,int fontStyle) {
		Typeface tf = null;
		if (fontName.toLowerCase().contains("book")) {
			tf =  Typeface.create(Typeface.DEFAULT, fontStyle);			
		}else if (fontName.toLowerCase().contains("default")) {
			tf =  Typeface.create(Typeface.DEFAULT, fontStyle);			
		}else if (fontName.toLowerCase().contains("mono")) {
			tf =  Typeface.create(Typeface.MONOSPACE, fontStyle);			
		}else if ((fontName.toLowerCase().contains("sans"))) {
			tf =  Typeface.create(Typeface.SANS_SERIF, fontStyle);			
		}else if ((fontName.toLowerCase().contains("serif"))) {
			tf =  Typeface.create(Typeface.SERIF, fontStyle);			
		} 		
		return tf;
	}
	
	public String getFontName(int fontIndex) {
		if (fontIndex<0) fontIndex = 0;
		if (fontIndex>(fontNames.length-1)) fontIndex = fontNames.length-1;
		String name = fontNames[fontIndex];
		return name;
	}
	
	// CustomFont
	public CustomFont getCustomFont(int fontIndex) {
		if (fontIndex<0) fontIndex = 0;
		if (fontIndex>(fonts.size()-1)) fontIndex = fonts.size()-1;
		return fonts.get(fontIndex);		
	}
	
	// CustomFont
	public int getFontIndex(String fontName) {
		for (int i=0; i<fonts.size(); i++) {
			CustomFont customFont = fonts.get(i);
			String name = customFont.getFullName();
			if (name.equalsIgnoreCase(fontName)) return i;			
		}
		return 0;
	}
	
	public void hideFontBox() {
		fontBox.setVisibility(View.INVISIBLE);
		fontBox.setVisibility(View.GONE);	
		isBoxesShown = false;
		this.hideOutsideButton();		
	}
	
	public void showFontBox() {
		isBoxesShown = true;
		this.showOutsideButton();
		int width = 450; 
		int height = 500;
		int left,top;
		if (!this.isTablet()) {
			if (this.isHighDensityPhone()) {
				left =  pxr(width+20);
				top = ps(40);
				if (!isPortrait()) {
					top = ps(35);
					left =  pxr(width+80);								
				}
			}else {
				left =  pxr(width+50);
				top = ps(75);
				if (!isPortrait()) {
					top = ps(35);
					left =  pxr(width+80);								
				}
			}
		}else {
			if (this.isPortrait()) {
				left = pxr(width+230);
				top = ps(120);
			}else {
				left = pxr(width+200);
				top = ps(120);
			}			
		}
		
		fontBox.setVisibility(View.VISIBLE);
		int sh = this.getHeight()-ps(240);
		int rh = sh- ps(150);
		this.setFrame(fontBox, left,top,ps(width),ps(height));
		fontBox.setArrowHeight(ps(25));
		fontBox.setArrowPosition(pxr(160),left,ps(width));
		brightBar.setProgress((int)(setting.brightness*999));
		
		this.checkSettings();
	}

	public void makeListBox() {
		this.listBox = new SkyLayout(this);
		listBox.setBackgroundColor(Color.TRANSPARENT);
//		listBox.setBackgroundColor(this.themes.get(this.themeIndex).backgroundColor | 0xD0000000);
		listTopButton = new Button(this);
		listTopButton.setId(9009);
		listTopButton.setOnClickListener(listener);
		listTopButton.setBackgroundColor(Color.TRANSPARENT);
		
		GradientDrawable gradForChecked = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xff407ee6, 0xff6ca2f9 });
		GradientDrawable grad 			= new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xfff4f4f4, 0xffcdcdcd });
		this.contentListButton = new Button(this);
		this.contentListButton.setId(2700);
		this.contentListButton.setOnClickListener(listener);
		this.contentListButton.setText(getString(R.string.contents));
		this.contentListButton.setTextSize(13);
		
		this.bookmarkListButton = new Button(this);
		this.bookmarkListButton.setId(2701);
		this.bookmarkListButton.setOnClickListener(listener);
		this.bookmarkListButton.setText(getString(R.string.bookmark));
		this.bookmarkListButton.setTextSize(13);
		
		this.highlightListButton = new Button(this);
		this.highlightListButton.setId(2702);
		this.highlightListButton.setOnClickListener(listener);
		this.highlightListButton.setText(getString(R.string.highlight));
		this.highlightListButton.setTextSize(13);
		
		this.listScrollView = new ScrollView(this);
		this.listView = new LinearLayout(this);
		listView.setOrientation(LinearLayout.VERTICAL);
		
		this.listBox.addView(listTopButton);
		this.listBox.addView(contentListButton);
		this.listBox.addView(bookmarkListButton);
		this.listBox.addView(highlightListButton);
		
		this.listBox.addView(listScrollView);
		this.listScrollView.addView(listView,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		this.ePubView.addView(this.listBox);				
		this.hideListBox();
	}
	
	public void hideListBox() {
		listBox.setVisibility(View.INVISIBLE);
		listBox.setVisibility(View.GONE);	
		isBoxesShown = false;
		this.hideOutsideButton();
	}
	
	
	public void showListBox() {		
		isBoxesShown = true;
		this.showOutsideButton();
		int lx,ly,lw,lh;		
		if (this.isDoublePagedForLandscape && !this.isPortrait()) {
			lw = this.getWidth()/2;
			lh = this.getHeight();
			lx = this.getWidth()/2;
			ly = 0;					
		}else {
			lx = 0;
			ly = 0;
			lw = this.getWidth();
			lh = this.getHeight();			
		}
		this.setFrame(listBox, lx, ly,lw, lh);
		
		float tbh = .1f;    // topButton height ratio;
		float bgy = .12f; 	// buttons guide line y ratio
		float bhr = .15f;
		float lgx = .1f; 	// left guide line x ratio
		float sgty = .22f; 	// scrollBox top ratio
		float sgby = .1f; 	// scrollBox bottom ratio
		
		int bh = ps(50); // button height;
		
		this.setFrame(listTopButton, 0, 0, lw, (int)(lh*tbh));	// topButton to hide listBox
		this.setFrame(contentListButton		,(int)(lw*lgx)								,(int)(lh*bgy),(int)((lw-(lw*lgx*2))/3),bh);
		this.setFrame(bookmarkListButton	,(int)(lw*lgx)+(int)((lw-(lw*lgx*2))/3)		,(int)(lh*bgy),(int)((lw-(lw*lgx*2))/3),bh);
		this.setFrame(highlightListButton	,(int)(lw*lgx)+(int)((lw-(lw*lgx*2))/3)*2	,(int)(lh*bgy),(int)((lw-(lw*lgx*2))/3),bh);
		
		this.setFrame(this.listScrollView,(int)(lw*lgx),(int)(lh*sgty),(int)(lw-(lw*lgx*2)),(int)(lh-(lh*sgty+lh*sgby)));
		
		this.checkListButton(listSelectedIndex);		
		this.listBox.setVisibility(View.VISIBLE);		
	}
	
	int listSelectedIndex = 0;
	
	public void checkListButton(int index) {
		GradientDrawable gradChecked = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xff407ee6, 0xff6ca2f9 });
		gradChecked.setStroke(ps(1), Color.BLUE);
		GradientDrawable grad 		 = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xfff4f4f4, 0xffcdcdcd });
		grad.setStroke(ps(1), Color.LTGRAY);
		listSelectedIndex = index;
		Button buttons[] = {contentListButton,bookmarkListButton,highlightListButton};
		for (int i=0; i<buttons.length; i++) {
			Button button = buttons[i];
			button.setBackgroundDrawable(grad);
		}
		Button target = buttons[index];
		target.setBackgroundDrawable(gradChecked);
		// show contents..
		if (listSelectedIndex==0) fillContentsList();
		else if (listSelectedIndex==1) fillBookmarkList();
		else if (listSelectedIndex==2) fillHighlightList();
	}
	
	private void displayNavPoints() {
		NavPoints nps = rv.getNavPoints();
		for (int i=0; i<nps.getSize(); i++) {
			NavPoint np = nps.getNavPoint(i);
			debug(""+i+":"+np.text);
		}
		
		// modify one NavPoint object at will
		NavPoint onp = nps.getNavPoint(1);
		onp.text = "preface - it is modified";
		
		for (int i=0; i<nps.getSize(); i++) {
			NavPoint np = nps.getNavPoint(i);
			debug(""+i+":"+np.text+"   :"+np.sourcePath);
		}	
	}
	
	public void fillContentsList() {
		this.listView.removeAllViews();
		NavPoints nps = rv.getNavPoints();
		for (int i=0; i<nps.getSize(); i++) {
			NavPoint np = nps.getNavPoint(i);
			Button contentButton = new Button(this);
			contentButton.setBackgroundColor(Color.TRANSPARENT);
			contentButton.setText(np.text);
			Theme theme = getCurrentTheme();
			contentButton.setTextColor(theme.foregroundColor);
			contentButton.setTextSize(14);
			contentButton.setGravity(Gravity.LEFT);
			contentButton.setId(i);
			contentButton.setOnClickListener(contentDelegate);			
			listView.addView(contentButton);
			debug(""+i+":"+np.text);
		}		
	}
	
	NavPoint targetNavPoint = null;
	private OnClickListener contentDelegate = new OnClickListener() {
		public void onClick(View arg) {
			int index = arg.getId();
			RectShape rs = new RectShape();
			GradientDrawable sd = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xff407ee6, 0xff6ca2f9 });
	        SkyDrawable ed = new SkyDrawable(rs,Color.TRANSPARENT,Color.TRANSPARENT,ps(1));
			blinkBackground(arg,sd,ed);
			NavPoints nps = rv.getNavPoints();
			targetNavPoint = nps.getNavPoint(index);
			new Handler().postDelayed(new Runnable() { 
	            public void run() {
	    			isPagesHidden = false;
	    			showPages();	    			
	    			rv.gotoPageByNavPoint(targetNavPoint);
	            } 
			}, 200);			
		}
	};

	
	public void fillBookmarkList() {
		this.listView.removeAllViews();
		ArrayList <PageInformation> pis = sd.fetchBookmarks(this.bookCode);
		for (int i=0; i<pis.size(); i++) {
			int textColor = Color.BLACK;
			Theme theme = getCurrentTheme();
			textColor = theme.foregroundColor;
			PageInformation pi = pis.get(i);
			SkyLayout item = new SkyLayout(this);
			setFrame(item, 0, 0, listBox.getWidth(),ps(80));
			ImageButton mark = this.makeImageButton(9898, R.drawable.bookmarked2x, ps(50),ps(90));
			item.addView(mark);
			setFrame(mark,ps(10),ps(5),ps(60),ps(120));
			int ci = pi.chapterIndex;
			if (rv.isRTL()) {
				ci = rv.getNumberOfChapters()-ci-1;
			}
			String chapterTitle = rv.getChapterTitle(ci);
			if (chapterTitle==null || chapterTitle.isEmpty()) chapterTitle = "Chapter "+ci;
			TextView chapterLabel = this.makeLabel(9899,chapterTitle, Gravity.LEFT, 16, textColor);
			setFrame(chapterLabel,ps(80),ps(5),this.listBox.getWidth()-ps(80),ps(40));
			item.addView(chapterLabel);
			TextView dateLabel = this.makeLabel(9899,pi.datetime, Gravity.LEFT, 12, textColor);
			setFrame(dateLabel,this.listBox.getWidth()-ps(50+250),ps(48),this.listBox.getWidth()-ps(40),ps(40));
			View lineView = new View(this);
			lineView.setBackgroundColor(Color.LTGRAY);
			setFrame(lineView,0,ps(79),this.listBox.getWidth(),ps(1));			
			item.addView(dateLabel);
			item.addView(lineView);
			item.setSkyLayoutListener(bookmarkListDelegate);
			item.setId(pi.code);
			item.data = pi;
			
			Button deleteButton = new Button(this);
			GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xffcf666e, 0xff671521 });
			grad.setStroke(ps(2), 0xff282828);
			deleteButton.setBackgroundDrawable(grad);
			deleteButton.setText(getString(R.string.delete));	
			deleteButton.setTextSize(12);
			deleteButton.setTypeface(null,Typeface.BOLD);
			deleteButton.setTextColor(Color.WHITE);
			deleteButton.setId(pi.code);
			deleteButton.setVisibility(View.INVISIBLE);
			deleteButton.setVisibility(View.GONE);
			deleteButton.setOnClickListener(deleteBookmarkDelegate);
			int dw = ps(120); int dh = ps(50);
			setFrame(deleteButton,this.listView.getWidth()-dw,(ps(80)-dh)/2,dw,dh);
			item.deleteControl = deleteButton;
			item.addView(deleteButton);			

			this.listView.addView(item);
		}		
	}
	
	View tempView;
	Drawable tempDrawable;
	private void blinkBackground(View view,Drawable startDrawable, Drawable endDrawable) {
		tempView = view;
		tempDrawable = endDrawable;
		view.setBackgroundDrawable(startDrawable);
		new Handler().postDelayed(new Runnable() { 
			public void run() {
				tempView.setBackgroundDrawable(tempDrawable);
				tempView = null;
			} 
		}, 100);
	}
	
	private OnClickListener deleteBookmarkDelegate = new OnClickListener() {
		public void onClick(View arg) {
			int targetCode = arg.getId();
			for (int i=0; i<listView.getChildCount(); i++) {
				SkyLayout view = (SkyLayout)listView.getChildAt(i);
				if (view.getId()==targetCode) {
					listView.removeViewAt(i);
					sd.deleteBookmarkByCode(targetCode);
				}
			}
		}
	};
	
	private SkyLayoutListener bookmarkListDelegate = new SkyLayoutListener() {
		@Override
		public void onShortPress(SkyLayout view,MotionEvent e) {
		}

		@Override
		public void onLongPress(SkyLayout view,MotionEvent e) {
			beep(100);
			Button deleteButton = (Button)view.deleteControl;
			int vt = deleteButton.getVisibility();
			if (vt!=View.VISIBLE) {
				deleteButton.setVisibility(View.VISIBLE);
			}else {
				deleteButton.setVisibility(View.INVISIBLE);
				deleteButton.setVisibility(View.GONE);	
			}			
		}
		
		void toggleDeleteButton(SkyLayout view) {
			beep(50);
			Button deleteButton = (Button)view.deleteControl;
			if (deleteButton.getVisibility()==View.VISIBLE) {
				deleteButton.setVisibility(View.INVISIBLE);
				deleteButton.setVisibility(View.GONE);
			}else {
				deleteButton.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onSwipeToLeft(SkyLayout view) {			
			toggleDeleteButton(view);	
		}

		@Override
		public void onSwipeToRight(SkyLayout view) {
			toggleDeleteButton(view);	
		}

		PageInformation targetPI = null;
		@Override
		public void onSingleTapUp(SkyLayout view, MotionEvent e) {
			Button deleteButton = (Button)view.deleteControl;
			int vt = deleteButton.getVisibility();
			if (vt==View.VISIBLE) return;			
			PageInformation pi = (PageInformation)view.data;
	        RectShape rs = new RectShape();
			GradientDrawable sd = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xff407ee6, 0xff6ca2f9 });
	        SkyDrawable ed = new SkyDrawable(rs,Color.TRANSPARENT,Color.TRANSPARENT,ps(1));
			blinkBackground(view,sd,ed);
			targetPI = pi;
			new Handler().postDelayed(new Runnable() { 
	            public void run() {
	    			isPagesHidden = false;
	    			showPages();	    			
	    			rv.gotoPageByPagePositionInBook(targetPI.pagePositionInBook);
	            } 
			}, 200);
		}				
	};
	
	public void fillHighlightList() {
		Theme theme = getCurrentTheme();
		int textColor = theme.foregroundColor;
		this.listView.removeAllViews();
		Highlights highlights = sd.fetchAllHighlights(this.bookCode);
		for (int i=0; i<highlights.getSize(); i++) {
			Highlight highlight = highlights.getHighlight(i);
			SkyLayout item = new SkyLayout(this);	
			int ci = highlight.chapterIndex;
			if (rv.isRTL()) {
				ci = rv.getNumberOfChapters()-ci-1;
			}
			String chapterTitle = rv.getChapterTitle(ci);
			if (chapterTitle==null || chapterTitle.isEmpty()) chapterTitle = "Chapter "+ci;
			
			TextView chapterLabel = this.makeLabel(9899,chapterTitle, Gravity.LEFT, 16, textColor);
			setFrame(chapterLabel,ps(20),ps(5),this.listView.getWidth()-ps(20),ps(40));
			item.addView(chapterLabel);
			
			GradientDrawable textGrad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {getBrighterColor(highlight.color),getDarkerColor(highlight.color)});			
			TextView textLabel = this.makeLabel(9899,highlight.text, Gravity.LEFT, 16, Color.BLACK);
			setFrame(textLabel,ps(20),ps(5+40+5),this.listView.getWidth()-ps(20),ps(70));
			textLabel.setBackgroundDrawable(textGrad);
			textLabel.getBackground().setAlpha(180);
			
			item.addView(textLabel);
			
			int noteHeight = 0;

			if (highlight.isNote && highlight.note!=null && highlight.note.length()!=0 && !highlight.note.equalsIgnoreCase("null") ) {
				TextView noteLabel = this.makeLabel(9899,highlight.note, Gravity.LEFT, 16, Color.BLACK);
				noteLabel.setTextColor(getDarkerColor(highlight.color));
				noteHeight = 70;
				setFrame(noteLabel,ps(20),ps(5+40+5+70+5),this.listView.getWidth()-ps(20),ps(noteHeight));
				item.addView(noteLabel);				
			}
			
			TextView dateLabel = this.makeLabel(9899,highlight.datetime, Gravity.RIGHT, 12,textColor);
			int lw = this.listView.getWidth();
			setFrame(dateLabel,0,ps(5+40+5+70+5+noteHeight+5),lw,ps(40));
			item.addView(dateLabel);

			int itemHeight = ps(5+40+5+90+5+noteHeight+5+15+5);
					
			View lineView = new View(this);
			lineView.setBackgroundColor(Color.LTGRAY);
			setFrame(lineView,0,itemHeight-ps(1),this.listView.getWidth(),ps(1));
			item.addView(lineView);

			setFrame(item, 0, 0, listView.getWidth(),itemHeight); 
			item.setSkyLayoutListener(highlightListDelegate);
			item.setId(highlight.code);
			item.data = highlight;
			
			Button deleteButton = new Button(this);
			GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xffcf666e, 0xff671521 });
			grad.setStroke(ps(2), 0xff282828);
			deleteButton.setBackgroundDrawable(grad);
			deleteButton.setText(getString(R.string.delete));	
			deleteButton.setTypeface(null,Typeface.BOLD);
			deleteButton.setTextColor(Color.WHITE);
			deleteButton.setTextSize(12);
			deleteButton.setId(highlight.code);
			deleteButton.setVisibility(View.INVISIBLE);
			deleteButton.setVisibility(View.GONE);
			deleteButton.setOnClickListener(deleteHighlightDelegate);
			int dw = ps(120); int dh = ps(50);
			setFrame(deleteButton,this.listView.getWidth()-dw,(itemHeight-dh)/2,dw,dh);
			item.deleteControl = deleteButton;
			item.addView(deleteButton);			

			this.listView.addView(item);		}
	}
	
	private void beep(int ms) {
		Vibrator vibe = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(ms);	
	}

	
	private OnClickListener deleteHighlightDelegate = new OnClickListener() {
		public void onClick(View arg) {
			int targetCode = arg.getId();
			for (int i=0; i<listView.getChildCount(); i++) {
				SkyLayout view = (SkyLayout)listView.getChildAt(i);
				if (view.getId()==targetCode) {
					Highlight target = (Highlight)view.data;
					listView.removeViewAt(i);
					rv.deleteHighlight(target);
				}
			}
		}
	};
	
	private SkyLayoutListener highlightListDelegate = new SkyLayoutListener() {
		@Override
		public void onShortPress(SkyLayout view,MotionEvent e) {
		}

		@Override
		public void onLongPress(SkyLayout view,MotionEvent e) {
			beep(100);
			Button deleteButton = (Button)view.deleteControl;
			int vt = deleteButton.getVisibility();
			if (vt!=View.VISIBLE) {
				deleteButton.setVisibility(View.VISIBLE);
			}else {
				deleteButton.setVisibility(View.INVISIBLE);
				deleteButton.setVisibility(View.GONE);	
			}			
		}

		void toggleDeleteButton(SkyLayout view) {
			beep(50);
			Button deleteButton = (Button)view.deleteControl;
			if (deleteButton.getVisibility()==View.VISIBLE) {
				deleteButton.setVisibility(View.INVISIBLE);
				deleteButton.setVisibility(View.GONE);
			}else {
				deleteButton.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onSwipeToLeft(SkyLayout view) {			
			toggleDeleteButton(view);	
		}

		@Override
		public void onSwipeToRight(SkyLayout view) {
			toggleDeleteButton(view);	
		}

		Highlight targetHighlight = null;
		@Override
		public void onSingleTapUp(SkyLayout view, MotionEvent e) {
			Button deleteButton = (Button)view.deleteControl;
			int vt = deleteButton.getVisibility();
			if (vt==View.VISIBLE) return;			
			Highlight highlight = (Highlight)view.data;
	        RectShape rs = new RectShape();
			GradientDrawable sd = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xff407ee6, 0xff6ca2f9 });
	        SkyDrawable ed = new SkyDrawable(rs,Color.TRANSPARENT,Color.TRANSPARENT,ps(1));
			blinkBackground(view,sd,ed);
			targetHighlight = highlight;			
			new Handler().postDelayed(new Runnable() { 
	            public void run() {     
	            	isPagesHidden = false;
	    			showPages();
	    			rv.gotoPageByHighlight(targetHighlight);
	            } 
			}, 200);
		}				
	};
	
	public void makeFullScreen() {
//		if (SkyUtility.isNexus() && isFullScreenForNexus) {
			SkyUtility.makeFullscreen(this);
//		}
	}
	
	public void makeMediaBox() {
		mediaBox = new SkyLayout(this);
		setFrame(mediaBox, 30, 200, ps(320),ps(50));
		
		int bs = ps(32);
		int sb = 25;
		prevButton = this.makeImageButton(9898, R.drawable.prev2x, bs,bs);
		setLocation(prevButton,ps(10),ps(5));
		prevButton.setId(8080);
		prevButton.setOnClickListener(listener);
		playAndPauseButton = this.makeImageButton(9898, R.drawable.pause2x, bs,bs);
		setLocation(playAndPauseButton,ps(sb)+bs+ps(10),ps(5));
		playAndPauseButton.setId(8081);
		playAndPauseButton.setOnClickListener(listener);
		stopButton = this.makeImageButton(9898, R.drawable.stop2x, bs,bs);
		setLocation(stopButton,(ps(sb)+bs)*2,ps(5));
		stopButton.setId(8082);
		stopButton.setOnClickListener(listener);
		nextButton = this.makeImageButton(9898, R.drawable.next2x, bs,bs);
		setLocation(nextButton,(ps(sb)+bs)*3,ps(5));
		nextButton.setId(8083);
		nextButton.setOnClickListener(listener);
		
		mediaBox.setVisibility(View.INVISIBLE);
		mediaBox.setVisibility(View.GONE);		
		
		mediaBox.addView(prevButton);
		mediaBox.addView(playAndPauseButton);
		mediaBox.addView(stopButton);
		mediaBox.addView(nextButton);
		this.ePubView.addView(mediaBox);		
	}
	
	public void hideMediaBox() {
		if (mediaBox!=null) {
			titleLabel.setVisibility(View.VISIBLE);
			mediaBox.setVisibility(View.INVISIBLE);
			mediaBox.setVisibility(View.GONE);
		}
	}
	
	public void showMediaBox() {
		titleLabel.setVisibility(View.INVISIBLE);
		titleLabel.setVisibility(View.GONE);
		mediaBox.setVisibility(View.VISIBLE);
		this.changePlayAndPauseButton();
	}
	
	public void moveSkyBox(SkyBox box,int boxWidth,int boxHeight, Rect startRect, Rect endRect) {
		RelativeLayout.LayoutParams params = 
			    (RelativeLayout.LayoutParams)box.getLayoutParams();	
		int topMargin = ps(80);
		int bottomMargin = ps(80);
		int boxTop=0;
		int boxLeft=0;
		int arrowX;
		boolean isArrowDown;
	
		if (startRect.top - topMargin > boxHeight) {
			boxTop = startRect.top - boxHeight-ps(10);
			boxLeft = (startRect.left+startRect.width()/2-boxWidth/2);
			arrowX = (startRect.left+startRect.width()/2);
			isArrowDown = true;
		}else if ((this.getHeight()-endRect.bottom)-bottomMargin >boxHeight) { // ????????? ????????? ????????? ?????????. 
			boxTop = endRect.bottom+ps(10);
			boxLeft = (endRect.left+endRect.width()/2-boxWidth/2);
			arrowX = (endRect.left+endRect.width()/2);
			isArrowDown = false;
		}else {
			boxTop = ps(100);
			boxLeft = (startRect.left+startRect.width()/2-boxWidth/2);
			arrowX = (startRect.left+startRect.width()/2);
			isArrowDown = true;
		}
		
		if (boxLeft+boxWidth > this.getWidth()*.9) {
			boxLeft = (int)(this.getWidth()*.9) - boxWidth;
		}else if (boxLeft<this.getWidth()*.1) {
			boxLeft = (int)(this.getWidth()*.1);
		}
		
		box.setArrowPosition(arrowX, boxLeft, boxWidth);		
		box.setArrowDirection(isArrowDown);
		params.leftMargin = boxLeft;
		params.topMargin = boxTop;
		params.width = boxWidth;
		params.height = boxHeight;
		box.setLayoutParams(params);
		box.invalidate();
		
		boxFrame = new Rect();
		boxFrame.left = boxLeft;
		boxFrame.top = boxTop;
		boxFrame.right = boxLeft+boxWidth;
		boxFrame.bottom = boxTop+boxHeight;		
	}
	
	public ImageButton makeImageButton(int id,int resId,int width, int height) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		Drawable icon;
		ImageButton button = new ImageButton(this);
		button.setAdjustViewBounds(true);
		button.setId(id);
		button.setOnClickListener(listener);
		button.setBackgroundColor(Color.TRANSPARENT);
		icon = getResources().getDrawable(resId);
		icon.setBounds(0,0,width,height);
		
		Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, width, height, false);
		button.setImageBitmap(bitmapResized);
		button.setVisibility(View.VISIBLE);
		param.width = 		(int)(width);		
		param.height = 		(int)(height);
		button.setLayoutParams(param);		
		return button;		
	}

	
	public TextView makeLabel(int id, String text, int gravity,float textSize,int textColor) {
		TextView label = new TextView(this);
		label.setId(id);
		label.setGravity(gravity);
		label.setBackgroundColor(Color.TRANSPARENT);
		label.setText(text);
		label.setTextColor(textColor);		
		label.setTextSize(textSize);
		return label;
	}
	
	public void setFrame(View view,int dx, int dy, int width, int height) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		param.leftMargin = dx;
		param.topMargin =  dy;
		param.width = width;
		param.height = height;
		view.setLayoutParams(param);	
	}
	
	public void setLocation(View view,int px, int py) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		param.leftMargin = px;
		param.topMargin =  py;
		view.setLayoutParams(param);
	}
	
	public void setLocation2(View view,int px, int py) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT); // width,height
		param.leftMargin = px;
		param.topMargin =  py;
		view.setLayoutParams(param);
	}
	
	int getLabelWidth(TextView tv) {
		tv.measure(0, 0);       //must call measure!		
		return tv.getMeasuredWidth();  //get height
	}
	
	int getLabelHeight(TextView tv) {
		tv.measure(0, 0);       //must call measure!
		return tv.getMeasuredHeight(); //get width		
	}
	
	
	long timeRepainted = 0;
	public void toggleControls() {
		long timeNow = System.currentTimeMillis();
		long diff = timeNow-timeRepainted;
		
		if (diff<1000) return;	// prevent continuous tapping. 

		isControlsShown = !isControlsShown;
		if (isControlsShown) {
			showControls();
		}else {
			hideControls();
		}		
//		clearHighlightsForSearchResults();
		timeRepainted = System.currentTimeMillis();
	}
	
	public void showControls() {
		this.rotationButton.setVisibility(View.VISIBLE);
		this.listButton.setVisibility(View.VISIBLE);
		this.fontButton.setVisibility(View.VISIBLE);
		if (!rv.isScrollMode()) this.searchButton.setVisibility(View.VISIBLE);
		if (!rv.isPaging()) this.seekBar.setVisibility(View.VISIBLE);		
	}
	
	public void hideControls() {
		this.rotationButton.setVisibility(View.INVISIBLE);
		this.rotationButton.setVisibility(View.GONE);
		this.listButton.setVisibility(View.INVISIBLE);
		this.listButton.setVisibility(View.GONE);
		this.fontButton.setVisibility(View.INVISIBLE);
		this.fontButton.setVisibility(View.GONE);
		this.searchButton.setVisibility(View.INVISIBLE);
		this.searchButton.setVisibility(View.GONE);
		this.seekBar.setVisibility(View.INVISIBLE);
		this.seekBar.setVisibility(View.GONE);
	}
	
	public boolean isAboveIcecream() {
		if (android.os.Build.VERSION.SDK_INT >= 14) { //  api >= icecream 
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isHoneycomb() {
		return false;
		//		int API = android.os.Build.VERSION.SDK_INT;
//		if (API == 11 || API == 12 || API == 13 ) { 	// Honeycomb 
//			return true;
//		}else {
//			return false;
//		}
	}
	
	public void removeControls() {
		rv.customView.removeView(rotationButton);
		rv.customView.removeView(titleLabel);
		rv.customView.removeView(authorLabel);

		ePubView.removeView(rotationButton);
		ePubView.removeView(listButton);
		ePubView.removeView(fontButton);
		ePubView.removeView(searchButton);		

		ePubView.removeView(pageIndexLabel);
		ePubView.removeView(secondaryIndexLabel);	
		
		ePubView.removeView(seekBar);
	}
	
	public void makeControls() {
		this.removeControls();		
		Theme theme = getCurrentTheme();
		
		int bs = 38;
		if (this.isRotationLocked) 	rotationButton 		= this.makeImageButton(9000, R.drawable.rotationlocked2x, ps(42),ps(42));
		else 						rotationButton 		= this.makeImageButton(9000, R.drawable.rotation2x, ps(42),ps(42));
		listButton 			= this.makeImageButton(9001, R.drawable.list2x, getPS(bs),getPS(bs));
		fontButton 			= this.makeImageButton(9002, R.drawable.font2x,  getPS(bs),getPS(bs));
		searchButton 		= this.makeImageButton(9003, R.drawable.search2x, getPS(bs),getPS(bs));
		rotationButton.setOnTouchListener(new ImageButtonHighlighterOnTouchListener(rotationButton));
		listButton.setOnTouchListener(new ImageButtonHighlighterOnTouchListener(listButton));
		fontButton.setOnTouchListener(new ImageButtonHighlighterOnTouchListener(fontButton));
		searchButton.setOnTouchListener(new ImageButtonHighlighterOnTouchListener(searchButton));
		
		titleLabel				= this.makeLabel(3000,  title,	Gravity.CENTER_HORIZONTAL, 17, Color.argb(240, 94,61,35));	// setTextSize in android uses sp (Scaled Pixel) as default, they say that sp guarantees the device dependent size, but as usual in android it can't be 100% sure. 	
		authorLabel				= this.makeLabel(3000,  author,	Gravity.CENTER_HORIZONTAL, 17, Color.argb(240, 94,61,35));
		pageIndexLabel			= this.makeLabel(3000, 	"......",		Gravity.CENTER_HORIZONTAL, 13, Color.argb(240, 94,61,35));
		secondaryIndexLabel		= this.makeLabel(3000, 	"......",		Gravity.CENTER_HORIZONTAL, 13, Color.argb(240, 94,61,35));		
		
//		rv.customView.addView(rotationButton);
//		rv.customView.addView(listButton);
//		rv.customView.addView(fontButton);
//		rv.customView.addView(searchButton);		
		rv.customView.addView(titleLabel);
		rv.customView.addView(authorLabel);

		ePubView.addView(rotationButton);
		ePubView.addView(listButton);
		ePubView.addView(fontButton);
		if (!rv.isScrollMode()) ePubView.addView(searchButton);		
//		ePubView.addView(titleLabel);
//		ePubView.addView(authorLabel);

		ePubView.addView(pageIndexLabel);
		ePubView.addView(secondaryIndexLabel);		
		
		seekBar = new SkySeekBar(this);
		seekBar.setMax(999);
		seekBar.setId(999);
		RectShape rectShape = new RectShape();		
		ShapeDrawable thumb = new ShapeDrawable(rectShape);
		
		thumb.getPaint().setColor(theme.seekThumbColor);
		thumb.setIntrinsicHeight(getPS(28));
        thumb.setIntrinsicWidth(getPS(28));
        seekBar.setThumb(thumb);
		seekBar.setBackgroundColor(Color.TRANSPARENT);
		seekBar.setOnSeekBarChangeListener(new SeekBarDelegate());
		seekBar.setProgressDrawable(new DottedDrawable(theme.seekBarColor));
		seekBar.setThumbOffset(-3);
		seekBar.setMinimumHeight(24);
		
		int filterColor = theme.controlColor;
		rotationButton.setColorFilter(filterColor);
		listButton.setColorFilter(filterColor);
		fontButton.setColorFilter(filterColor);			
		searchButton.setColorFilter(filterColor);

		authorLabel.setTextColor(filterColor);
		titleLabel.setTextColor(filterColor);
		pageIndexLabel.setTextColor(filterColor);
		secondaryIndexLabel.setTextColor(filterColor);

		ePubView.addView(seekBar);
	}
	
	public void makePagingView() {
		Theme theme = getCurrentTheme();
		pagingView = new View(this);
		pagingView.setBackgroundDrawable(new DottedDrawable(Color.BLACK,theme.seekBarColor,100));		
		ePubView.addView(pagingView);
		this.hidePagingView();
	}
	
	public void showPagingView() {
		seekBar.setVisibility(View.INVISIBLE);
		seekBar.setVisibility(View.GONE);
		pagingView.setVisibility(View.VISIBLE);
	}
	
	public void hidePagingView() {
		pagingView.setVisibility(View.INVISIBLE);
		pagingView.setVisibility(View.GONE);	
		seekBar.setVisibility(View.VISIBLE);
	}
	
	public void changePagingView(int value) {
		Theme theme = this.getCurrentTheme();
		pagingView.setBackgroundDrawable(new DottedDrawable(Color.RED,theme.seekBarColor,value));
	}
	
	public void recalcFrames() {
		this.authorLabel.setVisibility(View.VISIBLE);
		this.secondaryIndexLabel.setVisibility(View.VISIBLE);
		int seekWidth = (int)(this.getWidth()*0.75);
		int seekLeft = (this.getWidth()-seekWidth)/2;

		if (!this.isTablet()) {				// for phones   					- tested with Galaxy S2, Galaxy S3, Galaxy S4
			if (this.isPortrait()) {
				this.setLocation(rotationButton, 	pxl(20),pyt(15-2));
				this.setLocation(listButton, 		pxl(20+(48+5)*1),pyt(15));
				this.setLocation(searchButton, 		pxr(40+(48+5)*3),pyt(15));
				this.setLocation(fontButton, 		pxr(40+(48+5)*2),pyt(15));					

				
				
				this.setFrame(seekBar,seekLeft,pyb(125),seekWidth,ps(36));				
				int brx = 36+(44)*1; int bry = 23;
				bookmarkRect 	= new Rect(pxr(brx),pyt(bry),pxr(brx-40),pyt(bry+40));
				bookmarkedRect 	= new Rect(pxr(brx),pyt(bry),pxr(brx-38),pyt(bry+70));
			}else {
				int sd = ps(40);
				this.setLocation(rotationButton, 	pxl(10),pyt(5-2));
				this.setLocation(listButton, 		pxl(10+(48+5)*1),pyt(5));
				this.setLocation(searchButton, 		pxr(60+(48+5)*3),pyt(5));
				this.setLocation(fontButton, 		pxr(60+(48+5)*2),pyt(5));

				this.setFrame(seekBar,seekLeft,pyb(108),seekWidth,ps(36));
				int brx = 40+(48+12)*1; int bry = 14;
				bookmarkRect 	= new Rect(pxr(brx),pyt(bry),pxr(brx-40),pyt(bry+40));
				bookmarkedRect 	= new Rect(pxr(brx),pyt(bry),pxr(brx-38),pyt(bry+70));
			}			
		}else {									// for tables				- tested with Galaxy Tap 10.1, Galaxy Note 10.1
			if (this.isPortrait()) {
				int ox = 50;
				int rx = 100;
				int oy = 30;
				
				this.setLocation(rotationButton, 	pxl(ox)					,pyt(oy-2));
				this.setLocation(listButton, 		pxl(ox+(65)*1)			,pyt(oy));				
				this.setLocation(searchButton, 		pxr(rx+(65)*3)			,pyt(oy));
				this.setLocation(fontButton, 		pxr(rx+(65)*2)			,pyt(oy));					


				this.setFrame(seekBar,seekLeft,pyb(140),seekWidth,ps(45));				
				int brx = rx-10+(44)*1; int bry = oy+10;
				bookmarkRect 	= new Rect(pxr(brx),pyt(bry),pxr(brx-50),pyt(bry+50));
				bookmarkedRect 	= new Rect(pxr(brx),pyt(bry),pxr(brx-50),pyt(bry+90));
			}else {
				int sd = ps(40);
				int ox = 40;
				int rx = 130;
				int oy = 20;				

				this.setLocation(rotationButton, 	pxl(ox)				,pyt(oy-2));
				this.setLocation(listButton, 		pxl(ox+(65)*1)		,pyt(oy));				
				this.setLocation(searchButton, 		pxr(rx+(65)*3)		,pyt(oy));
				this.setLocation(fontButton, 		pxr(rx+(65)*2)		,pyt(oy));


				this.setFrame(seekBar,seekLeft,pyb(123),seekWidth,ps(45));
				
				int brx = rx-20+(48+12)*1; int bry = oy+10;
				bookmarkRect 	= new Rect(pxr(brx),pyt(bry),pxr(brx-40),pyt(bry+40));
				bookmarkedRect 	= new Rect(pxr(brx),pyt(bry),pxr(brx-38),pyt(bry+70));
			}			
		}
		RelativeLayout.LayoutParams sl = (RelativeLayout.LayoutParams)seekBar.getLayoutParams();
		this.setFrame(pagingView,sl.leftMargin,sl.topMargin,sl.width,sl.height);
		
		this.recalcLabelsLayout();
		this.enableControlAfterPagination();
	}
	
	public void setLabelLength(TextView label, int maxLength) {
		String text = (String) label.getText();
		if (text.length()>maxLength) {
			text = text.substring(0, maxLength);
			text = text+"..";
		}
		label.setText(text);
	}

	public void recalcLabelsLayout() {
		int sd = this.getWidth()/40;
		this.titleLabel.setText(this.title);
		String authorText = this.author;
		if (authorText.length()>12) authorText = authorText.substring(0, 12);
		this.authorLabel.setText(authorText);

		if (!this.isTablet()) {															// phone
			if (this.isPortrait()) {
				this.setLabelLength(titleLabel,10);
				this.setLocation(titleLabel,(this.getWidth()/2-this.getLabelWidth(titleLabel)/2)-sd	,pyt(28));
				this.setLocation(mediaBox, this.getWidth()/2-ps(270)/2-ps(20)							,pyt(16));
				this.authorLabel.setVisibility(View.INVISIBLE);
				this.authorLabel.setVisibility(View.GONE);
				this.secondaryIndexLabel.setVisibility(View.INVISIBLE);
				this.secondaryIndexLabel.setVisibility(View.GONE);
				this.setLocation(pageIndexLabel,(this.getWidth()/2-this.getLabelWidth(pageIndexLabel)/2)-sd,pyb(90));
			}else {
				if (this.isDoublePagedForLandscape) {
					this.setLabelLength(titleLabel,10);
					if (this.isHighDensityPhone()) {
						this.authorLabel.setVisibility(View.INVISIBLE);
						this.authorLabel.setVisibility(View.GONE);
					}else {
						this.authorLabel.setVisibility(View.VISIBLE);
					}					
					this.secondaryIndexLabel.setVisibility(View.VISIBLE);
					this.setLocation(titleLabel,			this.getWidth()/4-this.getLabelWidth(titleLabel)/2,									pyt(17));
					this.setLocation(mediaBox,				this.getWidth()/4-ps(270)/2+sd,														pyt(4));
					this.setLocation(authorLabel,			this.getWidth()/2+this.getWidth()/4-this.getLabelWidth(authorLabel)/2-sd*4,						pyt(17));
					if (!this.isRTL) {
						this.setLocation(pageIndexLabel,		this.getWidth()/4-this.getLabelWidth(pageIndexLabel)/2,											pyb(85));
						this.setLocation(secondaryIndexLabel,	this.getWidth()/2+this.getWidth()/4-this.getLabelWidth(secondaryIndexLabel)/2,						pyb(85));
					}else {
						this.setLocation(secondaryIndexLabel,		this.getWidth()/4-this.getLabelWidth(pageIndexLabel)/2,											pyb(85));
						this.setLocation(pageIndexLabel,	this.getWidth()/2+this.getWidth()/4-this.getLabelWidth(secondaryIndexLabel)/2,						pyb(85));
					}
				}else {
					this.setLabelLength(titleLabel,40);
					this.setLocation(titleLabel,(this.getWidth()/2-this.getLabelWidth(titleLabel)/2)-sd	,pyt(17));
					this.setLocation(mediaBox, this.getWidth()/2-ps(270)/2-sd*2							,pyt(4));
					this.authorLabel.setVisibility(View.INVISIBLE);
					this.authorLabel.setVisibility(View.GONE);
					this.secondaryIndexLabel.setVisibility(View.INVISIBLE);
					this.secondaryIndexLabel.setVisibility(View.GONE);
					this.setLocation(pageIndexLabel,(this.getWidth()/2-this.getLabelWidth(pageIndexLabel)/2)-sd,pyb(85));					
				}
			}
		}else {
			if (this.isPortrait()) {													// tablet
				this.setLabelLength(titleLabel,20);
				this.setLocation(titleLabel,(this.getWidth()/2-this.getLabelWidth(titleLabel)/2)-sd,pyt(28+20));
				this.setLocation(mediaBox, this.getWidth()/2-ps(270)/2-sd*2							,pyt(28+14));
				this.authorLabel.setVisibility(View.INVISIBLE);
				this.authorLabel.setVisibility(View.GONE);
				this.secondaryIndexLabel.setVisibility(View.INVISIBLE);
				this.secondaryIndexLabel.setVisibility(View.GONE);
				if (this.isHoneycomb()) {
					this.setLocation(pageIndexLabel,(this.getWidth()/2-this.getLabelWidth(pageIndexLabel)/2)-sd,pyb(100+80));
				}else {
					this.setLocation(pageIndexLabel,(this.getWidth()/2-this.getLabelWidth(pageIndexLabel)/2)-sd,pyb(100));
				}
			}else {
				if (this.isDoublePagedForLandscape) {
					this.setLabelLength(titleLabel,20);
					this.setLocation(titleLabel,			this.getWidth()/4-this.getLabelWidth(titleLabel)/2,										pyt(30));
					this.setLocation(mediaBox, 				this.getWidth()/4-ps(270)/2,															pyt(25));
					this.setLocation(authorLabel,			this.getWidth()/2+this.getWidth()/4-this.getLabelWidth(authorLabel)/2-sd*4,				pyt(30));

					this.setLocation(pageIndexLabel,		this.getWidth()/4-this.getLabelWidth(pageIndexLabel)/2,								pyb(88));
					this.setLocation(secondaryIndexLabel,	this.getWidth()/2+this.getWidth()/4-this.getLabelWidth(secondaryIndexLabel)/2,			pyb(88));
				}else {
					this.setLabelLength(titleLabel,50);
					this.setLocation(titleLabel,(this.getWidth()/2-this.getLabelWidth(titleLabel)/2)-sd,			pyt(27));
					this.setLocation(mediaBox, this.getWidth()/2-ps(270)/2-sd*2							,			pyt(27));
					this.authorLabel.setVisibility(View.INVISIBLE);
					this.authorLabel.setVisibility(View.GONE);
					this.secondaryIndexLabel.setVisibility(View.INVISIBLE);
					this.secondaryIndexLabel.setVisibility(View.GONE);

					this.setLocation(pageIndexLabel,(this.getWidth()/2-this.getLabelWidth(pageIndexLabel)/2)-sd,pyb(73));
				}
			}		
		}
	}
	
	public void setLabelsText(String title,String author) {
		titleLabel.setText(title);
		authorLabel.setText(author);	
	}
	
	public void setIndexLabelsText(int pageIndex, int pageCount) {
		if (pageIndex==-1 || pageCount==-1 || pageCount==0) {
			pageIndexLabel.setText("");
			secondaryIndexLabel.setText("");
			return;
		}
		
		int pi = 0;
		int si = 0; 
		int pc;
		if (rv.isDoublePaged()) {
			pc = pageCount*2;
			pi = pageIndex*2+1;
			si = pageIndex*2+2;
		}else {
			pc = pageCount;
			pi = pageIndex+1;
			si = pageIndex+2;
		}				
		String pt = String.format("%3d/%3d",pi,pc);
		String st = String.format("%3d/%3d",si,pc);		
		pageIndexLabel.setText(pt);
		secondaryIndexLabel.setText(st);	
	}	
	
	class SeekBarDelegate implements OnSeekBarChangeListener {
		public void onStopTrackingTouch(SeekBar seekBar) {
			int position = seekBar.getProgress();
			if (seekBar.getId()==999) {
				stopPlaying();
				if (rv.isGlobalPagination()) {
					int pib = position;
					double ppb = rv.getPagePositionInBookByPageIndexInBook(pib);
					rv.gotoPageByPagePositionInBook(ppb);					
				}else {
					double ppb = (double)position/(double)999;
					rv.gotoPageByPagePositionInBook(ppb);
				}
				hideSeekBox();
			}
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        	if (seekBar.getId()==999) {
        		showSeekBox();
        	}
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        	double ppb = 0;
        	PageInformation pi = null;
        	if (seekBar.getId()==999) {
        		if (rv.isGlobalPagination()) {        			
					int pib = progress;
					ppb = rv.getPagePositionInBookByPageIndexInBook(pib);
					pi = rv.getPageInformation(ppb);
        		}else {
        			ppb = (double)progress/(double)999.0f;
        			pi = rv.getPageInformation(ppb);
        		}
        		if (pi!=null) moveSeekBox(pi);
        	}
        	if (seekBar.getId()==997) {
        		setting.brightness = (float)progress/(float)999.f;
        		setBrightness((float)setting.brightness);
        	}
        }		
	}
	
	public void setBrightness(float brightness) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        getWindow().setAttributes(lp);		
	}
	
	public boolean isPortrait() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation==Configuration.ORIENTATION_PORTRAIT) return true;
		else return false;	
	}
	
	// this is not 100% accurate function. 
	public boolean isTablet() {
	    return (getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
    @SuppressLint("NewApi")
	public int getRawWidth() {
    	int width = 0, height = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;       

        try {
            // For JellyBeans and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                display.getRealMetrics(metrics);

                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
            }
            return width;
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return 0;
        }
    }
    
    @SuppressLint("NewApi")
	public int getRawHeight() {
    	int width = 0, height = 0;    	
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;       

        try {
            // For JellyBeans and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                display.getRealMetrics(metrics);
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");
                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return 0;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return 0;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return height;
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return 0;
        }
    }

	
	public int getWidth() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		if (SkyUtility.isNexus() && isFullScreenForNexus) {
			if (!this.isPortrait() && Build.VERSION.SDK_INT>=19) {
				width = this.getRawWidth();
			}
		}
		return width;		
	}
	
	// modify for fullscreen
	public int getHeight() {
		if (Build.VERSION.SDK_INT>=19) {
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			int height = this.getRawHeight();
			height+=ps(50);
			if (Build.DEVICE.contains("maguro") && this.isPortrait()) {
				height-=ps(65);
			}
			
			return height;
		}else {
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			int height = metrics.heightPixels;
			height+=ps(50);
			return height;
		}		
	}			
	
	public void log(String msg) {
		Log.w("EPub",msg);
	}
	
	// this event is called after device is rotated. 
	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		this.stopPlaying();
		if (this.isPortrait()) {
			log("portrait");
		}else {
			log("landscape");
		}
		this.hideBoxes();
		this.recalcFrames();
	}	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (SkyApplication)getApplication();
		sd = new SkyDatabase(this);
		setting = sd.fetchSetting();
		registerSkyReceiver(); // New in SkyEpub SDK 7.x
		this.makeFullScreen();
		this.makeLayout();	
	}
	
	private BroadcastReceiver skyReceiver = null;
	
	private void registerSkyReceiver() {
		if(skyReceiver != null) return;
		final IntentFilter theFilter = new IntentFilter();
		theFilter.addAction(Book.SKYERROR);
		this.skyReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int code = intent.getIntExtra("code", 0);
				int level = intent.getIntExtra("level", 0);
				String message = intent.getStringExtra("message");
				if (intent.getAction().equals(Book.SKYERROR)) {
					if (level==1) {
						showToast("SkyError "+message);
					}
				}
			}
		};
		this.registerReceiver(this.skyReceiver, theFilter);
	}
	
	private void unregisterSkyReceiver() {
		try {
			if(skyReceiver != null)
				this.unregisterReceiver(skyReceiver);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void lockRotation() {
		this.rv.setRotationLocked(true);
	}
	
	private void unlockRotation() {
		if (this.isRotationLocked) {
			this.rv.setRotationLocked(true);
		}else {
			this.rv.setRotationLocked(false);
		}
	}
	
	private void rotationPressed() {
		isRotationLocked = !isRotationLocked;
		if (isRotationLocked) {
			rv.setRotationLocked(true);
			
		}else {
			rv.setRotationLocked(false);
		}
		changeRotationButton();
	}
	
	private void changeRotationButton() {
		Drawable icon;
		int imageId = R.drawable.rotationlocked2x;
		if (isRotationLocked) {
			imageId = R.drawable.rotationlocked2x;
		}else {
			imageId = R.drawable.rotation2x;	
		}
		int bs = ps(42);
		icon = getResources().getDrawable(imageId);
		icon.setBounds(0,0,bs,bs);
		Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, bs, bs, false);
		rotationButton.setImageBitmap(bitmapResized);
	}
	
	private void changePlayAndPauseButton() {
		Drawable icon;
		int imageId;
		if (!rv.isPlayingStarted() || rv.isPlayingPaused()) {
			imageId = R.drawable.play2x;
		}else {
			imageId = R.drawable.pause2x;	
		}
		
		int bs = ps(32);
		icon = getResources().getDrawable(imageId);
		icon.setBounds(0,0,bs,bs);
		Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, bs, bs, false);
		playAndPauseButton.setImageBitmap(bitmapResized);
	}
	
	boolean isPagesHidden = false;
	
	private void hidePages() {
		this.rotationButton.setVisibility(View.INVISIBLE);
		this.fontButton.setVisibility(View.INVISIBLE);
		this.searchButton.setVisibility(View.INVISIBLE);
		this.seekBar.setVisibility(View.INVISIBLE);
		this.pageIndexLabel.setVisibility(View.INVISIBLE);
		this.mediaBox.setVisibility(View.INVISIBLE);
		
		this.rotationButton.setVisibility(View.GONE);
		this.fontButton.setVisibility(View.GONE);
		this.searchButton.setVisibility(View.GONE);
		this.seekBar.setVisibility(View.GONE);
		this.pageIndexLabel.setVisibility(View.GONE);
		this.mediaBox.setVisibility(View.GONE);
		
		if (!this.isPortrait() && this.isDoublePagedForLandscape) {
			this.secondaryIndexLabel.setVisibility(View.INVISIBLE);
			this.secondaryIndexLabel.setVisibility(View.GONE);		
		}
		this.showListBox();
		rv.hidePages();	
		rv.setVisibility(View.INVISIBLE);
	}
	
	private void showPages() {
		this.rotationButton.setVisibility(View.VISIBLE);
		this.fontButton.setVisibility(View.VISIBLE);
		this.searchButton.setVisibility(View.VISIBLE);
		this.seekBar.setVisibility(View.VISIBLE);
		this.pageIndexLabel.setVisibility(View.VISIBLE);
		if (!this.isPortrait() && this.isDoublePagedForLandscape) {
			this.secondaryIndexLabel.setVisibility(View.VISIBLE);		
		}
		
		if (rv.isMediaOverlayAvailable() && setting.mediaOverlay) {
			this.mediaBox.setVisibility(View.VISIBLE);
		}
		this.hideListBox();
		rv.showPages();
		rv.setVisibility(View.VISIBLE);
	}
	
	boolean isFirst = true;
	private void listPressed() { 
		this.playAndPause();
		if (!isPagesHidden) {
			this.isRotationLocked = false;
			this.rotationPressed();
			this.hidePages();	        
		}else {
			this.showPages();
			new Handler().postDelayed(new Runnable() { 
	            public void run() {        		
	            	rv.repaint();
	            } 
			}, 200);
		}
		isPagesHidden = !isPagesHidden;
	}
	
	
	private String getPageText() {
		String text = "";
		int si = rv.getStartIndexInPage();
		int ei = rv.getEndIndexInPage();
		
		int max = Math.max(si, ei);
		int min = Math.min(si, ei);
		
		for (int i=min; i<=max; i++) {
			String name = rv.getNodeNameByUniqueIndex(i);
			if (name.equalsIgnoreCase("sky")) {
				String nt = rv.getNodeTextByUniqueIndex(i);
				text =nt+"\r\n";
			}
		}
		return text;		
	}
	
	private int getNumberOfPagesForChapter(int chapterIndex) {
		PagingInformation pga = rv.makePagingInformation(chapterIndex);
		PagingInformation pgi = sd.fetchPagingInformation(pga);
		if (pgi!=null) return pgi.numberOfPagesInChapter;
		else return -1;
	}
	
	private void displayNumberOfPagesForChapters() {
		for (int i=0; i<rv.getNumberOfChapters(); i++) {
			int count = this.getNumberOfPagesForChapter(i);
			Log.w("EPub","chapterIndex:"+i+" numberOfPages:"+ count);
		}
	}

	boolean isPaging = false;
	private void fontPressed() {
		this.stopPlaying();
		this.showFontBox();
	}
	
	private void searchPressed() {
		this.stopPlaying();
		this.showSearchBox();
	}
	
	public void clearHighlightsForSearchResults() {
		if (rv.areHighlighsForSearchResultsDisplayed()) {
			rv.clearHighlightsForSearchResults();
		}
	}
	
	public void gotoPageBySearchResult(SearchResult sr,int color) {
//		showToast(sr.text);
		rv.gotoPageBySearchResult(sr,color);
	}
	
	public void gotoPageBySearchResult(SearchResult sr,ArrayList<SearchResult> srs,int color) {
//		showToast(sr.text);
		rv.gotoPageBySearchResult(sr,srs,color);
	}


	private OnClickListener listener = new OnClickListener() {
		public void onClick(View arg) {
			if (arg.getId() == 8080) {
				playPrev();
			} else if (arg.getId() == 8081) {
				playAndPause();
			} else if (arg.getId() == 8082) {
				stopPlaying();		
			} else if (arg.getId() == 8083) {
				playNext();				
			} else if (arg.getId() == 8084) {
				finish();				
			} 		
			
			if (arg.getId()==3001) {
				cancelPressed();
			}else if (arg.getId()==3093) {
				// search More
				removeLastResult();
//				showToast("Search More...");
				rv.searchMore();
			}else if (arg.getId()==3094) {
				removeLastResult();
				hideSearchBox();
				// stopSearch
			}
			
			if (arg.getId()==9000) {		// homePressed
				rotationPressed();				
			}else if (arg.getId()==9001 || arg.getId()==9009) { 	// listPressed
				listPressed();		
			}else if (arg.getId()==9002) { 	// fontPressed
				fontPressed();				
			}else if (arg.getId()==9003) { 	// searchPressed
				searchPressed();
			}
			
			if (arg.getId()==6000) {
				// highlightMenuButton
				mark();
				hideMenuBox();
				showHighlightBox();
			}else if (arg.getId()==6001) {
				mark();
				hideMenuBox();
				if (!rv.isPaging()) showNoteBox();
			}	
			
			if (arg.getId()==6002) {
				// Color Chooser 
				hideHighlightBox();
				showColorBox();
			}else if (arg.getId()==6003) {
				hideHighlightBox();
				rv.deleteHighlight(currentHighlight);
			}else if (arg.getId()==6004) {
				hideHighlightBox();
				if (!rv.isPaging()) showNoteBox();
			}
			
			int color;
			if (arg.getId()==6010) {
				color = getColorByIndex(0);
				changeHighlightColor(currentHighlight,color);
			}else if (arg.getId()==6011) {
				color = getColorByIndex(1);
				changeHighlightColor(currentHighlight,color);
			}else if (arg.getId()==6012) {
				color = getColorByIndex(2);
				changeHighlightColor(currentHighlight,color);
			}else if (arg.getId()==6013) {
				color = getColorByIndex(3);
				changeHighlightColor(currentHighlight,color);
			} 
			
			if (arg.getId()==9999) {
				hideOutsideButton();
				hideBoxes();
			}
			
			// click on the search result
			if (arg.getId()>=100000 && arg.getId()<200000) {
				int index = arg.getId()-100000;
				makeFullScreen();
				hideSearchBox();
				SearchResult sr = searchResults.get(index);
				gotoPageBySearchResult(sr,Color.GREEN);
//				gotoPageBySearchResult(sr,searchResults,Color.GREEN);
			}
			
			// fonts related
			if (arg.getId()==5000) {
				// decrease button
				decreaseFont();
			}else if (arg.getId()==5001) {
				// increase button
				increaseFont();
			}else if  (arg.getId()>=5100 && arg.getId()<5500) {
				// one of fontButtons is clicked.
				fontSelected(arg.getId()-5100);
			}
			
			// line space
			if (arg.getId()==4000) {
				// decrease button
				decreaseLineSpace();
			}else if (arg.getId()==4001) {
				// increase button
				increaseLineSpace();
			}
			
			// theme related
			if (arg.getId()>=7000 && arg.getId()<7100) {				
				themeIndex = arg.getId()-7000;
				setting.theme = themeIndex;
				checkSettings();
//				changeTheme2(themeIndex);
				changeTheme(themeIndex);
			}
			
			// list processing
			if (arg.getId()==2700) {
				checkListButton(0);				
			}else if (arg.getId()==2701) {
				checkListButton(1);
			}else if (arg.getId()==2702) {
				checkListButton(2);
			} 
			
			// list processing
			if (arg.getId()>=200000 && arg.getId()<300000) {  // click on the one of contents
				
			}else if (arg.getId()>=300000 && arg.getId()<400000) { // click on the bookmark of bookmark list
				
			}else if (arg.getId()>=400000 && arg.getId()<500000) { // click on the highlight of highlight list
				
			}
		}
	};
	
	int getRealFontSize(int fontSizeIndex) {
	    int rs = 0;
	    switch (fontSizeIndex) {
	        case 0:
	            rs = 24;
	            break;
	        case 1:
	            rs = 27;
	            break;
	        case 2:
	            rs = 30;
	            break;
	        case 3:
	            rs = 34;
	            break;
	        case 4:
	            rs = 37;
	            break;
	        default:
	            rs = 27;
	    }
	    if (this.getOSVersion()>=19) {
	    	rs = (int)((double)rs*0.75f);
	    }

	    if (Build.DEVICE.contains("maguro")) {
	    	rs = (int)((double)rs*0.75f);
	    }

	    return rs;
	}
	
	public void checkSettings() {
		if (this.setting.fontSize==0) {
			decreaseButton.setTextColor(Color.LTGRAY);			
		}else {
			decreaseButton.setTextColor(Color.BLACK);
		}
		
		if (this.setting.fontSize==4) {
			increaseButton.setTextColor(Color.LTGRAY);			
		}else {
			increaseButton.setTextColor(Color.BLACK);		
		}
		
		
		increaseLineSpaceButton.setEnabled(true);
		decreaseLineSpaceButton.setEnabled(true);
		increaseLineSpaceButton.setColorFilter(Color.BLACK);
		decreaseLineSpaceButton.setColorFilter(Color.BLACK);
		if (this.setting.lineSpacing==4) {
			increaseLineSpaceButton.setEnabled(false);	
			increaseLineSpaceButton.setColorFilter(Color.LTGRAY);
		}
		
		if (this.setting.lineSpacing==0) {
			decreaseLineSpaceButton.setEnabled(false);
			decreaseLineSpaceButton.setColorFilter(Color.LTGRAY);
		}
		
		
		int fontIndex = this.getFontIndex(setting.fontName);
		for (int i=0; i<fontListView.getChildCount();i++) {
			Button button = (Button)fontListView.getChildAt(i);		
			button.setTextColor(Color.BLACK);
		
		}
		for (int i=0; i<fontListView.getChildCount();i++) {
			Button button = (Button)fontListView.getChildAt(i);
			if (button.getId()==(fontIndex+5100)) {
				button.setTextColor(Color.BLUE);
			}
		}
		
		for (int i=0; i<themesView.getChildCount();i++) {
			Button button = (Button)themesView.getChildAt(i);
			Typeface tf = null;
			int size = 13;
			if (button.getId()==(themeIndex+7000)) {
				tf = this.getTypeface(setting.fontName,Typeface.BOLD);
				size = 18;
			}else {
				tf = this.getTypeface(setting.fontName,Typeface.NORMAL);
			}
			button.setTypeface(tf);
			button.setTextSize(size);
		}
	}

	
	public void decreaseFont() {
	    if (this.setting.fontSize!=0) {
	        this.setting.fontSize--;
	        rv.changeFont(setting.fontName,this.getRealFontSize(setting.fontSize));	        
	    }
	    this.checkSettings();
	}
	
	public void increaseFont() {
	    if (this.setting.fontSize!=4) {
	        this.setting.fontSize++;
	        rv.changeFont(setting.fontName,this.getRealFontSize(setting.fontSize));
	    }
	    this.checkSettings();
	}
	
	public int getRealLineSpace(int lineSpaceIndex) {
		int rs = -1;
		if (lineSpaceIndex==0) {
			rs = 125;
		}else if (lineSpaceIndex==1) {
			rs = 150;
		}else if (lineSpaceIndex==2) {
			rs = 165;
		}else if (lineSpaceIndex==3) {
			rs = 180;
		}else if (lineSpaceIndex==4) {
			rs = 200;
		}else {
			this.setting.lineSpacing = 1;
			rs = 150;
		}  
		return rs;
	}
	
	public void decreaseLineSpace() {
		if (this.setting.lineSpacing!=0) {
			this.setting.lineSpacing--;
			this.checkSettings();
			rv.changeLineSpacing(this.getRealLineSpace(setting.lineSpacing));				        
		}		
	}

	public void increaseLineSpace() {
		if (this.setting.lineSpacing!=4) {
			this.setting.lineSpacing++;
			this.checkSettings();
			rv.changeLineSpacing(this.getRealLineSpace(setting.lineSpacing));				        
		}		
	}

	
	public void fontSelected(int index) {
		CustomFont customFont = this.getCustomFont(index);
		String name = customFont.getFullName();
		if (!setting.fontName.equalsIgnoreCase(name)) {
			setting.fontName = name;
			checkSettings();
			rv.changeFont(setting.fontName,this.getRealFontSize(setting.fontSize));
		}
	}
	
	public void changeHighlightColor(Highlight highlight,int color) {
		currentHighlight.color = color;
		rv.changeHighlightColor(currentHighlight, color);
		this.hideColorBox();
	}
	
	private void mark() {
		rv.markSelection(currentColor,"");
//		rv.markSelection(Color.TRANSPARENT,"");
	}

	private void showToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	void hideBoxes() {
		this.hideColorBox();
		this.hideHighlightBox();
		this.hideMenuBox();
		this.hideNoteBox();
		this.hideSearchBox();
		this.hideFontBox();
		this.hideListBox();
		if (isPagesHidden) this.showPages();
	}
	
	public void reportMemory() {
		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		
//		Log.v("EPub", "maxMemory:" + Long.toString(maxMemory));
		
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass();
//		Log.v("EPub", "memoryClass:" + Integer.toString(memoryClass));	
		
		ActivityManager activityManager =  (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
//		Log.i("EPub", "AvailMem" + memoryInfo.availMem);
		
		long memoryAvail = memoryInfo.availMem;
		long memoryAlloc = maxMemory - memoryAvail;
		
		String message = String.format("Max :%d Avail:%d", maxMemory,memoryAvail);
		showToast(message);		
	}
	
	public void test02() {
		String str = "Capitulo%205%20EL%20PAPEL%20DE%20LOS%20CORTICOIDES.xhtml";
		String res = null;
		try {
			res = URLDecoder.decode(str, "UTF-8" );
		}catch (Exception e) {}
//		try {
//			res = java.net.URLEncoder.encode(str, "UTF-8");
//		}catch(Exception e) {}
		showToast(res);		
	}
	
	class ClickDelegate implements ClickListener {
		@Override
		public void onVideoClicked(int x, int y, String src) {
			// TODO Auto-generated method stub
			Log.w("EPub","Video Clicked at "+x+":"+y+" src:"+src);
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(src));
//			startActivity(browserIntent);
		}		
		
		public void onClick(int x,int y) {
			Log.w("EPub","click detected");
			if (isBoxesShown) {
				hideBoxes();				
			}else {
				toggleControls();	
			}
		}
		
		public void onImageClicked(int x,int y,String src) {
			showToast("Image Clicked at "+x+":"+y+" src:"+src);
			Log.w("EPub","Click on Image Detected at "+x+":"+y+" src:"+src);
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(src));
			startActivity(browserIntent);
		}
		
		public void onLinkClicked(int x,int y, String href) {
			showToast("Link Clicked at "+x+":"+y+" href:"+href);
			Log.w("EPub","Link Clicked at "+x+":"+y+" href:"+href);
		}

		@Override
		public boolean ignoreLink(int x,int y,String href) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLinkForLinearNoClicked(int x, int y, String href) {
			// TODO Auto-generated method stub
			Log.w("EPub","Link Clicked at "+x+":"+y+" href:"+href);	
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(href));
//			startActivity(browserIntent);
		}

		@Override
		public void onIFrameClicked(int x, int y, String src) {
			// TODO Auto-generated method stub
			Log.w("EPub","IFrame Clicked at "+x+":"+y+" src:"+src);
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(src));
//			startActivity(browserIntent);
		}

		

		@Override
		public void onAudioClicked(int x, int y, String src) {
			// TODO Auto-generated method stub
			Log.w("EPub","Audio Clicked at "+x+":"+y+" src:"+src);			
		}		
	}
	
	class StateDelegate implements StateListener {
		public void onStateChanged(State state) {
			if (state==State.LOADING) {
				showIndicator();
			}else if (state==State.ROTATING) {
//				showToast("Rotating...");
			}else if (state==State.BUSY) {
				showIndicator();
//				showToast("Busy...");				
			}else if (state==State.NORMAL) {
//				showToast("Normal...");
				hideIndicator();
//				if (dialog!=null) dialog.dismiss();
//				dialog = null;
			}
		}		
	}
	
	int getColorByIndex(int colorIndex) {
		int color;
		if (colorIndex==0) {
			color = Color.argb(255, 238, 230, 142);	// YELLOW
		}else if (colorIndex==1) {
			color = Color.argb(255, 218, 244, 160); // GREEN
		}else if (colorIndex==2) {
			color = Color.argb(255, 172, 201, 246); // BLUE
		}else if (colorIndex==3) {
			color = Color.argb(255, 249, 182, 214); // RED (PINK)
		}else {
			color = Color.argb(255, 249, 182, 214);
		}
		return color;
	}

	
	int getIndexByColor(int color) {
		int index;
		if (color==Color.argb(255, 238, 230, 142)) {
			index = 0;
		}else if (color==Color.argb(255, 218, 244, 160)) {
			index = 1;
		}else if (color==Color.argb(255, 172, 201, 246)) {
			index = 2;
		}else if (color== Color.argb(255, 249, 182, 214)) {
			index = 3;
		}else {
			index = 0;
		}		
		return index;		
	}
	
	BitmapDrawable getMarkerForColor(int color) {
		Drawable mr;
		int index = getIndexByColor(color);
		int di = 0;
		switch(index) {
		case 0:di=R.drawable.markeryellow;break;
		case 1:di=R.drawable.markergreen;break;
		case 2:di=R.drawable.markerblue;break;
		case 3:di=R.drawable.markerred;break;
		default :di=R.drawable.markeryellow;break;
		}
		mr = getResources().getDrawable(di);
		BitmapDrawable marker = (BitmapDrawable)mr;
		return marker;			
	}
	
	class KeyDelegate implements KeyListener {
		@Override
		public String getKeyForEncryptedData(String uuidForContent, String contentName, String uuidForEpub) {
			// TODO Auto-generated method stub
			String key = app.keyManager.getKey(uuidForContent,uuidForEpub);
			return key;
		}

		@Override
		public Book getBook() {
			// TODO Auto-generated method stub
			return rv.getBook();
		}		
	}

	//	"function changePColor() {" +
//	"	var elements = document.getElementsByTagName('p');" +
//	"	for (var i=0; i<elements.length; i++) {" +
//	"		elements[i].style.color = '#FF0000';" +
//	"	}"+
//	"}"+
//	"changePColor();";

	
	class ScriptDelegate implements ScriptListener {
		@Override
		public String getScriptForChapter(int chapterIndex) {
			// TODO Auto-generated method stub
			String customScript = null;
//			customScript = "function ignoreBookStyle() { document.styleSheets[0].disabled = true; } ignoreBookStyle();";
			/*
			customScript = "" +
					"function preventPreloadVideo() {" +
						"var videos = document.getElementsByTagName('video');" +
						"for (var i=0; i<videos.length; i++) {" +
							"videos[i].preload = 'none';" +
						"}" +
					"}"+
					"preventPreloadVideo();"
					+"function beep() {"+
						    "var sound = new Audio('data:audio/wav;base64,//uQRAAAAWMSLwUIYAAsYkXgoQwAEaYLWfkWgAI0wWs/ItAAAGDgYtAgAyN+QWaAAihwMWm4G8QQRDiMcCBcH3Cc+CDv/7xA4Tvh9Rz/y8QADBwMWgQAZG/ILNAARQ4GLTcDeIIIhxGOBAuD7hOfBB3/94gcJ3w+o5/5eIAIAAAVwWgQAVQ2ORaIQwEMAJiDg95G4nQL7mQVWI6GwRcfsZAcsKkJvxgxEjzFUgfHoSQ9Qq7KNwqHwuB13MA4a1q/DmBrHgPcmjiGoh//EwC5nGPEmS4RcfkVKOhJf+WOgoxJclFz3kgn//dBA+ya1GhurNn8zb//9NNutNuhz31f////9vt///z+IdAEAAAK4LQIAKobHItEIYCGAExBwe8jcToF9zIKrEdDYIuP2MgOWFSE34wYiR5iqQPj0JIeoVdlG4VD4XA67mAcNa1fhzA1jwHuTRxDUQ//iYBczjHiTJcIuPyKlHQkv/LHQUYkuSi57yQT//uggfZNajQ3Vmz+Zt//+mm3Wm3Q576v////+32///5/EOgAAADVghQAAAAA//uQZAUAB1WI0PZugAAAAAoQwAAAEk3nRd2qAAAAACiDgAAAAAAABCqEEQRLCgwpBGMlJkIz8jKhGvj4k6jzRnqasNKIeoh5gI7BJaC1A1AoNBjJgbyApVS4IDlZgDU5WUAxEKDNmmALHzZp0Fkz1FMTmGFl1FMEyodIavcCAUHDWrKAIA4aa2oCgILEBupZgHvAhEBcZ6joQBxS76AgccrFlczBvKLC0QI2cBoCFvfTDAo7eoOQInqDPBtvrDEZBNYN5xwNwxQRfw8ZQ5wQVLvO8OYU+mHvFLlDh05Mdg7BT6YrRPpCBznMB2r//xKJjyyOh+cImr2/4doscwD6neZjuZR4AgAABYAAAABy1xcdQtxYBYYZdifkUDgzzXaXn98Z0oi9ILU5mBjFANmRwlVJ3/6jYDAmxaiDG3/6xjQQCCKkRb/6kg/wW+kSJ5//rLobkLSiKmqP/0ikJuDaSaSf/6JiLYLEYnW/+kXg1WRVJL/9EmQ1YZIsv/6Qzwy5qk7/+tEU0nkls3/zIUMPKNX/6yZLf+kFgAfgGyLFAUwY//uQZAUABcd5UiNPVXAAAApAAAAAE0VZQKw9ISAAACgAAAAAVQIygIElVrFkBS+Jhi+EAuu+lKAkYUEIsmEAEoMeDmCETMvfSHTGkF5RWH7kz/ESHWPAq/kcCRhqBtMdokPdM7vil7RG98A2sc7zO6ZvTdM7pmOUAZTnJW+NXxqmd41dqJ6mLTXxrPpnV8avaIf5SvL7pndPvPpndJR9Kuu8fePvuiuhorgWjp7Mf/PRjxcFCPDkW31srioCExivv9lcwKEaHsf/7ow2Fl1T/9RkXgEhYElAoCLFtMArxwivDJJ+bR1HTKJdlEoTELCIqgEwVGSQ+hIm0NbK8WXcTEI0UPoa2NbG4y2K00JEWbZavJXkYaqo9CRHS55FcZTjKEk3NKoCYUnSQ0rWxrZbFKbKIhOKPZe1cJKzZSaQrIyULHDZmV5K4xySsDRKWOruanGtjLJXFEmwaIbDLX0hIPBUQPVFVkQkDoUNfSoDgQGKPekoxeGzA4DUvnn4bxzcZrtJyipKfPNy5w+9lnXwgqsiyHNeSVpemw4bWb9psYeq//uQZBoABQt4yMVxYAIAAAkQoAAAHvYpL5m6AAgAACXDAAAAD59jblTirQe9upFsmZbpMudy7Lz1X1DYsxOOSWpfPqNX2WqktK0DMvuGwlbNj44TleLPQ+Gsfb+GOWOKJoIrWb3cIMeeON6lz2umTqMXV8Mj30yWPpjoSa9ujK8SyeJP5y5mOW1D6hvLepeveEAEDo0mgCRClOEgANv3B9a6fikgUSu/DmAMATrGx7nng5p5iimPNZsfQLYB2sDLIkzRKZOHGAaUyDcpFBSLG9MCQALgAIgQs2YunOszLSAyQYPVC2YdGGeHD2dTdJk1pAHGAWDjnkcLKFymS3RQZTInzySoBwMG0QueC3gMsCEYxUqlrcxK6k1LQQcsmyYeQPdC2YfuGPASCBkcVMQQqpVJshui1tkXQJQV0OXGAZMXSOEEBRirXbVRQW7ugq7IM7rPWSZyDlM3IuNEkxzCOJ0ny2ThNkyRai1b6ev//3dzNGzNb//4uAvHT5sURcZCFcuKLhOFs8mLAAEAt4UWAAIABAAAAAB4qbHo0tIjVkUU//uQZAwABfSFz3ZqQAAAAAngwAAAE1HjMp2qAAAAACZDgAAAD5UkTE1UgZEUExqYynN1qZvqIOREEFmBcJQkwdxiFtw0qEOkGYfRDifBui9MQg4QAHAqWtAWHoCxu1Yf4VfWLPIM2mHDFsbQEVGwyqQoQcwnfHeIkNt9YnkiaS1oizycqJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=');"  
						    +"sound.play(); }";
			*/
/*
			customScript = "function beep() {"+
				    "var sound = new Audio('data:audio/wav;base64,//uQRAAAAWMSLwUIYAAsYkXgoQwAEaYLWfkWgAI0wWs/ItAAAGDgYtAgAyN+QWaAAihwMWm4G8QQRDiMcCBcH3Cc+CDv/7xA4Tvh9Rz/y8QADBwMWgQAZG/ILNAARQ4GLTcDeIIIhxGOBAuD7hOfBB3/94gcJ3w+o5/5eIAIAAAVwWgQAVQ2ORaIQwEMAJiDg95G4nQL7mQVWI6GwRcfsZAcsKkJvxgxEjzFUgfHoSQ9Qq7KNwqHwuB13MA4a1q/DmBrHgPcmjiGoh//EwC5nGPEmS4RcfkVKOhJf+WOgoxJclFz3kgn//dBA+ya1GhurNn8zb//9NNutNuhz31f////9vt///z+IdAEAAAK4LQIAKobHItEIYCGAExBwe8jcToF9zIKrEdDYIuP2MgOWFSE34wYiR5iqQPj0JIeoVdlG4VD4XA67mAcNa1fhzA1jwHuTRxDUQ//iYBczjHiTJcIuPyKlHQkv/LHQUYkuSi57yQT//uggfZNajQ3Vmz+Zt//+mm3Wm3Q576v////+32///5/EOgAAADVghQAAAAA//uQZAUAB1WI0PZugAAAAAoQwAAAEk3nRd2qAAAAACiDgAAAAAAABCqEEQRLCgwpBGMlJkIz8jKhGvj4k6jzRnqasNKIeoh5gI7BJaC1A1AoNBjJgbyApVS4IDlZgDU5WUAxEKDNmmALHzZp0Fkz1FMTmGFl1FMEyodIavcCAUHDWrKAIA4aa2oCgILEBupZgHvAhEBcZ6joQBxS76AgccrFlczBvKLC0QI2cBoCFvfTDAo7eoOQInqDPBtvrDEZBNYN5xwNwxQRfw8ZQ5wQVLvO8OYU+mHvFLlDh05Mdg7BT6YrRPpCBznMB2r//xKJjyyOh+cImr2/4doscwD6neZjuZR4AgAABYAAAABy1xcdQtxYBYYZdifkUDgzzXaXn98Z0oi9ILU5mBjFANmRwlVJ3/6jYDAmxaiDG3/6xjQQCCKkRb/6kg/wW+kSJ5//rLobkLSiKmqP/0ikJuDaSaSf/6JiLYLEYnW/+kXg1WRVJL/9EmQ1YZIsv/6Qzwy5qk7/+tEU0nkls3/zIUMPKNX/6yZLf+kFgAfgGyLFAUwY//uQZAUABcd5UiNPVXAAAApAAAAAE0VZQKw9ISAAACgAAAAAVQIygIElVrFkBS+Jhi+EAuu+lKAkYUEIsmEAEoMeDmCETMvfSHTGkF5RWH7kz/ESHWPAq/kcCRhqBtMdokPdM7vil7RG98A2sc7zO6ZvTdM7pmOUAZTnJW+NXxqmd41dqJ6mLTXxrPpnV8avaIf5SvL7pndPvPpndJR9Kuu8fePvuiuhorgWjp7Mf/PRjxcFCPDkW31srioCExivv9lcwKEaHsf/7ow2Fl1T/9RkXgEhYElAoCLFtMArxwivDJJ+bR1HTKJdlEoTELCIqgEwVGSQ+hIm0NbK8WXcTEI0UPoa2NbG4y2K00JEWbZavJXkYaqo9CRHS55FcZTjKEk3NKoCYUnSQ0rWxrZbFKbKIhOKPZe1cJKzZSaQrIyULHDZmV5K4xySsDRKWOruanGtjLJXFEmwaIbDLX0hIPBUQPVFVkQkDoUNfSoDgQGKPekoxeGzA4DUvnn4bxzcZrtJyipKfPNy5w+9lnXwgqsiyHNeSVpemw4bWb9psYeq//uQZBoABQt4yMVxYAIAAAkQoAAAHvYpL5m6AAgAACXDAAAAD59jblTirQe9upFsmZbpMudy7Lz1X1DYsxOOSWpfPqNX2WqktK0DMvuGwlbNj44TleLPQ+Gsfb+GOWOKJoIrWb3cIMeeON6lz2umTqMXV8Mj30yWPpjoSa9ujK8SyeJP5y5mOW1D6hvLepeveEAEDo0mgCRClOEgANv3B9a6fikgUSu/DmAMATrGx7nng5p5iimPNZsfQLYB2sDLIkzRKZOHGAaUyDcpFBSLG9MCQALgAIgQs2YunOszLSAyQYPVC2YdGGeHD2dTdJk1pAHGAWDjnkcLKFymS3RQZTInzySoBwMG0QueC3gMsCEYxUqlrcxK6k1LQQcsmyYeQPdC2YfuGPASCBkcVMQQqpVJshui1tkXQJQV0OXGAZMXSOEEBRirXbVRQW7ugq7IM7rPWSZyDlM3IuNEkxzCOJ0ny2ThNkyRai1b6ev//3dzNGzNb//4uAvHT5sURcZCFcuKLhOFs8mLAAEAt4UWAAIABAAAAAB4qbHo0tIjVkUU//uQZAwABfSFz3ZqQAAAAAngwAAAE1HjMp2qAAAAACZDgAAAD5UkTE1UgZEUExqYynN1qZvqIOREEFmBcJQkwdxiFtw0qEOkGYfRDifBui9MQg4QAHAqWtAWHoCxu1Yf4VfWLPIM2mHDFsbQEVGwyqQoQcwnfHeIkNt9YnkiaS1oizycqJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=');"  
				    +"sound.play(); } beep();" ;
*/
//			customScript = "function changeColor() { this.getBody().style.color = '#FF0000';}";
			if (rv.isRTL()) {
				customScript = "function ignoreBookStyle() { document.styleSheets[0].disabled = true; } ignoreBookStyle();";
			}
			return customScript;
		}

		@Override
		public String getStyleForChapter(int chapterIndex) {
			// TODO Auto-generated method stub
			String customCSS = null;
//			customCSS = "p{ color: #ff0000; }";
			return customCSS;
		}		
	}
	
	class VideoDelegate implements VideoListener {
		@Override
		public void onVideoEntersFullScreen(View view) {
			// TODO Auto-generated method stub
			videoView = view;
			ePubView.addView(videoView);			
		}

		@Override
		public void onVideoExitsFromFullScreen() {
			// TODO Auto-generated method stub
			videoView.setVisibility(View.GONE);
			ePubView.removeView(videoView);			
		}
	}
	
	void dumpHighlight(String message,Highlight highlight) {
		Log.w("EPub",message+" "+highlight.startIndex+" "+highlight.startOffset+" "+highlight.endIndex+" "+highlight.endOffset);
	}

	class HighlightDelegate implements HighlightListener {
		public void onHighlightDeleted(Highlight highlight) {
			dumpHighlight("onHighlightDeleted",highlight);
			sd.deleteHighlight(highlight);
		}

		public void onHighlightInserted(Highlight highlight) {
			dumpHighlight("onHighlightInserted",highlight);
//			showToast(highlight.text);
			
//			Highlight modified = new Highlight();
//			modified.startIndex = 125;
//			modified.startOffset = 175;
//			modified.endIndex = 125; // 125
//			modified.endOffset =  182;
//			modified.chapterIndex = highlight.chapterIndex;
//			modified.code = highlight.code;
//			modified.color = highlight.color;
//			modified.bookCode = highlight.bookCode;
//			sd.insertHighlight(modified);
			
			sd.insertHighlight(highlight);
			showToast("startIndex "+highlight.startIndex+" startOffset "+highlight.startOffset+" endIndex "+highlight.endIndex+" endOffset "+highlight.endOffset+" text "+highlight.text);
		}

		public void onHighlightHit(Highlight highlight, int x, int y,Rect startRect, Rect endRect) {
//			debug("onHighlightHit at "+highlight.text);
			dumpHighlight("onHighlgihtHit",highlight);
			currentHighlight = highlight;
			currentColor = currentHighlight.color;
			showHighlightBox(startRect,endRect);			
		}

		public Highlights getHighlightsForChapter(int chapterIndex) {
//			Log.w("EPub","getHighlightsForChapter");
			return sd.fetchHighlights(bookCode, chapterIndex);
		}

		@Override
		public void onHighlightUpdated(Highlight highlight) {
			dumpHighlight("onHighlightUpdated",highlight);
			sd.updateHighlight(highlight);			
		}

		@Override
		public Bitmap getNoteIconBitmapForColor(int color,int style) {
			Drawable icon;
			Bitmap iconBitmap;
			int index = getIndexByColor(color);
			if (index==0) {
				icon = getResources().getDrawable(R.drawable.yellowmemo2x);
			}else if (index==1) {
				icon = getResources().getDrawable(R.drawable.greenmemo2x);
			}else if (index==2) {
				icon = getResources().getDrawable(R.drawable.bluememo2x);
			}else if (index==3) {
				icon = getResources().getDrawable(R.drawable.redmemo2x);
			}else {
				icon = getResources().getDrawable(R.drawable.yellowmemo2x);
			}
			iconBitmap = ((BitmapDrawable)icon).getBitmap();
			return iconBitmap;
		}

		@Override
		public void onNoteIconHit(Highlight highlight) {
			if (isBoxesShown) {
				hideBoxes();
				return;
			}
			currentHighlight = highlight;
			currentColor = highlight.color;
			if (!rv.isPaging()) showNoteBox();			
		}

		@Override
		public Rect getNoteIconRect(int color,int style) {
			// Rect should consists of offset X, offset Y, the width of icon and the height of icon
			// if multiple notes exist in the same line, 
			//   offset X and offset Y can be useful to avoid the overlapping the icons
			Rect rect = new Rect(0,0,ps(32),ps(32));
			return rect;
		}		

		@Override
		public void onDrawHighlightRect(Canvas canvas, Highlight highlight,
				Rect highlightRect) {
			// TODO Auto-generated method stub
			Log.w("EPub","onDrawHighlightRect is called for Rect "+highlightRect.left+":"+highlightRect.top+":"+highlightRect.right+":"+highlightRect.bottom);
			if (!highlight.isTemporary) {
				BitmapDrawable marker = getMarkerForColor(highlight.color);
				marker.setBounds(highlightRect);
				marker.draw(canvas);
			}else {
				BitmapDrawable marker = getMarkerForColor(highlight.color);
				marker.setBounds(new Rect(highlightRect.left,highlightRect.bottom-40,highlightRect.right,highlightRect.bottom));
				marker.draw(canvas);
			}
		}

		@Override
		public void onDrawCaret(Canvas canvas, Caret caret) {
			// TODO Auto-generated method stub
			if (caret==null) return;
			Paint paint = new Paint();
			paint.setColor(rv.selectorColor);
			paint.setStrokeWidth(2);	
			
			int cx = 0;
			if (!rv.isRTL()) {
				if (caret.isFirst) cx = caret.x;
				else cx = caret.x+caret.width;
			}else {
				if (caret.isFirst) cx = caret.x+caret.width;
				else cx = caret.x;			
			}
			
			canvas.drawLine((float)cx,(float)( caret.y-caret.height*.7f),(float)cx,(float)(caret.y+caret.height*.7f),paint);
			
			paint.setColor(Color.LTGRAY);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle((float)cx,(float)(caret.y-caret.height*.7f), 7.0f,paint);
			
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle((float)cx,(float)(caret.y-caret.height*.7f), 6.0f,paint);
			
			if (caret.isFirst) paint.setColor(Color.RED);
			else 	paint.setColor(Color.BLACK);
			
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle((float)cx,(float)(caret.y-caret.height*.7f), 5.0f,paint);
			
			paint.setColor(Color.LTGRAY);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle((float)cx,(float)(caret.y+caret.height*.7f), 7.0f,paint);
			
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle((float)cx,(float)(caret.y+caret.height*.7f), 6.0f,paint);
			
			if (caret.isFirst) paint.setColor(Color.BLACK);
			else 	paint.setColor(Color.RED);
			
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle((float)cx,(float)(caret.y+caret.height*.7f), 5.0f,paint);			
		}

		
	}
	
	public ColorFilter makeColorFilter(int color) {
		int red   = (color & 0xFF0000) / 0xFFFF;
		int green = (color & 0xFF00) / 0xFF;
		int blue  = color & 0xFF;

		float[] matrix = { 0, 0, 0, 0, red,
		                   0, 0, 0, 0, green,
		                   0, 0, 0, 0, blue,
		                   0, 0, 0, 1, 0 };

		ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
		return colorFilter;
	}

	private boolean colorMatched(int pixel,int targetColor,int thresHold) {
		int r = Color.red(targetColor);
        int g = Color.green(targetColor);
        int b = Color.blue(targetColor);
		
	    return 	Math.abs(Color.red(pixel) - r) < thresHold &&
	    		Math.abs(Color.green(pixel) - g) < thresHold &&
	    		Math.abs(Color.blue(pixel) - b) < thresHold;
	}
	
	private Drawable changeDrawableColor(Drawable drawable,int fromColor, int color) {
	    Bitmap src = ((BitmapDrawable)drawable).getBitmap();
	    Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
	    for(int x = 0;x < bitmap.getWidth();x++) {
	        for(int y = 0;y < bitmap.getHeight();y++) {	
	        	if (colorMatched(bitmap.getPixel(x, y),fromColor,10)) {
	                bitmap.setPixel(x, y, color);
	        	}
	        }
	    }
	    return new BitmapDrawable(bitmap);
	}
	
	class BookmarkDelegate implements BookmarkListener {
		@Override
		public void onBookmarkHit(PageInformation pi, boolean isBookmarked) {
			sd.toggleBookmark(pi);
			rv.repaintAll();
		}

		@Override
		public Rect getBookmarkRect(boolean isBookmarked) {
			if (isBookmarked) {
				return bookmarkedRect;				
			}else {
				return bookmarkRect;
			}
		}

		@Override
		public Bitmap getBookmarkBitmap(boolean isBookmarked) {
			debug("getBookmarkBitmap");
			Drawable markIcon = null;
			Bitmap iconBitmap = null;
			Theme theme = getCurrentTheme();
			try {
				if (isBookmarked) {
					markIcon = getResources().getDrawable(R.drawable.bookmarked2x);
				}else {
					markIcon = getResources().getDrawable(theme.bookmarkId);					
				}			
				if (markIcon!=null) {	
					markIcon = changeDrawableColor(markIcon,Color.LTGRAY,theme.controlColor);
//					markIcon.setColorFilter(makeColorFilter(theme.controlColor));
					iconBitmap = ((BitmapDrawable)markIcon).getBitmap();					
				}
			}catch(Exception e) {
				return null;
			}
			return iconBitmap;
		}

		@Override
		public boolean isBookmarked(PageInformation pi) {
			return sd.isBookmarked(pi);
		}		
	}
	
	public void disableControlBeforePagination() {
		showPagingView();
		
		int pi = rv.getPageIndexInChapter();
		int tn = rv.getNumberOfPagesInChapter();
		setIndexLabelsText(-1,-1); // do not display.
		
		Theme theme = this.getCurrentTheme();
		rotationButton.setColorFilter(theme.controlHighlightColor);
		searchButton.setColorFilter(theme.controlHighlightColor);
		fontButton.setColorFilter(theme.controlHighlightColor);	
		rotationButton.setEnabled(false);
		searchButton.setEnabled(false);
		fontButton.setEnabled(false);
		seekBar.setVisibility(View.INVISIBLE);
	}
	
	public void enableControlAfterPagination() {
		hidePagingView();
		int pi = rv.getPageIndexInBook();
		int tn = rv.getNumberOfPagesInBook();
		setIndexLabelsText(pi,tn);
		
		Theme theme = this.getCurrentTheme();
		rotationButton.setColorFilter(theme.controlColor);
		searchButton.setColorFilter(theme.controlColor);
		fontButton.setColorFilter(theme.controlColor);
		rotationButton.setEnabled(true);
		searchButton.setEnabled(true);
		fontButton.setEnabled(true);
		seekBar.setVisibility(View.VISIBLE);
		if (rv.isGlobalPagination()) {
			seekBar.setMax(rv.getNumberOfPagesInBook()-1);
			seekBar.setProgress(rv.getPageIndexInBook());
		}
		
	}
	
	class PagingDelegate implements PagingListener {
		public void onPagingStarted(int bookCode) {
			hideBoxes();
			disableControlBeforePagination();
		}

		public void onPaged(PagingInformation pagingInformation) {
			int ci = pagingInformation.chapterIndex;
			int cn = rv.getNumberOfChapters();
			int value = (int)((float)ci*100/(float)cn);
			changePagingView(value);			
			sd.insertPagingInformation(pagingInformation);
		}

		public void onPagingFinished(int bookCode) {
			enableControlAfterPagination();
		}

		public int getNumberOfPagesForPagingInformation(PagingInformation pagingInformation) {
			PagingInformation pgi = sd.fetchPagingInformation(pagingInformation);
			if (pgi==null) return 0;
			else return pgi.numberOfPagesInChapter;			
		}
	}
	
	private void processPageMoved(PageInformation pi) {	
		currentPageInformation = pi;
		double ppb = pi.pagePositionInBook;
		double pageDelta = ((1.0f/pi.numberOfChaptersInBook)/pi.numberOfPagesInChapter);
		int progress = (int)((double)999.0f * (ppb));				
		int pib = pi.pageIndexInBook;
		
		if (rv.isGlobalPagination()) {
			if (!rv.isPaging()) {
				seekBar.setMax(pi.numberOfPagesInBook-1);
				seekBar.setProgress(pib);
				int cgpi = rv.getPageIndexInBookByPagePositionInBook(pi.pagePositionInBook);				
				setIndexLabelsText(pi.pageIndexInBook,pi.numberOfPagesInBook);				
				debug("gpi "+pi.pageIndexInBook+" cgpi "+cgpi);
			}else {
				setIndexLabelsText(-1,-1); // do not display
			}				
		}else {
			seekBar.setProgress(progress);
			setIndexLabelsText(pi.pageIndex,pi.numberOfPagesInChapter);								
		}			
		pagePositionInBook = (float)pi.pagePositionInBook;
		
		if (!rv.isTTSEnabled()) {
			if (autoStartPlayingWhenNewPagesLoaded && !isPageTurnedByMediaOverlay) {
				if (isAutoPlaying) {
					rv.playFirstParallelInPage();		            
				}
			}
		}else {	// TTS
			if (autoStartPlayingWhenNewPagesLoaded) {
				if (isAutoPlaying) {
					rv.playFirstParallelInPage();		            
				}
			}
		}
		isPageTurnedByMediaOverlay = false;		
//		debug(pi.pageDescription);
//		debug("firstCharacterOffsetInPage "+pi.firstCharacterOffsetInPage+" textLengthInPage "+pi.textLengthInPage);
//		debug("coverURL "+rv.getCoverURL());
	}

	class PageMovedDelegate implements PageMovedListener {
		public void onPageMoved(PageInformation pi) {
			processPageMoved(pi);
		}
		
		public void onChapterLoaded(int chapterIndex) {
			if (rv.isMediaOverlayAvailable() && setting.mediaOverlay) {
				showMediaBox();
		        if (!rv.isTTSEnabled() &&  autoStartPlayingWhenNewPagesLoaded) {
		            if (isAutoPlaying) rv.playFirstParallelInPage();		            
		        }
			}else {
				hideMediaBox();
			}			
		}

		@Override
		public void onFailedToMove(boolean isFirstPage) {
			// TODO Auto-generated method stub
			if (isFirstPage) {
				showToast("This is the first page.");
			}else {
				showToast("This is the last page.");
			}			
		}
	}
	
	int numberOfSearched = 0;
	int ms = 10;
	class SearchDelegate implements SearchListener {
		public void onKeySearched(SearchResult searchResult) {
			addSearchResult(searchResult,0);
			debug("chapterIndex"+searchResult.chapterIndex+" pageIndex:" + searchResult.pageIndex + " startOffset:"
					+ searchResult.startOffset + " tag:" + searchResult.nodeName
					+ " pagePositionInChapter "+searchResult.pagePositionInChapter+ " pagePositionInBook "+searchResult.pagePositionInBook + " text:" + searchResult.text);

		}

		public void onSearchFinishedForChapter(SearchResult searchResult) {
			if (searchResult.numberOfSearchedInChapter!=0) {
				addSearchResult(searchResult,1);
				debug("Searching for Chapter:"+searchResult.chapterIndex+" is finished. ");
				rv.pauseSearch();
				numberOfSearched = searchResult.numberOfSearched;
			}else {
				rv.searchMore();
				numberOfSearched = searchResult.numberOfSearched;
			}
		}

		public void onSearchFinished(SearchResult searchResult) {
			debug("Searching is finished. ");
			addSearchResult(searchResult,2);
			hideIndicator();
		}
	}

	class SelectionDelegate implements SelectionListener {
		// startRect is the first rectangle for selection area
		// endRect is the last rectable for selection area.
		// highlight holds information for selected area. 
		public void selectionStarted(Highlight highlight,Rect startRect,Rect endRect) {
			hideMenuBox();
		}; // in case user touches down selection bar, normally hide custom
			

		public void selectionChanged(Highlight highlight,Rect startRect,Rect endRect) {
			hideMenuBox();
		}; // this may happen when user dragging selection.

		public int getOSVersion() {
			return Build.VERSION.SDK_INT;
		}
		
		
		public void selectionEnded(Highlight highlight,Rect startRect,Rect endRect) {
			currentHighlight  = highlight;
			currentHighlight.color = currentColor;
			showMenuBox(startRect,endRect);
//			int x = startRect.left+5;
//			int y = startRect.top + 5;
//			String text = rv.getNodeText(rv.toWebValue(x), rv.toWebValue(y));
//			Log.w("EPub",highlight.text);
			if (this.getOSVersion()>10) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(highlight.text);
			}
		}; // in case user touches up selection bar,custom menu view has to be
			// shown near endX,endY.

		public void selectionCancelled() {
			hideMenuBox();
		} // selection cancelled by user.
	}

	public void debug(String msg) {
		if (Setting.isDebug() && msg!=null) {
			Log.d(Setting.getTag(), msg);
		}
	}

	
	class MediaOverlayDelegate implements MediaOverlayListener {
		@Override
		public void onParallelStarted(Parallel parallel) {
			debug("onParallelStarted "+parallel.toString());
			currentParallel = parallel;	
			if (!rv.isTTSEnabled()) {	// for MediaOverlay						
				if (rv.pageIndexInChapter()!=parallel.pageIndex) {
					if (autoMoveChapterWhenParallesFinished) {
						rv.gotoPageInChapter(parallel.pageIndex);
						isPageTurnedByMediaOverlay = true;
					}
				}
				if (setting.highlightTextToVoice) {
					rv.changeElementColor("#FFFF00",parallel.hash);
				}
			}else {						// for TextToSpeech
				if (setting.highlightTextToVoice) {
					rv.markParellelHighlight(parallel,getColorByIndex(1));
				}
			}
		}

		@Override
		public void onParallelEnded(Parallel parallel) {
			debug("onParallelEnded !!");
			if (!rv.isTTSEnabled()) {
				if (setting.highlightTextToVoice) {
					rv.restoreElementColor();
				}
			}else {
				if (setting.highlightTextToVoice) {
					rv.removeParallelHighlights();
				}
			}						
		}

		@Override
		public void onParallelsEnded() {
			if (!rv.isTTSEnabled()) {
				rv.restoreElementColor();
			    if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = true;
			    if (autoMoveChapterWhenParallesFinished) {
			    	rv.gotoNextChapter();
			    }
			}else {
			    if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = true;			    
			    if (currentPageInformation.pageIndex==currentPageInformation.numberOfPagesInChapter-1) {
			    	if (setting.autoLoadNewChapter) {
			    		rv.gotoNextPage();
			    	}
			    }else {
			    	rv.gotoNextPage();
			    }
			}
		}		
	}
	
	int lastPageIndexPaused = -1;	
	void playAndPause() {
		if (!rv.isPlayingStarted()) {
			rv.playFirstParallelInPage();
			autoStartPlayingWhenNewPagesLoaded = true;
			isAutoPlaying = true;
		}else if (rv.isPlayingPaused()) {
			if (currentPageInformation.pageIndex==lastPageIndexPaused) {
	            rv.resumePlayingParallel();	        	
				autoStartPlayingWhenNewPagesLoaded = true;
				isAutoPlaying = true;
			}else {
				rv.playFirstParallelInPage();
				autoStartPlayingWhenNewPagesLoaded = true;
				isAutoPlaying = true;
			}
		}else {
			lastPageIndexPaused = currentPageInformation.pageIndex;
	        rv.pausePlayingParallel();	        
	        if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = false;
		}
		
		this.changePlayAndPauseButton();
	}
	
	void stopPlaying() {
	    rv.stopPlayingParallel();
	    rv.restoreElementColor();
	    if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = false;
	    this.changePlayAndPauseButton();
	}
	
	void playPrev() {
	    rv.playPrevParallel();
	}

	void playNext() {
	    rv.playNextParallel();
	}	
	
	@Override
	protected void onPause() {
		super.onPause();
//		log("onPause() in BookViewActivity");
		sd.updatePosition(bookCode, pagePositionInBook);
		sd.updateSetting(setting);
		
		rv.stopPlayingMedia();
		rv.stopPlayingParallel();
		rv.restoreElementColor();
		
		this.enableHaptic();
	}

	@Override
	protected void onStop() {
		super.onStop();
//		log("onStop() in BookViewActivity");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
//		log("onRestart() in BookViewActivity");
	}

	@Override
	protected void onResume() {
		super.onResume();
//		log("onResume() in BookViewActivity");
		
		rv.playFirstParallelInPage();
		this.disableHaptic();
	}

	@Override
	protected void onStart() {
		super.onStart();
//		log("onStart() in BookViewActivity");
	}
	
	@Override
	 public void onBackPressed() {
		if (this.isBoxesShown) {
			hideBoxes();
		}else {
			if (videoView!=null) {
				ePubView.removeView(videoView);
			}	
			finish();
			return;
		}
		//	      log("onBackPressed() in BookViewActivity");
	 }
}

interface SkyLayoutListener {
	void onShortPress(SkyLayout view,MotionEvent e);
	void onLongPress(SkyLayout view,MotionEvent e);
	void onSingleTapUp(SkyLayout view,MotionEvent e);
	void onSwipeToLeft(SkyLayout view);
	void onSwipeToRight(SkyLayout view);
}

class SkyLayout extends RelativeLayout  implements android.view.GestureDetector.OnGestureListener {
	public Object data;
	public View editControl;
	public View deleteControl;
	private GestureDetector gestureScanner;
	private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 1024;
    private static final int SWIPE_THRESHOLD_VELOCITY = 50;
    
    private SkyLayoutListener skyLayoutListener = null;
	 
	public SkyLayout(Context context) {
		super(context);		
		gestureScanner = new GestureDetector(this);
	}
	
	public void setSkyLayoutListener(SkyLayoutListener sl) {
		this.skyLayoutListener = sl;
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }
 
    public boolean onDown(MotionEvent e) {
        return true;
    }
 
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
 
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
                if (this.skyLayoutListener!=null) {
                	skyLayoutListener.onSwipeToLeft(this);
                }
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
                if (this.skyLayoutListener!=null) {
                	skyLayoutListener.onSwipeToRight(this);
                }
            }
            // down to up swipe
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getContext(), "Swipe up", Toast.LENGTH_SHORT).show();
            }
            // up to down swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getContext(), "Swipe down", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
 
        }
        return true;
    }
 
    public void onLongPress(MotionEvent e) {
    	if (this.skyLayoutListener!=null) {
    		this.skyLayoutListener.onLongPress(this,e);
    	}
    }
 
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }
 
    public void onShowPress(MotionEvent e) {
    	if (this.skyLayoutListener!=null) {
    		this.skyLayoutListener.onShortPress(this,e);
    	}
    }
 
    public boolean onSingleTapUp(MotionEvent e) {
    	if (this.skyLayoutListener!=null) {
    		this.skyLayoutListener.onSingleTapUp(this,e);
    	}   	
    	return true;
    }
}

class SkyBox extends RelativeLayout {
	public boolean isArrowDown;
	int boxColor;
	int strokeColor;
	public float arrowPosition;
	float boxX,boxWidth;
	public float arrowHeight;
	RelativeLayout contentView;
	boolean layoutChanged;
	
	public SkyBox(Context context) {
		super(context);
		this.setWillNotDraw(false);
		arrowHeight = 50;
		boxColor = Color.YELLOW;
		strokeColor = Color.DKGRAY;
		contentView = new RelativeLayout(context);
		this.addView(contentView);
	}
	
	public void setArrowDirection(boolean isArrowDown) {
		this.isArrowDown = isArrowDown;
		layoutChanged = true;
	}
	
	public void setArrowHeight(float arrowHeight) {
		this.arrowHeight = arrowHeight;
		layoutChanged = true;
	}
	
	public int getDarkerColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		int darker = Color.HSVToColor(hsv);
		return darker;
	}
	
	public int getBrighterColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 1.2f; // value component
		int darker = Color.HSVToColor(hsv);
		return darker;
	}
	
	public void setBoxColor(int boxColor) {
		this.boxColor = boxColor;
		this.strokeColor = this.getDarkerColor(boxColor);		
	}
	
	public void setArrowPosition(int arrowX,int boxLeft,int boxWidth) {
		this.boxX = boxLeft;
		this.boxWidth = boxWidth;
		this.arrowPosition = arrowX-boxX;		
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return true;
	}

	private void recalcLayout() {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		param.leftMargin = 0;
		param.width = this.getWidth();
		if (this.isArrowDown) {
			param.topMargin = 0;
			param.height = this.getHeight()-(int)this.arrowHeight+10;
		}else {
			param.topMargin =  (int)this.arrowHeight-10;
			param.height = this.getHeight()-(int)this.arrowHeight+14;
		}		
		contentView.setLayoutParams(param);		
	}
	
	@SuppressLint({ "DrawAllocation", "DrawAllocation" })
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		
		float sl,sr,st,sb;
		sl = 0;
		sr=this.getWidth();
		float ah = this.arrowHeight; // arrow Height;
		if (this.isArrowDown) {
			st = 0;
			sb=this.getHeight()-ah;
		}else {
			st = ah-10;
			sb=this.getHeight()-10;			
		}
		
		Path boxPath = new Path();
		boxPath.addRoundRect(new RectF(sl,st,sr,sb),20,20, Path.Direction.CW);	
		
		if (arrowPosition<=arrowHeight*1.5f) {
			arrowPosition = arrowHeight*1.5f;
		}else if (arrowPosition>=this.getWidth()-arrowHeight*1.5f) {
			arrowPosition = this.getWidth()-arrowHeight*1.5f;
		}
		
		Path arrowPath = new Path();
		if (isArrowDown) {
			arrowPath.moveTo(arrowPosition, sb+ah);
			arrowPath.lineTo((float) (arrowPosition-ah*0.75), sb-10);
			arrowPath.lineTo((float) (arrowPosition+ah*0.75), sb-10);
			arrowPath.close();
		}else {
			arrowPath.moveTo(arrowPosition, 0);
			arrowPath.lineTo((float)(arrowPosition-ah*0.75), ah+10);
			arrowPath.lineTo((float)(arrowPosition+ah*0.75), ah+10);
			arrowPath.close();
		}		
		
		paint.setColor(this.strokeColor);
		paint.setStyle(Paint.Style.FILL);
		boxPath.addPath(arrowPath);		
		canvas.drawPath(boxPath, paint);
		
		paint.setColor(this.boxColor);
		paint.setStyle(Paint.Style.FILL);
		boxPath.addPath(arrowPath);
		canvas.save();
		float sf = 0.995f;
		float ox = (this.getWidth()-(this.getWidth()*sf))/2.0f;
		float oy = ((this.getHeight()-arrowHeight)-((this.getHeight()-arrowHeight)*sf))/2.0f;
		
	    canvas.translate(ox, oy);
		canvas.scale(sf,sf);
		canvas.drawPath(boxPath, paint);
		canvas.restore();
		
		if (layoutChanged) {
			this.recalcLayout();
			layoutChanged = false;
		}
	}
}


class DottedDrawable extends Drawable {
    private Paint mPaint;
    int color;
    int inactiveColor;
    int value;

    public DottedDrawable(int color) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        this.color = color;
        this.inactiveColor = color;
        this.value = 100;
    }
    
    public DottedDrawable(int activeColor,int inactiveColor,int value) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        this.color = activeColor;
        this.inactiveColor = inactiveColor;
        this.value = value;
    }

 
    @Override
	protected
    boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

	@Override
	public void draw(Canvas canvas) {
        int lvl = getLevel();
        Rect b = getBounds();
        float x = (float)b.width() * (float)lvl / 10000.0f;
        float y = (b.height() - mPaint.getStrokeWidth()) / 2;
        mPaint.setStyle(Paint.Style.FILL);
        for (int cx = 10; cx<b.width(); cx+=30) {
        	float cr = (float)((float)(cx-10)/(float)(b.width()-10))*100;
        	if (cr<=this.value) {
        		mPaint.setColor(color);
        		if (color!=inactiveColor) {
        			canvas.drawCircle(cx, y,6, mPaint);
        		}else {
        			canvas.drawCircle(cx, y,4, mPaint);
        		}
        		
        	}else {
        		mPaint.setColor(inactiveColor);
        		canvas.drawCircle(cx, y,4, mPaint);
        	}        	
        	
        }
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
	}
}

class LineDrawable extends Drawable {
    private Paint mPaint;
    private int mColor;
    private int mStrokeWidth;

    public LineDrawable(int color,int strokeWidth) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        mColor = color;
        mStrokeWidth = strokeWidth;
    }
 
    @Override
	protected
    boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
    }
    
    public void setColor(int color) {
    	this.mColor = color;
    }
    
    public void setStokeWidth(int strokeWidth) {
    	mStrokeWidth = strokeWidth;
    }
    
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

	@Override
	public void draw(Canvas canvas) {
        Rect b = getBounds();
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawLine(0, b.height()/2+b.height()*0.1f, b.width(),b.height()/2+b.height()*.1f,mPaint);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}
}

class Theme {
	public String name;
	public int foregroundColor;
	public int backgroundColor;
	public int controlColor;
	public int controlHighlightColor;
	public String portraitName = "";
	public String landscapeName = "";
	public String doublePagedName = "";
	public int seekBarColor;
	public int seekThumbColor;
	public int selectorColor;
	public int selectionColor;
	public int bookmarkId;
	
	Theme(String name,int foregroundColor,int backgroundColor,int controlColor,int controlHighlightColor,int seekBarColor,int seekThumbColor,int selectorColor,int selectionColor,String portraitName,String landscapeName,String doublePagedName,int bookmarkId) {
		this.name = name;
		this.foregroundColor = foregroundColor;
		this.backgroundColor=backgroundColor;
		this.portraitName = portraitName;
		this.landscapeName = landscapeName;
		this.doublePagedName = doublePagedName;
		this.controlColor = controlColor;
		this.controlHighlightColor = controlHighlightColor;
		this.seekBarColor = seekBarColor;
		this.seekThumbColor = seekThumbColor;
		this.selectorColor = selectorColor;
		this.selectionColor = selectionColor;
		this.bookmarkId = bookmarkId;
	}
}

class SkySeekBar extends SeekBar {
	boolean isReversed = false;
	
    public SkySeekBar(Context context) {
        super(context);
    }

    public SkySeekBar(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }

    public SkySeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setReversed(boolean value) {
    	this.isReversed = value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	if (this.isReversed) {
    		float px = this.getWidth() / 2.0f;
    		float py = this.getHeight() / 2.0f;
    		canvas.scale(-1, 1, px, py);
    	}
    	super.onDraw(canvas);    		
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (this.isReversed) {
    		event.setLocation(this.getWidth() - event.getX(), event.getY());
    	}
        return super.onTouchEvent(event);
    }
}
