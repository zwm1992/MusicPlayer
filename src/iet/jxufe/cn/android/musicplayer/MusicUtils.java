package iet.jxufe.cn.android.musicplayer;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;



public class MusicUtils {
	public static List<Music> getMusicData(Context context) {
		ContentResolver mResolver = context.getContentResolver();// ��ȡ���ݽ�����
		if (mResolver != null) {// ��ȡ���и���
			// ��һ��������ʾϵͳ�������ṩ�ߵ�URI
			// �ڶ���������ʾ��Ҫ��ȡ���е���Ϣ
			// ������������ʾ��ѯ����
			// ���ĸ�������ʾ�����е�ռλ����ֵ
			// �����������ʾ��ѯ���������ʽ��
			Cursor cursor = mResolver.query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			return cursorToList(cursor, context);

		}
		return null;
	}

	public static List<Music> cursorToList(Cursor cursor, Context context) {
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		List<Music> musicList = new ArrayList<Music>();// �������ϣ����ڴ����������
		while (cursor.moveToNext()) {
			Music m = new Music();// �������ֶ���
			String title = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE));// ��ȡ���ֱ���
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST));// ��ȡ����������
			if ("<unknown>".equals(artist)) {
				artist = "δ֪������";
			}
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));// ��ȡ����ר��
			int album_id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));			
			long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE));// ��ȡ���ִ�С
			int time = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION));// ��ȡ���ֳ���ʱ�䣬��λΪ����
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA));// ��ȡ���ֱ���·��
			String name = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));// ��ȡ������������׺
			String sub = name.substring(name.lastIndexOf(".") + 1);
			// ��ȡ�ļ�����չ��
			if (sub.equals("mp3") && time > 50000) {// ��MP3��β���ҳ��ȴ���5��				
				m.setTitle(title);// ��������
				m.setSinger(artist);// �������ݳ���
				m.setAlbum(album);// ��������ר��
				m.setAlbum_id(album_id);// ��������ר���ı��
				m.setSize(size);// �����Ĵ�С
				m.setTime(time);// ������ʱ��
				m.setUrl(url);// ������ŵ�·��
				m.setName(name);// ��������������׺
				musicList.add(m);// ��������ӵ�������
			}
		}
		cursor.close();// �ر��α�
		return musicList;
	}

	public static String timeToString(int time) {// ʱ���ʽת����������ת���ɷ������ʽ
		int temp = time / 1000;// ������ת������
		int minute = temp / 60;// ����һ���ж��ٷ�
		int second = temp % 60;// ������Щ�ֺ󣬻�ʣ������
		return String.format("%02d:%02d", minute, second);// �Է������ʽ��ʾ
	}

	public static List<Music> getDataFromDB(SQLiteDatabase db) {
		List<Music> musics = new ArrayList<Music>();
		Cursor cursor = db.rawQuery("select * from music_tb", null);
		if (cursor == null || cursor.getCount() == 0) {
			return musics;
		}
		while (cursor.moveToNext()) {
			Music music = new Music();
			music.setTitle(cursor.getString(cursor.getColumnIndex(("title"))));
			music.setSinger(cursor.getString(cursor.getColumnIndex("artist")));
			music.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
			music.setAlbum_id(cursor.getInt(cursor.getColumnIndex("album_id")));
			music.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			music.setTime(cursor.getInt(cursor.getColumnIndex("time")));
			musics.add(music);
		}
		return musics;
	}	
	public static Bitmap getAlbumPic(Context context, Music music) {
		ContentResolver mResolver = context.getContentResolver();// ��ȡ���ݽ�����
		Uri uri = ContentUris.withAppendedId(Constants.ALBUM_URL, music.getAlbum_id());
		try {
			InputStream inputStream = mResolver.openInputStream(uri);
			return BitmapFactory.decodeStream(inputStream);
		} catch (FileNotFoundException ex) {// ������������׳��쳣
			try {
				ParcelFileDescriptor pfd = mResolver.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
					return bitmap;
				}
			} catch (Exception e) {
				return null;
			}			
			return null;
		}
	}
}
