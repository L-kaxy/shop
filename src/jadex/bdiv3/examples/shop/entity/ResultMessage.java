/**
 * Copyright (c) 2007-2015 WteamFly.  All rights reserved. 网飞网络公司 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.entity;

import java.util.Map;

/**
 * 服务返回信息类.
 * 
 * @since 3.0.0
 */
public class ResultMessage {
	/**
	 * 用户令牌判断结果.
	 */
	private Boolean userToken;

	/**
	 * 服务类执行结果.
	 */
	private Long serviceResult;

	/**
	 * 返回结果信息.
	 */
	private String resultInfo;

	/**
	 * 返回参数，又vo的json字符串组成.
	 */
	private Map<String, Object> resultParm;

	/**
	 * @return 获取的userToken
	 */
	public final Boolean getUserToken() {
		return userToken;
	}

	/**
	 * 设置userToken的方法.
	 * 
	 * @param userToken
	 *            赋值给userToken的值
	 */
	public final void setUserToken(final Boolean userToken) {
		this.userToken = userToken;
	}

	/**
	 * @return 获取的serviceResult
	 */
	public final Long getServiceResult() {
		return serviceResult;
	}

	/**
	 * 设置serviceResult的方法.
	 * 
	 * @param serviceResult
	 *            赋值给serviceResult的值
	 */
	public final void setServiceResult(final Long serviceResult) {
		this.serviceResult = serviceResult;
	}

	/**
	 * @return 获取的resultInfo
	 */
	public final String getResultInfo() {
		return resultInfo;
	}

	/**
	 * 设置resultInfo的方法.
	 * 
	 * @param resultInfo
	 *            赋值给resultInfo的值
	 */
	public final void setResultInfo(final String resultInfo) {
		this.resultInfo = resultInfo;
	}

	/**
	 * @return 获取的resultParm
	 */
	public final Map<String, Object> getResultParm() {
		return resultParm;
	}

	/**
	 * 设置resultParm的方法.
	 * 
	 * @param resultParm
	 *            赋值给resultParm的值
	 */
	public final void setResultParm(final Map<String, Object> resultParm) {
		this.resultParm = resultParm;
	}

}
