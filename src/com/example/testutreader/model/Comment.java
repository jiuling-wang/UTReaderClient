package com.example.testutreader.model;

import java.util.Date;

public class Comment {
	public String uidString;
	public int cid;
	public int id;
	public String time;
	public String name;
	public String content;
	
	public Comment(int cid, int id, String name,
			String content){
		Date date = new Date();
		this.time=date.toString();
		this.uidString = "";
		this.cid = cid;
		this.id = id;
		this.content = content;
		this.name = name;
	}

}
