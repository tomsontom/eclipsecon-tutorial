package vc.todo.model;

import java.util.Date;

public class Todo {
	
	private String title;
	private String extraInfo;
	private Date startDate;
	private Date endDate;
	
	public Todo(String title, String extraInfo, Date startDate, Date endDate) {
		super();
		this.title = title;
		this.extraInfo = extraInfo;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
