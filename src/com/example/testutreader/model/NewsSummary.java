package com.example.testutreader.model;


public class NewsSummary {

	public String title;
	public String source;
	public String time; 
	public String digest;
	public int cid;
	public NewsSummary(){
		
	}
	public NewsSummary(String title, String digest,String source, String time,int cid) {
		this.title = title;
		this.digest = digest;
		this.source = source;
		this.time = time;
		this.cid = cid;
	}
	
}
