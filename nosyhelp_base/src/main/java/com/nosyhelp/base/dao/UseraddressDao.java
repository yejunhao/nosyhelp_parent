package com.nosyhelp.base.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nosyhelp.base.pojo.Useraddress;
/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface UseraddressDao extends JpaRepository<Useraddress,String>,JpaSpecificationExecutor<Useraddress>{
	
}
