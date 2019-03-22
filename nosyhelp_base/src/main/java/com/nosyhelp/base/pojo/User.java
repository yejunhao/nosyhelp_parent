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
@Table(name="tb_user")
public class User implements Serializable{

	@Id
	private String userid;//用户ID 使用推特雪花算法生成ID与UsersInfo的ID关联


	
	private String bilid;//账单ID
	private String username;//用户名
	private String password;//密码
	private Integer grade;//用户等级 0
	private Integer sex;//性别 1:男,2:女
	private String phone;//手机号码 初级认证
	private String alipay;//支付宝账号 初级认证
	private String wechat;//微信账号
	private String banknum;//银行卡号 高级认证
	private String idcard;//身份证号码 高级认证
	private Integer cooperationlevel;//合作商级别 0:个人,1:白银,2:白金
	private String integral;//积分
	private String imginfoid;//图片信息 与图片信息表关联
	private java.util.Date createdtime;//创建时间
	private java.util.Date updatedtime;//更新时间

	
	public String getUserid() {		
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getBilid() {		
		return bilid;
	}
	public void setBilid(String bilid) {
		this.bilid = bilid;
	}

	public String getUsername() {		
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {		
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getGrade() {		
		return grade;
	}
	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getSex() {		
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getPhone() {		
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAlipay() {		
		return alipay;
	}
	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}

	public String getWechat() {		
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getBanknum() {		
		return banknum;
	}
	public void setBanknum(String banknum) {
		this.banknum = banknum;
	}

	public String getIdcard() {		
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public Integer getCooperationlevel() {		
		return cooperationlevel;
	}
	public void setCooperationlevel(Integer cooperationlevel) {
		this.cooperationlevel = cooperationlevel;
	}

	public String getIntegral() {		
		return integral;
	}
	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public String getImginfoid() {		
		return imginfoid;
	}
	public void setImginfoid(String imginfoid) {
		this.imginfoid = imginfoid;
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
