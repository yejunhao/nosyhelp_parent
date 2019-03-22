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
@Table(name="tb_useraddress")
public class Useraddress implements Serializable{

	@Id
	private String addressid;//地址Id


	
	private String userid;//用户Id
	private String addressname;//地址
	private java.util.Date createdtime;//创建时间

	
	public String getAddressid() {		
		return addressid;
	}
	public void setAddressid(String addressid) {
		this.addressid = addressid;
	}

	public String getUserid() {		
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getAddressname() {		
		return addressname;
	}
	public void setAddressname(String addressname) {
		this.addressname = addressname;
	}

	public java.util.Date getCreatedtime() {		
		return createdtime;
	}
	public void setCreatedtime(java.util.Date createdtime) {
		this.createdtime = createdtime;
	}


	
}
