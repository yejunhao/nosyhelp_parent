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
@Table(name="tb_taskinfo")
public class Taskinfo implements Serializable{

	@Id
	private String taskid;//任务ID


	
	private String userid;//用户ID
	private Integer tasktype;//任务类型 0.注册，1.投票，2.转发，3.关注，4.浏览，5.互粉，6.转发，7.点赞，8.发帖，9.评论，10.高价，11.长单，12.电商购物，13.其他
	private Integer device;//支持设备 0.不限，1.安卓，2.苹果
	private String taskname;//任务名
	private Double taskbid;//任务出价
	private String tasknum;//任务数量
	private java.util.Date deadlinetime;//任务截止时间
	private String taskimg;//任务审核图片
	private String taskoperation;//任务操作说明 可为空，但是要提示用户
	private String taskimgoperation;//任务操作图片说明 如果操作说明不为空，则此项也不为空
	private String taskurl;//任务链接
	private String tasktext;//任务文字审核验证
	private String tasknote;//备注
	private java.util.Date createdtime;//创建时间
	private java.util.Date updatedtime;//更新时间

	
	public String getTaskid() {		
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getUserid() {		
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Integer getTasktype() {		
		return tasktype;
	}
	public void setTasktype(Integer tasktype) {
		this.tasktype = tasktype;
	}

	public Integer getDevice() {		
		return device;
	}
	public void setDevice(Integer device) {
		this.device = device;
	}

	public String getTaskname() {		
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}

	public Double getTaskbid() {		
		return taskbid;
	}
	public void setTaskbid(Double taskbid) {
		this.taskbid = taskbid;
	}

	public String getTasknum() {		
		return tasknum;
	}
	public void setTasknum(String tasknum) {
		this.tasknum = tasknum;
	}

	public java.util.Date getDeadlinetime() {		
		return deadlinetime;
	}
	public void setDeadlinetime(java.util.Date deadlinetime) {
		this.deadlinetime = deadlinetime;
	}

	public String getTaskimg() {		
		return taskimg;
	}
	public void setTaskimg(String taskimg) {
		this.taskimg = taskimg;
	}

	public String getTaskoperation() {		
		return taskoperation;
	}
	public void setTaskoperation(String taskoperation) {
		this.taskoperation = taskoperation;
	}

	public String getTaskimgoperation() {		
		return taskimgoperation;
	}
	public void setTaskimgoperation(String taskimgoperation) {
		this.taskimgoperation = taskimgoperation;
	}

	public String getTaskurl() {		
		return taskurl;
	}
	public void setTaskurl(String taskurl) {
		this.taskurl = taskurl;
	}

	public String getTasktext() {		
		return tasktext;
	}
	public void setTasktext(String tasktext) {
		this.tasktext = tasktext;
	}

	public String getTasknote() {		
		return tasknote;
	}
	public void setTasknote(String tasknote) {
		this.tasknote = tasknote;
	}

	public java.util.Date getCreatedtime() {		
		return createdtime;
	}
	public void setCreatedtime(java.util.Date createdtime) {
		this.createdtime = createdtime;
	}

	public java.util.Date getUpdatedtime() {		
		return updatedtime;
	}
	public void setUpdatedtime(java.util.Date updatedtime) {
		this.updatedtime = updatedtime;
	}


	
}
