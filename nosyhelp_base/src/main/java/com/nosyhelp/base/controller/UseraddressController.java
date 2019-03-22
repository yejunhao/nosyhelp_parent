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

import com.nosyhelp.base.pojo.Useraddress;
import com.nosyhelp.base.service.UseraddressService;

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
@RequestMapping("/useraddress")
public class UseraddressController {

	@Autowired
	private UseraddressService useraddressService;
	
	
	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true,StatusCode.OK,"查询成功",useraddressService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param addressid ID
	 * @return
	 */
	@RequestMapping(value="/{addressid}",method= RequestMethod.GET)
	public Result findById(@PathVariable String addressid){
		return new Result(true,StatusCode.OK,"查询成功",useraddressService.findById(addressid));
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
		Page<Useraddress> pageList = useraddressService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<Useraddress>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",useraddressService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param useraddress
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody Useraddress useraddress  ){
		useraddressService.add(useraddress);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param useraddress
	 */
	@RequestMapping(value="/{addressid}",method= RequestMethod.PUT)
	public Result update(@RequestBody Useraddress useraddress, @PathVariable String addressid ){
		useraddress.setAddressid(addressid);
		useraddressService.update(useraddress);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param addressid
	 */
	@RequestMapping(value="/{addressid}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String addressid ){
		useraddressService.deleteById(addressid);
		return new Result(true,StatusCode.OK,"删除成功");
	}
	
}
