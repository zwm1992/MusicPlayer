package iet.jxufe.cn.android.musicplayer;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class MusicPlayActivity extends Activity {// 音乐播放主界面，可控制音乐的播放
	private List<Music> musicList;//记录音乐列表
	private TextView titleView, singerView, currentTimeView, totalTimeView;// 显示歌曲名、作者信息、当前播放时间、总时间的文本显示框
	private SeekBar playProgress;// 拖动条
	private Spinner styleSpinner;//选择播放形式的下拉列表
	private ImageButton control;// 播放/暂停按钮
	private ImageView picView;//显示图片
	private ServerReceiver serverReceiver;// 接收后台服务发送的广播的广播接收器
	private Music currentMusic;//记录当前播放的音乐
	private boolean isPause=false;//是否暂停
	private int currentPosition;//当前音乐的索引
	private int listType;//音乐列表类型：所有音乐还是播放列表中的音乐	
	private String[] styles=new String[]{Constants.LIST_LOOP,Constants.SINGLE_LOOP,Constants.RANDOM_PLAY,Constants.OVER_FINISH};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏显示
		setContentView(R.layout.play_item);//加载主界面		
		initView();// 初始化界面
	}
	public void initView() {//执行初始化操作
		styleSpinner=(Spinner)findViewById(R.id.styleSpinner);
		styleSpinner.setAdapter(new ArrayAdapter<String>(this,R.layout.spinner_text,styles));
		styleSpinner.setOnItemSelectedListener(new SpinnerItemClickListener());
		picView=(ImageView)findViewById(R.id.picView);
		currentTimeView = (TextView) findViewById(R.id.currentTime);// 显示音乐当前播放的时间的文本控件
		totalTimeView = (TextView) findViewById(R.id.totalTime);// 显示音乐的总时长的文本控件
		titleView = (TextView) findViewById(R.id.title);// 显示音乐标题的文本控件
		singerView = (TextView) findViewById(R.id.singer);// 显示音乐演唱者的文本控件
		playProgress = (SeekBar) findViewById(R.id.playProgress);// 显示当前音乐播放进度的控件
		control = (ImageButton) findViewById(R.id.control);// 控制播放或暂停的控件
		playProgress.setOnSeekBarChangeListener(new MySeekBarChangeListener());// 为拖动条添加事件处理
		serverReceiver = new ServerReceiver();//创建广播接收器
		IntentFilter filter = new IntentFilter();//可以接收到的广播类型
		filter.addAction(Constants.COMPLETE_ACTION);// 音乐播放结束的事件
		filter.addAction(Constants.UPDATE_ACTION);// 更新进度的动作
		registerReceiver(serverReceiver, filter);//注册广播接收器		
		currentMusic = (Music) getIntent().getSerializableExtra("music");// 获取当前播放的音乐
		if(currentMusic==null){//如果当前音乐为空
			SharedPreferences musicPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
			currentPosition=musicPreferences.getInt("position", 0);
			listType=musicPreferences.getInt("listType", Constants.ALL_MUSIC);
			if(listType==Constants.ALL_MUSIC){//如果是所有的音乐
				musicList=Constants.musiclist;
			}else{//如果是播放列表
				musicList=Constants.playlist;
			}
			currentMusic=musicList.get(currentPosition);//获取当前的音乐
			String styleString=musicPreferences.getString("style", Constants.LIST_LOOP);
			for(int i=0;i<styles.length;i++){
				if(styles[i].equalsIgnoreCase(styleString)){
					styleSpinner.setSelection(i);
					break;
				}
			}
			showInfo();//显示音乐信息
			isPause=true;//继续播放
			control(null);
		}else{//直接播放音乐
			currentPosition=getIntent().getIntExtra("position",0);//默认为第一首
			listType=getIntent().getIntExtra("listType", Constants.ALL_MUSIC);//获取列表的类型，是从所有的音乐还是播放列表中播放
			playNewMusic();//播放音乐
			if(listType==Constants.ALL_MUSIC){//如果是所有的音乐
				musicList=Constants.musiclist;
			}else{//如果是播放列表
				musicList=Constants.playlist;
			}
		}		
		
	}
	public void showInfo(){
		totalTimeView.setText(MusicUtils.timeToString(currentMusic.getTime()));//显示音乐的总时长
		titleView.setText("        " + currentMusic.getTitle() + "        ");// 显示歌曲名
		singerView.setText(currentMusic.getSinger());// 显示演唱者
		Bitmap bitmap=MusicUtils.getAlbumPic(this, currentMusic);
		if(bitmap!=null){
			picView.setImageBitmap(bitmap);
		}else{
			picView.setImageResource(R.drawable.background);
		}
	}
	public void playNewMusic() {
		currentTimeView.setText(MusicUtils.timeToString(0));//显示当前播放的时间，默认为0
		showInfo();
		playProgress.setProgress(0);//进度为0
		control.setImageResource(R.drawable.pause);//显示暂停的按钮		
		// 发送广播，通知后台播放音乐
		Intent controlIntent = new Intent(Constants.CONTROL_ACTION);
		controlIntent.putExtra("new", Constants.NEW);//这是一首新音乐
		controlIntent.putExtra("position",currentPosition);//传递当前音乐的序号
		controlIntent.putExtra("listType",listType);//传递音乐列表的类型
		sendBroadcast(controlIntent);//发送广播
		isPause=false;//是否暂停为false
	}
	private class MySeekBarChangeListener implements OnSeekBarChangeListener {
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) { // 进度发送变化时调用该方法
		}
		public void onStartTrackingTouch(SeekBar seekBar) {// 开始拖动时调用
		}
		public void onStopTrackingTouch(SeekBar seekBar) {// 结束拖动时调用			
			Intent seekIntent = new Intent(Constants.SEEKBAR_ACTION);//发送广播通知拖动条变化
			seekIntent.putExtra("progress", seekBar.getProgress());//将当前的进度传递进去
			sendBroadcast(seekIntent);// 发送广播
		}
	}
	private class ServerReceiver extends BroadcastReceiver {// 广播接收器，用于接收后台服务发送的广播
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction()==Constants.UPDATE_ACTION){//更新进度的广播处理
				int position=intent.getIntExtra("position",0);//获取音乐播放的位置				
				currentTimeView.setText(MusicUtils.timeToString(position));//显示当前的播放时长
				playProgress.setProgress((int)(position*1.0/currentMusic.getTime()*100));//根据位置计算进度条的进度
			}else if(intent.getAction()==Constants.COMPLETE_ACTION){//音乐播放完成的事件处理
				currentPosition=intent.getIntExtra("position", 0);//获取当前播放的音乐的序号
				currentMusic=musicList.get(currentPosition);//获取当前的音乐
				showInfo();//显示当前音乐的信息
			}
		}
	}
	private class SpinnerItemClickListener implements OnItemSelectedListener{		
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {			
			Intent styleIntent=new Intent(Constants.UPDATE_STYLE);
			styleIntent.putExtra("style",styles[position]);
			sendBroadcast(styleIntent);
		}
		public void onNothingSelected(AdapterView<?> parent) {			
		}		
	}
	public void chooseMusic(View view){//选择歌曲按钮的事件处理
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
		this.finish();
	}
	public void first(View view) {// 第一首按钮的事件处理
		currentPosition=0;
		currentMusic=musicList.get(currentPosition);
		playNewMusic();
	}
	public void pre(View view) {// 前一首按钮的事件处理
		currentPosition=(currentPosition-1+musicList.size())%musicList.size();
		currentMusic=musicList.get(currentPosition);
		playNewMusic();
	}
	public void control(View view) {// 播放和暂停按钮的事件处理
		Intent controlIntent=new Intent(Constants.CONTROL_ACTION);//控制音乐播放和暂停
		if(!isPause){//如果处于播放状态，发送广播通知暂停
			controlIntent.putExtra("control",Constants.PAUSE);
			control.setImageResource(R.drawable.play);//改变图标
		}else{//如果处于暂停状态，发送广播通知播放
			controlIntent.putExtra("control",Constants.PLAY);
			control.setImageResource(R.drawable.pause);//改变图标
		}
		isPause=!isPause;
		sendBroadcast(controlIntent);//发送广播
	}
	public void next(View view) {// 下一首按钮的事件处理
		currentPosition=(currentPosition+1)%musicList.size();
		currentMusic=musicList.get(currentPosition);
		playNewMusic();
	}
	public void last(View view) {// 最后一首按钮的事件处理
		currentPosition=musicList.size()-1;
		currentMusic=musicList.get(currentPosition);
		playNewMusic();
	}
	protected void onDestroy() {//服务销毁时，取消广播接收器的注册
		if (serverReceiver != null) {
			unregisterReceiver(serverReceiver);// Activity销毁时，取消注册
		}
		super.onDestroy();
	}
}
