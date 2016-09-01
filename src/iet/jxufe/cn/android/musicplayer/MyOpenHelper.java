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
	//���ݿⴴ���󣬻ص��÷�����ִ�н�������Ͳ����ʼ������	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTableSQL);
	}
	//���ݿ�汾����ʱ���ص��÷���	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("�汾�仯:"+oldVersion+"-------->"+newVersion);
	}
}
