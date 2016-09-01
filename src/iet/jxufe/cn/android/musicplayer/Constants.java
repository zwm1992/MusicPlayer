package iet.jxufe.cn.android.musicplayer;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

public class Constants {
	public static List<Music> musiclist=new ArrayList<Music>();//�������ּ���
	public static List<Music> playlist=new ArrayList<Music>();//���ֲ����б�
	public static final String CONTROL_ACTION="iet.jxufe.cn.android.control";//�������ֲ��Ŷ��������Ż���ͣ
	public static final String SEEKBAR_ACTION="iet.jxufe.cn.android.seekbar";//���ֽ��ȷ��ͱ仯����
	public static final String COMPLETE_ACTION="iet.jxufe.cn.android.complete";//���ֲ��Ž�������
	public static final String UPDATE_ACTION="iet.jxufe.cn.android.update";//���½�����
	public static final String UPDATE_STYLE="iet.jxufe.cn.android.style";//���²�����ʽ
	public static final Uri ALBUM_URL=Uri.parse("content://media/external/audio/albumart");
	public static final String LIST_LOOP="�б�ѭ��";
	public static final String SINGLE_LOOP="����ѭ��";
	public static final String OVER_FINISH="������ֹͣ";
	public static final String RANDOM_PLAY="�������";	
	public static final int NEW=6;//��ʼһ���µ�����
	public static final int PLAY=1;//����
	public static final int PAUSE=2;//��ͣ
	public static final int ALL_MUSIC=0x11;//�������е�����
	public static final int PLAY_LIST_MUSIC=0x12;//���Ų����б��е�����	
}
