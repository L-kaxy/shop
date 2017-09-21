/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.service;

import java.util.List;

import jadex.bdiv3.examples.shop.entity.ResultMessage;
import jadex.bdiv3.examples.shop.entity.po.CommodityPo;

/**
 * @author 罗佳欣
 *
 */
public interface IStoreService {

	/**
	 * @param shopname
	 * @return
	 */
	ResultMessage initStore(String shopname);

	/**
	 * @param commodityPo
	 * @return
	 */
	ResultMessage sellCommodity(CommodityPo commodityPo);

	/**
	 * @param shopname
	 * @return
	 */
	ResultMessage getCommodityList(String shopname);

	/**
	 * @param shopname
	 * @param money
	 * @return
	 */
	ResultMessage addMoney(String shopname, double money);

	/**
	 * @param shopname
	 * @param commodityList
	 * @return
	 */
	ResultMessage updateCommodity(String shopname, List<CommodityPo> commodityList);

	/**
	 * @param shopname
	 * @return
	 */
	ResultMessage purchase(String shopname);

	/**
	 * @param shopname
	 * @param commodityPo
	 * @return
	 */
	ResultMessage purchaseCommodity(String shopname, CommodityPo commodityPo);

}
