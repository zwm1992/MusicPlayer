package iet.jxufe.cn.android.musicplayer;

import java.util.List;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PlayListFragment extends ListFragment {
	public List<Music> musicList=Constants.playlist;//播放列表中的音乐
	private PlayListAdapter adapter;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		if (musicList != null&&musicList.size()!=0) {
			adapter=new PlayListAdapter();
			setListAdapter(adapter);			
		} else {
			Toast.makeText(getActivity(), "播放列表中暂时没有音乐，请添加音乐...",
					Toast.LENGTH_SHORT).show();
		}		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	public void onStart() {
		registerForContextMenu(getListView());// 为音乐列表注册上下文菜单
		super.onStart();
	}
	private class PlayListAdapter extends BaseAdapter {
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
						R.layout.music_item, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);// 显示图标的控件
			TextView title = (TextView) convertView.findViewById(R.id.title);// 显示歌曲名的控件
			TextView artist = (TextView) convertView.findViewById(R.id.artist);// 显示演唱者的控件
			TextView time = (TextView) convertView.findViewById(R.id.time);// 显示时间的控件
			Bitmap bitmap=MusicUtils.getAlbumPic(getActivity(), musicList.get(position));
			if(bitmap!=null){
				icon.setImageBitmap(bitmap);
			}else {
				icon.setImageResource(R.drawable.music);
			}
			title.setText(musicList.get(position).getTitle());
			artist.setText(musicList.get(position).getSinger());
			time.setText(MusicUtils.timeToString(musicList.get(position)
					.getTime()));
			return convertView;
		}
	}
	public void onListItemClick(ListView l, View v, int position, long id) {// 音乐项被单击事件处理
		Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
		intent.putExtra("listType", Constants.PLAY_LIST_MUSIC);//音乐播放的列表类型：播放列表
		intent.putExtra("music", musicList.get(position));//当前播放的音乐
		intent.putExtra("position", position);//当前音乐在列表中的索引
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {// 创建上下文菜单
		getActivity().getMenuInflater().inflate(R.menu.playlist_context, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.deleteAll://清空播放列表	
			musicList.clear();
			System.out.println(musicList);			
			break;
		case R.id.deleteFromList://从播放列表中删除	
			musicList.remove(info.position);						
			break;
		default:
			break;
		}
		adapter.notifyDataSetChanged();
		Constants.playlist=musicList;
		return super.onContextItemSelected(item);
	}
}
