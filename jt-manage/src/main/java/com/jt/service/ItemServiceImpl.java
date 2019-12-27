package com.jt.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jt.mapper.ItemMapper;
import com.jt.pojo.Item;
import com.jt.vo.EasyUITable;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemMapper itemMapper;


	/**
	 * 分页查询数据  20条
	 * 	select * from tb_item limit 起始位置,记录数
	 * 第一页:
	 * 	 select * from tb_item limit 0,20	0,19
	 * 第二页:
	 * 	 select * from tb_item limit 20,20  20,39
	 * 第三页:
	 * 	 select * from tb_item limit 40,20  40,59
	 * 第N页:
	 * 	 select * from tb_item limit (page-1)rows,rows
	 */
	public EasyUITable findItemByPageOld(Integer page, Integer rows) {
		//开始毫秒数
		Long startTime = System.currentTimeMillis();
		//查询记录总数 
		int total = itemMapper.selectCount(null);
		//分页查询记录
		int start = (page-1) * rows;
		List<Item> items = 
				itemMapper.findItemByPage(start,rows);
		Long endTime = System.currentTimeMillis();
		System.out.println("执行时间:"+(endTime-startTime));
		//自己完成MP分页功能 1.完成API  2.需要序列化器 编辑配制类 
		//IPage<Item> iPage = new Page<>();
		//itemMapper.selectPage(null, null);
		return new EasyUITable(total, items);
	}


	/**
	 * 条件: order by updated desc
	 */
	@Override
	public EasyUITable findItemByPage(Integer page, Integer rows) {
		//开始毫秒数
		Long startTime = System.currentTimeMillis();
		//封装分页参数
		Page<Item> itemPage = new Page<>(page, rows);
		QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("updated");

		//iPage是分页操作工具API 总记录数/分页结果/条数....
		IPage<Item> iPage = itemMapper.selectPage(itemPage, queryWrapper);
		int count = new Long(iPage.getTotal()).intValue();
		List<Item> itemList = iPage.getRecords();
		Long endTime = System.currentTimeMillis();
		System.out.println("执行时间:"+(endTime-startTime));
		return new EasyUITable(count, itemList);
	}

	//需要添加事务控制
	@Transactional
	@Override
	public void saveItem(Item item) {
		
		item.setStatus(1)
			.setCreated(new Date())
			.setUpdated(item.getCreated());
		itemMapper.insert(item);
	}


	@Transactional
	@Override
	public void updateItem(Item item) {
		
		item.setUpdated(new Date());
		itemMapper.updateById(item);
	}


	/**
	 * sql:delete from tb_item where id in(100,101,102)
	 */
	@Override
	@Transactional
	public void deleteItems(Long[] ids) {
		
		//itemMapper.deleteByIds(ids);
		
		List<Long> idList = Arrays.asList(ids);
		itemMapper.deleteBatchIds(idList);
	}


	/**
	 * 方式1:
	 * 	Sql: update tb_item status = #{status},updated=#{date}
	 * 		 where id in (101,102)
	 * 	
	 * 方式2:MP
	 * 	entity:修改的数据
	 * 	updateWrapper:修改的条件
	 */
	@Override
	public void updateStatus(Long[] ids, Integer status) {
		Item item = new Item();
		item.setStatus(status)
			.setUpdated(new Date());
		List<Long> idList = Arrays.asList(ids);
		UpdateWrapper<Item> updateWrapper = new UpdateWrapper<>();
		updateWrapper.in("id", idList);
		itemMapper.update(item, updateWrapper);
	}








}
