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

public class AlbumListFragment extends ListFragment {
	private List<Music> musicList;// 所有音乐集合
	private List<MusicGroupByAlbum> albumList = new ArrayList<AlbumListFragment.MusicGroupByAlbum>();// 所有的专辑信息

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicList = MusicUtils.getMusicData(getActivity());// 获取所有音乐
		if (musicList != null) {
			MusicGroupByAlbum(musicList);// 调用方法对音乐进行分组
		} else {
			Toast.makeText(getActivity(), "存储卡中暂时没有音乐，请添加音乐...",
					Toast.LENGTH_SHORT).show();// 提示存储卡中没有音乐
		}
	}

	public void MusicGroupByAlbum(List<Music> musicList) {// 对音乐按专辑分组，并统计每个专辑包含的音乐数
		for (int i = 0; i < musicList.size(); i++) {// 循环遍历每一首音乐，获取其专辑
			int j = 0;
			for (; j < albumList.size(); j++) {// 循环遍历已有专辑，判断该专辑是否存在
				if (musicList.get(i).getAlbum()
						.equals(albumList.get(j).getAlbumName())) {// 如果已存在该专辑
					albumList.get(j).setCount(albumList.get(j).getCount() + 1);// 数量加1
					albumList.get(j).getMusics().add(musicList.get(i));// 并把音乐添加到集合中去
					break;// 退出循环
				}
			}
			if (j == albumList.size()) {// 如果列表中没有该专辑
				MusicGroupByAlbum album = new MusicGroupByAlbum();// 创建一个新专辑
				album.setAlbumName(musicList.get(i).getAlbum());
				album.setCount(1);// 默认歌曲为1
				List<Music> musics = new ArrayList<Music>();
				musics.add(musicList.get(i));
				album.setMusics(musics);
				albumList.add(album);
			}
		}
	}

	private class MusicGroupByAlbum {// 按专辑分组信息
		private String albumName;// 专辑名
		private int count;// 专辑中包含的歌曲数量
		private List<Music> musics;// 专辑中包含的歌曲

		// 相应的set和get方法
		public String getAlbumName() {
			return albumName;
		}

		public void setAlbumName(String albumName) {
			this.albumName = albumName;
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

		public void setMusics(List<Music> musicList) {
			this.musics = musicList;
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setListAdapter(new AlbumAdapter());//显示所有专辑信息
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private class AlbumAdapter extends BaseAdapter {
		public int getCount() {
			return albumList.size();
		}

		public Object getItem(int position) {
			return albumList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LinearLayout.inflate(getActivity(),
						R.layout.album_item, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);// 显示图标的控件
			TextView album = (TextView) convertView.findViewById(R.id.album);// 显示专辑名称
			TextView info = (TextView) convertView.findViewById(R.id.info);// 显示一共有多少首歌曲
			Bitmap bitmap = MusicUtils.getAlbumPic(getActivity(), albumList
					.get(position).getMusics().get(0));
			if (bitmap != null) {
				icon.setImageBitmap(bitmap);
			} else {
				icon.setImageResource(R.drawable.album);
			}
			album.setText(albumList.get(position).getAlbumName());
			info.setText(Html.fromHtml("共有<font color=red><b>"
					+ albumList.get(position).getCount() + "</b></font>首歌曲"));
			return convertView;
		}
	}

	public void onListItemClick(ListView l, View v, int position, long id) {//选中某一专辑后，显示该专辑中所包含的所有音乐信息
		MusicListFragment musicListFragment = new MusicListFragment();
		musicListFragment.setMusicList(albumList.get(position).getMusics());
		FragmentTransaction fTransaction = getActivity().getFragmentManager()
				.beginTransaction();// 开启事务
		fTransaction.replace(R.id.realContent, musicListFragment);
		fTransaction.commit();//提交事务
		super.onListItemClick(l, v, position, id);
	}
}
