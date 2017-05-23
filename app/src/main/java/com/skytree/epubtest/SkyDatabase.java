package com.skytree.epubtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.skytree.epub.BookInformation;
import com.skytree.epub.Highlight;
import com.skytree.epub.Highlights;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PagingInformation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;


public class SkyDatabase {
	private DBHelper opener; 
    private SQLiteDatabase db;
    Context context;
    
    public SkyDatabase(Context context) {
        this.context = context;
        this.opener = new DBHelper(context);
        db = opener.getWritableDatabase();
    }
	
	class DBHelper extends SQLiteOpenHelper {
		public static final int version = 3;
		
		public String stringFromAssets(String fileName) {
		    StringBuilder ReturnString = new StringBuilder();
		    InputStream fIn = null;
		    InputStreamReader isr = null;
		    BufferedReader input = null;
		    try {
		        fIn =  context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
		        isr = new InputStreamReader(fIn);
		        input = new BufferedReader(isr);
		        String line = "";
		        while ((line = input.readLine()) != null) {
		            ReturnString.append(line);
		        }
		    } catch (Exception e) {
		        e.getMessage();
		    } finally {
		        try {
		            if (isr != null)
		                isr.close();
		            if (fIn != null)
		                fIn.close();
		            if (input != null)
		                input.close();
		        } catch (Exception e2) {
		            e2.getMessage();
		        }
		    }
		    return ReturnString.toString();
		}
		
	    public DBHelper(Context context) {	    	
	        super(context, SkySetting.getStorageDirectory() + "/" + "Books.db", null,DBHelper.version);
	        String dbPath = SkySetting.getStorageDirectory() + "/" + "Books.db";
	        Log.w("EPub","DBHelper "+dbPath);
	    }

	    // onCreate is called once if database not exists.
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	Log.w("EPub","SkyDB onCreate");	    	
	        db.execSQL(this.stringFromAssets("sql/book.ddl"));
	        db.execSQL(this.stringFromAssets("sql/highlight.ddl"));
	        db.execSQL(this.stringFromAssets("sql/bookmark.ddl"));
	        db.execSQL(this.stringFromAssets("sql/paging.ddl"));
	        db.execSQL(this.stringFromAssets("sql/setting.ddl"));	        
	        String sql = "INSERT INTO Setting(BookCode,FontName,FontSize,LineSpacing,Foreground,Background,Theme,Brightness,TransitionType,LockRotation,MediaOverlay,TTS,AutoStartPlaying,AutoLoadNewChapter,HighlightTextToVoice) VALUES(0,'',2,-1,-1,-1,0,1,2,1,1,0,1,1,1)";
	        db.execSQL(sql);
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	Log.w("EPub","Database Version : Old Version:"+oldVersion+"  New Version:"+newVersion);	 
	    	if( oldVersion == 1 ){
	    		db.execSQL("ALTER TABLE Book ADD COLUMN Spread INTEGER DEFAULT 0");
	    		db.execSQL("ALTER TABLE Book ADD COLUMN Orientation INTEGER DEFAULT 0");
	    	}
	    	
	    	if (oldVersion==2) {
	    		db.execSQL("ALTER TABLE Setting ADD COLUMN MediaOverlay INTEGER DEFAULT 1");	    		
	    		db.execSQL("ALTER TABLE Setting ADD COLUMN TTS          INTEGER DEFAULT 0");
	    		db.execSQL("ALTER TABLE Setting ADD COLUMN AutoStartPlaying INTEGER DEFAULT 1");
	    		db.execSQL("ALTER TABLE Setting ADD COLUMN AutoLoadNewChapter INTEGER DEFAULT 1");
	    		db.execSQL("ALTER TABLE Setting ADD COLUMN HighlightTextToVoice INTEGER DEFAULT 1");
	    	}
	    }		
	}	
    
	// Using db method
	// It's global setting for all books 
    public void updateSetting(SkySetting setting) {    	
       	ContentValues values = new ContentValues();
    	values.put("FontName",setting.fontName);
    	values.put("FontSize",setting.fontSize);
    	values.put("LineSpacing",setting.lineSpacing);
    	values.put("Foreground", setting.foreground);
    	values.put("Background",setting.background);
    	values.put("Theme", setting.theme);
    	values.put("Brightness", setting.brightness);
    	values.put("TransitionType",setting.transitionType);
    	values.put("LockRotation",setting.lockRotation ? 1:0);
    	values.put("DoublePaged",setting.doublePaged ? 1:0);
    	values.put("Allow3G",setting.allow3G ? 1:0);
    	values.put("GlobalPagination",setting.globalPagination ? 1:0);
    	
    	values.put("MediaOverlay",setting.mediaOverlay ? 1:0);
    	values.put("TTS",setting.tts ? 1:0);
    	values.put("AutoStartPlaying",setting.autoStartPlaying ? 1:0);
    	values.put("AutoLoadNewChapter",setting.autoLoadNewChapter ? 1:0);
    	values.put("HighlightTextToVoice",setting.highlightTextToVoice ? 1:0);
    	
    	String where = "BookCode=0";
    	db.update("Setting", values, where, null);
    }
    
    public SkySetting fetchSetting() {
    	String sql = "SELECT * FROM Setting where BookCode=0";
        Cursor result = db.rawQuery(sql, null);
        if (result.moveToFirst()) {
        	SkySetting setting = new SkySetting();
            setting.bookCode =      result.getInt(0);
            setting.fontName =      result.getString(1);
            setting.fontSize =      result.getInt(2);
            setting.lineSpacing=    result.getInt(3);
            setting.foreground=     result.getInt(4);
            setting.background=     result.getInt(5);
            setting.theme  =        result.getInt(6);
            setting.brightness =    result.getDouble(7);
            setting.transitionType= result.getInt(8);
            setting.lockRotation =  result.getInt(9)!=0;
            setting.doublePaged =  result.getInt(10)!=0;
            setting.allow3G 	=  result.getInt(11)!=0;
            setting.globalPagination =  result.getInt(12)!=0;     
            
            setting.mediaOverlay =  result.getInt(13)!=0;
            setting.tts	= result.getInt(14)!=0;
            setting.autoStartPlaying = result.getInt(15)!=0;
            setting.autoLoadNewChapter = result.getInt(16)!=0;
            setting.highlightTextToVoice = result.getInt(17)!=0;

            
            result.close();
            return setting;
        }
        result.close();
        return null;
    }
    
    public String getDateString() {
    	Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    
    // Using db method
    public void insertBookmark(PageInformation pi) {
        double ppb = pi.pagePositionInBook;
        double ppc = pi.pagePositionInChapter;
        int ci = pi.chapterIndex;
        int bc = pi.bookCode;
        String dateInString = this.getDateString();  

        ContentValues values = new ContentValues();
    	values.put("BookCode", pi.bookCode);
    	values.put("ChapterIndex", ci);
    	values.put("PagePositionInChapter",ppc);
    	values.put("PagePositionInBook",ppb);
    	values.put("CreatedDate",dateInString);
    	db.insert("Bookmark", null, values);

    }
    
    public void deleteBookmarkByCode(int code) {
    	String sql = String.format(Locale.US,"DELETE FROM Bookmark where Code = %d",code);
    	db.execSQL(sql); 
    }

    public void deleteBookByBookCode(int bookCode) {
    	String sql = String.format(Locale.US,"DELETE FROM Book where BookCode = %d",bookCode);
    	db.execSQL(sql); 
    }
    
    public void deleteBookmarksByBookCode(int bookCode) {
    	String sql = String.format(Locale.US,"DELETE FROM Bookmark where BookCode = %d",bookCode);
    	db.execSQL(sql); 
    }
    
    
    public void deleteHighlightsByBookCode(int bookCode) {
    	String sql = String.format(Locale.US,"DELETE FROM Highlight where BookCode = %d",bookCode);
    	db.execSQL(sql); 
    }
    
    public void deletePagingsByBookCode(int bookCode) {
    	String sql = String.format(Locale.US,"DELETE FROM Paging where BookCode = %d",bookCode);
    	db.execSQL(sql); 
    }

    
    public void deleteBookmark(PageInformation pi) {
    	int code = pi.code;
    	this.deleteBookmarkByCode(code);    	
    }    
    
    public int getBookmarkCode(PageInformation pi) {
    	int bookCode = pi.bookCode;
    	BookInformation bi = this.fetchBookInformation(bookCode);
    	if (bi==null) return -1;
    	boolean isFixedLayout = bi.isFixedLayout;
    	
    	if (!isFixedLayout) {
    		double pageDelta = 1.0f/pi.numberOfPagesInChapter;
    		double target = pi.pagePositionInChapter;
    		String selectSql = String.format(Locale.US,"SELECT Code,PagePositionInChapter from Bookmark where BookCode=%d and ChapterIndex=%d",bookCode,pi.chapterIndex);
    		Cursor cursor = db.rawQuery(selectSql, null);
    		while (cursor.moveToNext()) {
    			double ppc = cursor.getDouble(1);
    			int code = cursor.getInt(0);
    			if (target>=(ppc-pageDelta/2) && target<=(ppc+pageDelta/2.0f)) {
    				cursor.close();
    				return code;
    			}
    		}
    		cursor.close();
    	}else {
    		String selectSql = String.format(Locale.US,"SELECT Code from Bookmark where BookCode=%d and ChapterIndex=%d",bookCode,pi.chapterIndex);
    		Cursor cursor = db.rawQuery(selectSql, null);
    		while (cursor.moveToNext()) {
    			int code = cursor.getInt(0);
    			return code;
    		}
    		cursor.close();
    	}    	
		return -1;
    }
    
   
    public ArrayList<PageInformation> fetchBookmarks(int bookCode) {
    	ArrayList<PageInformation>pis = new ArrayList<PageInformation>();
    	String selectSql = String.format(Locale.US,"SELECT * from Bookmark where bookCode=%d ORDER BY ChapterIndex",bookCode);
        Cursor cursor = db.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
        	PageInformation pi = new PageInformation();
        	pi.bookCode = cursor.getInt(0);
        	pi.code = cursor.getInt(1);
        	pi.chapterIndex = cursor.getInt(2);
        	pi.pagePositionInChapter = cursor.getDouble(3);
        	pi.pagePositionInBook = cursor.getDouble(4);
        	pi.datetime = cursor.getString(6);
        	pis.add(pi);
        }
		cursor.close();
		return pis;    	
    } 
    /*
    CREATE TABLE IF NOT EXISTS Paging (
    		0 BookCode INTEGER NOT NULL,
    		1 Code INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL,
    		2 ChapterIndex INTEGER,
    		3 NumberOfPagesInChapter INTEGER,
    		4 FontName TEXT,
    		5 FontSize INTEGER,
    		6 LineSpacing INTEGER,
    		7 Width INTEGER,
    		8 Height INTEGER,
    		9 VerticalGapRatio REAL,
    		A HorizontalGapRatio REAL,
    		B IsPortrait INTEGER,
    		C IsDoublePagedForLandscape INTEGER
    		);
    */
    public PagingInformation fetchPagingInformation(PagingInformation pgi) {
//    	String sql = String.format(Locale.US,	"SELECT * FROM Paging WHERE BookCode=%d AND ChapterIndex=%d AND FontName='%s' AND FontSize=%d AND LineSpacing=%d AND ABS(Width-%d)<=1 AND ABS(Height-%d)<=1 AND HorizontalGapRatio=%f AND VerticalGapRatio=%f AND IsPortrait=%d AND IsDoublePagedForLandscape=%d",
//    															pgi.bookCode,	pgi.chapterIndex,		pgi.fontName,		pgi.fontSize,		pgi.lineSpacing,	pgi.width,		pgi.height,		pgi.horizontalGapRatio,		pgi.verticalGapRatio,		pgi.isPortrait ? 1:0,	pgi.isDoublePagedForLandscape?1:0);

    	String sql = String.format(Locale.US,	"SELECT * FROM Paging WHERE BookCode=%d AND ChapterIndex=%d AND FontName='%s' AND FontSize=%d AND LineSpacing=%d AND ABS(Width-%d)<=2 AND ABS(Height-%d)<=2 AND IsPortrait=%d AND IsDoublePagedForLandscape=%d",
				pgi.bookCode,	pgi.chapterIndex,		pgi.fontName,		pgi.fontSize,		pgi.lineSpacing,	pgi.width,		pgi.height,		pgi.isPortrait ? 1:0,	pgi.isDoublePagedForLandscape?1:0);

    	
        Cursor result = db.rawQuery(sql, null);
        if (result.moveToFirst()) {
        	PagingInformation pg = new PagingInformation();
        	pg.bookCode = result.getInt(0);
        	pg.code = result.getInt(1);
        	pg.chapterIndex = result.getInt(2);
        	pg.numberOfPagesInChapter = result.getInt(3);
        	pg.fontName = result.getString(4);
        	pg.fontSize = result.getInt(5);
        	pg.lineSpacing = result.getInt(6);
        	pg.width = result.getInt(7);
        	pg.height = result.getInt(8);
        	pg.verticalGapRatio = result.getDouble(9);
        	pg.horizontalGapRatio = result.getDouble(10);
        	pg.isPortrait = result.getInt(11)!=0;
        	pg.isDoublePagedForLandscape = result.getInt(12)!=0;
            return pg;
        }
        result.close();
        return null;   	
    }
    
    public void toggleBookmark(PageInformation pi) {
        int code = this.getBookmarkCode(pi);
        if (code == -1) { // if not exist
            this.insertBookmark(pi);        	
        }else {
        	this.deleteBookmarkByCode(code); // if exist, delete it            
        }    	
    }
    
    public boolean isBookmarked(PageInformation pi) {
        int code = this.getBookmarkCode(pi);
        if (code==-1) {
            return false;
        }else {
            return true;
        }    	
    } 
    
    // Using db method    
    public void updatePosition(int bookCode,double position) {
    	ContentValues values = new ContentValues();
    	values.put("Position", position);
    	values.put("LastRead",getDateString());
    	values.put("IsRead", 1);
    	String where = String.format(Locale.US,"BookCode=%d",bookCode);
    	db.update("Book", values, where, null);
    }

    // Using db method
    public void insertBook(BookInformation bi) {
    	ContentValues values = new ContentValues();
    	values.put("Title", bi.title);
    	values.put("Author", bi.creator);
    	values.put("Publisher", bi.publisher);
    	values.put("Subject", bi.subject);
    	values.put("Type", bi.type);
    	values.put("Date", bi.date);
    	values.put("Language",bi.language);
    	values.put("Filename", bi.fileName);
    	values.put("FileSize", bi.fileSize);
    	values.put("Position", bi.position);
    	values.put("IsDownloaded", (bi.isDownloaded ?1:0));    	
    	values.put("IsFixedLayout",(bi.isFixedLayout ? 1:0));
    	values.put("CustomOrder", bi.customOrder);
    	values.put("URL", bi.url);
    	values.put("DOWNSIZE", bi.downSize);
    	values.put("CoverURL",bi.coverUrl);
    	values.put("IsRead",(bi.isRead ? 1:0));
    	values.put("LastRead", bi.lastRead);
    	values.put("IsRTL",(bi.isRTL ? 1:0));
    	values.put("IsVerticalWriting",(bi.isVerticalWriting ? 1:0));
    	values.put("Res0",bi.res0);
    	values.put("Res1",bi.res1);	
    	values.put("Res2",bi.res2);	
    	values.put("Etc",bi.etc);
    	values.put("Spread", bi.spread);
    	values.put("Orientation", bi.orientation);
    	db.insert("Book", null, values);
    }
    
 // Using db method
    public void updateBook(BookInformation bi) {
    	ContentValues values = new ContentValues();
    	if (bi.title!=null && !bi.title.isEmpty()) values.put("Title", bi.title);
    	if (bi.creator!=null && !bi.creator.isEmpty()) values.put("Author", bi.creator);
    	if (bi.publisher!=null && !bi.publisher.isEmpty()) values.put("Publisher", bi.publisher);
    	if (bi.subject!=null && !bi.subject.isEmpty()) values.put("Subject", bi.subject);
    	if (bi.type!=null && !bi.type.isEmpty()) values.put("Type", bi.type);
    	if (bi.date!=null && !bi.date.isEmpty()) values.put("Date", bi.date);
    	if (bi.language!=null && !bi.language.isEmpty()) values.put("Language",bi.language);
    	if (bi.fileName!=null && !bi.fileName.isEmpty()) values.put("Filename", bi.fileName);
    	if (bi.fileSize!=-1 && bi.fileSize!=0) values.put("FileSize", bi.fileSize);
    	if (bi.downSize!=-1) values.put("DownSize", bi.downSize);
    	values.put("IsDownloaded", (bi.isDownloaded ?  1:0));    	
    	values.put("IsFixedLayout",(bi.isFixedLayout ? 1:0));
    	values.put("IsRead", (bi.isRead ? 1:0));
    	if (bi.url!=null && !bi.url.isEmpty()) values.put("URL", bi.url);
    	if (bi.coverUrl!=null && !bi.coverUrl.isEmpty()) values.put("CoverURL", bi.coverUrl);
    	if (bi.customOrder!=-1) values.put("CustomOrder", bi.customOrder);
    	if (bi.lastRead!=null && !bi.lastRead.isEmpty()) values.put("LastRead", bi.lastRead);
    	values.put("IsRTL",(bi.isRTL ? 1:0));
    	values.put("IsVerticalWriting",(bi.isVerticalWriting ? 1:0));
    	if (bi.res0!=-1) values.put("Res0", bi.res0);
    	if (bi.res1!=-1) values.put("Res1", bi.res1);	
    	if (bi.res2!=-1) values.put("Res2", bi.res2);	
    	if (bi.etc!=null && !bi.etc.isEmpty()) values.put("Etc", bi.etc);
    	if (bi.spread!=-1) values.put("Spread", bi.spread);
    	if (bi.orientation!=-1) values.put("Orientation", bi.orientation);
    	String where = String.format(Locale.US,"BookCode=%d"
    			,bi.bookCode
    	);
    	db.update("Book", values, where, null);
    }
    
    public void updateDownloadProcess(BookInformation bi) {
    	ContentValues values = new ContentValues();
    	if (bi.fileSize!=-1 && bi.fileSize!=0) values.put("FileSize", bi.fileSize);
    	if (bi.downSize!=-1) values.put("DownSize", bi.downSize);
    	String where = String.format(Locale.US,"BookCode=%d"
    			,bi.bookCode
    	);
    	db.update("Book", values, where, null);
    }
    
    /*
     * 	0 BookCode INTEGER UNIQUE NOT NULL PRIMARY KEY AUTOINCREMENT,
		1 Title TEXT,
		2 Author TEXT,
		3 Publisher TEXT,
		4 Subject TEXT,
		5 Type INTEGER,
		6 Date TEXT,
		7 Language TEXT,
		8 FileName TEXT,
		9 Position REAL DEFAULT 0,
		0 IsFixedLayout INTEGER DEFAULT 0,
		1 IsGlobalPagination INTEGER DEFAULT 0,
		2 IsDownloaded INTEGER DEFAULT 0,
		3 FileSize INTEGER DEFAULT -1,
		4 CustomOrder INTEGER DEFAULT 0,
		5 URL	TEXT,
		6 CoverURL TEXT,
		7 DownSize INTEGER DEFAULT -1,
		8 IsRead INTEGER DEFAULT 0
     * 
     */
    
    public ArrayList<BookInformation> fetchBookInformations(int sortType,String key) {
    	ArrayList<BookInformation>bis = new ArrayList<BookInformation>();
    	String orderBy;
    	if (sortType==0)		orderBy = "";
    	else if (sortType==1) 	orderBy = " ORDER BY Title";
    	else if (sortType==2)	orderBy = " ORDER BY Author";
    	else 					orderBy = " ORDER BY LastRead DESC";
    	String condition = "";
    	if (!(key==null || key.isEmpty())) {
    		condition =String.format(Locale.US," WHERE Title like '%%%s%%' OR Author like '%%%s%%'",key,key);
    	}
    	String selectSql = "SELECT* from Book "+condition+orderBy;
        Cursor cursor = db.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
        	BookInformation bi = new BookInformation();
        	bi.bookCode = cursor.getInt(0);
        	bi.title = cursor.getString(1);
        	bi.creator = cursor.getString(2);
        	bi.publisher = cursor.getString(3);
        	bi.subject = cursor.getString(4);
        	bi.type = cursor.getString(5);
        	bi.date = cursor.getString(6);
        	bi.language = cursor.getString(7);
        	bi.fileName = cursor.getString(8);
        	bi.position = cursor.getDouble(9);
        	bi.isFixedLayout = cursor.getInt(10)!=0;
        	bi.isGlobalPagination = cursor.getInt(11)!=0;
        	bi.isDownloaded = cursor.getInt(12)!=0;
        	bi.fileSize = cursor.getInt(13);
        	bi.customOrder = cursor.getInt(14);
        	bi.url = cursor.getString(15);
        	bi.coverUrl = cursor.getString(16);
        	bi.downSize = cursor.getInt(17);
        	bi.isRead = cursor.getInt(18)!=0;
        	bi.lastRead = cursor.getString(19);
        	bi.isRTL = cursor.getInt(20)!=0;
        	bi.isVerticalWriting = cursor.getInt(21)!=0;
        	bi.res0 = cursor.getInt(22);
        	bi.res1 = cursor.getInt(23);
        	bi.res2 = cursor.getInt(24);
        	bi.etc = cursor.getString(25); 
        	bi.spread = cursor.getInt(26);
        	bi.orientation = cursor.getInt(27);
        	bis.add(bi);
        }
		cursor.close();
		return bis;    	
    }
    
    public BookInformation fetchBookInformation(int bookCode) {
    	BookInformation bi = null;
    	String condition = String.format(Locale.US," WHERE BookCode=%d",bookCode);    	
    	String selectSql = "SELECT* from Book "+condition;
        Cursor cursor = db.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
        	bi = new BookInformation();
        	bi.bookCode = cursor.getInt(0);
        	bi.title = cursor.getString(1);
        	bi.creator = cursor.getString(2);
        	bi.publisher = cursor.getString(3);
        	bi.subject = cursor.getString(4);
        	bi.type = cursor.getString(5);
        	bi.date = cursor.getString(6);
        	bi.language = cursor.getString(7);
        	bi.fileName = cursor.getString(8);
        	bi.position = cursor.getDouble(9);
        	bi.isFixedLayout = cursor.getInt(10)!=0;
        	bi.isGlobalPagination = cursor.getInt(11)!=0;
        	bi.isDownloaded = cursor.getInt(12)!=0;
        	bi.fileSize = cursor.getInt(13);
        	bi.customOrder = cursor.getInt(14);
        	bi.url = cursor.getString(15);
        	bi.coverUrl = cursor.getString(16);
        	bi.downSize = cursor.getInt(17);
        	bi.isRead = cursor.getInt(18)!=0;
        	bi.lastRead = cursor.getString(19);
        	bi.isRTL = cursor.getInt(20)!=0;
        	bi.isVerticalWriting = cursor.getInt(21)!=0;
        	bi.res0 = cursor.getInt(22);
        	bi.res1 = cursor.getInt(23);
        	bi.res2 = cursor.getInt(24);
        	bi.etc = cursor.getString(25); 
        	bi.spread = cursor.getInt(26);
        	bi.orientation = cursor.getInt(27);
        }
		cursor.close();
		return bi;    	
    }     
    
    public String getFileNameByBookCode(int bookCode) {
    	String numberPart = String.format(Locale.US,"%07d",bookCode);
    	String fileName = "sb"+numberPart+".epub";
    	return fileName;
    }
    
    public String getDirNameByBookCode(int bookCode) {
    	String numberPart = String.format(Locale.US,"%07d",bookCode);
    	String fileName = "sb"+numberPart;
    	return fileName;
    }
    
    public String getCorerNameByBookCode(int bookCode) {
    	String numberPart = String.format(Locale.US,"%07d",bookCode);
    	String fileName = "sb"+numberPart+".jpg";
    	return fileName;
    }
    
	public String getCoverPathByBookCode(int bookCode) {		
		String targetName = getFileNameByBookCode(bookCode);
		String dirName = getDirNameByBookCode(bookCode);
		targetName = targetName.replace(".epub",".jpg");
		String filePath = new String(SkySetting.getStorageDirectory() + "/covers/"+targetName);
		return filePath;
	}
	
    public int getBookCodeByFileName(String fileName) {
    	String numberPart = fileName.substring(2, 9);
    	Integer io = Integer.parseInt(numberPart);
    	int bookCode = io.intValue();
    	return bookCode;    	
    }
    
    public int insertEmptyBook(String url,String coverUrl,String title,String author,long downloadId) {
    	// 1st, insert null information to make blank row in book table. 
    	BookInformation bi = new BookInformation();
    	bi.title = title;
    	bi.creator = author;
    	bi.url = url;
    	bi.coverUrl = coverUrl;
    	bi.isDownloaded = false;
    	bi.res0 = (int)downloadId;
    	this.insertBook(bi);
//    	String lastSql = "SELECT * FROM Book WHERE ID = (SELECT MAX(ID) FROM Book)";
    	String lastSql = "SELECT * FROM Book";    	
    	Cursor cursor = db.rawQuery(lastSql, null);
    	cursor.moveToLast();
    	int bookCode = cursor.getInt(0);
    	return bookCode;
    }
    
    public int insertEmptyBook(String url,String coverUrl,String title,String author) {
    	return this.insertEmptyBook(url, coverUrl, title, author, 0);    	
    }
    
    
    
    Highlights fetchHighlights(int bookCode, int chapterIndex) {
    	Highlights results = new Highlights();
    	String selectSql = String.format(Locale.US,"SELECT * FROM Highlight where BookCode=%d and ChapterIndex=%d ORDER BY ChapterIndex",bookCode,chapterIndex);
        Cursor cursor = db.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
        	Highlight highlight = new Highlight();
        	highlight.bookCode = bookCode;
        	highlight.code = cursor.getInt(1);
        	highlight.chapterIndex = chapterIndex;
        	highlight.startIndex 	= cursor.getInt(3);
        	highlight.startOffset   = cursor.getInt(4);
            highlight.endIndex      = cursor.getInt(5);
            highlight.endOffset     = cursor.getInt(6);
            highlight.color= cursor.getInt(7);
            highlight.text 			= cursor.getString(8);
            highlight.note 			= cursor.getString(9);
            highlight.isNote 		= cursor.getInt(10)!=0;
            highlight.datetime 		= cursor.getString(11);
            highlight.style 		= cursor.getInt(12);
            results.addHighlight(highlight);        	
        }
		cursor.close();
		return results;    	
    }
    
    Highlights fetchAllHighlights(int bookCode) {
    	Highlights results = new Highlights();
    	String selectSql = String.format(Locale.US,"SELECT * FROM Highlight where BookCode=%d ORDER BY ChapterIndex",bookCode);
        Cursor cursor = db.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
        	Highlight highlight = new Highlight();
        	highlight.bookCode = bookCode;
        	highlight.code = cursor.getInt(1);
        	highlight.chapterIndex = cursor.getInt(2);
        	highlight.startIndex 	= cursor.getInt(3);
        	highlight.startOffset   = cursor.getInt(4);
            highlight.endIndex      = cursor.getInt(5);
            highlight.endOffset     = cursor.getInt(6);
            highlight.color= cursor.getInt(7);
            highlight.text 			= cursor.getString(8);
            highlight.note 			= cursor.getString(9);
            highlight.isNote 		= cursor.getInt(10)!=0;
            highlight.datetime 		= cursor.getString(11);
            highlight.style 		= cursor.getInt(12);
            results.addHighlight(highlight);        	
        }
		cursor.close();
		return results;    	
    }
    
    
    public void deletePagingInformation(PagingInformation pgi) {
    	String sql = String.format(Locale.US,"DELETE FROM Paging WHERE BookCode=%d AND ChapterIndex=%d AND FontName='%s' AND FontSize=%d AND LineSpacing=%d AND Width=%d AND Height=%d AND HorizontalGapRatio=%f AND VerticalGapRatio=%f AND IsPortrait=%d AND IsDoublePagedForLandscape=%d",
    			pgi.bookCode,	pgi.chapterIndex,		pgi.fontName,		pgi.fontSize,		pgi.lineSpacing,	pgi.width,		pgi.height,		pgi.horizontalGapRatio,		pgi.verticalGapRatio,		pgi.isPortrait ? 1:0,	pgi.isDoublePagedForLandscape?1:0);
    	db.execSQL(sql);
    }

    // if existing pagingInformation found, update it. 
    public void insertPagingInformation(PagingInformation pgi) {
    	PagingInformation tgi = this.fetchPagingInformation(pgi);
    	if (tgi!=null) {
    		this.deletePagingInformation(tgi);
    	}    	
    	ContentValues values = new ContentValues();
    	values.put("BookCode", pgi.bookCode);
    	values.put("ChapterIndex", pgi.chapterIndex);
    	values.put("NumberOfPagesInChapter", pgi.numberOfPagesInChapter);
    	values.put("FontName", pgi.fontName);
    	values.put("FontSize", pgi.fontSize);    	
    	values.put("LineSpacing", pgi.lineSpacing);
    	values.put("Width", pgi.width);
    	values.put("height", pgi.height);
    	values.put("VerticalGapRatio", pgi.verticalGapRatio);
    	values.put("HorizontalGapRatio", pgi.horizontalGapRatio);    	
    	values.put("IsPortrait", pgi.isPortrait ? 1:0);
    	values.put("IsDoublePagedForLandscape", pgi.isDoublePagedForLandscape ? 1:0);
    	db.insert("Paging", null, values);
    }
    
    
    
    public void deleteHighlight(Highlight highlight) {
    	String sql = String.format(Locale.US,"DELETE FROM Highlight where BookCode=%d and ChapterIndex=%d and StartIndex=%d and StartOffset=%d and EndIndex=%d and EndOffset=%d"
    			,highlight.bookCode
    			,highlight.chapterIndex
    			,highlight.startIndex
    			,highlight.startOffset
    			,highlight.endIndex
    			,highlight.endOffset);
    	db.execSQL(sql);
    	Log.w("EPub",sql);
    }
    
    public void deleteHighlightByCode(int code) {
    	String sql = String.format(Locale.US,"DELETE FROM Highlight where Code=%d",code);
    	db.execSQL(sql);
    	Log.w("EPub",sql);
    }
    
    // Using db method
    public void insertHighlight(Highlight highlight) {
    	String dateString = this.getDateString();
    	ContentValues values = new ContentValues();
    	values.put("BookCode", highlight.bookCode);
    	values.put("ChapterIndex", highlight.chapterIndex);
    	values.put("StartIndex", highlight.startIndex);
    	values.put("StartOffset", highlight.startOffset);
    	values.put("EndIndex", highlight.endIndex);
    	values.put("EndOffset", highlight.endOffset);
    	values.put("Color",highlight.color);
    	values.put("Text", highlight.text);
    	values.put("Note", highlight.note);
    	values.put("IsNote",highlight.isNote?1:0);
    	values.put("CreatedDate", dateString);
    	values.put("Style", highlight.style);
    	db.insert("Highlight", null, values);
    }

    
    // Update is 1 Based
    // using db method
    public void updateHighlight(Highlight highlight) {
    	ContentValues values = new ContentValues();
    	values.put("StartIndex", highlight.startIndex);
    	values.put("StartOffset", highlight.startOffset);
    	values.put("EndIndex", highlight.endIndex);
    	values.put("EndOffset", highlight.endOffset);
    	values.put("Color",highlight.color);
    	values.put("Text", highlight.text);
    	values.put("Note", highlight.note);
    	values.put("IsNote",highlight.isNote?1:0);
    	values.put("Style",highlight.style);
    	String where = String.format(Locale.US,"BookCode=%d and ChapterIndex=%d and StartIndex=%d and StartOffset=%d and EndIndex=%d and EndOffset=%d"
    			,highlight.bookCode
        		,highlight.chapterIndex
        		,highlight.startIndex
        		,highlight.startOffset
        		,highlight.endIndex
        		,highlight.endOffset);
    	db.update("Highlight", values, where, null);
    }
    
    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
    
    public void clearDownload() {
    	String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    	String path = extDir+"/Download";
    	File downDir = new File(path);        
    	String[] files;
    	files = downDir.list();  
    	for (int i=0; i<files.length; i++) {    		
    		File file = new File(downDir, files[i]);
    		if (file.getName().startsWith("sb") && file.getName().endsWith(".epub")) {
    			file.delete();
    		}    		  
    	} 
    }
    
    public void deleteRecursive(String path) {
    	File fileOrDirectory = new File(path);
    	this.deleteRecursive(fileOrDirectory);
    }

}
