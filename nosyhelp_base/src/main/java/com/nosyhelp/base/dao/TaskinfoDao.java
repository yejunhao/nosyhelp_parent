package com.nosyhelp.base.dao;

import com.nosyhelp.base.pojo.Taskinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface TaskinfoDao extends JpaRepository<Taskinfo,String>,JpaSpecificationExecutor<Taskinfo>{
	
}
