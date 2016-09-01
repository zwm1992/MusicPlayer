package iet.jxufe.cn.android.musicplayer;

import java.io.Serializable;

public class Music implements Serializable {
	private static final long serialVersionUID=1; 
	private String title;//歌曲文件标题
	private String singer;//歌曲演唱者
	private String album;//歌曲专辑
	private int album_id;//专辑编号
	private String url;//歌曲文件路径
	private long size;//歌曲文件大小
	private int time;//歌曲文件时长，单位为毫秒
	private String name;//歌曲文件名，包含后缀
		
	public int getAlbum_id() {
		return album_id;
	}
	public void setAlbum_id(int album_id) {
		this.album_id = album_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String toString() {//显示歌曲名和演唱者
		return "Music [title=" + title + ", singer=" + singer + "]";
	}
}
