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
 * 用户资产.
 * 
 * @author 罗佳欣
 *
 */
@Entity
@Table(name = "t_inventory")
public class InventoryPo {

	/**
	 * 资产编号.
	 */
	private Long inventoryid;

	/**
	 * 资产名称.
	 */
	private String inventoryname;

	/**
	 * 资产数量.
	 */
	private Long quantity;

	/**
	 * 资产总价钱.
	 */
	private Double price;

	/**
	 * 用户名.
	 */
	private String username;

	/**
	 * @return the inventoryid
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	public Long getInventoryid() {
		return inventoryid;
	}

	/**
	 * @param inventoryid
	 *            the inventoryid to set
	 */
	public void setInventoryid(Long inventoryid) {
		this.inventoryid = inventoryid;
	}

	/**
	 * @return the inventoryname
	 */
	@Column(nullable = false)
	public String getInventoryname() {
		return inventoryname;
	}

	/**
	 * @param inventoryname
	 *            the inventoryname to set
	 */
	public void setInventoryname(String inventoryname) {
		this.inventoryname = inventoryname;
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
	 * @return the price
	 */
	@Column(nullable = false)
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
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

}
