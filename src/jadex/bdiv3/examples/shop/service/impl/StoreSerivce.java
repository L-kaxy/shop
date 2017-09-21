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
import jadex.bdiv3.examples.shop.entity.po.CommodityPo;
import jadex.bdiv3.examples.shop.entity.po.PurchasePo;
import jadex.bdiv3.examples.shop.entity.po.UserPo;
import jadex.bdiv3.examples.shop.helper.JPAHelper;
import jadex.bdiv3.examples.shop.service.IStoreService;

/**
 * @author 罗佳欣
 *
 */
public class StoreSerivce implements IStoreService {

	/**
	 * 单例模式.
	 */
	private static StoreSerivce instance = new StoreSerivce();

	private StoreSerivce() {
	}

	public static StoreSerivce getInstance() {
		return instance;
	}

	@Override
	public ResultMessage initStore(String shopname) {
		if (shopname == null) {
			throw new RuntimeException("shopname must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			UserPo userPo = new UserPo();
			userPo.setUsername(shopname);
			if (daoProxy.hasEntity(userPo)) {
				userPo = daoProxy.queryEntity(userPo);
			} else {
				userPo.setMoney(100D);
				daoProxy.addEntity(userPo);
			}

			// 获取最新的商品列表
			CommodityPo tempPo = new CommodityPo();
			tempPo.setUsername(shopname);
			List<CommodityPo> commodityList = daoProxy.queryList(tempPo);

			Map<String, Object> resultParm = new HashMap<>();
			resultParm.put("money", userPo.getMoney());
			resultParm.put("commodityList", commodityList);

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
	public ResultMessage sellCommodity(CommodityPo commodityPo) {
		if (commodityPo == null) {
			throw new RuntimeException("commodityPo must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			CommodityPo hasPo = new CommodityPo();
			hasPo.setUsername(commodityPo.getUsername());
			hasPo.setCommodityname(commodityPo.getCommodityname());
			if (daoProxy.hasEntity(hasPo)) {
				CommodityPo oldPo = daoProxy.queryEntity(hasPo);
				if (oldPo.getQuantity() > 0) {
					CommodityPo editPo = new CommodityPo();
					editPo.setCommodityid(oldPo.getCommodityid());
					editPo.setQuantity(oldPo.getQuantity() - commodityPo.getQuantity());
					daoProxy.editEntity(editPo);

					// 修改商户金钱
					UserPo userPo = new UserPo();
					userPo.setUsername(commodityPo.getUsername());
					userPo = daoProxy.queryEntity(userPo);

					userPo.setMoney(userPo.getMoney() + oldPo.getSingleprice() * commodityPo.getQuantity());
					daoProxy.editEntity(userPo);

					// 获取最新的资产列表
					CommodityPo tempPo = new CommodityPo();
					tempPo.setUsername(commodityPo.getUsername());
					List<CommodityPo> commodityList = daoProxy.queryList(tempPo);

					Map<String, Object> resultParm = new HashMap<>();
					resultParm.put("money", userPo.getMoney());
					resultParm.put("commodityList", commodityList);

					rs.setServiceResult(1L);
					rs.setResultParm(resultParm);
				} else {
					// 商品数量不足
					rs.setServiceResult(2L);
				}
			} else {
				// 商品不存在
				rs.setServiceResult(3L);
			}
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
	public ResultMessage getCommodityList(String shopname) {
		if (shopname == null) {
			throw new RuntimeException("shopname must not be null");
		}

		ResultMessage rs = new ResultMessage();
		try {
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();
			// ---
			// 获取最新的资产列表
			CommodityPo tempPo = new CommodityPo();
			tempPo.setUsername(shopname);
			List<CommodityPo> commodityList = daoProxy.queryList(tempPo);

			Map<String, Object> resultParm = new HashMap<>();
			resultParm.put("commodityList", commodityList);

			rs.setServiceResult(1L);
			rs.setResultParm(resultParm);
			// ---
		} catch (Exception e) {
			e.printStackTrace();
			rs.setServiceResult(0L);
		}
		return rs;
	}

	@Override
	public ResultMessage addMoney(String shopname, double money) {
		if (shopname == null) {
			throw new RuntimeException("shopname must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			UserPo userPo = new UserPo();
			userPo.setUsername(shopname);
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
	public ResultMessage updateCommodity(String shopname, List<CommodityPo> commodityList) {
		if (shopname == null) {
			throw new RuntimeException("shopname must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			CommodityPo tempPo = new CommodityPo();
			tempPo.setUsername(shopname);
			List<CommodityPo> oldCommodityList = daoProxy.queryList(tempPo);

			Map<String, CommodityPo> oldCommodityMap = new HashMap<>();
			for (CommodityPo oldPo : oldCommodityList) {
				oldCommodityMap.put(oldPo.getCommodityname(), oldPo);
			}

			for (CommodityPo newPo : commodityList) {
				String commodityName = newPo.getCommodityname();
				CommodityPo oldPo = oldCommodityMap.get(commodityName);

				if (oldPo == null) {
					// 新的商品采购规划
					CommodityPo addPo = new CommodityPo();
					addPo.setCommodityname(commodityName);
					addPo.setQuantity(0L);
					addPo.setTotal(newPo.getTotal());
					addPo.setPurchase(newPo.getPurchase());
					addPo.setSingleprice(newPo.getSingleprice());
					addPo.setUsername(shopname);

					daoProxy.addEntity(addPo);
				} else {
					// 旧的..
					oldPo.setSingleprice(newPo.getSingleprice());
					oldPo.setTotal(newPo.getTotal());
					oldPo.setPurchase(newPo.getPurchase());

					daoProxy.editEntity(oldPo);
				}

				oldCommodityMap.remove(commodityName);
			}

			UserPo userPo = new UserPo();
			userPo.setUsername(shopname);
			userPo = daoProxy.queryEntity(userPo);

			// 余下的商品列表按成本价回收给批发商(有缺陷)
			for (CommodityPo oldPo : oldCommodityMap.values()) {
				String commodityName = oldPo.getCommodityname();

				PurchasePo purchasePo = new PurchasePo();
				purchasePo.setCommodityname(commodityName);
				purchasePo = daoProxy.queryEntity(purchasePo);

				userPo.setMoney(userPo.getMoney() + purchasePo.getSingleprice() * oldPo.getQuantity());
				daoProxy.editEntity(userPo);

				// 删除商品
				daoProxy.deleteEntity(oldPo);
			}

			List<CommodityPo> resultList = daoProxy.queryList(tempPo);

			Map<String, Object> resultParm = new HashMap<>();
			resultParm.put("commodityList", resultList);
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
	public ResultMessage purchase(String shopname) {
		if (shopname == null) {
			throw new RuntimeException("shopname must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			CommodityPo tempPo = new CommodityPo();
			tempPo.setUsername(shopname);
			List<CommodityPo> oldCommodityList = daoProxy.queryList(tempPo);

			Double needMoney = 0D;
			for (CommodityPo oldPo : oldCommodityList) {
				Long needQuantity = oldPo.getTotal() - oldPo.getQuantity();

				PurchasePo purchasePo = new PurchasePo();
				purchasePo.setCommodityname(oldPo.getCommodityname());
				if (daoProxy.hasEntity(purchasePo)) {
					purchasePo = daoProxy.queryEntity(purchasePo);

					// 总成本价
					needMoney += needQuantity * purchasePo.getSingleprice();
					oldPo.setQuantity(oldPo.getQuantity() + needQuantity);
					daoProxy.editEntity(oldPo);
				} else {
					// 不进货
				}
			}
			UserPo userPo = new UserPo();
			userPo.setUsername(shopname);
			userPo = daoProxy.queryEntity(userPo);

			if (userPo.getMoney() > needMoney) {
				userPo.setMoney(userPo.getMoney() - needMoney);
				daoProxy.editEntity(userPo);

				List<CommodityPo> resultList = daoProxy.queryList(tempPo);

				Map<String, Object> resultParm = new HashMap<>();
				resultParm.put("commodityList", resultList);
				resultParm.put("money", userPo.getMoney());

				rs.setServiceResult(1L);
				rs.setResultParm(resultParm);
				entityManager.getTransaction().commit();
			} else {
				rs.setServiceResult(2L);
				entityManager.getTransaction().rollback();
			}
			// ---
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
	public ResultMessage purchaseCommodity(String shopname, CommodityPo commodityPo) {
		if (shopname == null) {
			throw new RuntimeException("shopname must not be null");
		}

		ResultMessage rs = new ResultMessage();
		EntityManager entityManager = JPAHelper.getEntityManager();
		try {
			entityManager.getTransaction().begin();
			SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();

			// ---
			CommodityPo selPo = new CommodityPo();
			selPo.setCommodityname(commodityPo.getCommodityname());
			selPo.setUsername(shopname);
			selPo = daoProxy.queryEntity(selPo);
			if (selPo.getQuantity() < selPo.getPurchase()) {
				System.out.println("purchaseCommodity============================>>>>");
				Long needQuantity = selPo.getTotal() - selPo.getQuantity();

				PurchasePo purchasePo = new PurchasePo();
				purchasePo.setCommodityname(selPo.getCommodityname());

				Double needMoney = 0D;
				if (daoProxy.hasEntity(purchasePo)) {
					purchasePo = daoProxy.queryEntity(purchasePo);
					// 总成本价
					needMoney = needQuantity * purchasePo.getSingleprice();
					selPo.setQuantity(selPo.getQuantity() + needQuantity);
					daoProxy.editEntity(selPo);
				} else {
					// 不进货
				}

				UserPo userPo = new UserPo();
				userPo.setUsername(shopname);
				userPo = daoProxy.queryEntity(userPo);

				if (userPo.getMoney() > needMoney) {
					userPo.setMoney(userPo.getMoney() - needMoney);
					daoProxy.editEntity(userPo);

					CommodityPo tempPo = new CommodityPo();
					tempPo.setUsername(shopname);
					List<CommodityPo> resultList = daoProxy.queryList(tempPo);

					Map<String, Object> resultParm = new HashMap<>();
					resultParm.put("commodityList", resultList);
					resultParm.put("money", userPo.getMoney());

					rs.setServiceResult(1L);
					rs.setResultParm(resultParm);
				} else {
					// 现金不够
					rs.setServiceResult(2L);
				}
			} else {
				// 无需采购
				rs.setServiceResult(3L);
			}
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

}
