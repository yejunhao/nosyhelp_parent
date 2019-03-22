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

import com.nosyhelp.base.dao.UseraddressDao;
import com.nosyhelp.base.pojo.Useraddress;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class UseraddressService {

	@Autowired
	private UseraddressDao useraddressDao;
	
	@Autowired
	private IdWorker idWorker;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Useraddress> findAll() {
		return useraddressDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Useraddress> findSearch(Map whereMap, int page, int size) {
		Specification<Useraddress> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return useraddressDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Useraddress> findSearch(Map whereMap) {
		Specification<Useraddress> specification = createSpecification(whereMap);
		return useraddressDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param addressid
	 * @return
	 */
	public Useraddress findById(String addressid) {
		return useraddressDao.findById(addressid).get();
	}

	/**
	 * 增加
	 * @param useraddress
	 */
	public void add(Useraddress useraddress) {
		useraddress.setAddressid( idWorker.nextId()+"" );
		Date date_time = new Date();
		useraddress.setCreatedtime(date_time);
		useraddressDao.save(useraddress);
	}

	/**
	 * 修改
	 * @param useraddress
	 */
	public void update(Useraddress useraddress) {
		Date date_time = new Date();
		useraddress.setCreatedtime(date_time);
		useraddressDao.save(useraddress);
	}

	/**
	 * 删除
	 * @param addressid
	 */
	public void deleteById(String addressid) {
		useraddressDao.deleteById(addressid);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Useraddress> createSpecification(Map searchMap) {

		return new Specification<Useraddress>() {

			@Override
			public Predicate toPredicate(Root<Useraddress> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // 地址Id
                if (searchMap.get("addressid")!=null && !"".equals(searchMap.get("addressid"))) {
                	predicateList.add(cb.like(root.get("addressid").as(String.class), "%"+(String)searchMap.get("addressid")+"%"));
                }
                // 用户Id
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 地址
                if (searchMap.get("addressname")!=null && !"".equals(searchMap.get("addressname"))) {
                	predicateList.add(cb.like(root.get("addressname").as(String.class), "%"+(String)searchMap.get("addressname")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

}
