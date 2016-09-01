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
	private List<Music> musicList;//�����б�
	private int position;//��ǰ���ֵ����
	private MediaPlayer mediaPlayer;//ý�岥����
	private ActivityReceiver activityReceiver;//�㲥������
	private Music currentMusic;//��ǰ���ŵ�����		
	private Timer timer;//��ʱ��
	private int listType;//�б������
	private String styleString=Constants.LIST_LOOP;//���ֲ�����ʽ,Ĭ���б�ѭ��
	public void onCreate() {//��������	
		mediaPlayer=new MediaPlayer();
		activityReceiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();//����Intent������
		filter.addAction(Constants.CONTROL_ACTION);//�������ֲ��ŵĶ������������ź���ͣ
		filter.addAction(Constants.SEEKBAR_ACTION);//�ı����ֲ��Ž���
		filter.addAction(Constants.UPDATE_STYLE);//�ı����ֲ�����ʽ
		registerReceiver(activityReceiver, filter);//ע��㲥������
		super.onCreate();
	}	
	public IBinder onBind(Intent intent) {		
		return null;
	}
	private class ActivityReceiver extends BroadcastReceiver{//��ȡǰ̨���͵Ĺ㲥
		public void onReceive(Context context, Intent intent) {			
			if(intent.getAction()==Constants.CONTROL_ACTION){//���յ����Ʋ��ŵĹ㲥����ʼ�����֡���ͣ�����ţ�
				int isNew=intent.getIntExtra("new", -1);
				if(isNew!=-1){//����һ��������
					listType=intent.getIntExtra("listType",Constants.ALL_MUSIC);
					if(listType==Constants.ALL_MUSIC){//��������е�����
						musicList=Constants.musiclist;
					}else{//����ǲ����б�
						musicList=Constants.playlist;
					}
					position=intent.getIntExtra("position", 0);
					currentMusic=musicList.get(position);//��ȡ��ǰ��Ҫ���ŵ�����
					preparedAndPlay(currentMusic);//׼������������
				}else{
					int control=intent.getIntExtra("control",-1);
					if(control==Constants.PAUSE){//��ʾҪ��ͣ����
						mediaPlayer.pause();//������ͣ
						timer.cancel();//ȡ����ʱ��
					}else if(control==Constants.PLAY){//��ʾ������������
						mediaPlayer.start();
						startTimer();//������ʱ��
					}
				}
			}else if(intent.getAction()==Constants.SEEKBAR_ACTION){
				int progress=intent.getIntExtra("progress",0);//��ȡ���ݵĽ���
				int position=(int)(currentMusic.getTime()*progress*1.0/100);//������ת������Ӧ��ʱ��λ��
				mediaPlayer.seekTo(position);//������ת��ָ��λ�ü�������				
			}else if(intent.getAction()==Constants.UPDATE_STYLE){
				styleString=intent.getStringExtra("style");	
				SharedPreferences musicPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
				Editor editor=musicPreferences.edit();//��ȡ�����༭��
				editor.putString("style", styleString);				
				editor.commit();//�ύ����
			}
		}		
	}
	public void preparedAndPlay(Music music){// ׼������������						
		try{
			mediaPlayer.reset();//����ý�岥����
			mediaPlayer.setDataSource(music.getUrl());//�������ֲ��ŵ�·��					
			mediaPlayer.prepare();// ׼����������
			mediaPlayer.start();// ��������
			startTimer();//������ʱ��
			sendNotification();//���͹㲥
			saveInfo();//��������
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {//���ֲ��Ž����¼�������					
				public void onCompletion(MediaPlayer mp) {//���ֲ�����ɺ󣬸������õĲ������ͽ��в��ţ���֪ͨǰ̨�ı�
					if(!Constants.OVER_FINISH.equalsIgnoreCase(styleString)){//������ǲ��Ž���ֹͣ
						if(Constants.LIST_LOOP.equalsIgnoreCase(styleString)){
							position=(position+1)%musicList.size();//�Զ�������һ��
						}else if(Constants.RANDOM_PLAY.equalsIgnoreCase(styleString)){
							position=new Random().nextInt(musicList.size());
						}
						currentMusic=musicList.get(position);//��ȡ��ǰ������
						preparedAndPlay(currentMusic);//׼������������
						Intent intent=new Intent(Constants.COMPLETE_ACTION);
						intent.putExtra("position", position);
						sendBroadcast(intent);//���͹㲥	
					}else{
						stopSelf();//����
					}									
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	public void saveInfo(){//������Ϣ
		SharedPreferences musicPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
		Editor editor=musicPreferences.edit();//��ȡ�����༭��
		editor.putInt("listType",listType);//���������б�����
		editor.putInt("position",position);//�������ֵ�λ��
		editor.commit();//�ύ����
	}
	public void sendNotification(){//��̨����֪ͨ
		NotificationManager notificationManager=(NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);//��ȡ֪ͨ������
		Builder builder=new Notification.Builder(this);//֪ͨ������
		builder.setAutoCancel(false);//��֪ͨ���Զ�����Ϊfalse
		builder.setTicker("���ֲ���");//��һ�γ���ʱ����ʾ��״̬����֪ͨ��ʾ��Ϣ
		builder.setSmallIcon(R.drawable.music);//����֪ͨ��Сͼ��
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.largeicon));//����֪ͨ�Ĵ�ͼ��		
		builder.setContentTitle("���ڲ�������");//����֪ͨ���ݵı���
		builder.setContentText(currentMusic.getTitle()+"  "+currentMusic.getSinger());//����֪ͨ������
		Intent intent=new Intent("iet.jxufe.cn.android.music_play");//֪ͨ������ҳ��
		PendingIntent pIntent=PendingIntent.getActivity(this,0, intent, 0);
		builder.setContentIntent(pIntent);//����֪ͨ�����ĳ���
		notificationManager.notify(0x11, builder.build());//����֪ͨ
	}	
	public void startTimer(){//������ʱ��
		timer=new Timer();//������ʱ������
		timer.schedule(new TimerTask() {//��ʱִ�е�����
			public void run() {//���͹㲥��֪ͨ����ǰ̨������
				Intent updateIntent=new Intent(Constants.UPDATE_ACTION);
				updateIntent.putExtra("position", mediaPlayer.getCurrentPosition());
				sendBroadcast(updateIntent);
			}
		}, 0,1000);//ÿ��1�뷢һ��		
	}
	public void onDestroy() {//��������ʱ���ø÷���		
		if(mediaPlayer!=null){//�������ֲ�����
			mediaPlayer.reset();
		}
		if(activityReceiver!=null){//ȡ���㲥������
			unregisterReceiver(activityReceiver);
		}
		super.onDestroy();
	}
}
