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

public class MusicPlayActivity extends Activity {// ���ֲ��������棬�ɿ������ֵĲ���
	private List<Music> musicList;//��¼�����б�
	private TextView titleView, singerView, currentTimeView, totalTimeView;// ��ʾ��������������Ϣ����ǰ����ʱ�䡢��ʱ����ı���ʾ��
	private SeekBar playProgress;// �϶���
	private Spinner styleSpinner;//ѡ�񲥷���ʽ�������б�
	private ImageButton control;// ����/��ͣ��ť
	private ImageView picView;//��ʾͼƬ
	private ServerReceiver serverReceiver;// ���պ�̨�����͵Ĺ㲥�Ĺ㲥������
	private Music currentMusic;//��¼��ǰ���ŵ�����
	private boolean isPause=false;//�Ƿ���ͣ
	private int currentPosition;//��ǰ���ֵ�����
	private int listType;//�����б����ͣ��������ֻ��ǲ����б��е�����	
	private String[] styles=new String[]{Constants.LIST_LOOP,Constants.SINGLE_LOOP,Constants.RANDOM_PLAY,Constants.OVER_FINISH};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȫ����ʾ
		setContentView(R.layout.play_item);//����������		
		initView();// ��ʼ������
	}
	public void initView() {//ִ�г�ʼ������
		styleSpinner=(Spinner)findViewById(R.id.styleSpinner);
		styleSpinner.setAdapter(new ArrayAdapter<String>(this,R.layout.spinner_text,styles));
		styleSpinner.setOnItemSelectedListener(new SpinnerItemClickListener());
		picView=(ImageView)findViewById(R.id.picView);
		currentTimeView = (TextView) findViewById(R.id.currentTime);// ��ʾ���ֵ�ǰ���ŵ�ʱ����ı��ؼ�
		totalTimeView = (TextView) findViewById(R.id.totalTime);// ��ʾ���ֵ���ʱ�����ı��ؼ�
		titleView = (TextView) findViewById(R.id.title);// ��ʾ���ֱ�����ı��ؼ�
		singerView = (TextView) findViewById(R.id.singer);// ��ʾ�����ݳ��ߵ��ı��ؼ�
		playProgress = (SeekBar) findViewById(R.id.playProgress);// ��ʾ��ǰ���ֲ��Ž��ȵĿؼ�
		control = (ImageButton) findViewById(R.id.control);// ���Ʋ��Ż���ͣ�Ŀؼ�
		playProgress.setOnSeekBarChangeListener(new MySeekBarChangeListener());// Ϊ�϶�������¼�����
		serverReceiver = new ServerReceiver();//�����㲥������
		IntentFilter filter = new IntentFilter();//���Խ��յ��Ĺ㲥����
		filter.addAction(Constants.COMPLETE_ACTION);// ���ֲ��Ž������¼�
		filter.addAction(Constants.UPDATE_ACTION);// ���½��ȵĶ���
		registerReceiver(serverReceiver, filter);//ע��㲥������		
		currentMusic = (Music) getIntent().getSerializableExtra("music");// ��ȡ��ǰ���ŵ�����
		if(currentMusic==null){//�����ǰ����Ϊ��
			SharedPreferences musicPreferences=getSharedPreferences("music", Context.MODE_PRIVATE);
			currentPosition=musicPreferences.getInt("position", 0);
			listType=musicPreferences.getInt("listType", Constants.ALL_MUSIC);
			if(listType==Constants.ALL_MUSIC){//��������е�����
				musicList=Constants.musiclist;
			}else{//����ǲ����б�
				musicList=Constants.playlist;
			}
			currentMusic=musicList.get(currentPosition);//��ȡ��ǰ������
			String styleString=musicPreferences.getString("style", Constants.LIST_LOOP);
			for(int i=0;i<styles.length;i++){
				if(styles[i].equalsIgnoreCase(styleString)){
					styleSpinner.setSelection(i);
					break;
				}
			}
			showInfo();//��ʾ������Ϣ
			isPause=true;//��������
			control(null);
		}else{//ֱ�Ӳ�������
			currentPosition=getIntent().getIntExtra("position",0);//Ĭ��Ϊ��һ��
			listType=getIntent().getIntExtra("listType", Constants.ALL_MUSIC);//��ȡ�б�����ͣ��Ǵ����е����ֻ��ǲ����б��в���
			playNewMusic();//��������
			if(listType==Constants.ALL_MUSIC){//��������е�����
				musicList=Constants.musiclist;
			}else{//����ǲ����б�
				musicList=Constants.playlist;
			}
		}		
		
	}
	public void showInfo(){
		totalTimeView.setText(MusicUtils.timeToString(currentMusic.getTime()));//��ʾ���ֵ���ʱ��
		titleView.setText("        " + currentMusic.getTitle() + "        ");// ��ʾ������
		singerView.setText(currentMusic.getSinger());// ��ʾ�ݳ���
		Bitmap bitmap=MusicUtils.getAlbumPic(this, currentMusic);
		if(bitmap!=null){
			picView.setImageBitmap(bitmap);
		}else{
			picView.setImageResource(R.drawable.background);
		}
	}
	public void playNewMusic() {
		currentTimeView.setText(MusicUtils.timeToString(0));//��ʾ��ǰ���ŵ�ʱ�䣬Ĭ��Ϊ0
		showInfo();
		playProgress.setProgress(0);//����Ϊ0
		control.setImageResource(R.drawable.pause);//��ʾ��ͣ�İ�ť		
		// ���͹㲥��֪ͨ��̨��������
		Intent controlIntent = new Intent(Constants.CONTROL_ACTION);
		controlIntent.putExtra("new", Constants.NEW);//����һ��������
		controlIntent.putExtra("position",currentPosition);//���ݵ�ǰ���ֵ����
		controlIntent.putExtra("listType",listType);//���������б������
		sendBroadcast(controlIntent);//���͹㲥
		isPause=false;//�Ƿ���ͣΪfalse
	}
	private class MySeekBarChangeListener implements OnSeekBarChangeListener {
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) { // ���ȷ��ͱ仯ʱ���ø÷���
		}
		public void onStartTrackingTouch(SeekBar seekBar) {// ��ʼ�϶�ʱ����
		}
		public void onStopTrackingTouch(SeekBar seekBar) {// �����϶�ʱ����			
			Intent seekIntent = new Intent(Constants.SEEKBAR_ACTION);//���͹㲥֪ͨ�϶����仯
			seekIntent.putExtra("progress", seekBar.getProgress());//����ǰ�Ľ��ȴ��ݽ�ȥ
			sendBroadcast(seekIntent);// ���͹㲥
		}
	}
	private class ServerReceiver extends BroadcastReceiver {// �㲥�����������ڽ��պ�̨�����͵Ĺ㲥
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction()==Constants.UPDATE_ACTION){//���½��ȵĹ㲥����
				int position=intent.getIntExtra("position",0);//��ȡ���ֲ��ŵ�λ��				
				currentTimeView.setText(MusicUtils.timeToString(position));//��ʾ��ǰ�Ĳ���ʱ��
				playProgress.setProgress((int)(position*1.0/currentMusic.getTime()*100));//����λ�ü���������Ľ���
			}else if(intent.getAction()==Constants.COMPLETE_ACTION){//���ֲ�����ɵ��¼�����
				currentPosition=intent.getIntExtra("position", 0);//��ȡ��ǰ���ŵ����ֵ����
				currentMusic=musicList.get(currentPosition);//��ȡ��ǰ������
				showInfo();//��ʾ��ǰ���ֵ���Ϣ
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
	public void chooseMusic(View view){//ѡ�������ť���¼�����
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
		this.finish();
	}
	public void first(View view) {// ��һ�װ�ť���¼�����
		currentPosition=0;
		currentMusic=musicList.get(currentPosition);
		playNewMusic();
	}
	public void pre(View view) {// ǰһ�װ�ť���¼�����
		currentPosition=(currentPosition-1+musicList.size())%musicList.size();
		currentMusic=musicList.get(currentPosition);
		playNewMusic();
	}
	public void control(View view) {// ���ź���ͣ��ť���¼�����
		Intent controlIntent=new Intent(Constants.CONTROL_ACTION);//�������ֲ��ź���ͣ
		if(!isPause){//������ڲ���״̬�����͹㲥֪ͨ��ͣ
			controlIntent.putExtra("control",Constants.PAUSE);
			control.setImageResource(R.drawable.play);//�ı�ͼ��
		}else{//���������ͣ״̬�����͹㲥֪ͨ����
			controlIntent.putExtra("control",Constants.PLAY);
			control.setImageResource(R.drawable.pause);//�ı�ͼ��
		}
		isPause=!isPause;
		sendBroadcast(controlIntent);//���͹㲥
	}
	public void next(View view) {// ��һ�װ�ť���¼�����
		currentPosition=(currentPosition+1)%musicList.size();
		currentMusic=musicList.get(currentPosition);
		playNewMusic();
	}
	public void last(View view) {// ���һ�װ�ť���¼�����
		currentPosition=musicList.size()-1;
		currentMusic=musicList.get(currentPosition);
		playNewMusic();
	}
	protected void onDestroy() {//��������ʱ��ȡ���㲥��������ע��
		if (serverReceiver != null) {
			unregisterReceiver(serverReceiver);// Activity����ʱ��ȡ��ע��
		}
		super.onDestroy();
	}
}
