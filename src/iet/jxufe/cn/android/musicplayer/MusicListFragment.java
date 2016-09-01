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

public class MusicListFragment extends ListFragment {//默认显示所有音乐信息，所以在此启动后台音乐播放服务
	public List<Music> musicList;// 要显示的音乐的集合
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicList = getMusicList();// 获取音乐
		if (musicList == null || musicList.size() == 0) {// 如果音乐为空
			musicList = MusicUtils.getMusicData(getActivity());//获取所有音乐
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Constants.musiclist = musicList;
		if (musicList != null) {
			setListAdapter(new MusicAdapter());//显示音乐列表
		} else {
			Toast.makeText(getActivity(), "存储卡中暂时没有音乐，请添加音乐...",
					Toast.LENGTH_SHORT).show();//提示存储卡中没有音乐
		}
		Intent intent = new Intent(getActivity(), MusicService.class);// 创建Intent，启动指定的服务
		getActivity().startService(intent);// 启动服务
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void onStart() {
		registerForContextMenu(getListView());// 为音乐列表注册上下文菜单
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
						R.layout.music_item, null);//将布局文件转换成View对象
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);// 显示图标的控件
			TextView title = (TextView) convertView.findViewById(R.id.title);// 显示歌曲名的控件
			TextView artist = (TextView) convertView.findViewById(R.id.artist);// 显示演唱者的控件
			TextView time = (TextView) convertView.findViewById(R.id.time);// 显示时间的控件
			Bitmap bitmap=MusicUtils.getAlbumPic(getActivity(), musicList.get(position));//显示专辑图片的控件
			if(bitmap!=null){//如果专辑图片不为空，则显示；如果为空，则显示默认图片
				icon.setImageBitmap(bitmap);
			}else {
				icon.setImageResource(R.drawable.music);//显示默认的图片
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
	public void onListItemClick(ListView l, View v, int position, long id) {// 音乐项被单击事件处理
		Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
		intent.putExtra("listType", Constants.ALL_MUSIC);//音乐播放的列表类型：所有音乐的列表
		intent.putExtra("music", musicList.get(position));//当前的音乐
		intent.putExtra("position", position);//当前音乐对应的位置
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {// 创建上下文菜单
		getActivity().getMenuInflater().inflate(R.menu.musiclist_context, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();//获取上下文菜单信息
		switch (item.getItemId()) {
		case R.id.setToBell:// 设置为手机铃声
			setRing(musicList.get(info.position));
			break;
		case R.id.addToPlayList:// 添加到收藏列表			
			Music music = musicList.get(info.position);			
			int i = 0;
			for (; i < Constants.playlist.size(); i++) {//循环遍历播放列表中是否已经存在该音乐
				if (Constants.playlist.get(i).getTitle()
						.equalsIgnoreCase(music.getTitle())) {
					break;//如果存在则不需要添加，直接退出
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
	public void setRing(Music music) {//设置铃声
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, music.getUrl());//音乐路径
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);//是否是铃声
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);//是否是通知声
		values.put(MediaStore.Audio.Media.IS_ALARM, false);//是否是闹钟声
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);//是否是音乐
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getUrl());//根据路径获取对应的URI
		Uri newUri = getActivity().getContentResolver().insert(uri, values);//插入新的值
		RingtoneManager.setActualDefaultRingtoneUri(getActivity(),RingtoneManager.TYPE_RINGTONE, newUri);
	}
}
