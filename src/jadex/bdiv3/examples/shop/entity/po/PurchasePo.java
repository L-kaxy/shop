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
 * 商品采购.
 * 
 * @author 罗佳欣
 *
 */
@Entity
@Table(name = "t_purchase")
public class PurchasePo {

	/**
	 * 采购编号.
	 */
	private Long purchaseid;

	/**
	 * 商品名称.
	 */
	private String commodityname;

	/**
	 * 商品成本单价.
	 */
	private Double singleprice;

	/**
	 * @return the purchaseid
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	public Long getPurchaseid() {
		return purchaseid;
	}

	/**
	 * @param purchaseid 
	 *				the purchaseid to set
	 */
	public void setPurchaseid(Long purchaseid) {
		this.purchaseid = purchaseid;
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
	 *				the commodityname to set
	 */
	public void setCommodityname(String commodityname) {
		this.commodityname = commodityname;
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
	 *				the singleprice to set
	 */
	public void setSingleprice(Double singleprice) {
		this.singleprice = singleprice;
	}

}
