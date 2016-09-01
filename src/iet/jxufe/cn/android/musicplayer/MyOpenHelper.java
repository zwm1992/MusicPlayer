package iet.jxufe.cn.android.musicplayer;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper {
	public String createTableSQL="create table if not exists music_tb" +
			"(_id integer primary key autoincrement,title,artist,album,album_id,time,url)";
	public MyOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);		
	}
	//数据库创建后，回调该方法，执行建表操作和插入初始化数据	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTableSQL);
	}
	//数据库版本更新时，回调该方法	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("版本变化:"+oldVersion+"-------->"+newVersion);
	}
}
