package com.skytree.epubtest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.skytree.epub.Book;
import com.skytree.epub.CacheListener;
import com.skytree.epub.Caret;
import com.skytree.epub.ClickListener;
import com.skytree.epub.ContentListener;
import com.skytree.epub.FixedControl;
import com.skytree.epub.Highlight;
import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;
import com.skytree.epub.ItemRef;
import com.skytree.epub.KeyListener;
import com.skytree.epub.MediaOverlayListener;
import com.skytree.epub.NavPoint;
import com.skytree.epub.NavPoints;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.skytree.epub.PageTransition;
import com.skytree.epub.Parallel;
import com.skytree.epub.SearchListener;
import com.skytree.epub.SearchResult;
import com.skytree.epub.SelectionListener;
import com.skytree.epub.Setting;
import com.skytree.epub.SkyProvider;
import com.skytree.epub.State;
import com.skytree.epub.StateListener;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

public class MagazineActivity extends Activity {
	RelativeLayout ePubView,topView;
	FixedControl fv;
	SkyPieView pieView;
	View pieBack;

	boolean isBoxesShown;
	
	Rect bookmarkRect,bookmarkedRect;
	
	ImageButton rotationButton;
	ImageButton listButton;	
	ImageButton searchButton;
	ImageButton bookmarkButton;
	
	TextView titleLabel;
	String title;
	
	SkyLayout mediaBox;
	ImageButton playAndPauseButton;
 	ImageButton stopButton;
 	ImageButton prevButton;
 	ImageButton nextButton;
 	
 	ImageButton actionButton;
 	
 	Button outsideButton;

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
 	ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
 	
 	LinearLayout fontListView;
 	
 	SkyLayout listBox;
 	Button contentListButton;
 	Button bookmarkListButton;
 	Button highlightListButton;
 	ScrollView listScrollView;
 	LinearLayout listView;
 	Button listTopButton;
 	
 	SkyLayout thumbnailBox;

	
	Parallel currentParallel;
	boolean autoStartPlayingWhenNewPagesLoaded = true;  
	boolean autoMovePageWhenParallesFinished = true;	
	boolean isAutoPlaying = true;						
	int bookCode;
	double pagePositionInBook;
	
 	SkySetting setting;
 	SkyDatabase sd;
 	boolean isRotationLocked;
 	
 	Highlight currentHighlight;
 	int currentColor;
 	
 	PageInformation currentPageInformation;
 	
 	ImageButton menuButton;
	
	private void debug(String msg) {
		Log.w("EPub",msg);
	}
	
	public boolean isDoublePagedForLandscape() {
		boolean res = this.fv.isDoublePagedForLandscape();
		return res;
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		showToast("onCreateOptionsMenu");
	    return super.onCreateOptionsMenu(menu);
	}
	
	private ActionMode mActionMode = null;	
	@Override
    public void onActionModeStarted(ActionMode mode) {
		mode.getMenu().removeGroup(0);
		mode.getMenu().clear();
        super.onActionModeStarted(mode);
    }
	
	private class CustomActionModeCallback implements ActionMode.Callback {
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			showToast("onCreateActionMode");
            return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			return false;
		}
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

	
    public void onCreate(Bundle savedInstanceState) {    	
    	super.onCreate(savedInstanceState);
    	
    	sd = new SkyDatabase(this);
		setting = sd.fetchSetting();
		
		registerSkyReceiver(); // New in SkyEpub sdk 7.x 
    	
        String fileName = new String();
		Bundle bundle = getIntent().getExtras();
		fileName = bundle.getString("BOOKNAME");
		bookCode = bundle.getInt("BOOKCODE");
		pagePositionInBook = bundle.getDouble("POSITION");
		title = bundle.getString("TITLE");		
		int spread = bundle.getInt("SPREAD");
		int orientation = bundle.getInt("ORIENTATION");	
		
		autoStartPlayingWhenNewPagesLoaded = setting.autoStartPlaying;  
		boolean autoMovePageWhenParallesFinished = setting.autoLoadNewChapter;
		
		currentColor = this.getColorByIndex(0);		
		super.onCreate(savedInstanceState); 		
		ePubView = new RelativeLayout(this);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
		ePubView.setLayoutParams(rlp);		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height        
		Bitmap pagesStack = 	BitmapFactory.decodeFile(SkySetting.getStorageDirectory()+"/images/PagesStack.png");
		Bitmap pagesCenter = 	BitmapFactory.decodeFile(SkySetting.getStorageDirectory()+"/images/PagesCenter.png");		
		
		// set the number of threads to generate Cached Images, for low spec devices set this value 1 or 2 for best performance.
		// Do not increase this value too much in low spec devices.		
		int numberOfThreadsForCaching = 2;		
		// create FixedControl with Spread, orientation 
		// and the number of threads to be used for caching to generate thumbnail images.
		fv = new FixedControl(this,spread,orientation,numberOfThreadsForCaching); 
//		fv = new FixedControl(this,spread,orientation);
//		fv = new FixedControl(this,Book.SpreadAuto,orientation); 
//		fv = new FixedControl(this,Book.SpreadNone,orientation);
//		fv = new FixedControl(this,Book.SpreadBoth,orientation);
//		fv.setPagesCenterImage(pagesCenter);
//		fv.setPagesStackImage(pagesStack);
		// Set the CacheDelegate to generate Thumbnail Images
		fv.setCacheListener(new CacheDelegate());
		// Set the StateDelete to monitor the state of engine
		fv.setStateListener(new StateDelegate());
		// Set the SelectionDelegate to hanle Text Selection
		fv.setSelectionListener(new SelectionDelegate());
		// Set the HighlightDelegate for text highlighting
		fv.setHighlightListener(new HighlightDelegate());
		// Set the SearchDelegate to search keyword. 
		fv.setSearchListener(new SearchDelegate());
		// Set the ClickDelegate to handle user's click event
        fv.setClickListener(new ClickDelegate());        
        // set the PageMovedDelegate which is called whenever page is moved. 
        fv.setPageMovedListener(new PageMovedDelegate());
        // set the MediaOverlayListener for MediaOverlay.
        fv.setMediaOverlayListener(new MediaOverlayDelegate());
		
//		fv.setBaseDirectory(SkySetting.getStorageDirectory() + "/books");
//      fv.setBookName(fileName);
		// Set the path of book to open.  since 6.0
		fv.setBookPath(SkySetting.getStorageDirectory() + "/books/"+fileName);
		// Set the bookCode to identifiy book file. 
        fv.bookCode = this.bookCode;
        
        // SkyProvider is the default Epub File Handler inside SkyEpub SDK since 5.0
		SkyProvider skyProvider = new SkyProvider();
		skyProvider.setKeyListener(new KeyDelegate());
		fv.setContentProvider(skyProvider);		
		SkyProvider skyProviderForCache = new SkyProvider();
		skyProviderForCache.setKeyListener(new KeyDelegate());
		fv.setContentProviderForCache(skyProviderForCache);
		
		// If you want to handle the epub unzipped, use SkyProvider of EpubProvider (Code is open in Advanced demo)
		// When you need to make your own ContentProvider, Look into EpubProvider.java code deelpy. 
//		fv.setContentProvider(new EpubProvider());
//		fv.setContentProviderForCache(new EpubProvider());

		fv.setLayoutParams(params);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.width =  LayoutParams.FILL_PARENT;	// 400;
        params.height = LayoutParams.FILL_PARENT;	// 600;
        
        // set the delay time for rendering, for low spec device , this value is needed to be increased. 
        fv.setTimeForRendering(500); 
        // set the image quality for curling transition
        // if "Out of memory" occures, this value should be decreased like 0.25. 
        fv.setCurlQuality(0.6f);
        // set the area to navigate to left or right page
        fv.setNavigationAreaWidthRatio(0.05f);
        // if true, audio file will be played by sequence based.  
        fv.setSequenceBasedForMediaOverlay(false); 

        // If you want to get the license key for commercial or non commercial use, please don't hesitate to email us. (skytree21@gmail.com). 
		// Without the proper license key, watermark message(eg.'unlicensed') may be shown in background. 
		fv.setLicenseKey("0000-0000-0000-0000");
		int transitionType = bundle.getInt("transitionType");
		if (transitionType==0) {
			fv.setPageTransition(PageTransition.None);
		}else if (transitionType==1) {
			fv.setPageTransition(PageTransition.Slide);
		}else if (transitionType==2) {
			fv.setPageTransition(PageTransition.Curl);
		}
		
		// in nexus 5, enforce the sdk to use software layer to render epub to avoid some display issues. 
		String modelName = SkyUtility.getModelName();
		if (modelName.toLowerCase().contains("hammerhead") || modelName.toLowerCase().contains("nexus 5")) {
			fv.useSoftwareLayer();
		}
		
		// set the minimum speed for pageTransition 
		// to avoid the conflict between dragging object inside page and swiping for turning page.		
//		fv.setSwipeSpeedForPageTransition(1.3f);
//		fv.setSwipeSpeedForPageTransition(0.6f);
//		fv.setSwipeSpeedForPageTransition(0.25f);
		
	    double weightedLoadForCachingTask = 0.33;
		double weightedLoadForForwardTransition = 3.0f;
		double weightedLoadForBackwardTransition = 3.0f;
		double weightedLoadThresholdForTransition = 6.0f;
		double weightedLoadThresholdToUseCachedForCurling = 7.0f; // 3.5f
		double weightedLoadThresholdToPauseCacheTask = 1.5f;
		double weightedLoadDeltaForNormarlDecrease = 0.20f;
		
		// DO NOT CHANGE THE VALUES BELOW
		// the weighted load is the value to determin the load overhead of skyepub task. 
		// whenever page is turned or cache task is processed, the weighted load value is increased.
		// and every 100 mils , the weighted value is decreased by delta value. 
		// the weighted load affects the performace of sdk greatly.
		/*
		// set the weighted load value to add when caching task is being processed 
		fv.setWeightedLoadForCachingTask(weightedLoadForCachingTask);
		// set the weighted load value to add when turning to the next page 
		fv.setWeightedLoadForForwardTransition(weightedLoadForCachingTask);		
		// set the weighted load value to add when turning to the prev page
		fv.setWeightedLoadForBackwardTransition(weightedLoadForCachingTask);		
		// set the threshod value to determine when cached image from sd card is used for curling. 
		fv.setWeightedLoadThresholdToUseCachedForCurling(weightedLoadThresholdToUseCachedForCurling);		
		// set the threshod value to determine to pause caching task. 
		fv.setWeightedLoadThresholdToPauseCacheTask(weightedLoadThresholdToUseCachedForCurling);		
		// set the deta value to decrease weighted load value for every 100 mils. 
		fv.setWeightedLoadDeltaForNormarlDecrease(weightedLoadDeltaForNormarlDecrease);		
		// set the threshold value to limit/prevent turning page. 
		fv.setWeightedLoadThresholForTransiton(weightedLoadThresholdForTransition);
		*/
		// set custom script for all pages in this epub.
		// custom script will be loaded on each page.
		String script = "function beep() {"+
		    "var sound = new Audio('data:audio/wav;base64,//uQRAAAAWMSLwUIYAAsYkXgoQwAEaYLWfkWgAI0wWs/ItAAAGDgYtAgAyN+QWaAAihwMWm4G8QQRDiMcCBcH3Cc+CDv/7xA4Tvh9Rz/y8QADBwMWgQAZG/ILNAARQ4GLTcDeIIIhxGOBAuD7hOfBB3/94gcJ3w+o5/5eIAIAAAVwWgQAVQ2ORaIQwEMAJiDg95G4nQL7mQVWI6GwRcfsZAcsKkJvxgxEjzFUgfHoSQ9Qq7KNwqHwuB13MA4a1q/DmBrHgPcmjiGoh//EwC5nGPEmS4RcfkVKOhJf+WOgoxJclFz3kgn//dBA+ya1GhurNn8zb//9NNutNuhz31f////9vt///z+IdAEAAAK4LQIAKobHItEIYCGAExBwe8jcToF9zIKrEdDYIuP2MgOWFSE34wYiR5iqQPj0JIeoVdlG4VD4XA67mAcNa1fhzA1jwHuTRxDUQ//iYBczjHiTJcIuPyKlHQkv/LHQUYkuSi57yQT//uggfZNajQ3Vmz+Zt//+mm3Wm3Q576v////+32///5/EOgAAADVghQAAAAA//uQZAUAB1WI0PZugAAAAAoQwAAAEk3nRd2qAAAAACiDgAAAAAAABCqEEQRLCgwpBGMlJkIz8jKhGvj4k6jzRnqasNKIeoh5gI7BJaC1A1AoNBjJgbyApVS4IDlZgDU5WUAxEKDNmmALHzZp0Fkz1FMTmGFl1FMEyodIavcCAUHDWrKAIA4aa2oCgILEBupZgHvAhEBcZ6joQBxS76AgccrFlczBvKLC0QI2cBoCFvfTDAo7eoOQInqDPBtvrDEZBNYN5xwNwxQRfw8ZQ5wQVLvO8OYU+mHvFLlDh05Mdg7BT6YrRPpCBznMB2r//xKJjyyOh+cImr2/4doscwD6neZjuZR4AgAABYAAAABy1xcdQtxYBYYZdifkUDgzzXaXn98Z0oi9ILU5mBjFANmRwlVJ3/6jYDAmxaiDG3/6xjQQCCKkRb/6kg/wW+kSJ5//rLobkLSiKmqP/0ikJuDaSaSf/6JiLYLEYnW/+kXg1WRVJL/9EmQ1YZIsv/6Qzwy5qk7/+tEU0nkls3/zIUMPKNX/6yZLf+kFgAfgGyLFAUwY//uQZAUABcd5UiNPVXAAAApAAAAAE0VZQKw9ISAAACgAAAAAVQIygIElVrFkBS+Jhi+EAuu+lKAkYUEIsmEAEoMeDmCETMvfSHTGkF5RWH7kz/ESHWPAq/kcCRhqBtMdokPdM7vil7RG98A2sc7zO6ZvTdM7pmOUAZTnJW+NXxqmd41dqJ6mLTXxrPpnV8avaIf5SvL7pndPvPpndJR9Kuu8fePvuiuhorgWjp7Mf/PRjxcFCPDkW31srioCExivv9lcwKEaHsf/7ow2Fl1T/9RkXgEhYElAoCLFtMArxwivDJJ+bR1HTKJdlEoTELCIqgEwVGSQ+hIm0NbK8WXcTEI0UPoa2NbG4y2K00JEWbZavJXkYaqo9CRHS55FcZTjKEk3NKoCYUnSQ0rWxrZbFKbKIhOKPZe1cJKzZSaQrIyULHDZmV5K4xySsDRKWOruanGtjLJXFEmwaIbDLX0hIPBUQPVFVkQkDoUNfSoDgQGKPekoxeGzA4DUvnn4bxzcZrtJyipKfPNy5w+9lnXwgqsiyHNeSVpemw4bWb9psYeq//uQZBoABQt4yMVxYAIAAAkQoAAAHvYpL5m6AAgAACXDAAAAD59jblTirQe9upFsmZbpMudy7Lz1X1DYsxOOSWpfPqNX2WqktK0DMvuGwlbNj44TleLPQ+Gsfb+GOWOKJoIrWb3cIMeeON6lz2umTqMXV8Mj30yWPpjoSa9ujK8SyeJP5y5mOW1D6hvLepeveEAEDo0mgCRClOEgANv3B9a6fikgUSu/DmAMATrGx7nng5p5iimPNZsfQLYB2sDLIkzRKZOHGAaUyDcpFBSLG9MCQALgAIgQs2YunOszLSAyQYPVC2YdGGeHD2dTdJk1pAHGAWDjnkcLKFymS3RQZTInzySoBwMG0QueC3gMsCEYxUqlrcxK6k1LQQcsmyYeQPdC2YfuGPASCBkcVMQQqpVJshui1tkXQJQV0OXGAZMXSOEEBRirXbVRQW7ugq7IM7rPWSZyDlM3IuNEkxzCOJ0ny2ThNkyRai1b6ev//3dzNGzNb//4uAvHT5sURcZCFcuKLhOFs8mLAAEAt4UWAAIABAAAAAB4qbHo0tIjVkUU//uQZAwABfSFz3ZqQAAAAAngwAAAE1HjMp2qAAAAACZDgAAAD5UkTE1UgZEUExqYynN1qZvqIOREEFmBcJQkwdxiFtw0qEOkGYfRDifBui9MQg4QAHAqWtAWHoCxu1Yf4VfWLPIM2mHDFsbQEVGwyqQoQcwnfHeIkNt9YnkiaS1oizycqJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=');"  
		    +"sound.play(); }";
		fv.setCustomScript(script);
		
		// change the background color 
		fv.changeBackgroundColor(Color.WHITE);
		// change the color of window. 
		fv.changeWindowColor(Color.DKGRAY);
	
		getWindow().getDecorView().setBackgroundColor(Color.DKGRAY);
		
		isRotationLocked = setting.lockRotation;
		
        ePubView.addView(fv);

        // set the start page index to open.
        // use the last page index which was stored in the last time. 
        int startPageIndex = (int)pagePositionInBook;
        fv.setStartPageIndex(startPageIndex); 
                
        setContentView(ePubView);
        fv.setImmersiveMode(true);
        SkyUtility.makeFullscreen(this);
        
		topView = new RelativeLayout(this);
		RelativeLayout.LayoutParams tlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
		topView.setLayoutParams(tlp);
		topView.setVisibility(View.INVISIBLE);
		topView.setVisibility(View.GONE);
		topView.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        return true;
		    }
		});
		
		ePubView.addView(topView);			
		
		pieView = new SkyPieView(this);
		pieView.setId(7080);
		pieView.isHidden = false;
		pieView.setVisibility(View.VISIBLE);
		int pw = getPS(300);
		int ph = pw;
		int px = (this.getWidth()-pw)/2;
		int py = (this.getHeight()-ph)/2;
		SkyUtility.setFrame(pieView,px,py,pw,ph);			

		pieBack = new View(this);
		int bw = getPS(180);
		int bh = bw;
		int bx = (this.getWidth()-bw)/2;
		int by = (this.getHeight()-bh)/2;

		SkyUtility.setFrame(pieBack,bx,by,bw,bh);
		pieBack.setBackgroundColor(Color.argb(200, 80, 80, 80));
		topView.addView(pieBack);		
		topView.addView(pieView);		
		
		
		this.makeBoxes();		
		this.makeIndicator();
		
		this.recalcFrames();
		
		if (fv.isDebugging()) {
//			showToast("In Debugging Mode, Caching Task Not Started.!!!!");
		}
    }
    
    
    SkyLayout controlBox;
    int filterColor = 0xFFAAAAAA;
    
    public void makeControlBox() { 
		this.controlBox = new SkyLayout(this);
    	int bs = 38;
		if (this.isRotationLocked) 	rotationButton 		= this.makeImageButton(9000, R.drawable.rotationlocked2x, ps(42),ps(42));
		else 						rotationButton 		= this.makeImageButton(9000, R.drawable.rotation2x, ps(42),ps(42));
		listButton 			= this.makeImageButton(9001, R.drawable.list2x, getPS(bs),getPS(bs));
		searchButton 		= this.makeImageButton(9003, R.drawable.search2x, getPS(bs),getPS(bs));
		bookmarkButton 		= this.makeImageButton(9004, R.drawable.bookmark2x, getPS(42),getPS(42));
		
		titleLabel				= this.makeLabel(3000,  title,	Gravity.CENTER_HORIZONTAL, 17, filterColor);	// setTextSize in android uses sp (Scaled Pixel) as default, they say that sp guarantees the device dependent size, but as usual in android it can't be 100% sure. 	

		rotationButton.setColorFilter(filterColor);
		listButton.setColorFilter(filterColor);
		searchButton.setColorFilter(filterColor);
		bookmarkButton.setColorFilter(filterColor);
		
		controlBox.addView(rotationButton);
		controlBox.addView(listButton);
		controlBox.addView(searchButton);
		controlBox.addView(bookmarkButton);		
		controlBox.addView(titleLabel);
		this.makeMediaBox();
		ePubView.addView(controlBox);	
		
		menuButton = this.makeImageButton(9099, R.drawable.menu, getPS(bs),getPS(bs));
		menuButton.setColorFilter(filterColor);
		ePubView.addView(menuButton);
		
		this.hideControlBox();
    }

    public void makeMediaBox() {
		mediaBox = new SkyLayout(this);
		setFrame(mediaBox, 0, 0, ps(270),ps(80));
		
		int bs = ps(35);
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
		
		mediaBox.addView(prevButton);
		mediaBox.addView(playAndPauseButton);
		mediaBox.addView(stopButton);
		mediaBox.addView(nextButton);
		
		prevButton.setColorFilter(filterColor);
		playAndPauseButton.setColorFilter(filterColor);
		stopButton.setColorFilter(filterColor);
		nextButton.setColorFilter(filterColor);
		
		this.controlBox.addView(mediaBox);	
		this.hideMediaBox();
	}
    
    
	public void hideMediaBox() {
		if (mediaBox!=null) {
			mediaBox.setVisibility(View.INVISIBLE);
			mediaBox.setVisibility(View.GONE);
		}
	}
	
	public void showMediaBox() {
		mediaBox.setVisibility(View.VISIBLE);
	}
    
	boolean isControlsShown = false;

	public void toggleControls() {
		isControlsShown = !isControlsShown;
		if (isControlsShown) {
			showControlBox();
			showThumbnailBox();
			if (fv.isMediaOverlayAvailable() && setting.mediaOverlay) showMediaBox();
		}else {
			hideControlBox();
			hideThumbnailBox();
		}		
	}
	
	public void hideMenuButton() {
		this.menuButton.setVisibility(View.INVISIBLE);
		this.menuButton.setVisibility(View.GONE);
	}
	
	public void showMenuButton() {
		this.menuButton.setVisibility(View.VISIBLE);		
	}

    
    public void showControlBox() {
    	GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {0xBB131313,0xBB616261});
        gd.setCornerRadius(0f);

        this.controlBox.setBackgroundDrawable(gd);
    	this.setFrame(this.controlBox,0,0,this.getWidth(),this.getHeight()/7);
		this.controlBox.setVisibility(View.VISIBLE);
		this.hideMenuButton();
	}
    
	public Handler hideControlsHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideControlBox();
			hideThumbnailBox();
		}
	};
	
	public void hideControlBox() {
		this.controlBox.setVisibility(View.INVISIBLE);
		this.controlBox.setVisibility(View.GONE);
		this.showMenuButton();
	}
	
	public void setLabelLength(TextView label, int maxLength) {
		String text = (String) label.getText();
		if (text.length()>maxLength) {
			text = text.substring(0, maxLength);
			text = text+"..";
		}
		label.setText(text);
	}
	
	int getLabelWidth(TextView tv) {
		tv.measure(0, 0);       //must call measure!		
		return tv.getMeasuredWidth();  //get height
	}
	
	int getLabelHeight(TextView tv) {
		tv.measure(0, 0);       //must call measure!
		return tv.getMeasuredHeight(); //get width		
	}
	
	boolean isBookmarked(PageInformation pageInformation) {
		if (currentPageInformation==null) return false;
		boolean res = sd.isBookmarked(pageInformation); 
		return res;		
	}
	
	boolean isBookmarked() {		
		return this.isBookmarked(currentPageInformation);
	}
    
    public void recalcFrames() {
    	this.setLocation(menuButton, pxl(20),pyt(10));
		if (!this.isTablet()) {				// for phones   					- tested with Galaxy S2, Galaxy S3, Galaxy S4
			if (this.isPortrait()) {
				this.setLocation(rotationButton, 	pxl(20),pyt(15-2));
				this.setLocation(listButton, 		pxl(20+(48+5)*1),pyt(15));
				this.setLocation(searchButton, 		pxr(40+(48+5)*2),pyt(15));
				this.setLocation(bookmarkButton, 	pxr(40+(48+5)*1),pyt(15));
				
				int ld = this.getWidth()/40;
				this.setLabelLength(titleLabel,10);
				this.setLocation(titleLabel,(this.getWidth()/2-this.getLabelWidth(titleLabel)/2)-ld	,pyt(28));
				
				setLocation(mediaBox,ps(65), ps(65));				
			}else {
				int sd = ps(40);
				this.setLocation(rotationButton, 	pxl(10),pyt(5-2));
				this.setLocation(listButton, 		pxl(10+(48+5)*1),pyt(5));
				this.setLocation(searchButton, 		pxr(60+(48+5)*2),pyt(5));
				this.setLocation(bookmarkButton, 	pxr(40+(48+5)*1),pyt(5));
				
				this.setLabelLength(titleLabel,40);
				this.setLocation(titleLabel,(this.getWidth()/2-this.getLabelWidth(titleLabel)/2)-sd	,pyt(17));
				
				setLocation(mediaBox, ps(135), ps(2));
			}
		}else {									// for tables				- tested with Galaxy Tap 10.1, Galaxy Note 10.1
			if (this.isPortrait()) {
				int ox = 50;
				int rx = 100;
				int oy = 30;
				
				this.setLocation(rotationButton, 	pxl(ox)					,pyt(oy-2));
				this.setLocation(listButton, 		pxl(ox+(65)*1)			,pyt(oy));				
				this.setLocation(searchButton, 		pxr(rx+(65)*2)			,pyt(oy));
				this.setLocation(bookmarkButton, 	pxr(rx+(65)*1)			,pyt(oy));
				int ld = this.getWidth()/40;
				this.setLabelLength(titleLabel,20);
				this.setLocation(titleLabel,(this.getWidth()/2-this.getLabelWidth(titleLabel)/2)-ld,pyt(28+20));
				
				setLocation(mediaBox,ps(100), ps(100));				
			}else {
				int sd = ps(40);
				int ox = 40;
				int rx = 130;
				int oy = 20;				

				this.setLocation(rotationButton, 	pxl(ox)				,pyt(oy-2));
				this.setLocation(listButton, 		pxl(ox+(65)*1)		,pyt(oy));				
				this.setLocation(searchButton, 		pxr(rx+(65)*2)		,pyt(oy));
				this.setLocation(bookmarkButton, 	pxr(rx+(65)*1)		,pyt(oy));
				this.setLabelLength(titleLabel,50);
				this.setLocation(titleLabel,(this.getWidth()/2-this.getLabelWidth(titleLabel)/2)-sd,			pyt(27));
				
				setLocation(mediaBox, ps(165), ps(oy));
			}
		}	
		
		bringControlsToFront();
    }
    
    public void bringControlsToFront() {
    	ePubView.bringChildToFront(rotationButton);
    	ePubView.bringChildToFront(listButton);
    	ePubView.bringChildToFront(searchButton);
    	ePubView.bringChildToFront(bookmarkButton);
    	ePubView.bringChildToFront(titleLabel);
    }
	
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
		this.makeThumbnailBox();
		this.fillThumbnailBox();
		this.recalcFrames();
	}	
    
	public void log(String msg) {
		Log.w("EPub",msg);
	}    
    
    
	public void makeBoxes() {
		this.removeBoxes();
		this.makeOutsideButton();
		this.makeListBox();
		this.makeThumbnailBox();
		this.makeMenuBox();
		this.makeHighlightBox();
		this.makeColorBox();
		this.makeNoteBox();		
		this.makeControlBox();
		this.makeSearchBox();
	}
	
	public void removeBoxes() {
		this.ePubView.removeView(listBox);
		this.ePubView.removeView(thumbnailBox);
		this.ePubView.removeView(menuBox);
		this.ePubView.removeView(highlightBox);
		this.ePubView.removeView(colorBox);
		this.ePubView.removeView(noteBox);		
		this.ePubView.removeView(controlBox);
		this.ePubView.removeView(searchBox);
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
	
	
	boolean isMenuBoxShown = false;
	public void showMenuBox(Rect startRect, Rect endRect) {
		this.showOutsideButton();
		menuBox.setVisibility(View.VISIBLE);
		this.moveSkyBox(menuBox,ps(280),ps(85), startRect, endRect);
		isBoxesShown = true;
		isMenuBoxShown = true;
	}
	
	public void hideMenuBox() {
		this.hideOutsideButton();
		if (menuBox.getVisibility()!=View.VISIBLE) return;
		menuBox.setVisibility(View.INVISIBLE);
		menuBox.setVisibility(View.GONE);
		isBoxesShown = false;
		isMenuBoxShown = false;
		hideOutsideButton();
		fv.deselectAll();
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
		Rect startRect = fv.getCurrentRect();
		Rect endRect   = fv.getCurrentRect();
		
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
		fv.changeHighlightNote(currentHighlight, note);
	}
	
	public void dismissKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(noteEditor.getWindowToken(), 0);		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		makeFullScreen();
	}
	
	private void lockRotation() {
		this.fv.setRotationLocked(true);
	}
	
	private void unlockRotation() {
		if (this.isRotationLocked) {
			this.fv.setRotationLocked(true);
		}else {
			this.fv.setRotationLocked(false);
		}
	}
	
	public void makeFullScreen() {
//		if (SkyUtility.isNexus() && isFullScreenForNexus) {
			SkyUtility.makeFullscreen(this);
//		}
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
	
	public boolean isTablet() {
	    return (getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
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
		}else if ((this.getHeight()-endRect.bottom)-bottomMargin >boxHeight) {  
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
						fv.searchKey(key);
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
		fv.stopSearch();
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
			ci+=1;
			String chapterText = "";
			chapterText = sr.chapterTitle;
			String positionText = String.format("%d/%d",sr.chapterIndex+1,sr.numberOfChaptersInBook);
			if (chapterText==null ||chapterText.isEmpty()) {
				chapterText = "Page "+ci;
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
	
	private void listPressed() { 
		this.isRotationLocked = false;
		this.rotationPressed();
		this.hideBoxes();
		this.showListBox();	        
	}
	
	private void rotationPressed() {
		isRotationLocked = !isRotationLocked;
		if (isRotationLocked) {
			fv.setRotationLocked(true);
			
		}else {
			fv.setRotationLocked(false);
		}
		changeRotationButton();
	}
	
	public void makeListBox() {
		this.listBox = new SkyLayout(this);
//		listBox.setBackgroundColor(Color.TRANSPARENT);
		listBox.setBackgroundColor(Color.WHITE);
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
		if (this.fv.isDoublePagedForLandscape() && !this.isPortrait()) {
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
		this.hideControlBox();
		this.listButton.setVisibility(View.VISIBLE);
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
	
	public Bitmap resizeBitmap(Bitmap source, int maxResolution) {
	    int width = source.getWidth();
	    int height = source.getHeight();
	    int newWidth = width;
	    int newHeight = height;
	    float rate = 0.0f;	 
	    if(width > height) {
	        if(maxResolution < width)  {
	            rate = maxResolution / (float) width;
	            newHeight = (int) (height * rate);
	            newWidth = maxResolution;
	        }
	    }else {
	        if(maxResolution < height) {
	            rate = maxResolution / (float) height;
	            newWidth = (int) (width * rate);
	            newHeight = maxResolution;
	        }
	    }	 
	    return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
	}
	

	class ThumbnailView extends SkyLayout{
		public int pageIndex;
		public boolean isImageLoaded = false;
		public int backgroundColor;
		public Button button;
		Context context;		
		public ImageView imageView;
		
		public ThumbnailView(Context context,int pageIndex,int backgroundColor,OnClickListener listener) {
			super(context);
			// TODO Auto-generated constructor stub
			this.context = context.getApplicationContext();
			this.pageIndex = pageIndex;
			this.backgroundColor = backgroundColor;
			this.setBackgroundColor(backgroundColor);
			imageView = new ImageView(this.context);
			RelativeLayout.LayoutParams tlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
			imageView.setLayoutParams(tlp);
			
			this.button = new Button(this.context);
			this.button.setBackgroundColor(Color.TRANSPARENT);
			tlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
			button.setLayoutParams(tlp);
			int pi = pageIndex;
			if (fv.book.isRTL) {
				pi = (fv.book.spine.size()-1)-pageIndex;
			}
			button.setText(""+pi);
			button.setId(8000+pageIndex);
			button.setOnClickListener(listener);	
			
			GradientDrawable gd = new GradientDrawable();
	        gd.setColor(0x00FFFFFF); // Changes this drawbale to use a single color instead of a gradient
	        gd.setCornerRadius(0);
	        gd.setStroke(1, 0xFF000000);
	        button.setBackgroundDrawable(gd);
			
			this.addView(imageView);
			this.addView(button);
		}		
		
		// resize and load it. 
		public void loadBitmap(Bitmap bitmap) {
			if (bitmap!=null && !bitmap.isRecycled()) {
				Bitmap resized = resizeBitmap(bitmap,100);
				this.imageView.setImageBitmap(resized);
				this.isImageLoaded = true;
			}
		}
		
		public void mark() {
			GradientDrawable gd = new GradientDrawable();
	        gd.setColor(0x00FFFFFF); // Changes this drawbale to use a single color instead of a gradient
	        gd.setCornerRadius(1);
	        gd.setStroke(4, 0xFF000000);
	        button.setBackgroundDrawable(gd);
		}
		
		public void unmark() {
			GradientDrawable gd = new GradientDrawable();
	        gd.setColor(0x00FFFFFF); // Changes this drawbale to use a single color instead of a gradient
	        gd.setCornerRadius(0);
	        gd.setStroke(1, 0xFF000000);
	        button.setBackgroundDrawable(gd);
		}
		
		public void clear() {
			if (imageView==null) return;
			if ((BitmapDrawable)imageView.getDrawable()==null) return;
			if (((BitmapDrawable)imageView.getDrawable()).getBitmap()==null) return;
			if (((BitmapDrawable)imageView.getDrawable()).getBitmap().isRecycled()) return;			

			((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
			this.removeView(this.imageView);
			this.imageView = null;
		}
		
		public Bitmap getBitmap() {
			try {
				return ((BitmapDrawable)imageView.getDrawable()).getBitmap();
			}catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	HorizontalScrollView thumbnailScrollView;
	LinearLayout thumbnailsView;
	
	public ThumbnailView getThumbnailView(int pageIndex) {
		if (this.thumbnailsView==null) return null;
		ThumbnailView tv = (ThumbnailView)this.thumbnailsView.getChildAt(pageIndex);
		return tv;
	}
	
	public void makeThumbnailBox() {	
		this.ePubView.removeView(thumbnailBox);
		this.thumbnailBox = new SkyLayout(this);
		thumbnailScrollView = new HorizontalScrollView(this);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
		thumbnailScrollView.setLayoutParams(rlp);
		this.thumbnailBox.addView(thumbnailScrollView);
		thumbnailScrollView.setBackgroundColor(Color.TRANSPARENT);
		
		thumbnailsView = new LinearLayout(this);
		thumbnailsView.setOrientation(LinearLayout.HORIZONTAL);
        
		thumbnailScrollView.addView(thumbnailsView,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));		
		this.ePubView.addView(this.thumbnailBox);
		
		this.hideThumbnailBox();
	}
	
	public void clearThumbnailBox() {
		for (int i=0;  i<this.thumbnailsView.getChildCount(); i++) {
			ThumbnailView tv = (ThumbnailView)thumbnailsView.getChildAt(i);
			tv.clear();
		}
		thumbnailScrollView.removeView(thumbnailsView);
		thumbnailsView = null;
		thumbnailBox.removeView(thumbnailScrollView);		
	}
	
	public void terminateViews() {		
		this.clearThumbnailBox();
//		this.removeBoxes();
//		this.ePubView.removeAllViews();	
	}
	
	public void processFinish() {
		this.terminateViews();	
		finish();
	}
	
	Timer thumbloadTimer = null;
	int currentThumbnailIndex = 0;

	class ThumbLoadTask extends TimerTask {
		public void run() {		
			thumbLoadHandler.obtainMessage(1).sendToTarget();
		}
	}
	
	public Handler thumbLoadHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	// TODO Real Task	    
	    	if (thumbnailsView==null) return;
			ThumbnailView tv = (ThumbnailView)thumbnailsView.getChildAt(currentThumbnailIndex);
			if (tv!=null && !tv.isImageLoaded) {
	            Bitmap bitmap = getBitmap(getCachePath(currentThumbnailIndex));    
	            tv.loadBitmap(bitmap);
			}
	    	currentThumbnailIndex++;
	   	}
	};
	
	public void startThumbLoadTask() {
		stopThumbLoadTask();
		currentThumbnailIndex = 0;
		final int interval = 100;	
		thumbloadTimer = new Timer();
		ThumbLoadTask xt = new ThumbLoadTask();			
		thumbloadTimer.schedule(xt, interval,interval);
	}
	
	public void stopThumbLoadTask() {
		if (thumbloadTimer!=null) thumbloadTimer.cancel();
		thumbloadTimer = null;
	}	
	
	
	public void fillThumbnailBox() {
		if (this.fv.book==null) return;
		if (this.fv.book.spine==null || this.fv.book.spine.size()==0) return;
		if (this.thumbnailsView==null) return;
		
		thumbnailsView.removeAllViews();		
		
		double aspect = (double)fv.book.fixedWidth/(double)fv.book.fixedHeight;
		int TH = this.fv.getRealHeight()/5;
		int TW = (int)((double)TH*(double)aspect);
		for (int i=0; i<fv.book.spine.size();i++) {
			ThumbnailView tv = new ThumbnailView(this,i,Color.rgb(220, 220, 220),listener);
        	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(TW,TH);
            layoutParams.setMargins(24,0,24,0);
            thumbnailsView.addView(tv,layoutParams);
		}		
		startThumbLoadTask();
	}
	
	public void showThumbnailBox() {
		if (this.thumbnailBox==null) return;
		int thumbnailBoxHeight = this.fv.getRealHeight()/5;
		int BM = 0; 
		BM = thumbnailBoxHeight/10;
		setFrame(this.thumbnailBox,0,this.fv.getRealHeight()-(thumbnailBoxHeight+BM),this.getWidth(),thumbnailBoxHeight);
		this.thumbnailBox.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() { 
            public void run() {
            	markThumbnail(currentPageInformation.pageIndex);            } 
		}, 200);		
	}
	
	public void hideThumbnailBox() {
		this.thumbnailBox.setVisibility(View.INVISIBLE);
		this.thumbnailBox.setVisibility(View.GONE);
	}
	
	public void markThumbnail(int pageIndex) {
		if (thumbnailsView==null) return;
		double tx = 0;
		ThumbnailView targetTv = null;
		for (int i=0; i<fv.book.spine.size();i++) {
			ThumbnailView tv = (ThumbnailView)this.thumbnailsView.getChildAt(i);
			if (tv!=null) tv.unmark();
		}
		targetTv = (ThumbnailView)this.thumbnailsView.getChildAt(pageIndex);
		if (targetTv!=null) {
			targetTv.mark();
			int sw = this.getWidth();
			tx = -sw/2+targetTv.getWidth()+targetTv.getX();
			thumbnailScrollView.scrollTo((int)tx,0);
		}
	}	
	
	private void displayNavPoints() {
		NavPoints nps = fv.getNavPoints();
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
		NavPoints nps = fv.getNavPoints();
		for (int i=0; i<nps.getSize(); i++) {
			NavPoint np = nps.getNavPoint(i);
			Button contentButton = new Button(this);
			contentButton.setBackgroundColor(Color.TRANSPARENT);
			contentButton.setText(np.text);			
			contentButton.setTextColor(Color.BLACK);
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
			NavPoints nps = fv.getNavPoints();
			targetNavPoint = nps.getNavPoint(index);
			new Handler().postDelayed(new Runnable() { 
	            public void run() {
	            	hideListBox();
	    			fv.gotoPageByNavPoint(targetNavPoint);
	            } 
			}, 200);			
		}
	};

	
	public void fillBookmarkList() {
		this.listView.removeAllViews();
		ArrayList <PageInformation> pis = sd.fetchBookmarks(this.bookCode);
		for (int i=0; i<pis.size(); i++) {
			int textColor = Color.BLACK;
			textColor = Color.BLACK;
			PageInformation pi = pis.get(i);
			SkyLayout item = new SkyLayout(this);
			setFrame(item, 0, 0, listBox.getWidth(),ps(80));
			ImageButton mark = this.makeImageButton(9898, R.drawable.bookmarked2x, ps(50),ps(90));
			item.addView(mark);
			setFrame(mark,ps(10),ps(5),ps(60),ps(120));
			int ci = pi.chapterIndex;
			String chapterTitle = fv.book.getChapterTitle(ci);
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
	            	hideListBox();
	    			fv.gotoPage(targetPI.chapterIndex);
	            } 
			}, 200);
		}				
	};
	
	public void fillHighlightList() {		
		int textColor = Color.BLACK;
		this.listView.removeAllViews();
		Highlights highlights = sd.fetchAllHighlights(this.bookCode);
		for (int i=0; i<highlights.getSize(); i++) {
			Highlight highlight = highlights.getHighlight(i);
			SkyLayout item = new SkyLayout(this);	
			int ci = highlight.chapterIndex;
			String chapterTitle = fv.book.getChapterTitle(ci);
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
					fv.deleteHighlight(target);
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
	            	hideListBox();
	    			fv.gotoPageByHighlight(targetHighlight);
	            } 
			}, 200);
		}				
	};
	
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
	
	
	void hideBoxes() {
		this.hideColorBox();
		this.hideHighlightBox();
		this.hideMenuBox();
		this.hideNoteBox();
		this.hideSearchBox();
		this.hideListBox();
		this.hideControlBox();
		this.hideMediaBox();
		this.hideThumbnailBox();
	}
	
    
    public void startCaching() {
    	fv.startCaching();
    }
    
	class KeyDelegate implements KeyListener {
		@Override
		public String getKeyForEncryptedData(String uuidForContent, String contentName, String uuidForEpub) {
			// TODO Auto-generated method stub
			return "test";
		}

		@Override
		public Book getBook() {
			// TODO Auto-generated method stub
			return fv.getBook();
		}		
	}
	
	public void toggleBookmark() {
		sd.toggleBookmark(currentPageInformation);
		changeBookmarkButton();
	}
	
	private void changeRotationButton() {
		int filterColor = 0xFFAAAAAA;
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
		rotationButton.setColorFilter(filterColor);
	}
	
	public void changeBookmarkButton() {
		Drawable icon;
		int imageId = R.drawable.bookmark2x;
		int bw = ps(42);
		int bh = ps(42);
		if (this.isBookmarked()) {
			imageId = R.drawable.bookmarked2x;
			bw = ps(38);
			bh = ps(58);
		}else {
			imageId = R.drawable.bookmark2x;
			bw = ps(42);
			bh = ps(42);
		}		
		icon = getResources().getDrawable(imageId);
		icon.setBounds(0,0,bw,bh);
		Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, bw, bh, false);
		bookmarkButton.setImageBitmap(bitmapResized);
		if (this.isBookmarked()) {
			bookmarkButton.setColorFilter(Color.RED);
		}else {
			bookmarkButton.setColorFilter(filterColor);
		}
		this.setSize(bookmarkButton,bw,bh);
	}


	private OnClickListener listener=new OnClickListener(){
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
				processFinish();			
			}

			if (arg.getId()==6000) {
				// highlightMenuButton
				fv.makeSelectionHighlight(currentColor);				
				hideMenuBox();
				showHighlightBox();
			}else if (arg.getId()==6001) {
				fv.makeSelectionHighlight(currentColor);
				hideMenuBox();
				showNoteBox();
			}	
			
			if (arg.getId()==6002) {
				// Color Chooser 
				hideHighlightBox();
				showColorBox();
			}else if (arg.getId()==6003) {
				hideHighlightBox();
				fv.deleteHighlight(currentHighlight);
			}else if (arg.getId()==6004) {
				hideHighlightBox();
				showNoteBox();
			}
			
			int color;
			if (arg.getId()==6010) {
				color = getColorByIndex(0);
				fv.changeHighlightColor(currentHighlight,color);
			}else if (arg.getId()==6011) {
				color = getColorByIndex(1);
				fv.changeHighlightColor(currentHighlight,color);
			}else if (arg.getId()==6012) {
				color = getColorByIndex(2);
				fv.changeHighlightColor(currentHighlight,color);
			}else if (arg.getId()==6013) {
				color = getColorByIndex(3);
				fv.changeHighlightColor(currentHighlight,color);
			} 
			
			if (arg.getId()==3001) {
				cancelPressed();
			}else if (arg.getId()==3093) {
				// search More
				removeLastResult();
//				showToast("Search More...");
				fv.searchMore();
			}else if (arg.getId()==3094) {
				removeLastResult();
				hideSearchBox();
				// stopSearch
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
			
			if (arg.getId()==9999) {
				hideOutsideButton();
				hideBoxes();
			}
			if (arg.getId()==9000) {
				rotationPressed();
				
			}else if (arg.getId()==9001 || arg.getId()==9009) {
				listPressed();				
			}else if (arg.getId()==9002) {
				
			}else if (arg.getId()==9003) {
				showSearchBox();				
			}else if (arg.getId()==9004) {
				toggleBookmark();
			} 
			
			// list processing
			if (arg.getId()==2700) {
				checkListButton(0);				
			}else if (arg.getId()==2701) {
				checkListButton(1);
			}else if (arg.getId()==2702) {
				checkListButton(2);
			} 
			
			// thumbnail
			if (arg.getId()>=8000 && arg.getId()<8999) {
				int pageIndex = arg.getId()-8000;
//				toggleControls();
				fv.gotoPage(pageIndex);
			}
			
			if (arg.getId()==9099) {
				toggleControls();				
			}
		}
	};
	
	int getColorByIndex(int colorIndex) {
		int color;
		if (colorIndex==0) {
			color = Color.argb(255, 238, 230, 142);
		}else if (colorIndex==1) {
			color = Color.argb(255, 218, 244, 160);
		}else if (colorIndex==2) {
			color = Color.argb(255, 172, 201, 246);
		}else if (colorIndex==3) {
			color = Color.argb(255, 249, 182, 214);
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
	
	class SelectionDelegate implements SelectionListener {
		@Override
		public void selectionStarted(Highlight highlight, Rect startRect,
				Rect endRect) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void selectionChanged(Highlight highlight, Rect startRect,
				Rect endRect) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void selectionEnded(Highlight highlight, Rect startRect,
				Rect endRect) {
			// TODO Auto-generated method stub
			currentHighlight  = highlight;
			currentHighlight.color = currentColor;
			showMenuBox(startRect,endRect);
		}

		@Override
		public void selectionCancelled() {
			// TODO Auto-generated method stub
			hideBoxes();
		}
		
	}
	
	class HighlightDelegate implements HighlightListener {

		@Override
		public void onHighlightDeleted(Highlight highlight) {
			// TODO Auto-generated method stub
			sd.deleteHighlight(highlight);
		}

		@Override
		public void onHighlightInserted(Highlight highlight) {
			// TODO Auto-generated method stub
			currentHighlight = highlight;
			currentColor = currentHighlight.color;
			sd.insertHighlight(highlight);			
		}

		@Override
		public void onHighlightUpdated(Highlight highlight) {
			// TODO Auto-generated method stub
			sd.updateHighlight(highlight);			
		}

		@Override
		public void onHighlightHit(Highlight highlight, int x, int y,
				Rect startRect, Rect endRect) {
			// TODO Auto-generated method stub
			currentHighlight = highlight;
			currentColor = currentHighlight.color;
			showHighlightBox(startRect,endRect);			
		}

		@Override
		public Highlights getHighlightsForChapter(int chapterIndex) {
			// TODO Auto-generated method stub
			return sd.fetchHighlights(bookCode, chapterIndex);
		}

		@Override
		public Bitmap getNoteIconBitmapForColor(int color, int style) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onNoteIconHit(Highlight highlight) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Rect getNoteIconRect(int color, int style) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onDrawHighlightRect(Canvas canvas, Highlight highlight,
				Rect highlightRect) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDrawCaret(Canvas canvas, Caret caret) {
			// TODO Auto-generated method stub
			
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
				fv.pauseSearch();
				numberOfSearched = searchResult.numberOfSearched;
			}else {
				fv.searchMore();
				numberOfSearched = searchResult.numberOfSearched;
			}
		}

		public void onSearchFinished(SearchResult searchResult) {
			debug("Searching is finished. ");
			addSearchResult(searchResult,2);
			hideIndicator();
		}
	}

	
	
	class MediaOverlayDelegate implements MediaOverlayListener {
		@Override
		public void onParallelStarted(Parallel parallel) {
			// TODO Auto-generated method stub
			if (setting.highlightTextToVoice) {
				fv.changeElementColor("#FFFF00",parallel.hash,parallel.pageIndex);
			}
			currentParallel = parallel;			
		}

		@Override
		public void onParallelEnded(Parallel parallel) {
			// TODO Auto-generated method stub
			if (setting.highlightTextToVoice) {
				fv.restoreElementColor();
			}
		}

		@Override
		public void onParallelsEnded() {
			// TODO Auto-generated method stub
			if (setting.highlightTextToVoice) {
				fv.restoreElementColor();
			}
		    if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = true;
		    if (autoMovePageWhenParallesFinished) {
		        fv.gotoNextPage();
		    }
		}		
	}
	
	void playAndPause() {
		if (fv.isPlayingPaused()) {
	        if (!fv.isPlayingStarted()) {
	            fv.playFirstParallel();	        	
	            if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = true;
	        }else {
	            fv.resumePlayingParallel();	        	
	            if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = true;
	        }        
	    
	    }else {	        
	        fv.pausePlayingParallel();
	        if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = false;
	    }
		this.changePlayAndPauseButton();
	}
	
	void stopPlaying() {
	    fv.stopPlayingParallel();
	    fv.restoreElementColor();
	    if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = false;
	    this.changePlayAndPauseButton();
	}
	
	void playPrev() {
	    fv.restoreElementColor();
	    if (currentParallel.parallelIndex==0) {
	        if (autoMovePageWhenParallesFinished) fv.gotoPrevPage();
	    }else {
	        fv.playPrevParallel();
	    }
	}

	void playNext() {
	    fv.restoreElementColor();
		fv.playNextParallel();
	}
	
	public void onStop() {
		super.onStop();
//		debug("onStop");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		sd.updatePosition(bookCode, pagePositionInBook);
		sd.updateSetting(setting);
		
		fv.stopPlayingParallel();
		fv.restoreElementColor();
		fv.stopCaching();

		stopThumbLoadTask();		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		fv.playFirstParallel();
		
		new Handler().postDelayed(
				new Runnable() { 
					public void run() {
						startCaching();
					}
				}
		,1000);		
	}

	public void onDestory() {
		this.unregisterSkyReceiver(); // New in SkyEpub sdk 7.x
		super.onDestroy();
		debug("onDestory");		
	}
	
	@Override
	 public void onBackPressed() {
		if (this.isBoxesShown) {
			hideBoxes();
		}else if (this.isControlsShown) {
			toggleControls();
		}else {
			processFinish();
		}
	 }
	
	public boolean isPortrait() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation==Configuration.ORIENTATION_PORTRAIT) return true;
		else return false;	
	}
	

	
	private void changePlayAndPauseButton() {
		Drawable icon;
		int imageId;
		if (!fv.isPlayingStarted() || fv.isPlayingPaused()) {
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

	boolean isInitialized = false;
	class PageMovedDelegate implements PageMovedListener {
		public void onPageMoved(PageInformation pi) {
			currentPageInformation = pi;
			pagePositionInBook = pi.pageIndex;
			String msg = String.format("pn:%d/tn:%d ps:%f",pi.pageIndex,pi.numberOfPagesInChapter,pi.pagePositionInBook);
//			Log.w("EPub",msg);
//			Log.w("EPub"," "+fv.getPageIndex()+"/"+fv.getPageCount());
			
	        if (fv.isMediaOverlayAvailable() && setting.mediaOverlay) {
	        	showMediaBox();
	        	if (isAutoPlaying) {
	        		fv.playFirstParallel();
	        	}
	        }else {
	        	hideMediaBox();
	        }
	        
	        fv.setUserInteractionEnabled(false);
	        new Handler().postDelayed(
	        		new Runnable() { 
	        			public void run() {
	        				fv.setUserInteractionEnabled(true);		
	        			}
	        		}
	        ,100);	        
	        changeBookmarkButton();
	        if (!isInitialized) {
				fillThumbnailBox();
	        	isInitialized = true;
	        }
	        markThumbnail(pi.pageIndex);
		}
		
		public void onChapterLoaded(int chapterIndex) {
			// do nothing in FixedLayout. 
		}

		@Override
		public void onFailedToMove(boolean toForward) {
			// TODO Auto-generated method stub
			if (toForward) {
				showToast("This is the last page");
			}else {
				showToast("This is the first page");
			}
		}
	}
	
	public void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			view.setEnabled(enabled);
			if (view instanceof ViewGroup) {
				enableDisableViewGroup((ViewGroup) view, enabled);
			}
		}
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
	
	public void setFrame(View view,Rect rect) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		this.setFrame(view, rect.left,rect.top,rect.width(),rect.height());			
	}
	
	public void setSize(View view, int width,int height) {
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)view.getLayoutParams();
		int ox = param.leftMargin;
		int oy = param.topMargin;
		this.setFrame(view, ox,oy,width,height);
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
	
	public int getDensityDPI() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int density = metrics.densityDpi;
		return density;
	}
	
	public int getPS(float dip) {
		float densityDPI = this.getDensityDPI();
		int px = (int)(dip*(densityDPI/240));
		return px;		
//		int px = (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics()));
//		return px;
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
	
	public int getWidth() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		return width;		
	}
	
	public int getHeight() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int height = metrics.heightPixels;
		return height;			
	}
	
	public ImageButton makeImageButton(int id,String imageName,int width, int height) {
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
		Drawable icon;
		ImageButton button = new ImageButton(this);	
		button.setId(id);
		button.setOnClickListener(listener);
		button.setBackgroundColor(Color.TRANSPARENT);
		icon = this.getDrawableFromAssets(imageName);
		icon.setBounds(0,0,width,height);
		Bitmap iconBitmap = ((BitmapDrawable)icon).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, width, height, false);
		button.setImageBitmap(bitmapResized);
		button.setVisibility(View.VISIBLE);
		param.width = 		width;		
		param.height = 		height;
		button.setLayoutParams(param);		
		button.setOnTouchListener(new ImageButtonHighlighterOnTouchListener(button));
		
		
		return button;		
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
		button.setOnTouchListener(new ImageButtonHighlighterOnTouchListener(button));
		return button;		
	}

	
	Drawable getDrawableFromAssets(String name) {
		try {
//			InputStream ims = getResources().getAssets().open(name);
//			Drawable d = Drawable.createFromStream(ims, null);
			Drawable d = Drawable.createFromStream(getAssets().open(name), null);
			return d;
		}catch(Exception e) {
			return null;
		}	
	}
	
	class ImageButtonHighlighterOnTouchListener implements OnTouchListener {
		  final ImageButton button;

		  public ImageButtonHighlighterOnTouchListener(final ImageButton button) {
		    super();
		    this.button = button;
		  }
		  
		  @Override
		  public boolean onTouch(final View view, final MotionEvent motionEvent) {
			  if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				  button.setColorFilter(Color.WHITE);
			  } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				  button.setColorFilter(filterColor);
			  }
		    return false;
		  }
	}
	
	public void showToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
 	ProgressBar progressBar;
	
	public void makeIndicator() {
		progressBar = new ProgressBar(this, null,android.R.attr.progressBarStyleSmallInverse);		
		ePubView.addView(progressBar);		
		this.hideIndicator();
	}
	
	public void showIndicator() {
		RelativeLayout.LayoutParams params = 
			    (RelativeLayout.LayoutParams)progressBar.getLayoutParams();		
		params.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
		progressBar.setLayoutParams(params);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();
	}
	
	public void showPieView() {
		this.pieView.setVisibility(View.VISIBLE);
		this.pieBack.setVisibility(View.VISIBLE);
	}
	
	public void hidePieView() {
		this.pieView.setVisibility(View.INVISIBLE);
		this.pieBack.setVisibility(View.INVISIBLE);
		this.pieView.setVisibility(View.GONE);
		this.pieBack.setVisibility(View.GONE);
	}
	
	public void hideIndicator() {
		if (progressBar!=null) {
			progressBar.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}
	
	public void gotoPageBySearchResult(SearchResult sr,int color) {
//		showToast(sr.text);
		fv.gotoPageBySearchResult(sr,color);
	}
	
	class StateDelegate implements StateListener {
		public void onStateChanged(State state) {
			if (state==State.LOADING) {
				showIndicator();
			}else if (state==State.ROTATING) {
				showIndicator();				
			}else if (state==State.BUSY) {				
				showIndicator();
			}else if (state==State.NORMAL) {				
				hideIndicator();
			}
		}		
	}
	
	private String getCachePath(int pageIndex) {
		String prefix = this.getCacheFolder();
		String name = String.format("sb%d-cache%d.jpg",this.bookCode,pageIndex);
	    String filePath = prefix+"/"+name;		
		return filePath;
	}
	
	private String getCacheFolder() {
		String dir = SkySetting.getStorageDirectory() + "/caches";
		File folder = new File(dir);
		if (!folder.exists()) folder.mkdirs();		
		return dir;		
	}	
	
	private void saveBitmap(Bitmap bitmap,String filePath) {
		try {		    
			if (bitmap==null) return;
			FileOutputStream out = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);			
			out.flush();
			out.close();
			
		} catch (Exception e) {
		       e.printStackTrace();
		}		
	}
	
	public Bitmap getBitmpFromThumbnail(int pageIndex) {
		try {
			ThumbnailView tv = getThumbnailView(pageIndex);
			if (tv!=null) return tv.getBitmap();
			else return null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Bitmap getBitmap(String filePath) {
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				Log.w("EPub","file length "+file.length()+" for "+filePath);
				bitmap = BitmapFactory.decodeFile(filePath);
			}else {
				Log.w("EPub",filePath + " does not exist");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public Bitmap getBitmap(int pageIndex) {
		try {
			Bitmap bitmap = null;
			String filePath = getCachePath(pageIndex);
		    File file = new File(filePath);
		    if (file.exists()) {
		    	// uses bitmap inside thumbnail rather than inside file because of speed. 
//		    	bitmap = getBitmpFromThumbnail(pageIndex);
		    	if (bitmap==null) {
		    		bitmap =  this.getBitmap(filePath);
		    	}
		    	Bitmap target = bitmap.copy(bitmap.getConfig(), true);
		    	return target;
		    }else {
		    	return null;
		    }
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	class CacheDelegate implements CacheListener {
		@Override
		public void onCachingStarted(int numberOfUncached) {
//			showToast("onCachingStarted");
		}

		@Override
		public void onCachingFinished(int numberOfCached) {
//			showToast("onCachingFinished");
		}


		@Override
		public void onCached(int pageIndex, Bitmap bitmap, double progress) {
			// TODO Auto-generated method stub
			final Bitmap targetBitmap = bitmap;
//			showToast("onCached for "+pageIndex);
			String filePath = getCachePath(pageIndex);
			saveBitmap(targetBitmap,filePath);
			final ThumbnailView tv = getThumbnailView(pageIndex);
			new Handler().post(
				new Runnable() {
					public void run() {						
						if (tv!=null && !tv.isImageLoaded) {
							tv.loadBitmap(targetBitmap);	 
						}
					}
				}
			);		    			
						
		}

		@Override
		public boolean cacheExist(int pageIndex) {
			// TODO Auto-generated method stub			
			String filePath = getCachePath(pageIndex);
		    File file = new File(filePath);
		    if (file.exists()) {
		    	return true;		    
		    }
			return false;
		}

		@Override
		public Bitmap getCachedBitmap(int pageIndex) {
			Bitmap bitmap = getBitmap(pageIndex);
			return bitmap;
		}		
	}
	
	Timer conntrolTimer = new Timer();
	
	private void startControlBoxTimer() {
		conntrolTimer = new Timer();
		ControlBoxTask at = new ControlBoxTask();
		conntrolTimer.schedule(at,2000); 
	}
	
	
	class ControlBoxTask extends TimerTask {
		public void run() {
			if (isControlsShown) {
				hideControlsHandler.obtainMessage(1).sendToTarget();
			}
		}
	}
	
	class ClickDelegate implements ClickListener {
		@Override
		public void onClick(int x,int y) {
			Log.w("EPub","click detected");
			int ch = getHeight()/7;
			if (!isMenuBoxShown) {
				if (!isControlsShown) {
					if (y<=ch) {
						toggleControls();
					}
				}else {
					toggleControls();
				}
			}
		}
		
		public void onImageClicked(int x,int y,String src) {}
		public void onLinkClicked(int x,int y,String href) {}
		@Override
		public boolean ignoreLink(int x,int y,String href) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public void onLinkForLinearNoClicked(int x, int y, String href) {}
		@Override
		public void onIFrameClicked(int x, int y, String src) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onVideoClicked(int x, int y, String src) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onAudioClicked(int x, int y, String src) {
			// TODO Auto-generated method stub
			
		}
	}
}
