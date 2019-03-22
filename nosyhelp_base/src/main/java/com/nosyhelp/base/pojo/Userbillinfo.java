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
@Table(name="tb_userbillinfo")
public class Userbillinfo implements Serializable{

	@Id
	private String bilid;//账单ID


	
	private String userid;//用户ID 与用户表的ID关联
	private String taskid;//任务ID 与任务表的ID关联
	private Double taskcurrency;//任务金币 默认：0.00
	private Double incomebalance;//收入金币 默认：0.00
	private Double marginbalance;//保证金金币 默认：0.00
	private java.util.Date updatedtime;//更新时间

	
	public String getBilid() {
		return bilid;
	}
	public void setBilid(String bilid) {
		this.bilid = bilid;
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

	public Double getTaskcurrency() {		
		return taskcurrency;
	}
	public void setTaskcurrency(Double taskcurrency) {
		this.taskcurrency = taskcurrency;
	}

	public Double getIncomebalance() {		
		return incomebalance;
	}
	public void setIncomebalance(Double incomebalance) {
		this.incomebalance = incomebalance;
	}

	public Double getMarginbalance() {		
		return marginbalance;
	}
	public void setMarginbalance(Double marginbalance) {
		this.marginbalance = marginbalance;
	}

	public java.util.Date getUpdatedtime() {		
		return updatedtime;
	}
	public void setUpdatedtime(java.util.Date updatedtime) {
		this.updatedtime = updatedtime;
	}


	
}
