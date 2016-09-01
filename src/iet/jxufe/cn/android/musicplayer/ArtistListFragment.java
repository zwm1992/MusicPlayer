package iet.jxufe.cn.android.musicplayer;


import java.util.ArrayList;
import java.util.List;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ArtistListFragment extends ListFragment {
	private List<Music> musicList;// 所有音乐集合
	private List<MusicGroupByArtist> artistsList = new ArrayList<MusicGroupByArtist>();// 所有的艺术家集合
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicList = MusicUtils.getMusicData(getActivity());// 获取所有音乐		
		if (musicList != null) {
			MusicGroupByArtist(musicList);// 调用方法对音乐进行分组
		} else {
			Toast.makeText(getActivity(), "存储卡中暂时没有音乐，请添加音乐...",
					Toast.LENGTH_SHORT).show();// 提示存储卡中没有音乐
		}
	}

	public void MusicGroupByArtist(List<Music> musicList) {//对音乐进行分组，并统计每个艺术家包含的音乐数		
		for (int i = 0; i < musicList.size(); i++) {// 循环遍历每一首音乐，判断其专辑
			int j = 0;
			for (; j < artistsList.size(); j++) {//循环遍历已有的艺术家，判断是否已经存在该艺术家
				if (musicList.get(i).getSinger()
						.equals(artistsList.get(j).getArtistName())) {// 如果已经存在该艺术家，则添加一个
					artistsList.get(j).setCount(
							artistsList.get(j).getCount() + 1);// 数量加1
					artistsList.get(j).getMusics().add(musicList.get(i));// 并把音乐添加到集合中去
					break;// 退出循环
				}
			}
			if (j == artistsList.size()) {// 如果不存在该艺术家，则将其添加进去
				MusicGroupByArtist artist = new MusicGroupByArtist();// 创建一个艺术家
				artist.setArtistName(musicList.get(i).getSinger());//设置艺术家的名字
				artist.setCount(1);// 默认歌曲数为1
				List<Music> musics = new ArrayList<Music>();//创建一个集合用于保存该艺术家所有的音乐
				musics.add(musicList.get(i));//向集合中添加音乐
				artist.setMusics(musics);
				artistsList.add(artist);//将艺术家添加到集合中去
			}
		}
	}

	private class MusicGroupByArtist {//艺术家分组信息
		private String artistName;//艺术家的名字
		private int count;//包含的音乐数量
		private List<Music> musics;//具体包含的音乐集合
		//相关属性的set和get方法
		public String getArtistName() {
			return artistName;
		}

		public void setArtistName(String artistName) {
			this.artistName = artistName;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public List<Music> getMusics() {
			return musics;
		}

		public void setMusics(List<Music> musics) {
			this.musics = musics;
		}
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		setListAdapter(new ArtistAdapter());//显示按艺术家分类的结果		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private class ArtistAdapter extends BaseAdapter {
		public int getCount() {
			return artistsList.size();
		}
		public Object getItem(int position) {
			return artistsList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LinearLayout.inflate(getActivity(),
						R.layout.artist_item, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);// 显示图标的控件
			TextView album = (TextView) convertView.findViewById(R.id.artist);// 显示演唱者名称
			TextView info = (TextView) convertView.findViewById(R.id.info);// 显示一共有多少首歌曲
			Bitmap bitmap=MusicUtils.getAlbumPic(getActivity(), artistsList.get(position).getMusics().get(0));
			if(bitmap!=null){
				icon.setImageBitmap(bitmap);
			}else {
				icon.setImageResource(R.drawable.artist);
			}	
			album.setText(artistsList.get(position).getArtistName());
			info.setText(Html.fromHtml("共有<font color=red><b>" + artistsList.get(position).getCount() + "</b></font>首歌曲"));
			return convertView;
		}
	}
	public void onListItemClick(ListView l, View v, int position, long id) {//单击某个艺术家后，显示该艺术家的所有音乐
		MusicListFragment musicListFragment=new MusicListFragment();
		musicListFragment.setMusicList(artistsList.get(position).getMusics());
		FragmentTransaction fTransaction=getActivity().getFragmentManager().beginTransaction();//开启事务
		fTransaction.replace(R.id.realContent, musicListFragment);
		fTransaction.commit();//提交事务
		super.onListItemClick(l, v, position, id);
	}
}
