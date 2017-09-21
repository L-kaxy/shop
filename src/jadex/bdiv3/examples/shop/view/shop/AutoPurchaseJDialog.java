/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.view.shop;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import jadex.bdiv3.examples.shop.dao.SuperDaoProxy;
import jadex.bdiv3.examples.shop.entity.po.CommodityPo;
import jadex.bdiv3.examples.shop.entity.po.PurchasePo;

/**
 * @author 罗佳欣
 *
 */
class AutoPurchaseJDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean isOk = false;
	private JButton add;
	private JButton delete;
	private JButton edit;
	private JButton ok;
	private JButton cancel;

	public List<CommodityPo> commodityList;
	private Map<String, Double> purchaseMap;
	private List<PurchasePo> purchaseList;

	private JTable shoptable;

	private AbstractTableModel shopmodel;

	public AutoPurchaseJDialog(JFrame parent, List<CommodityPo> commodityList) {
		super(parent, true);
		this.commodityList = commodityList;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("自动采购设置");
		setSize(400, 490);
		setLocationRelativeTo(parent);
		setResizable(false);
		setLayout(null);

		initPurchaseList();

		shopmodel = new PurchaseTableModel(commodityList, purchaseMap);
		shoptable = new JTable(shopmodel);
		shoptable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		shoptable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane goodsPane = new JScrollPane(shoptable);
		goodsPane.setBorder(new TitledBorder(new EtchedBorder(), "规划"));
		goodsPane.setBounds(0, 0, 400, 400);
		add(goodsPane);

		addButton(parent);
		editButton(parent);
		deleteButton();
		okButton();
		cancelButton();

		setVisible(true);
	}

	private void initPurchaseList() {
		purchaseMap = new HashMap<>();
		purchaseList = new ArrayList<>();
		SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();
		try {
			purchaseList = daoProxy.queryList(new PurchasePo());
			for (PurchasePo po : purchaseList) {
				purchaseMap.put(po.getCommodityname(), po.getSingleprice());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cancelButton() {
		cancel = new JButton("取消");
		cancel.setBounds(250, 430, 80, 25);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		add(cancel);
	}

	private void okButton() {
		ok = new JButton("保存设置");
		ok.setBounds(50, 430, 100, 25);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isOk = true;
				dispose();
			}
		});
		add(ok);
	}

	private void deleteButton() {
		delete = new JButton("删除");
		delete.setBounds(250, 400, 80, 25);
		add(delete);
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int select = shoptable.getSelectedRow();
				if (select == -1) {
					JOptionPane.showMessageDialog(null, "请选择要修改的商品", "错误信息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				commodityList.remove(select);
				shopmodel.fireTableDataChanged();
			}
		});
	}

	private void editButton(JFrame parent) {
		edit = new JButton("修改");
		edit.setBounds(150, 400, 80, 25);

		edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int select = shoptable.getSelectedRow();
				if (select == -1) {
					JOptionPane.showMessageDialog(null, "请选择要修改的商品", "错误信息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				MyJDialog myJDialog = new MyJDialog(parent, "更新商品", true, select);
				if (myJDialog.isOk) {
					CommodityPo commodityPo = commodityList.get(select);
					commodityPo.setSingleprice(myJDialog.newPrice);
					commodityPo.setPurchase(myJDialog.newPurchase);
					commodityPo.setTotal(myJDialog.newTotal);
					commodityList.set(select, commodityPo);
					shopmodel.fireTableDataChanged();
				}
			}
		});

		add(edit);
	}

	private void addButton(JFrame parent) {
		add = new JButton("添加");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MyJDialog myJDialog = new MyJDialog(parent, "添加规划", false, -1);
				if (myJDialog.isOk) {
					CommodityPo commodityPo = new CommodityPo();
					commodityPo.setCommodityname(myJDialog.newName);
					commodityPo.setSingleprice(myJDialog.newPrice);
					commodityPo.setPurchase(myJDialog.newPurchase);
					commodityPo.setTotal(myJDialog.newTotal);
					commodityList.add(commodityPo);
					shopmodel.fireTableDataChanged();
				}
			}
		});
		add.setBounds(50, 400, 80, 25);
		add(add);
	}

	class MyJDialog extends JDialog {
		private static final long serialVersionUID = 1L;

		public String newName = "";
		public double newPrice = 0;
		public Long newPurchase = 0L;
		public Long newTotal = 0L;
		public boolean isOk = false;
		public boolean isUpdate = false;
		JComboBox<String> commodityName;
		JTextField orginPrice;
		JTextField sellPrice;
		JTextField purchaseQuantity;
		JTextField totalQuantity;
		JButton save;
		JButton cancel;

		MyJDialog(JFrame parent, String title, final boolean isUpdate, final int select) {
			super(parent, true);
			this.isUpdate = isUpdate;

			commodityName = new JComboBox<>();
			orginPrice = new JTextField(30);
			sellPrice = new JTextField(30);
			purchaseQuantity = new JTextField(30);
			totalQuantity = new JTextField(30);

			save = new JButton("保存");
			cancel = new JButton("取消");

			setTitle(title);
			setSize(330, 300);
			setLocationRelativeTo(parent);
			setResizable(false);
			setLayout(null);

			JLabel Text1 = new JLabel("商品名字:");
			JLabel Text2 = new JLabel("成本价格:");
			JLabel Text3 = new JLabel("计划售价:");
			JLabel Text4 = new JLabel("自动采购:");
			JLabel Text5 = new JLabel("采购目标:");
			Text1.setBounds(50, 30, 60, 25);
			Text2.setBounds(50, 60, 60, 25);
			Text3.setBounds(50, 90, 60, 25);
			Text4.setBounds(50, 120, 60, 25);
			Text5.setBounds(50, 150, 60, 25);
			add(Text1);
			add(Text2);
			add(Text3);
			add(Text4);
			add(Text5);

			commodityName.setBounds(120, 30, 120, 25);
			for (PurchasePo purchasePo : purchaseList) {
				commodityName.addItem(purchasePo.getCommodityname());
			}
			orginPrice.setBounds(120, 60, 120, 25);
			orginPrice.setEditable(false);
			orginPrice.setText(purchaseMap.get(commodityName.getSelectedItem()) + "");
			sellPrice.setBounds(120, 90, 120, 25);
			purchaseQuantity.setBounds(120, 120, 120, 25);
			totalQuantity.setBounds(120, 150, 120, 25);
			add(commodityName);
			add(orginPrice);
			add(sellPrice);
			add(purchaseQuantity);
			add(totalQuantity);

			save.setBounds(70, 180, 80, 25);
			cancel.setBounds(170, 180, 80, 25);
			add(save);
			add(cancel);

			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			if (isUpdate) {
				commodityName.setSelectedItem(commodityList.get(select).getCommodityname());
				commodityName.setEnabled(false);
				orginPrice.setText(purchaseMap.get(commodityName.getSelectedItem()) + "");
				sellPrice.setText(commodityList.get(select).getSingleprice() + "");
				purchaseQuantity.setText(commodityList.get(select).getPurchase() + "");
				totalQuantity.setText(commodityList.get(select).getTotal() + "");
			}
			commodityName.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					orginPrice.setText(purchaseMap.get(commodityName.getSelectedItem()) + "");
				}
			});

			save.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String tName = commodityName.getSelectedItem().toString();
					String tPrice = sellPrice.getText().trim();
					String tPurchase = purchaseQuantity.getText().trim();
					String tTotal = totalQuantity.getText().trim();

					if (select == -1) {
						for (CommodityPo po : commodityList) {
							if (po.getCommodityname().equals(tName)) {
								JOptionPane.showMessageDialog(null, "此商品已存在", "错误信息", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
					}

					if (tPrice.replaceAll("[0.0-9.0]", "").length() != 0 || tPrice.equals("")
							|| tPrice.split("\\.").length > 2 || tPrice.endsWith(".")) {
						JOptionPane.showMessageDialog(null, "价格：输入中含有非法字符或为空", "错误信息", JOptionPane.ERROR_MESSAGE);
						return;
					} else if (tPurchase.replaceAll("[0-9]", "").length() != 0 || tPurchase.equals("")) {
						JOptionPane.showMessageDialog(null, "数量:输入中含有非法字符或为空", "错误信息", JOptionPane.ERROR_MESSAGE);
						return;
					} else if (tTotal.replaceAll("[0-9]", "").length() != 0 || tTotal.equals("")) {
						JOptionPane.showMessageDialog(null, "数量:输入中含有非法字符或为空", "错误信息", JOptionPane.ERROR_MESSAGE);
						return;
					} else if (Long.valueOf(tTotal) < Long.valueOf(tPurchase)) {
						JOptionPane.showMessageDialog(null, "采购目标数量必须大于自动采购数量", "错误信息", JOptionPane.ERROR_MESSAGE);
						return;
					}
					newName = commodityName.getSelectedItem().toString();
					newPrice = Double.valueOf(sellPrice.getText().trim());
					newPurchase = Long.valueOf(purchaseQuantity.getText().trim());
					newTotal = Long.valueOf(totalQuantity.getText().trim());
					isOk = true;
					dispose();
				}

			});
			cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			setVisible(true);
		}
	}

}