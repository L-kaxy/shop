/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import jadex.bdiv3.examples.shop.dao.SuperDaoProxy;
import jadex.bdiv3.examples.shop.entity.ResultMessage;
import jadex.bdiv3.examples.shop.entity.po.InventoryPo;
import jadex.bdiv3.examples.shop.entity.po.UserPo;
import jadex.bdiv3.examples.shop.helper.JPAHelper;
import jadex.bdiv3.examples.shop.service.ICustomerService;

/**
 * @author 罗佳欣
 *
 */
public class CustomerSerivce implements ICustomerService {

	/**
	 * 单例模式.
	 */
	private static CustomerSerivce instance = new CustomerSerivce();

	private CustomerSerivce() {
	}

	public static CustomerSerivce getInstance() {
		return instance;
	}

	@Override
	public ResultMessage initUserMoney(String customerName) {
		if (customerName == null) {
			throw new RuntimeException("customerName must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			UserPo userPo = new UserPo();
			userPo.setUsername(customerName);
			if (daoProxy.hasEntity(userPo)) {
				userPo = daoProxy.queryEntity(userPo);
			} else {
				userPo.setMoney(100D);
				daoProxy.addEntity(userPo);
			}

			Map<String, Object> resultParm = new HashMap<>();
			resultParm.put("money", userPo.getMoney());

			rs.setServiceResult(1L);
			rs.setResultParm(resultParm);
			// ---

			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			rs.setServiceResult(0L);
		} finally {
			entityManager.close();
		}
		return rs;
	}

	@Override
	public ResultMessage buyItem(InventoryPo inventoryPo) {
		if (inventoryPo == null) {
			throw new RuntimeException("inventoryPo must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			InventoryPo hasPo = new InventoryPo();
			hasPo.setUsername(inventoryPo.getUsername());
			hasPo.setInventoryname(inventoryPo.getInventoryname());
			if (daoProxy.hasEntity(hasPo)) {
				InventoryPo editPo = daoProxy.queryEntity(hasPo);
				editPo.setQuantity(editPo.getQuantity() + inventoryPo.getQuantity());
				editPo.setPrice(editPo.getPrice() + inventoryPo.getPrice());
				daoProxy.editEntity(editPo);
			} else {
				daoProxy.addEntity(inventoryPo);
			}

			// 修改用户金钱
			UserPo userPo = new UserPo();
			userPo.setUsername(inventoryPo.getUsername());
			userPo = daoProxy.queryEntity(userPo);

			userPo.setMoney(userPo.getMoney() - inventoryPo.getPrice());
			daoProxy.editEntity(userPo);

			// 获取最新的资产列表
			InventoryPo tempPo = new InventoryPo();
			tempPo.setUsername(inventoryPo.getUsername());
			List<InventoryPo> inventoryList = daoProxy.queryList(tempPo);

			Map<String, Object> resultParm = new HashMap<>();
			resultParm.put("money", userPo.getMoney());
			resultParm.put("inventoryList", inventoryList);

			rs.setServiceResult(1L);
			rs.setResultParm(resultParm);
			// ---

			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			rs.setServiceResult(0L);
		} finally {
			entityManager.close();
		}
		return rs;
	}

	@Override
	public ResultMessage addMoney(String customerName, Double money) {
		if (customerName == null) {
			throw new RuntimeException("customerName must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			UserPo userPo = new UserPo();
			userPo.setUsername(customerName);
			userPo = daoProxy.queryEntity(userPo);

			userPo.setMoney(userPo.getMoney() + money);
			daoProxy.editEntity(userPo);

			Map<String, Object> resultParm = new HashMap<>();
			resultParm.put("money", userPo.getMoney());

			rs.setServiceResult(1L);
			rs.setResultParm(resultParm);
			// ---

			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			e.printStackTrace();
			rs.setServiceResult(0L);
		} finally {
			entityManager.close();
		}
		return rs;
	}

	@Override
	public ResultMessage getInventoryList(String customerName) {
		if (customerName == null) {
			throw new RuntimeException("customerName must not be null");
		}

		ResultMessage rs = new ResultMessage();
		try {
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			// 获取最新的资产列表
			InventoryPo tempPo = new InventoryPo();
			tempPo.setUsername(customerName);
			List<InventoryPo> inventoryList = daoProxy.queryList(tempPo);

			Map<String, Object> resultParm = new HashMap<>();
			resultParm.put("inventoryList", inventoryList);

			rs.setServiceResult(1L);
			rs.setResultParm(resultParm);
			// ---
		} catch (Exception e) {
			e.printStackTrace();
			rs.setServiceResult(0L);
		}
		return rs;
	}

}
