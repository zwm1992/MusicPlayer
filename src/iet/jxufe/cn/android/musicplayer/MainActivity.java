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

public class MainActivity extends Activity {//������	
	private TabHost mTabHost;//ѡ�
	private String[] titles = new String[] { "������", "����", "ר��", "�����б�" };//ѡ�����
	private String[] tags = new String[] { "artist", "music", "album","playlist" };//ѡ����
	private int[] icons = new int[] { R.drawable.music, R.drawable.artist,
			R.drawable.album, R.drawable.playlist };//ѡ��ͼ��
	private MyOpenHelper mHelper;//���ݿ⸨����
	private SQLiteDatabase mDatabase;//���ݿ���
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȫ����ʾ
		setContentView(R.layout.activity_main);
		initData();//��ʼ������
		mTabHost = (TabHost) findViewById(R.id.mTabHost);
		mTabHost.setup();
		for (int i = 0; i < titles.length; i++) {// ѭ�����ѡ�
			TabSpec tabSpec = mTabHost.newTabSpec(tags[i]);// ����һ��ѡ���ָ������
			View view = getLayoutInflater().inflate(R.layout.tab, null);// �������ļ�תΪView����
			TextView titleView = (TextView) view.findViewById(R.id.title);
			ImageView iconView = (ImageView) view.findViewById(R.id.icon);
			titleView.setText(titles[i]);// ���ñ���
			iconView.setImageResource(icons[i]);// ����ͼ��
			tabSpec.setIndicator(view);// Ϊѡ�����ñ����ͼ��
			tabSpec.setContent(R.id.realContent);// Ϊÿ��ѡ����������
			mTabHost.addTab(tabSpec);// ��ѡ����ӵ�ѡ���
		}
		mTabHost.setOnTabChangedListener(new MyTabChangedListener());// ���ѡ��ı��¼�����
		mTabHost.setCurrentTab(1);//Ĭ����ʾ�ڶ���		
	}
	public void initData(){
		mHelper=new MyOpenHelper(this, "music",null, 1);//�õ����ݿ⸨����
		mDatabase=mHelper.getWritableDatabase();//��ȡ���ݿ�
		Constants.playlist=MusicUtils.getDataFromDB(mDatabase);//��ʼ�������б�
	}
	private class MyTabChangedListener implements OnTabChangeListener {// �Զ���ѡ��ı��¼�������
		public void onTabChanged(String tabTag) {
			FragmentTransaction fragmentTransaction = getFragmentManager()
					.beginTransaction();// ��ʼ����
			if (tabTag.equalsIgnoreCase("music")) {//�л��������б�
				MusicListFragment musicListFragment=new MusicListFragment();
				musicListFragment.setMusicList(null);
				fragmentTransaction.replace(R.id.realContent,
						musicListFragment);
			} else if (tabTag.equalsIgnoreCase("artist")) {//�л����������ҷ���				
				fragmentTransaction.replace(R.id.realContent,
						new ArtistListFragment());
			} else if (tabTag.equalsIgnoreCase("album")) {//�л�����ר������
				fragmentTransaction.replace(R.id.realContent,
						new AlbumListFragment());
			} else if (tabTag.equalsIgnoreCase("playlist")) {
				fragmentTransaction.replace(R.id.realContent,
						new PlayListFragment());
			}
			fragmentTransaction.commit();// �ύ����
		}
	}
	protected void onDestroy() {//�رյ�ʱ�򽫲����б��е����ݱ��浽���ݿ���
		mDatabase.execSQL("delete from music_tb");//ɾ�����е���������
		for(int i=0;i<Constants.playlist.size();i++){//ѭ�����������б��е�����
			Music music=Constants.playlist.get(i);//��ȡ����
			mDatabase.execSQL("insert into music_tb (title,artist,album,album_id,time,url)values(?,?,?,?,?,?)",new String[]{
					music.getTitle(),music.getSinger(),music.getAlbum(),music.getAlbum_id()+"",music.getTime()+"",music.getUrl()});//��������Ϣ���浽���ݿ�
		}
		super.onDestroy();
	}
}
