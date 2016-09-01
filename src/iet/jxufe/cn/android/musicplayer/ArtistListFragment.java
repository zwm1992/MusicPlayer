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
	private List<Music> musicList;// �������ּ���
	private List<MusicGroupByArtist> artistsList = new ArrayList<MusicGroupByArtist>();// ���е������Ҽ���
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicList = MusicUtils.getMusicData(getActivity());// ��ȡ��������		
		if (musicList != null) {
			MusicGroupByArtist(musicList);// ���÷��������ֽ��з���
		} else {
			Toast.makeText(getActivity(), "�洢������ʱû�����֣����������...",
					Toast.LENGTH_SHORT).show();// ��ʾ�洢����û������
		}
	}

	public void MusicGroupByArtist(List<Music> musicList) {//�����ֽ��з��飬��ͳ��ÿ�������Ұ�����������		
		for (int i = 0; i < musicList.size(); i++) {// ѭ������ÿһ�����֣��ж���ר��
			int j = 0;
			for (; j < artistsList.size(); j++) {//ѭ���������е������ң��ж��Ƿ��Ѿ����ڸ�������
				if (musicList.get(i).getSinger()
						.equals(artistsList.get(j).getArtistName())) {// ����Ѿ����ڸ������ң������һ��
					artistsList.get(j).setCount(
							artistsList.get(j).getCount() + 1);// ������1
					artistsList.get(j).getMusics().add(musicList.get(i));// ����������ӵ�������ȥ
					break;// �˳�ѭ��
				}
			}
			if (j == artistsList.size()) {// ��������ڸ������ң�������ӽ�ȥ
				MusicGroupByArtist artist = new MusicGroupByArtist();// ����һ��������
				artist.setArtistName(musicList.get(i).getSinger());//���������ҵ�����
				artist.setCount(1);// Ĭ�ϸ�����Ϊ1
				List<Music> musics = new ArrayList<Music>();//����һ���������ڱ�������������е�����
				musics.add(musicList.get(i));//�򼯺����������
				artist.setMusics(musics);
				artistsList.add(artist);//����������ӵ�������ȥ
			}
		}
	}

	private class MusicGroupByArtist {//�����ҷ�����Ϣ
		private String artistName;//�����ҵ�����
		private int count;//��������������
		private List<Music> musics;//������������ּ���
		//������Ե�set��get����
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
		setListAdapter(new ArtistAdapter());//��ʾ�������ҷ���Ľ��		
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
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);// ��ʾͼ��Ŀؼ�
			TextView album = (TextView) convertView.findViewById(R.id.artist);// ��ʾ�ݳ�������
			TextView info = (TextView) convertView.findViewById(R.id.info);// ��ʾһ���ж����׸���
			Bitmap bitmap=MusicUtils.getAlbumPic(getActivity(), artistsList.get(position).getMusics().get(0));
			if(bitmap!=null){
				icon.setImageBitmap(bitmap);
			}else {
				icon.setImageResource(R.drawable.artist);
			}	
			album.setText(artistsList.get(position).getArtistName());
			info.setText(Html.fromHtml("����<font color=red><b>" + artistsList.get(position).getCount() + "</b></font>�׸���"));
			return convertView;
		}
	}
	public void onListItemClick(ListView l, View v, int position, long id) {//����ĳ�������Һ���ʾ�������ҵ���������
		MusicListFragment musicListFragment=new MusicListFragment();
		musicListFragment.setMusicList(artistsList.get(position).getMusics());
		FragmentTransaction fTransaction=getActivity().getFragmentManager().beginTransaction();//��������
		fTransaction.replace(R.id.realContent, musicListFragment);
		fTransaction.commit();//�ύ����
		super.onListItemClick(l, v, position, id);
	}
}
