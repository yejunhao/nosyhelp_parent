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

import com.nosyhelp.base.dao.ImageinfoDao;
import com.nosyhelp.base.pojo.Imageinfo;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class ImageinfoService {

	@Autowired
	private ImageinfoDao imageinfoDao;
	
	@Autowired
	private IdWorker idWorker;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Imageinfo> findAll() {
		return imageinfoDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Imageinfo> findSearch(Map whereMap, int page, int size) {
		Specification<Imageinfo> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return imageinfoDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Imageinfo> findSearch(Map whereMap) {
		Specification<Imageinfo> specification = createSpecification(whereMap);
		return imageinfoDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public Imageinfo findById(String id) {
		return imageinfoDao.findById(id).get();
	}

	/**
	 * 增加
	 * @param imageinfo
	 */
	public void add(Imageinfo imageinfo) {
		imageinfo.setImgid( idWorker.nextId()+"" );
		Date date_time = new Date();
		imageinfo.setUpdatedtime(date_time);
		imageinfoDao.save(imageinfo);
	}

	/**
	 * 修改
	 * @param imageinfo
	 */
	public void update(Imageinfo imageinfo) {
		Date date_time = new Date();
		imageinfo.setUpdatedtime(date_time);
		imageinfoDao.save(imageinfo);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		imageinfoDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Imageinfo> createSpecification(Map searchMap) {

		return new Specification<Imageinfo>() {

			@Override
			public Predicate toPredicate(Root<Imageinfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // 图片ID
                if (searchMap.get("imgid")!=null && !"".equals(searchMap.get("imgid"))) {
                	predicateList.add(cb.like(root.get("imgid").as(String.class), "%"+(String)searchMap.get("imgid")+"%"));
                }
                // 用户ID 与用户表的ID关联
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 任务ID 与任务表的ID关联
                if (searchMap.get("taskid")!=null && !"".equals(searchMap.get("taskid"))) {
                	predicateList.add(cb.like(root.get("taskid").as(String.class), "%"+(String)searchMap.get("taskid")+"%"));
                }
                // 图片名 用于区分每张图片
                if (searchMap.get("imgname")!=null && !"".equals(searchMap.get("imgname"))) {
                	predicateList.add(cb.like(root.get("imgname").as(String.class), "%"+(String)searchMap.get("imgname")+"%"));
                }
                // 图片地址
                if (searchMap.get("imgurl")!=null && !"".equals(searchMap.get("imgurl"))) {
                	predicateList.add(cb.like(root.get("imgurl").as(String.class), "%"+(String)searchMap.get("imgurl")+"%"));
                }
                // 是否过期 0:否，1：是
                if (searchMap.get("istrue")!=null && !"".equals(searchMap.get("istrue"))) {
                	predicateList.add(cb.like(root.get("istrue").as(String.class), "%"+(String)searchMap.get("istrue")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

}
