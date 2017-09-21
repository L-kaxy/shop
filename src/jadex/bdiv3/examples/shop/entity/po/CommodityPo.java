/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.entity.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商店商品.
 * 
 * @author 罗佳欣
 *
 */
@Entity
@Table(name = "t_commodity")
public class CommodityPo {

	/**
	 * 商品编号.
	 */
	private Long commodityid;

	/**
	 * 商品名称.
	 */
	private String commodityname;

	/**
	 * 商品剩余数量.
	 */
	private Long quantity;

	/**
	 * 触发采购数量.
	 */
	private Long purchase;

	/**
	 * 目标数量.
	 */
	private Long total;

	/**
	 * 商品单价.
	 */
	private Double singleprice;

	/**
	 * 用户名.
	 */
	private String username;

	/**
	 * @return the commodityid
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	public Long getCommodityid() {
		return commodityid;
	}

	/**
	 * @param commodityid
	 *            the commodityid to set
	 */
	public void setCommodityid(Long commodityid) {
		this.commodityid = commodityid;
	}

	/**
	 * @return the commodityname
	 */
	@Column(nullable = false)
	public String getCommodityname() {
		return commodityname;
	}

	/**
	 * @param commodityname
	 *            the commodityname to set
	 */
	public void setCommodityname(String commodityname) {
		this.commodityname = commodityname;
	}

	/**
	 * @return the quantity
	 */
	@Column(nullable = false)
	public Long getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the singleprice
	 */
	@Column(nullable = false)
	public Double getSingleprice() {
		return singleprice;
	}

	/**
	 * @param singleprice
	 *            the singleprice to set
	 */
	public void setSingleprice(Double singleprice) {
		this.singleprice = singleprice;
	}

	/**
	 * @return the username
	 */
	@Column(nullable = false)
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the purchase
	 */
	@Column(nullable = false)
	public Long getPurchase() {
		return purchase;
	}

	/**
	 * @param purchase 
	 *				the purchase to set
	 */
	public void setPurchase(Long purchase) {
		this.purchase = purchase;
	}

	/**
	 * @return the total
	 */
	@Column(nullable = false)
	public Long getTotal() {
		return total;
	}

	/**
	 * @param total 
	 *				the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}

}
