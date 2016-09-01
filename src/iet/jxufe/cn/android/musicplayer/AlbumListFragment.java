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
	private List<Music> musicList;// �������ּ���
	private List<MusicGroupByAlbum> albumList = new ArrayList<AlbumListFragment.MusicGroupByAlbum>();// ���е�ר����Ϣ

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicList = MusicUtils.getMusicData(getActivity());// ��ȡ��������
		if (musicList != null) {
			MusicGroupByAlbum(musicList);// ���÷��������ֽ��з���
		} else {
			Toast.makeText(getActivity(), "�洢������ʱû�����֣����������...",
					Toast.LENGTH_SHORT).show();// ��ʾ�洢����û������
		}
	}

	public void MusicGroupByAlbum(List<Music> musicList) {// �����ְ�ר�����飬��ͳ��ÿ��ר��������������
		for (int i = 0; i < musicList.size(); i++) {// ѭ������ÿһ�����֣���ȡ��ר��
			int j = 0;
			for (; j < albumList.size(); j++) {// ѭ����������ר�����жϸ�ר���Ƿ����
				if (musicList.get(i).getAlbum()
						.equals(albumList.get(j).getAlbumName())) {// ����Ѵ��ڸ�ר��
					albumList.get(j).setCount(albumList.get(j).getCount() + 1);// ������1
					albumList.get(j).getMusics().add(musicList.get(i));// ����������ӵ�������ȥ
					break;// �˳�ѭ��
				}
			}
			if (j == albumList.size()) {// ����б���û�и�ר��
				MusicGroupByAlbum album = new MusicGroupByAlbum();// ����һ����ר��
				album.setAlbumName(musicList.get(i).getAlbum());
				album.setCount(1);// Ĭ�ϸ���Ϊ1
				List<Music> musics = new ArrayList<Music>();
				musics.add(musicList.get(i));
				album.setMusics(musics);
				albumList.add(album);
			}
		}
	}

	private class MusicGroupByAlbum {// ��ר��������Ϣ
		private String albumName;// ר����
		private int count;// ר���а����ĸ�������
		private List<Music> musics;// ר���а����ĸ���

		// ��Ӧ��set��get����
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
		setListAdapter(new AlbumAdapter());//��ʾ����ר����Ϣ
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
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);// ��ʾͼ��Ŀؼ�
			TextView album = (TextView) convertView.findViewById(R.id.album);// ��ʾר������
			TextView info = (TextView) convertView.findViewById(R.id.info);// ��ʾһ���ж����׸���
			Bitmap bitmap = MusicUtils.getAlbumPic(getActivity(), albumList
					.get(position).getMusics().get(0));
			if (bitmap != null) {
				icon.setImageBitmap(bitmap);
			} else {
				icon.setImageResource(R.drawable.album);
			}
			album.setText(albumList.get(position).getAlbumName());
			info.setText(Html.fromHtml("����<font color=red><b>"
					+ albumList.get(position).getCount() + "</b></font>�׸���"));
			return convertView;
		}
	}

	public void onListItemClick(ListView l, View v, int position, long id) {//ѡ��ĳһר������ʾ��ר����������������������Ϣ
		MusicListFragment musicListFragment = new MusicListFragment();
		musicListFragment.setMusicList(albumList.get(position).getMusics());
		FragmentTransaction fTransaction = getActivity().getFragmentManager()
				.beginTransaction();// ��������
		fTransaction.replace(R.id.realContent, musicListFragment);
		fTransaction.commit();//�ύ����
		super.onListItemClick(l, v, position, id);
	}
}
