package iet.jxufe.cn.android.musicplayer;

import java.util.List;

import android.app.ListFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicListFragment extends ListFragment {//Ĭ����ʾ����������Ϣ�������ڴ�������̨���ֲ��ŷ���
	public List<Music> musicList;// Ҫ��ʾ�����ֵļ���
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicList = getMusicList();// ��ȡ����
		if (musicList == null || musicList.size() == 0) {// �������Ϊ��
			musicList = MusicUtils.getMusicData(getActivity());//��ȡ��������
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Constants.musiclist = musicList;
		if (musicList != null) {
			setListAdapter(new MusicAdapter());//��ʾ�����б�
		} else {
			Toast.makeText(getActivity(), "�洢������ʱû�����֣����������...",
					Toast.LENGTH_SHORT).show();//��ʾ�洢����û������
		}
		Intent intent = new Intent(getActivity(), MusicService.class);// ����Intent������ָ���ķ���
		getActivity().startService(intent);// ��������
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void onStart() {
		registerForContextMenu(getListView());// Ϊ�����б�ע�������Ĳ˵�
		super.onStart();
	}

	private class MusicAdapter extends BaseAdapter {
		public int getCount() {
			return musicList.size();
		}
		public Object getItem(int position) {
			return musicList.get(position);
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LinearLayout.inflate(getActivity(),
						R.layout.music_item, null);//�������ļ�ת����View����
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);// ��ʾͼ��Ŀؼ�
			TextView title = (TextView) convertView.findViewById(R.id.title);// ��ʾ�������Ŀؼ�
			TextView artist = (TextView) convertView.findViewById(R.id.artist);// ��ʾ�ݳ��ߵĿؼ�
			TextView time = (TextView) convertView.findViewById(R.id.time);// ��ʾʱ��Ŀؼ�
			Bitmap bitmap=MusicUtils.getAlbumPic(getActivity(), musicList.get(position));//��ʾר��ͼƬ�Ŀؼ�
			if(bitmap!=null){//���ר��ͼƬ��Ϊ�գ�����ʾ�����Ϊ�գ�����ʾĬ��ͼƬ
				icon.setImageBitmap(bitmap);
			}else {
				icon.setImageResource(R.drawable.music);//��ʾĬ�ϵ�ͼƬ
			}
			title.setText(musicList.get(position).getTitle());
			artist.setText(musicList.get(position).getSinger());
			time.setText(MusicUtils.timeToString(musicList.get(position)
					.getTime()));
			return convertView;
		}
	}
	public List<Music> getMusicList() {
		return musicList;
	}
	public void setMusicList(List<Music> musicList) {
		this.musicList = musicList;
	}
	public void onListItemClick(ListView l, View v, int position, long id) {// ����������¼�����
		Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
		intent.putExtra("listType", Constants.ALL_MUSIC);//���ֲ��ŵ��б����ͣ��������ֵ��б�
		intent.putExtra("music", musicList.get(position));//��ǰ������
		intent.putExtra("position", position);//��ǰ���ֶ�Ӧ��λ��
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {// ���������Ĳ˵�
		getActivity().getMenuInflater().inflate(R.menu.musiclist_context, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();//��ȡ�����Ĳ˵���Ϣ
		switch (item.getItemId()) {
		case R.id.setToBell:// ����Ϊ�ֻ�����
			setRing(musicList.get(info.position));
			break;
		case R.id.addToPlayList:// ��ӵ��ղ��б�			
			Music music = musicList.get(info.position);			
			int i = 0;
			for (; i < Constants.playlist.size(); i++) {//ѭ�����������б����Ƿ��Ѿ����ڸ�����
				if (Constants.playlist.get(i).getTitle()
						.equalsIgnoreCase(music.getTitle())) {
					break;//�����������Ҫ��ӣ�ֱ���˳�
				}
			}
			if (i == Constants.playlist.size()) {
				Constants.playlist.add(music);
			}
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	public void setRing(Music music) {//��������
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, music.getUrl());//����·��
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);//�Ƿ�������
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);//�Ƿ���֪ͨ��
		values.put(MediaStore.Audio.Media.IS_ALARM, false);//�Ƿ���������
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);//�Ƿ�������
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getUrl());//����·����ȡ��Ӧ��URI
		Uri newUri = getActivity().getContentResolver().insert(uri, values);//�����µ�ֵ
		RingtoneManager.setActualDefaultRingtoneUri(getActivity(),RingtoneManager.TYPE_RINGTONE, newUri);
	}
}
