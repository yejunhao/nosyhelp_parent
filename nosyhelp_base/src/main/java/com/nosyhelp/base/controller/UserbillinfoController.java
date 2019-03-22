package com.nosyhelp.base.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nosyhelp.base.pojo.Userbillinfo;
import com.nosyhelp.base.service.UserbillinfoService;

import entity.PageResult;
import entity.Result;
import entity.StatusCode;
/**
 * 控制器层
 * @author Administrator
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/userbillinfo")
public class UserbillinfoController {

	@Autowired
	private UserbillinfoService userbillinfoService;
	
	
	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true,StatusCode.OK,"查询成功",userbillinfoService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param bilid ID
	 * @return
	 */
	@RequestMapping(value="/{bilid}",method= RequestMethod.GET)
	public Result findById(@PathVariable String bilid){
		return new Result(true,StatusCode.OK,"查询成功",userbillinfoService.findById(bilid));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		Page<Userbillinfo> pageList = userbillinfoService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<Userbillinfo>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",userbillinfoService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param userbillinfo
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody Userbillinfo userbillinfo  ){
		userbillinfoService.add(userbillinfo);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param userbillinfo
	 */
	@RequestMapping(value="/{bilid}",method= RequestMethod.PUT)
	public Result update(@RequestBody Userbillinfo userbillinfo, @PathVariable String bilid ){
		userbillinfo.setBilid(bilid);
		userbillinfoService.update(userbillinfo);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param bilid
	 */
	@RequestMapping(value="/{bilid}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String bilid ){
		userbillinfoService.deleteById(bilid);
		return new Result(true,StatusCode.OK,"删除成功");
	}
	
}
