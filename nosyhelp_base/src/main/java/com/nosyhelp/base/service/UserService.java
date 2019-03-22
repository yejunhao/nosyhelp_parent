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

import com.nosyhelp.base.dao.UserDao;
import com.nosyhelp.base.pojo.User;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private IdWorker idWorker;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<User> findAll() {
		return userDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<User> findSearch(Map whereMap, int page, int size) {
		Specification<User> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return userDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<User> findSearch(Map whereMap) {
		Specification<User> specification = createSpecification(whereMap);
		return userDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public User findById(String id) {
		return userDao.findById(id).get();
	}

	/**
	 * 增加
	 * @param user
	 */
	public void add(User user) {
		user.setUserid( idWorker.nextId()+"" );
		Date date_time = new Date();
		user.setCreatedtime(date_time);
		user.setUpdatedtime(date_time);
		userDao.save(user);
	}

	/**
	 * 修改
	 * @param user
	 */
	public void update(User user) {
		Optional<User> byId = userDao.findById(user.getUserid());
		Date createdtime = byId.get().getCreatedtime();
		Date date_time = new Date();
		user.setCreatedtime(createdtime);
		user.setUpdatedtime(date_time);

		userDao.save(user);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		userDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<User> createSpecification(Map searchMap) {

		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // 用户ID 使用推特雪花算法生成ID与UsersInfo的ID关联
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 账单ID
                if (searchMap.get("bilid")!=null && !"".equals(searchMap.get("bilid"))) {
                	predicateList.add(cb.like(root.get("bilid").as(String.class), "%"+(String)searchMap.get("bilid")+"%"));
                }
                // 用户名
                if (searchMap.get("username")!=null && !"".equals(searchMap.get("username"))) {
                	predicateList.add(cb.like(root.get("username").as(String.class), "%"+(String)searchMap.get("username")+"%"));
                }
                // 密码
                if (searchMap.get("password")!=null && !"".equals(searchMap.get("password"))) {
                	predicateList.add(cb.like(root.get("password").as(String.class), "%"+(String)searchMap.get("password")+"%"));
                }
                // 手机号码 初级认证
                if (searchMap.get("phone")!=null && !"".equals(searchMap.get("phone"))) {
                	predicateList.add(cb.like(root.get("phone").as(String.class), "%"+(String)searchMap.get("phone")+"%"));
                }
                // 支付宝账号 初级认证
                if (searchMap.get("alipay")!=null && !"".equals(searchMap.get("alipay"))) {
                	predicateList.add(cb.like(root.get("alipay").as(String.class), "%"+(String)searchMap.get("alipay")+"%"));
                }
                // 微信账号
                if (searchMap.get("wechat")!=null && !"".equals(searchMap.get("wechat"))) {
                	predicateList.add(cb.like(root.get("wechat").as(String.class), "%"+(String)searchMap.get("wechat")+"%"));
                }
                // 银行卡号 高级认证
                if (searchMap.get("banknum")!=null && !"".equals(searchMap.get("banknum"))) {
                	predicateList.add(cb.like(root.get("banknum").as(String.class), "%"+(String)searchMap.get("banknum")+"%"));
                }
                // 身份证号码 高级认证
                if (searchMap.get("idcard")!=null && !"".equals(searchMap.get("idcard"))) {
                	predicateList.add(cb.like(root.get("idcard").as(String.class), "%"+(String)searchMap.get("idcard")+"%"));
                }
                // 积分
                if (searchMap.get("integral")!=null && !"".equals(searchMap.get("integral"))) {
                	predicateList.add(cb.like(root.get("integral").as(String.class), "%"+(String)searchMap.get("integral")+"%"));
                }
                // 图片信息 与图片信息表关联
                if (searchMap.get("imginfoid")!=null && !"".equals(searchMap.get("imginfoid"))) {
                	predicateList.add(cb.like(root.get("imginfoid").as(String.class), "%"+(String)searchMap.get("imginfoid")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

}
