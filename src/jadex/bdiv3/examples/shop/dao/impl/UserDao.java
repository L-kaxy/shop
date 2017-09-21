/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.dao.impl;

import jadex.bdiv3.examples.shop.dao.IUserDao;

/**
 * @author 罗佳欣
 *
 */
public class UserDao implements IUserDao {

	/**
	 * 单例模式.
	 */
	private static UserDao instance = new UserDao();

	private UserDao() {
	}

	public static UserDao getInstance() {
		return instance;
	}

}
