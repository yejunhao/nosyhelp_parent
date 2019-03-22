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

import com.nosyhelp.base.dao.TaskinfoDao;
import com.nosyhelp.base.pojo.Taskinfo;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class TaskinfoService {

	@Autowired
	private TaskinfoDao taskinfoDao;
	
	@Autowired
	private IdWorker idWorker;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Taskinfo> findAll() {
		return taskinfoDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Taskinfo> findSearch(Map whereMap, int page, int size) {
		Specification<Taskinfo> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return taskinfoDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Taskinfo> findSearch(Map whereMap) {
		Specification<Taskinfo> specification = createSpecification(whereMap);
		return taskinfoDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public Taskinfo findById(String id) {
		return taskinfoDao.findById(id).get();
	}

	/**
	 * 增加
	 * @param taskinfo
	 */
	public void add(Taskinfo taskinfo) {
		taskinfo.setTaskid( idWorker.nextId()+"" );
		Date date_time = new Date();
		taskinfo.setCreatedtime(date_time);
		taskinfo.setUpdatedtime(date_time);
		taskinfoDao.save(taskinfo);
	}

	/**
	 * 修改
	 * @param taskinfo
	 */
	public void update(Taskinfo taskinfo) {

		Optional<Taskinfo> byId = taskinfoDao.findById(taskinfo.getTaskid());
		Date createtime = byId.get().getCreatedtime();
		Date date_time = new Date();
		taskinfo.setCreatedtime(createtime);
		taskinfo.setUpdatedtime(date_time);
		taskinfoDao.save(taskinfo);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		taskinfoDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Taskinfo> createSpecification(Map searchMap) {

		return new Specification<Taskinfo>() {

			@Override
			public Predicate toPredicate(Root<Taskinfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // 任务ID
                if (searchMap.get("taskid")!=null && !"".equals(searchMap.get("taskid"))) {
                	predicateList.add(cb.like(root.get("taskid").as(String.class), "%"+(String)searchMap.get("taskid")+"%"));
                }
                // 用户ID
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 任务名
                if (searchMap.get("taskname")!=null && !"".equals(searchMap.get("taskname"))) {
                	predicateList.add(cb.like(root.get("taskname").as(String.class), "%"+(String)searchMap.get("taskname")+"%"));
                }
                // 任务数量
                if (searchMap.get("tasknum")!=null && !"".equals(searchMap.get("tasknum"))) {
                	predicateList.add(cb.like(root.get("tasknum").as(String.class), "%"+(String)searchMap.get("tasknum")+"%"));
                }
                // 任务审核图片
                if (searchMap.get("taskimg")!=null && !"".equals(searchMap.get("taskimg"))) {
                	predicateList.add(cb.like(root.get("taskimg").as(String.class), "%"+(String)searchMap.get("taskimg")+"%"));
                }
                // 任务操作说明 可为空，但是要提示用户
                if (searchMap.get("taskoperation")!=null && !"".equals(searchMap.get("taskoperation"))) {
                	predicateList.add(cb.like(root.get("taskoperation").as(String.class), "%"+(String)searchMap.get("taskoperation")+"%"));
                }
                // 任务操作图片说明 如果操作说明不为空，则此项也不为空
                if (searchMap.get("taskimgoperation")!=null && !"".equals(searchMap.get("taskimgoperation"))) {
                	predicateList.add(cb.like(root.get("taskimgoperation").as(String.class), "%"+(String)searchMap.get("taskimgoperation")+"%"));
                }
                // 任务链接
                if (searchMap.get("taskurl")!=null && !"".equals(searchMap.get("taskurl"))) {
                	predicateList.add(cb.like(root.get("taskurl").as(String.class), "%"+(String)searchMap.get("taskurl")+"%"));
                }
                // 任务文字审核验证
                if (searchMap.get("tasktext")!=null && !"".equals(searchMap.get("tasktext"))) {
                	predicateList.add(cb.like(root.get("tasktext").as(String.class), "%"+(String)searchMap.get("tasktext")+"%"));
                }
                // 备注
                if (searchMap.get("tasknote")!=null && !"".equals(searchMap.get("tasknote"))) {
                	predicateList.add(cb.like(root.get("tasknote").as(String.class), "%"+(String)searchMap.get("tasknote")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

}
