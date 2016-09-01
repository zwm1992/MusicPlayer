package iet.jxufe.cn.android.musicplayer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends Activity {//主界面	
	private TabHost mTabHost;//选项卡
	private String[] titles = new String[] { "艺术家", "音乐", "专辑", "播放列表" };//选项标题
	private String[] tags = new String[] { "artist", "music", "album","playlist" };//选项标记
	private int[] icons = new int[] { R.drawable.music, R.drawable.artist,
			R.drawable.album, R.drawable.playlist };//选项图标
	private MyOpenHelper mHelper;//数据库辅助类
	private SQLiteDatabase mDatabase;//数据库类
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏显示
		setContentView(R.layout.activity_main);
		initData();//初始化数据
		mTabHost = (TabHost) findViewById(R.id.mTabHost);
		mTabHost.setup();
		for (int i = 0; i < titles.length; i++) {// 循环添加选项卡
			TabSpec tabSpec = mTabHost.newTabSpec(tags[i]);// 创建一个选项，并指定其标记
			View view = getLayoutInflater().inflate(R.layout.tab, null);// 将布局文件转为View对象
			TextView titleView = (TextView) view.findViewById(R.id.title);
			ImageView iconView = (ImageView) view.findViewById(R.id.icon);
			titleView.setText(titles[i]);// 设置标题
			iconView.setImageResource(icons[i]);// 设置图标
			tabSpec.setIndicator(view);// 为选项设置标题和图标
			tabSpec.setContent(R.id.realContent);// 为每个选项设置内容
			mTabHost.addTab(tabSpec);// 将选项添加到选项卡中
		}
		mTabHost.setOnTabChangedListener(new MyTabChangedListener());// 添加选项改变事件处理
		mTabHost.setCurrentTab(1);//默认显示第二个		
	}
	public void initData(){
		mHelper=new MyOpenHelper(this, "music",null, 1);//得到数据库辅助类
		mDatabase=mHelper.getWritableDatabase();//获取数据库
		Constants.playlist=MusicUtils.getDataFromDB(mDatabase);//初始化播放列表
	}
	private class MyTabChangedListener implements OnTabChangeListener {// 自定义选项改变事件监听器
		public void onTabChanged(String tabTag) {
			FragmentTransaction fragmentTransaction = getFragmentManager()
					.beginTransaction();// 开始事务
			if (tabTag.equalsIgnoreCase("music")) {//切换到音乐列表
				MusicListFragment musicListFragment=new MusicListFragment();
				musicListFragment.setMusicList(null);
				fragmentTransaction.replace(R.id.realContent,
						musicListFragment);
			} else if (tabTag.equalsIgnoreCase("artist")) {//切换到按艺术家分类				
				fragmentTransaction.replace(R.id.realContent,
						new ArtistListFragment());
			} else if (tabTag.equalsIgnoreCase("album")) {//切换到按专辑分类
				fragmentTransaction.replace(R.id.realContent,
						new AlbumListFragment());
			} else if (tabTag.equalsIgnoreCase("playlist")) {
				fragmentTransaction.replace(R.id.realContent,
						new PlayListFragment());
			}
			fragmentTransaction.commit();// 提交事务
		}
	}
	protected void onDestroy() {//关闭的时候将播放列表中的数据保存到数据库中
		mDatabase.execSQL("delete from music_tb");//删除已有的所有数据
		for(int i=0;i<Constants.playlist.size();i++){//循环遍历播放列表中的音乐
			Music music=Constants.playlist.get(i);//获取音乐
			mDatabase.execSQL("insert into music_tb (title,artist,album,album_id,time,url)values(?,?,?,?,?,?)",new String[]{
					music.getTitle(),music.getSinger(),music.getAlbum(),music.getAlbum_id()+"",music.getTime()+"",music.getUrl()});//将音乐信息保存到数据库
		}
		super.onDestroy();
	}
}
