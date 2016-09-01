package iet.jxufe.cn.android.musicplayer;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
public class MusicService extends Service {	
	private List<Music> musicList;//音乐列表
	private int position;//当前音乐的序号
	private MediaPlayer mediaPlayer;//媒体播放器
	private ActivityReceiver activityReceiver;//广播接收器
	private Music currentMusic;//当前播放的音乐		
	private Timer timer;//定时器
	private int listType;//列表的类型
	private String styleString=Constants.LIST_LOOP;//音乐播放形式,默认列表循环
	public void onCreate() {//启动服务	
		mediaPlayer=new MediaPlayer();
		activityReceiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();//创建Intent过滤器
		filter.addAction(Constants.CONTROL_ACTION);//控制音乐播放的动作，包括播放和暂停
		filter.addAction(Constants.SEEKBAR_ACTION);//改变音乐播放进度
		filter.addAction(Constants.UPDATE_STYLE);//改变音乐播放样式
		registerReceiver(activityReceiver, filter);//注册广播接收器
		super.onCreate();
	}	
	public IBinder onBind(Intent intent) {		
		return null;
	}
	private class ActivityReceiver extends BroadcastReceiver{//获取前台发送的广播
		public void onReceive(Context context, Intent intent) {			
			if(intent.getAction()==Constants.CONTROL_ACTION){//接收到控制播放的广播（开始新音乐、暂停、播放）
				int isNew=intent.getIntExtra("new", -1);
				if(isNew!=-1){//播放一首新音乐
					listType=intent.getIntExtra("listType",Constants.ALL_MUSIC);
					if(listType==Constants.ALL_MUSIC){//如果是所有的音乐
						musicList=Constants.musiclist;
					}else{//如果是播放列表
						musicList=Constants.playlist;
					}
					position=intent.getIntExtra("position", 0);
					currentMusic=musicList.get(position);//获取当前需要播放的音乐
					preparedAndPlay(currentMusic);//准备并播放音乐
				}else{
					int control=intent.getIntExtra("control",-1);
					if(control==Constants.PAUSE){//表示要暂停音乐
						mediaPlayer.pause();//音乐暂停
						timer.cancel();//取消定时器
					}else if(control==Constants.PLAY){//表示继续播放音乐
						mediaPlayer.start();
						startTimer();//启动定时器
					}
				}
			}else if(intent.getAction()==Constants.SEEKBAR_ACTION){
				int progress=intent.getIntExtra("progress",0);//获取传递的进度
				int position=(int)(currentMusic.getTime()*progress*1.0/100);//将进度转换成相应的时间位置
				mediaPlayer.seekTo(position);//音乐跳转到指定位置继续播放				
			}else if(intent.getAction()==Constants.UPDATE_STYLE){
				styleString=intent.getStringExtra("style");	
				SharedPreferences musicPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
				Editor editor=musicPreferences.edit();//获取参数编辑器
				editor.putString("style", styleString);				
				editor.commit();//提交数据
			}
		}		
	}
	public void preparedAndPlay(Music music){// 准备并播放音乐						
		try{
			mediaPlayer.reset();//重置媒体播放器
			mediaPlayer.setDataSource(music.getUrl());//设置音乐播放的路径					
			mediaPlayer.prepare();// 准备播放音乐
			mediaPlayer.start();// 播放音乐
			startTimer();//启动定时器
			sendNotification();//发送广播
			saveInfo();//保存数据
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {//音乐播放结束事件监听器					
				public void onCompletion(MediaPlayer mp) {//音乐播放完成后，根据设置的播放类型进行播放，并通知前台改变
					if(!Constants.OVER_FINISH.equalsIgnoreCase(styleString)){//如果不是播放结束停止
						if(Constants.LIST_LOOP.equalsIgnoreCase(styleString)){
							position=(position+1)%musicList.size();//自动播放下一首
						}else if(Constants.RANDOM_PLAY.equalsIgnoreCase(styleString)){
							position=new Random().nextInt(musicList.size());
						}
						currentMusic=musicList.get(position);//获取当前的音乐
						preparedAndPlay(currentMusic);//准备并播放音乐
						Intent intent=new Intent(Constants.COMPLETE_ACTION);
						intent.putExtra("position", position);
						sendBroadcast(intent);//发送广播	
					}else{
						stopSelf();//结束
					}									
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	public void saveInfo(){//保存信息
		SharedPreferences musicPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
		Editor editor=musicPreferences.edit();//获取参数编辑器
		editor.putInt("listType",listType);//保存音乐列表类型
		editor.putInt("position",position);//保存音乐的位置
		editor.commit();//提交数据
	}
	public void sendNotification(){//后台发送通知
		NotificationManager notificationManager=(NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);//获取通知服务器
		Builder builder=new Notification.Builder(this);//通知构建器
		builder.setAutoCancel(false);//打开通知后自动消除为false
		builder.setTicker("音乐播放");//第一次出现时，显示在状态栏的通知提示信息
		builder.setSmallIcon(R.drawable.music);//设置通知的小图标
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.largeicon));//设置通知的大图标		
		builder.setContentTitle("正在播放音乐");//设置通知内容的标题
		builder.setContentText(currentMusic.getTitle()+"  "+currentMusic.getSinger());//设置通知的内容
		Intent intent=new Intent("iet.jxufe.cn.android.music_play");//通知启动的页面
		PendingIntent pIntent=PendingIntent.getActivity(this,0, intent, 0);
		builder.setContentIntent(pIntent);//设置通知启动的程序
		notificationManager.notify(0x11, builder.build());//发送通知
	}	
	public void startTimer(){//启动定时器
		timer=new Timer();//创建定时器对象
		timer.schedule(new TimerTask() {//定时执行的任务
			public void run() {//发送广播，通知更新前台进度条
				Intent updateIntent=new Intent(Constants.UPDATE_ACTION);
				updateIntent.putExtra("position", mediaPlayer.getCurrentPosition());
				sendBroadcast(updateIntent);
			}
		}, 0,1000);//每隔1秒发一次		
	}
	public void onDestroy() {//服务销毁时调用该方法		
		if(mediaPlayer!=null){//重置音乐播放器
			mediaPlayer.reset();
		}
		if(activityReceiver!=null){//取消广播接收器
			unregisterReceiver(activityReceiver);
		}
		super.onDestroy();
	}
}
