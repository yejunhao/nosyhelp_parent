package com.nosyhelp.base.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * 实体类
 * @author Administrator
 *
 */
@Entity
@Table(name="tb_imageinfo")
public class Imageinfo implements Serializable{

	@Id
	private String imgid;//图片ID


	
	private String userid;//用户ID 与用户表的ID关联
	private String taskid;//任务ID 与任务表的ID关联
	private String imgname;//图片名 用于区分每张图片
	private String imgurl;//图片地址
	private String istrue;//是否过期 0:否，1：是
	private java.util.Date updatedtime;//更新时间

	
	public String getImgid() {		
		return imgid;
	}
	public void setImgid(String imgid) {
		this.imgid = imgid;
	}

	public String getUserid() {		
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getTaskid() {		
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getImgname() {		
		return imgname;
	}
	public void setImgname(String imgname) {
		this.imgname = imgname;
	}

	public String getImgurl() {		
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getIstrue() {		
		return istrue;
	}
	public void setIstrue(String istrue) {
		this.istrue = istrue;
	}

	public java.util.Date getUpdatedtime() {		
		return updatedtime;
	}
	public void setUpdatedtime(java.util.Date updatedtime) {
		this.updatedtime = updatedtime;
	}


	
}
