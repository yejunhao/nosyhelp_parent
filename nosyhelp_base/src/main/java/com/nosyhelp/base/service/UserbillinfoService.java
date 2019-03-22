package com.nosyhelp.base.service;

import java.util.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import util.IdWorker;

import com.nosyhelp.base.dao.UserbillinfoDao;
import com.nosyhelp.base.pojo.Userbillinfo;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class UserbillinfoService {

	@Autowired
	private UserbillinfoDao userbillinfoDao;
	
	@Autowired
	private IdWorker idWorker;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Userbillinfo> findAll() {
		return userbillinfoDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Userbillinfo> findSearch(Map whereMap, int page, int size) {
		Specification<Userbillinfo> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return userbillinfoDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Userbillinfo> findSearch(Map whereMap) {
		Specification<Userbillinfo> specification = createSpecification(whereMap);
		return userbillinfoDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param bilid
	 * @return
	 */
	public Userbillinfo findById(String bilid) {
		return userbillinfoDao.findById(bilid).get();
	}

	/**
	 * 增加
	 * @param userbillinfo
	 */
	public void add(Userbillinfo userbillinfo) {
		userbillinfo.setBilid( idWorker.nextId()+"" );
		Date date_time = new Date();
		userbillinfo.setUpdatedtime(date_time);
		userbillinfoDao.save(userbillinfo);
	}

	/**
	 * 修改
	 * @param userbillinfo
	 */
	public void update(Userbillinfo userbillinfo) {
		Date date_time = new Date();
		userbillinfo.setUpdatedtime(date_time);
		userbillinfoDao.save(userbillinfo);
	}

	/**
	 * 删除
	 * @param bilid
	 */
	public void deleteById(String bilid) {
		userbillinfoDao.deleteById(bilid);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Userbillinfo> createSpecification(Map searchMap) {

		return new Specification<Userbillinfo>() {

			@Override
			public Predicate toPredicate(Root<Userbillinfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // 账单ID
                if (searchMap.get("bilId")!=null && !"".equals(searchMap.get("bilId"))) {
                	predicateList.add(cb.like(root.get("bilId").as(String.class), "%"+(String)searchMap.get("bilId")+"%"));
                }
                // 用户ID 与用户表的ID关联
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 任务ID 与任务表的ID关联
                if (searchMap.get("taskid")!=null && !"".equals(searchMap.get("taskid"))) {
                	predicateList.add(cb.like(root.get("taskid").as(String.class), "%"+(String)searchMap.get("taskid")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

}
