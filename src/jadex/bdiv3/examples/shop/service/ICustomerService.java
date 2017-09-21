/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.service;

import jadex.bdiv3.examples.shop.entity.ResultMessage;
import jadex.bdiv3.examples.shop.entity.po.InventoryPo;

/**
 * @author 罗佳欣
 *
 */
public interface ICustomerService {

	/**
	 * @param customerName
	 * @return
	 */
	ResultMessage initUserMoney(String customerName);

	/**
	 * @param inventoryPo
	 * @return
	 */
	ResultMessage buyItem(InventoryPo inventoryPo);

	/**
	 * @param customerName
	 * @param money
	 * @return
	 */
	ResultMessage addMoney(String customerName, Double money);

	/**
	 * @param customerName
	 * @return
	 */
	ResultMessage getInventoryList(String customerName);

}
