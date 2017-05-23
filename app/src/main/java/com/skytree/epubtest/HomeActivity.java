package com.skytree.epubtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.skytree.epub.BookInformation;
import com.skytree.epub.SearchResult;
import com.skytree.epub.Setting;
import com.skytree.epubtest.BookViewActivity.ButtonHighlighterOnTouchListener;
import com.skytree.epubtest.BookViewActivity.ImageButtonHighlighterOnTouchListener;
import com.skytree.epubtest.SkyDrawable;
import com.skytree.epubtest.LocalService.LocalBinder;
import com.skytree.epubtest.BookViewActivity;
import com.skytree.epubtest.MagazineActivity;
import com.skytree.epubtest.R;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView.OnEditorActionListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Region;

public class HomeActivity extends Activity {
	LinearLayout homeView = null; 
	RelativeLayout topView = null;
	View dummyView = null;
	SkyDatabase sd;
	SkyUtility  st;
	final private String TAG = "EPub"; 
	
	LinearLayout bodyView = null;
	SkyGridView gridView;
	SkyGridAdapter gridAdapter;
	
	Intent serviceIntent;
	boolean isBound = false;
	
	LocalService ls = null;
	Timer startTimer = null;
	SkyApplication app;
	
	final String RELOAD_ACTION = "com.skytree.android.intent.action.RELOAD";
	final String RELOADBOOK_ACTION = "com.skytree.android.intent.action.RELOADBOOK";	
	final String PROGRESS_ACTION = "com.skytree.android.intent.action.PROGRESS";
	
	SkyReceiver reloadReceiver;
	SkyReceiver progressReceiver;	
	SkyReceiver reloadBookReceiver;
	
	ImageButton libraryButton;
	ImageButton searchButton;
	ImageButton sortButton;
	ImageButton gridButton;
	ImageButton settingButton;
	
	RelativeLayout topSearchView;
	EditText searchEdit; 
	boolean isSearchMode = false;
	
	
	int gridType = 1; // 0= itemGrid , 1=detailGrid;
	BookInformation lastBook = null;
	
	public class SkyReceiver extends BroadcastReceiver{		 
	    @Override
	    public void onReceive(Context context, Intent intent) {	         
	        if (intent.getAction().equals(PROGRESS_ACTION)) {
	        	int bookCode = intent.getIntExtra("BOOKCODE",-1);
	        	int bytes_downloaded = intent.getIntExtra("BYTES_DOWNLOADED",-1);
	        	int bytes_total = intent.getIntExtra("BYTES_TOTAL",-1);
	        	double percent = intent.getDoubleExtra("PERCENT",0);	        	
//	        	debug("Receiver BookCode:"+bookCode+" "+percent);
        		Message msg = new Message();
                Bundle b = new Bundle();
                b.putInt("BOOKCODE", bookCode);
                b.putInt("BYTES_DOWNLOADED", bytes_downloaded);
                b.putInt("BYTES_TOTAL", bytes_total);
                b.putDouble("PERCENT", percent);                
                msg.setData(b);
                new Handler() {
        	        @Override
        	        public void handleMessage(Message msg) {
        	        	int bookCode = msg.getData().getInt("BOOKCODE");
        	        	int bytes_downloaded = msg.getData().getInt("BYTES_DOWNLOADED");
        	        	int bytes_total = msg.getData().getInt("BYTES_TOTAL");
        	        	double percent = msg.getData().getDouble("PERCENT");
        	        	refreshPieView(bookCode,percent);
        	        }                	
                }.sendMessage(msg);
	        }else if  (intent.getAction().equals(RELOAD_ACTION)) {
                debug("Reload Requested");
                reload();                
	        }else if (intent.getAction().equals(RELOADBOOK_ACTION)) {
	        	int bookCode = intent.getIntExtra("BOOKCODE",-1);	        	
                reload(bookCode);                
	        } 
	    }
	}
	
	public boolean isPortrait() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation== Configuration.ORIENTATION_PORTRAIT) {
			return true;
		}else {
			return false;
		}
	}

	
	public int getPositionByBookCode(int bookCode) {
		int position = -1;
		for (int i=0; i<app.bis.size(); i++) {
			BookInformation bi = app.bis.get(i);
			if (bi.bookCode==bookCode) {
				position = i;
				break;
			}
		}
		return position;		
	}
	
	public SkyPieView getSkyPieView(int position) {
		// PieView확보
		ViewGroup parent = (RelativeLayout)this.gridView.getChildAt(position);
		if (parent==null) return null;
		if (gridType==0) {
			parent = (ViewGroup) parent.getChildAt(0);
			for (int i=0; i<parent.getChildCount(); i++) {
				View cv = parent.getChildAt(i);
				if (cv.getId()==7080) {
					return (SkyPieView)cv;
				}
			}
		}else {
			parent = (ViewGroup) parent.getChildAt(0);
			parent = (ViewGroup) parent.getChildAt(0);
			for (int i=0; i<parent.getChildCount(); i++) {
				View cv = parent.getChildAt(i);
				if (cv.getId()==7080) {
					return (SkyPieView)cv;
				}
			}		
		}
		return null;
	}
	
	public void refreshPieView(int bookCode,double percent) {
		// 포지션 확보 
		int position = this.getPositionByBookCode(bookCode);
		if (position==-1) {
			debug("Failed to get Position By bookCode");
			return;
		}
		SkyPieView pv = this.getSkyPieView(position);
		if (pv==null) {
			debug("Failed to get Pie By position");
			return;
		}
		pv.setValue(percent);
		pv.invalidate();
	}
	
//	public boolean isTablet() {
//	    return (getResources().getConfiguration().screenLayout
//	            & Configuration.SCREENLAYOUT_SIZE_MASK)
//	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
//	}
	
	public static final int TABLET_MIN_DP_WEIGHT = 450;  
	
//	public boolean isTablet(){
//		
//	    DisplayMetrics metrics = new DisplayMetrics();
//	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//	    int dpi = 0;
//	    if (metrics.widthPixels < metrics.heightPixels){
//	        dpi = (int) (metrics.widthPixels / metrics.density);
//	    }
//	    else{
//	        dpi = (int) (metrics.heightPixels / metrics.density);
//	    }
//
//	    if (dpi < TABLET_MIN_DP_WEIGHT)         return false;
//	    else                                    return true;
//	}
	
	public boolean isTablet() {
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
			return true;
		}else{
			return false;
		}
	}
	
	public void makeFullScreen() {
		SkyUtility.makeFullscreen(this);
	}
	
	public void registerFonts() {
		this.registerCustomFont("Underwood","uwch.ttf");
		this.registerCustomFont("Mayflower","Mayflower Antique.ttf");		
	}
	
	public void registerCustomFont(String fontFaceName,String fontFileName) {
		st.copyFontToDevice(fontFileName);
		app.customFonts.add(new CustomFont(fontFaceName,fontFileName));
	}

	public String getApplicationName() {
	    int stringId = this.getApplicationInfo().labelRes;
	    return this.getString(stringId);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		app = (SkyApplication)getApplication();
		doBindService();
		st = new SkyUtility(this);
		st.makeSetup();
		this.registerFonts();
		this.makeLayout();		
    	this.reload();
    	Setting.prepare();        
	}
	@Override
    public void onStart(){
    	super.onStart();
    }	
	
    @Override
    public void onResume(){
    	super.onResume();
    	IntentFilter filter0 = new IntentFilter(RELOAD_ACTION);
    	reloadReceiver = new SkyReceiver();
    	registerReceiver(reloadReceiver, filter0);    	
    	IntentFilter filter1 = new IntentFilter(PROGRESS_ACTION);
    	progressReceiver = new SkyReceiver();
    	registerReceiver(progressReceiver, filter1);
    	IntentFilter filter2 = new IntentFilter(RELOADBOOK_ACTION);
    	reloadBookReceiver = new SkyReceiver();
    	registerReceiver(reloadBookReceiver, filter2);     
    	if (lastBook!=null) {
    		this.reload(lastBook);
    	}
		this.makeFullScreen();
    }    

	@Override
	public void onPause(){
		super.onPause();
		if(reloadReceiver != null) this.unregisterReceiver(reloadReceiver);
		if(progressReceiver != null) this.unregisterReceiver(progressReceiver);
		if(reloadBookReceiver != null) this.unregisterReceiver(reloadBookReceiver);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unbindService(mConnection);
	}
	
    private ServiceConnection mConnection = new ServiceConnection() {
        /* 두번째 파라미터 service는 Service의 onBind()가 리턴한 LocalBinder 인스턴스이다 */
        public void onServiceConnected(ComponentName className,IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            ls = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
	
	void doBindService() {
	    Intent intent = new Intent(this, LocalService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	    isBound = true;
	}

	void doUnbindService() {
	    if (isBound) {
	        unbindService(mConnection);
	        isBound = false;
	    }
	}
	
	public int px(int dp) {
		return SkyUtility.getPX(this, dp);
	}
	
	public void reload() {
		app.reloadBookInformations();
		this.recalcGridFactors();
		this.gridAdapter.reset();
		this.gridView.invalidateViews();
		this.gridView.setAdapter(gridAdapter);
	}	
	
	public void reload(BookInformation bi) {
		gridAdapter.removeItem(bi);
		app.reloadBookInformations();
		gridView.invalidateViews();			
	}
	
	public void reload(int bookCode) {
		gridAdapter.removeItem(bookCode);
		app.reloadBookInformations();
		gridView.invalidateViews();
	}
	
	public void reload(String key) {
		app.reloadBookInformations(key);
		this.gridAdapter.reset();
		this.gridView.invalidateViews();
		this.gridView.setAdapter(gridAdapter);		
	}
	
	
	public void beep(int ms) {
		Vibrator vibe = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(ms);	
	}
	
	class ImageButtonHighlighterOnTouchListener implements OnTouchListener {
		  int oldColor;
		  
		  @Override
		  public boolean onTouch(final View view, final MotionEvent motionEvent) {
			  ImageButton button = (ImageButton)view;
			  if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				  button.setBackgroundColor(Color.argb(165,255,255,255));
				  beep(1);
			  } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				  button.setBackgroundColor(Color.TRANSPARENT);
			  }
			  
		    return false;
		  }
	}
	
	public void libraryButtonClick() {
		// when you need to install epub from file system, just use ls.installBook
		ls.installBook("file://android_asset/books/Alice.epub");
		ls.installBook("file://android_asset/books/TestEpub3.1.epub");
		ls.installBook("file://android_asset/books/Master-One.epub");
		// when you have to download and install from remote server, use ls.startDownload
		ls.startDownload("http://scs.skyepub.net/samples/Alice.epub","","Alice's Adventures","Lewis Carroll");
		ls.startDownload("http://scs.skyepub.net/samples/Saadi.epub","","سعدی‎","سعدی‎");
		ls.startDownload("http://scs.skyepub.net/samples/Doctor.epub","","시골의사 박경철의 자기혁명","박경철");
	}
	
	public void searchButtonClick() {
		this.showSearchView();
	}
	
	public void sortButtonClick() {
		SkySortDialog dialog = new SkySortDialog(this);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();		
	}
	
	public void settingButtonClick() {
		this.openSetting();		
	}
	
	private void toggleGridButton() {
		int rid;
		if (gridType==0) rid = R.drawable.grid;
		else rid = R.drawable.list;
		int bs = 22;
		SkyUtility.changeImageButton(this,gridButton,rid,px(bs),px(bs));
	}
	
	public void gridButtonClick() {
		if (gridType==0) gridType=1;
		else  gridType =0; 		
		toggleGridButton();
		this.reload();
		
	}
	
	public void closeSearchButtonClick() {	
		if (isSearchMode) {
			if (searchEdit.getText().length()!=0) {
				searchEdit.setText("");
			}else {
				searchEdit.setText("");
				this.hideSearchView();
				this.reload();
			}
		}
	}
	
	public void dismissKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	public void showKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);		
	}
	

	
	private OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View arg) {
			if (arg.getId() == 1001) {
				libraryButtonClick();
			}
			if (arg.getId() == 1002) {
				searchButtonClick();
			}
			if (arg.getId() == 1003) {
				sortButtonClick();
			}
			if (arg.getId() == 1004) {
				gridButtonClick();
			}
			if (arg.getId() == 1005) {
				settingButtonClick();
			}
			if (arg.getId() == 9009) {
				closeSearchButtonClick();
			}
		}
	};
	
	private void showToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void makeLayout() {	
		homeView = new LinearLayout(this);
		homeView.setOrientation(LinearLayout.VERTICAL);
		homeView.setBackgroundResource(R.drawable.homeground);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		homeView.setLayoutParams(lp);	
		
		topView = this.makeTopView();
		bodyView = this.makeBodyView();
        
        homeView.addView(topView);
        homeView.addView(bodyView);
        
        this.toggleGridButton();
        this.hideSearchView();
		setContentView(homeView);		
	}
	
	private RelativeLayout makeTopView() {		
		RelativeLayout topView = new RelativeLayout(this);
		topView.setBackgroundResource(R.drawable.topground);
		LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,px(38));
        topView.setLayoutParams(tlp);
        
        LinearLayout topLeftView = new LinearLayout(this);
        RelativeLayout.LayoutParams tlvp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        tlvp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tlvp.leftMargin = px(10);
        topLeftView.setLayoutParams(tlvp);
        tlvp.setMargins(px(10), 0, 0,0);
        topLeftView.setPadding(0, px(3), 0, px(0));

        LinearLayout topRightView = new LinearLayout(this);
        RelativeLayout.LayoutParams trvp = new RelativeLayout.LayoutParams(px(180),LayoutParams.FILL_PARENT);
        trvp.setMargins(0, 0, px(5),0);
        trvp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        topRightView.setLayoutParams(trvp);
        topRightView.setPadding(0, px(3), px(0),0);
        ImageButtonHighlighterOnTouchListener onTouchListener = new ImageButtonHighlighterOnTouchListener();
        int bs = 22;
        libraryButton = SkyUtility.makeImageButton(this,1001,R.drawable.library, px(bs), px(bs),onClickListener,onTouchListener);
        searchButton = SkyUtility.makeImageButton(this,1002,R.drawable.searchblack, px(bs), px(bs),onClickListener,onTouchListener);
        sortButton = SkyUtility.makeImageButton(this,1003,R.drawable.sort, px(bs), px(bs),onClickListener,onTouchListener);
        gridButton = SkyUtility.makeImageButton(this,1004,R.drawable.grid, px(bs), px(bs),onClickListener,onTouchListener);
        settingButton = SkyUtility.makeImageButton(this,1005,R.drawable.setting, px(bs), px(bs),onClickListener,onTouchListener);
		
        int bc = Color.BLACK;
		libraryButton.setColorFilter(bc);
		searchButton.setColorFilter(bc);
		sortButton.setColorFilter(bc);
		gridButton.setColorFilter(bc);
		settingButton.setColorFilter(bc);
        
        topLeftView.addView(libraryButton);
		topRightView.addView(searchButton);
		topRightView.addView(sortButton);
		topRightView.addView(gridButton);
		topRightView.addView(settingButton);
		
		topSearchView = new RelativeLayout(this);		
		RelativeLayout.LayoutParams tsvp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		topSearchView.setLayoutParams(tsvp);
		topSearchView.setPadding(px(55), px(5), px(55),px(5));
		searchEdit = this.makeSearchEdit();
		SkyUtility.setSize(searchEdit, 0, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		topSearchView.addView(searchEdit);
		ImageButton closeButton = SkyUtility.makeImageButton(this, 9009, R.drawable.close, px(22), px(22), onClickListener,onTouchListener);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, px(-35));
		closeButton.setLayoutParams(lp);
		topSearchView.addView(closeButton);
		
        topView.addView(topLeftView);
        topView.addView(topRightView);
		topView.addView(topSearchView);
        
        return topView;		
	}	
	
	public void showSearchView() {
		searchEdit.setText("");
		SkyUtility.hide(searchButton);
		SkyUtility.hide(sortButton);
		SkyUtility.hide(gridButton);		
		SkyUtility.show(topSearchView);
		isSearchMode = true;
		searchEdit.setFocusable(true);
		searchEdit.setFocusableInTouchMode(true);
		(new Handler()).postDelayed(new Runnable() {
			public void run() {
				searchEdit.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
				searchEdit.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));   
			}
		},100);		
	}
	
	public void hideSearchView() {
		dismissKeyboard();
		SkyUtility.show(searchButton);
		SkyUtility.show(sortButton);
		SkyUtility.show(gridButton);
		SkyUtility.hide(topSearchView);		
		isSearchMode = false;
	}
	
	public EditText makeSearchEdit() {
		final EditText searchEditor = new EditText(this);
		searchEditor.setTextSize(15f);
		searchEditor.setEllipsize(TruncateAt.END);
		searchEditor.setBackgroundColor(0x33FFFFFF);
		Drawable icon = getResources().getDrawable(R.drawable.searchinbox);		
		int bs = 18;
		Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
		Drawable fd = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, px(30),px(bs), true));
		searchEditor.setCompoundDrawablesWithIntrinsicBounds(fd,null,null,null);
		searchEditor.setHint(getString(R.string.searchhint));
		searchEditor.setLines(1);
		searchEditor.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		searchEditor.setSingleLine();
		searchEditor.setPadding(px(20), px(0), px(20),0);
		int rs = 4;
		RoundRectShape rrs = new RoundRectShape(new float[] { px(rs),px(rs),px(rs),px(rs),px(rs),px(rs),px(rs),px(rs)}, null, null);
		SkyDrawable sd = new SkyDrawable(rrs,0x33FFFFFF,0x88FFFFFF,2);
		searchEditor.setBackgroundDrawable(sd);
		searchEditor.setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_NEXT){
					String key = searchEditor.getText().toString();
					if (key!=null && key.length()>1) {
						processSearch(key);
					}
				}
				return false;
			}
		});
		return searchEditor;		
	}
	
	public void processSearch(String key) {
		if (key==null || key.length()<=1 || key.isEmpty()) return;
		this.dismissKeyboard();
		this.reload(key);
	}

	
	private LinearLayout makeBodyView() {
        LinearLayout bodyView = new LinearLayout(this);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        bodyView.setLayoutParams(bp);
        gridView = new SkyGridView(this);
        int cn = this.getNumColumns();
        gridView.setNumColumns(cn);
        gridView.setHorizontalSpacing(px(20));
        gridView.setVerticalSpacing(px(20));
        gridView.setPadding(px(20), px(20), px(20), px(20));
        gridView.setOnItemClickListener(onClick);
        gridView.setOnItemLongClickListener(onLongClick);
        
        gridAdapter = new SkyGridAdapter(this);
        LayoutParams gp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(gp);
        bodyView.addView(gridView);
        
        return bodyView;		
	}
	
	AdapterView.OnItemClickListener onClick = new OnItemClickListener(){
    	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
    		BookInformation bi = app.bis.get(position);
    		lastBook = bi;
    		openBookViewer(bi);
    	}
    };
    
    public void showPopup(View view) {
    	LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
    	        .getSystemService(LAYOUT_INFLATER_SERVICE);
    	View popupView = layoutInflater.inflate(R.layout.homepopup, null);    	
    	final PopupWindow popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	popupWindow.showAsDropDown(view, 50, 50);
    	popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }
    
    public void showSortPopup(View view) {
    	LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
    	        .getSystemService(LAYOUT_INFLATER_SERVICE);
    	View popupView = layoutInflater.inflate(R.layout.homesort, null);    	
    	final PopupWindow popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	popupWindow.showAsDropDown(view, 50, 50);
    	popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }
    
	AdapterView.OnItemLongClickListener onLongClick = new AdapterView.OnItemLongClickListener() {
 		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    		BookInformation bi = app.bis.get(position);
    		if (!bi.isDownloaded) return false;
    		debug("LongClick on Book:"+bi.title);
    		SkyHomeDialog dialog = new SkyHomeDialog(parent.getContext(),bi);
    		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    		dialog.show();
			return true;
		}
    };

    public void openBookViewer(BookInformation bi) {	
    	this.openBookViewer(bi, false);
	}
    
    public void openBookViewer(BookInformation bi,boolean fromBeginning) {
		this.hideSearchView();	
		if (!bi.isDownloaded) return;
		Intent intent;
		if (!bi.isFixedLayout) {
			intent = new Intent(this,BookViewActivity.class);
		}else {
			intent = new Intent(this,MagazineActivity.class);
		}
		intent.putExtra("BOOKCODE",bi.bookCode);
		intent.putExtra("TITLE",bi.title);
		intent.putExtra("AUTHOR", bi.creator);
		intent.putExtra("BOOKNAME",bi.fileName);
		if (fromBeginning || bi.position<0.0f) {
			intent.putExtra("POSITION",(double)-1.0f); // 7.x -1 stands for start position for both LTR and RTL book.
		}else {
			intent.putExtra("POSITION",bi.position);
		}		
		intent.putExtra("THEMEINDEX",app.setting.theme);
		intent.putExtra("DOUBLEPAGED",app.setting.doublePaged);		
		intent.putExtra("transitionType",app.setting.transitionType);
		intent.putExtra("GLOBALPAGINATION",app.setting.globalPagination);
		intent.putExtra("RTL",bi.isRTL);
		intent.putExtra("VERTICALWRITING",bi.isVerticalWriting);	
		
		intent.putExtra("SPREAD", bi.spread);
		intent.putExtra("ORIENTATION", bi.orientation);
		
		startActivity(intent);
    }

	
	public void openSetting() {
		Intent intent;
		intent = new Intent(this,SettingActivity.class);
		startActivity(intent);		
	}
	
	class SkyHomeDialog extends Dialog implements OnClickListener {
		Button openButton;
		Button seeDetailsButton;
		Button deleteButton;
		Button deleteCachedButton;
		TextView titleTextView;
		TextView authorTextView;		
		BookInformation bi;
		
		public SkyHomeDialog(Context context,BookInformation bi) {			
			super(context);
			this.bi = bi;
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.homepopup);
			
			titleTextView = (TextView)findViewById(R.id.titleTextView);
			authorTextView = (TextView)findViewById(R.id.authorTextView);
			
			openButton = (Button)findViewById(R.id.OpenButton);
			seeDetailsButton = (Button)findViewById(R.id.SeeDetailsButton);
			deleteButton = (Button)findViewById(R.id.DeleteButton);
			deleteCachedButton = (Button)findViewById(R.id.DeleteCacheButton);
			
			openButton.setOnClickListener(this);
			seeDetailsButton.setOnClickListener(this);
			deleteButton.setOnClickListener(this);
			deleteCachedButton.setOnClickListener(this);
			
			titleTextView.setText(bi.title);
//			authorTextView.setText(bi.creator);
			authorTextView.setText("");
		}

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			Button button = (Button)view;
			button.setTextColor(Color.DKGRAY);
			button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			
			if (view==openButton) {
				openBookViewer(bi);
			}else if (view==seeDetailsButton) {
				openBookViewer(bi,true);
			}else if (view==deleteButton) {
				ls.deleteBookByBookCode(bi.bookCode);
				reload();
			}else if (view==deleteCachedButton) {
				ls.deleteCachedByBookCode(bi.bookCode);
			}
			dismiss();
			makeFullScreen();
		}	
		
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
		    Rect dialogBounds = new Rect();
		    getWindow().getDecorView().getHitRect(dialogBounds);
		    int ex = (int)ev.getX();
		    int ey = (int)ev.getY();
		    if (!dialogBounds.contains(ex,ey)) {
		        dismiss();
		        makeFullScreen();
		    }
		    return super.dispatchTouchEvent(ev);
		}
	}
	
	class SkySortDialog extends Dialog implements OnClickListener {
		Button sortByTitleButton;
		Button sortByAuthorButton;
		Button sortByLastReadButton;
		Button sortByDownloadButton;
		
		public SkySortDialog(Context context) {			
			super(context);
			
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.homesort);
			
			sortByTitleButton = (Button)findViewById(R.id.sortByTitleButton);
			sortByAuthorButton = (Button)findViewById(R.id.sortByAuthorButton);
			sortByLastReadButton = (Button)findViewById(R.id.sortByLastReadButton);
			sortByDownloadButton = (Button)findViewById(R.id.sortByDownloadButton);
			
			sortByTitleButton.setOnClickListener(this);
			sortByAuthorButton.setOnClickListener(this);
			sortByLastReadButton.setOnClickListener(this);
			sortByDownloadButton.setOnClickListener(this);
			
			changeButtonColor();
		}
		
		private void changeButtonColor() {
			Button button;
			if (app.sortType==1) {
				button = sortByTitleButton;
			}else if (app.sortType==2) {
				button = sortByAuthorButton;
			}else if (app.sortType==3) {
				button = sortByLastReadButton;
			}else {
				button = sortByDownloadButton;
			}
			button.setBackgroundColor(Color.BLUE);
			button.setTextColor(Color.LTGRAY);
			button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		}

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			Button button = (Button)view;
			button.setTextColor(Color.LTGRAY);
			button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			
			changeButtonColor();	
			
			int newType = 0;
			
			if (view==sortByTitleButton) {
				newType=1;				
			}else if (view==sortByAuthorButton) {
				newType=2;
			}else if (view==sortByLastReadButton) {
				newType=3;
			}else {
				newType=0;
			}
			
			if (newType!=app.sortType) {
				app.sortType = newType;
				reload();				
			}
						
			dismiss();			
		}	
		
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
		    Rect dialogBounds = new Rect();
		    getWindow().getDecorView().getHitRect(dialogBounds);
		    int ex = (int)ev.getX();
		    int ey = (int)ev.getY();
		    if (!dialogBounds.contains(ex,ey)) {
		        dismiss();
		    }
		    return super.dispatchTouchEvent(ev);
		}
	}
	
	public int getMaxBookWidth() {
		if (this.gridType==0) {
			return px(160);
		}else {
			int pw = px(290);
			int cn = 0;
			int vw = this.getWidth();
			if (this.isPortrait()) {
				cn = vw / pw;
				if (cn==1) {
					pw = vw;
				}else if (cn>1) {
					pw = vw/2;
				}				
			}else {
				cn = vw/pw;
				if (cn==1 || cn==2) {
					pw = vw/2;
				}else if (cn>2){					
					pw = vw/3;
				}
			}
			String os = "Landscape";
			if (isPortrait()) {
				os = "Portrait";
			}
//			debug("isPortrait:"+os+"maxBookWidth:"+pw);
			return pw;
		}		
	}
	
	public int getNumColumns() {
		int width = this.getWidth();
		int maxBookWidth = this.getMaxBookWidth();
		int cn = (int)(width / maxBookWidth);
		if (cn>=6) cn=5;
		return cn;
	}
	
	class SkyGridAdapter extends BaseAdapter  {
		Context context = null;
		ArrayList<SkyGridItem> items = new ArrayList<SkyGridItem>();

		public SkyGridAdapter(Context context) {
			this.context = context;
		}
		
		public void reset() {
			items.clear();
		}
		
		public void removeItem(BookInformation bi) {
			for (int i=0; i<this.items.size(); i++) {
				SkyGridItem si = items.get(i);
				if (si.bi.bookCode==bi.bookCode) {
					items.remove(i);
					break;
				}
			}
		}
		
		public void removeItem(int bookCode ) {
			for (int i=0; i<this.items.size(); i++) {
				SkyGridItem si = items.get(i);
				if (si.bi.bookCode==bookCode) {
					items.remove(i);
					break;
				}
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return 120;
			int size = 0;
			if (app.bis!=null) {
				size = app.bis.size();
			}
			return size;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return app.bis.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		private int getViewWidth() {
			int maxWidth = getMaxBookWidth();
			int vw = (int)((double)maxWidth*(0.8f));
			return vw;
			
//			int width = getWidth();
//			int height = getHeight();
//			int max = Math.max(width,height);
////			int vw = (int)((double)max*(0.16722f));
//			int vw = (int)((double)max*(0.14f));
//			return vw;
		}
		
		private String getBullets(double progress) {
			int bc = (int)(Math.round(progress*5));
			String ret = "";
			for (int i=0; i<bc; i++) {
				ret = ret+"\u2022";
			}
			return ret;			
		}
		
		private String getBigBullets(double progress) {
			int bc = (int)(Math.round(progress*5));
			String ret = "";
			for (int i=0; i<bc; i++) {
				ret = ret+"\u26AB";
			}
			return ret;			
		}	
		
		class BulletsView extends View {
			double value = 0;
			public BulletsView(Context context,double value) {
				super(context);
				this.value = value;
				// TODO Auto-generated constructor stub
			}
			
			private void drawCircle(Canvas canvas, int cx,int cy, int r) {
				Paint paint = new Paint();
				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(cx,cy, r,paint);				
				paint.setColor(Color.WHITE);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(cx,cy, r*0.6f,paint);
				paint.setColor(Color.LTGRAY);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(cx,cy, r*0.45f,paint);

			}
			
			@Override
			public void onDraw(Canvas canvas) {
				int bc = (int)(Math.round(value*5));
				int cy = this.getHeight()/2;
				int sw = this.getWidth()/5;
				int r = (int)((double)this.getHeight()*0.3f);
				for (int i=0; i<bc; i++) {
					this.drawCircle(canvas, i*sw+sw/2,cy,r);
				}				
			}			
		}
		
		private RelativeLayout makeContentView(BookInformation bi,int vw, int vh) {
			RelativeLayout contentView = new RelativeLayout(context);
			SkyUtility.setSize(contentView,RelativeLayout.CENTER_HORIZONTAL,vw, vh);			
			contentView.setBackgroundResource(R.drawable.home_book_back0);
			ImageView coverView = new ImageView(context);
			contentView.addView(coverView);
			int itemMargin = (int)((double)vw*0.05f);			
			int bookCode = bi.bookCode;
			String coverPath = app.sd.getCoverPathByBookCode(bookCode);
			File coverFile = new File(coverPath);
			coverView.setScaleType(ScaleType.FIT_XY);
				
			if (coverFile.exists() && bi.isDownloaded) {				
				BitmapDrawable drawable = new BitmapDrawable(coverPath);
				coverView.setImageDrawable(drawable);
//				SkyUtility.setMargins(coverView,itemMargin);				
				BulletsView bv = new BulletsView(context,bi.position);
				SkyUtility.setFrame(bv,(int)(vw*0.3f),(int)(vh*0.75f),vw-(int)(vw*0.3f*2),px(12));
				contentView.addView(bv);
				SkyUtility.setFrame(coverView,0,0,vw,vh);
				coverView.setPadding(itemMargin, itemMargin, itemMargin, itemMargin);
			}else {
				coverView.setImageResource(R.drawable.greencover);
				TextView titleLabel = SkyUtility.makeLabel(context,0,bi.title,Gravity.CENTER_HORIZONTAL, 14, Color.LTGRAY,true);
				int margin = (int)((double)(15.0f/90.0f)*(double)vw);
				int labelWidth = (int)((double)(70.0f/90.0f)*(double)vw);
				int titleTop =  (int)((double)(20.0f/120.0f)*(double)vh); 
				int titleHeight = (int)((double)(50.0f/120.0f)*(double)vh); // 50
				int authorTop = (int)((double)(70.0f/120.0f)*(double)vh); 
				int authorHeight = (int)((double)(40.0f/120.0f)*(double)vh);
				SkyUtility.setFrame(titleLabel,margin,titleTop,labelWidth,titleHeight);
				TextView authorLabel = SkyUtility.makeLabel(context,0,bi.creator,Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 10, Color.LTGRAY,false);
				SkyUtility.setFrame(authorLabel,margin,authorTop,labelWidth,authorHeight);
				contentView.addView(titleLabel);
				contentView.addView(authorLabel);
				SkyUtility.setFrame(coverView,0,0,vw,vh);
			}	
			SkyPieView pieView = new SkyPieView(context);
			pieView.setId(7080);
			if (!bi.isDownloaded) pieView.isHidden =false;
			else pieView.isHidden =true;
			SkyUtility.setFrame(pieView,0,0,vw,vh);			
			contentView.addView(pieView);
			
			double value = (double)bi.downSize /(double)bi.fileSize;
			if (value!=1) {
				pieView.setValue(value);
			}		
			
			return contentView;
		}
		
		private View makeItemView(BookInformation bi) {
			int vw = this.getViewWidth();
			int vh = (int)((double)vw*1.3333f);
			SkyGridItem item = new SkyGridItem(context,bi);
			RelativeLayout contentView = this.makeContentView(bi, vw,vh);
			item.addView(contentView);		
			return item;
		}
		
		public View makeDetailView(BookInformation bi,int vw,int vh) {
			LinearLayout detailView = new LinearLayout(context);
			SkyUtility.setSize(detailView, 0, vw,vh);
			
			detailView.setOrientation(LinearLayout.VERTICAL);
			detailView.setPadding(px(10), px(5),px(0),px(10));
				String title = bi.title;
				if (title.length()>65) title = title.substring(0,65);
				TextView titleLabel = SkyUtility.makeLabel(context,0,title,Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 14, Color.DKGRAY,true);
				
				titleLabel.setEllipsize(TruncateAt.MARQUEE);
				detailView.addView(titleLabel,SkyUtility.getWeightParams(0.55f,LinearLayout.VERTICAL));		
				
				TextView authorLabel = SkyUtility.makeLabel(context,0,bi.creator,Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 14, Color.BLACK,false);
				detailView.addView(authorLabel,SkyUtility.getWeightParams(0.25f,LinearLayout.VERTICAL));
				
				String extraInfo="";
				if (bi.subject!=null && !bi.subject.isEmpty()) extraInfo = bi.subject;
				if (bi.publisher!=null && !bi.publisher.isEmpty()) extraInfo = bi.publisher;
				if (extraInfo.length()>17) extraInfo = extraInfo.substring(0,17);
				TextView extraLabel = SkyUtility.makeLabel(context,0,extraInfo,Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 12, Color.DKGRAY,false);
				detailView.addView(extraLabel,SkyUtility.getWeightParams(0.2f,LinearLayout.VERTICAL));
			detailView.setWeightSum(1.0f);
			return detailView;
		}
		
		private View makeItemDetailView(BookInformation bi) {	
			int vw = this.getViewWidth();
			int vh = px(170);
			int cw = (int)((double)vh/1.33333f);
			SkyGridItem item = new SkyGridItem(context,bi);
				LinearLayout itemDetail = new LinearLayout(context);
				itemDetail.setOrientation(LinearLayout.HORIZONTAL);
				item.addView(itemDetail);
				SkyUtility.setSize(itemDetail,RelativeLayout.CENTER_HORIZONTAL,vw,vh);
					RelativeLayout contentView = this.makeContentView(bi,cw, vh);
					itemDetail.addView(contentView);
					
					LinearLayout detailView = (LinearLayout) this.makeDetailView(bi,LayoutParams.FILL_PARENT,vh);
					itemDetail.addView(detailView);
			
			return item;				
		}
		
		// 이제 cover가 있는 경우를 대비한다. 
		// 크기에 자유롭게 코딩을 수정한다. 
		private View makeView(BookInformation bi) {			
			View view;
			if (gridType==0) {
				 view = this.makeItemView(bi);				 
			}else {
				view = this.makeItemDetailView(bi);
			}
			return view;
		}
		
		private SkyGridItem getView(BookInformation bi) {
			for (int i=0; i<this.items.size(); i++) {
				SkyGridItem ti = items.get(i);
				if (ti.bi.bookCode==bi.bookCode) return ti;
			}
			return null;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View itemView = null;
			BookInformation bi = app.bis.get(position);
			itemView = this.getView(bi);

			if (itemView==null) {				
				itemView = this.makeView(bi);
				items.add((SkyGridItem)itemView);
			}
			
			return itemView;
		}		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
    	this.recalcGridFactors();
    	processRotation();
		new Handler().postDelayed(new Runnable() { 
			public void run() {
//		    	processRotation();
			} 
		}, 100);
	}
	
	public void recalcGridFactors() {
		int cn = this.getNumColumns();
		this.gridView.setNumColumns(cn);		
	}
	
	public void processRotation() {
		if (gridType==1) {
			gridAdapter.reset();
		}
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

    
	public void debug(String msg) {
//		if (Setting.isDebug()) {
			Log.w("EPub", msg);
//		}
	}
	
    public void test01() {
    	File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    	String filePath = path.getAbsolutePath()+"/test.epub";
    	showToast(filePath);    	
    }
}

// 현재로서는 아무것도 구현하지 않는다. 
class SkyGridView extends GridView {
	public SkyGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
}

class SkyGridItem extends RelativeLayout {
	public BookInformation bi;
	public SkyGridItem(Context context,BookInformation bi) {
		super(context);
		this.bi= bi;			
	}
}