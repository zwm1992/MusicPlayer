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
	public List<Music> musicList=Constants.playlist;//�����б��е�����
	private PlayListAdapter adapter;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		if (musicList != null&&musicList.size()!=0) {
			adapter=new PlayListAdapter();
			setListAdapter(adapter);			
		} else {
			Toast.makeText(getActivity(), "�����б�����ʱû�����֣����������...",
					Toast.LENGTH_SHORT).show();
		}		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	public void onStart() {
		registerForContextMenu(getListView());// Ϊ�����б�ע�������Ĳ˵�
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
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);// ��ʾͼ��Ŀؼ�
			TextView title = (TextView) convertView.findViewById(R.id.title);// ��ʾ�������Ŀؼ�
			TextView artist = (TextView) convertView.findViewById(R.id.artist);// ��ʾ�ݳ��ߵĿؼ�
			TextView time = (TextView) convertView.findViewById(R.id.time);// ��ʾʱ��Ŀؼ�
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
	public void onListItemClick(ListView l, View v, int position, long id) {// ����������¼�����
		Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
		intent.putExtra("listType", Constants.PLAY_LIST_MUSIC);//���ֲ��ŵ��б����ͣ������б�
		intent.putExtra("music", musicList.get(position));//��ǰ���ŵ�����
		intent.putExtra("position", position);//��ǰ�������б��е�����
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {// ���������Ĳ˵�
		getActivity().getMenuInflater().inflate(R.menu.playlist_context, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.deleteAll://��ղ����б�	
			musicList.clear();
			System.out.println(musicList);			
			break;
		case R.id.deleteFromList://�Ӳ����б���ɾ��	
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
