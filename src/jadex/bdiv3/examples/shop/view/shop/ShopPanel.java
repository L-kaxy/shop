/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.view.shop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import jadex.bdiv3.examples.shop.agent.customer.ICustService;
import jadex.bdiv3.examples.shop.agent.shop.ShopCapa;
import jadex.bdiv3.examples.shop.agent.shop.ShopCapa.AddMoneyGoal;
import jadex.bdiv3.examples.shop.agent.shop.ShopCapa.CommodityPurchaseGoal;
import jadex.bdiv3.examples.shop.agent.shop.ShopCapa.GetCommodityGoal;
import jadex.bdiv3.examples.shop.agent.shop.ShopCapa.PurchaseGoal;
import jadex.bdiv3.examples.shop.dao.SuperDaoProxy;
import jadex.bdiv3.examples.shop.entity.po.CommodityPo;
import jadex.bdiv3.examples.shop.entity.po.PurchasePo;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ICapability;
import jadex.bdiv3.runtime.impl.BeliefAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.rules.eca.ChangeInfo;

/**
 * @author 罗佳欣
 *
 */
public class ShopPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ICapability capa;

	protected JTextField jMoney;
	
	protected Double money;

	protected List<CommodityPo> commodityList;

	protected JTable shoptable;

	protected AbstractTableModel shopmodel;

	protected JTextField totalMoney;

	private Map<String, Double> purchaseMap;

	/**
	 * 构造体, 构建界面.
	 * 
	 * @param capa
	 * @param shopFrame
	 */
	public ShopPanel(ICapability capa, ShopFrame shopFrame) {
		this.capa = capa;
		setLayout(new BorderLayout());

		JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		moneyPanel.setBorder(new TitledBorder(new EtchedBorder(), "资产"));
		moneyPanel.add(new JLabel("现金："));
		jMoney = new JTextField(5);
		jMoney.setEditable(false);
		JButton chargeMoneyBtn = new JButton("充值");
		moneyPanel.add(jMoney);
		moneyPanel.add(chargeMoneyBtn);
		moneyPanel.add(new JLabel("         总资产："));
		totalMoney = new JTextField(5);
		totalMoney.setEditable(false);
		moneyPanel.add(totalMoney);
		add(moneyPanel, BorderLayout.NORTH);

		commodityList = ((ShopCapa) capa.getPojoCapability()).getCatalog();
		shopmodel = new CommodityTableModel(commodityList);
		shoptable = new JTable(shopmodel);
		shoptable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		shoptable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane goodsPane = new JScrollPane(shoptable);
		goodsPane.setBorder(new TitledBorder(new EtchedBorder(), "商品"));
		add(goodsPane, BorderLayout.CENTER);

		JPanel operate = new JPanel();
		operate.setBorder(new TitledBorder(new EtchedBorder(), "操作"));
		JButton purchaseBtn = new JButton("手动采购");
		JButton autoPurchaseBtn = new JButton("自动采购设置");
		operate.add(purchaseBtn);
		operate.add(autoPurchaseBtn);
		add(operate, BorderLayout.SOUTH);

		final NumberFormat df = NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		initPurchaseList();

		// 获取现金.
		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				final double value = ((ShopCapa) capa.getPojoCapability()).getMoney();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						jMoney.setText(df.format(value));
						money = value;
					}
				});
				return IFuture.DONE;
			}
		});

		// 监听现金金额.
		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			@Classname("money")
			public IFuture<Void> execute(IInternalAccess ia) {
				// 钱的信念监听器
				capa.addBeliefListener("money", new BeliefAdapter<Object>() {
					public void beliefChanged(final ChangeInfo<Object> info) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								jMoney.setText(df.format(info.getValue()));
								money = (Double) info.getValue();
								getTotalMoney();
							}
						});
					}
				});
				return IFuture.DONE;
			}
		});

		// 取得商品目录
		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			@Classname("getCommoditys")
			public IFuture<Void> execute(IInternalAccess ia) {
				GetCommodityGoal goal = new GetCommodityGoal();
				ia.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal);
				return IFuture.DONE;
			}
		});

		// 监听目录的变化，更新界面
		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			// 商品目录的信念监听器
			public IFuture<Void> execute(IInternalAccess ia) {
				capa.addBeliefListener("catalog", new BeliefAdapter<Object>() {
					@Override
					public void factRemoved(final ChangeInfo<Object> value) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								getTotalMoney();
								shopmodel.fireTableDataChanged();
							}

						});
					}

					@Override
					public void factAdded(final ChangeInfo<Object> value) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								getTotalMoney();
								refreshCustomer();
								shopmodel.fireTableDataChanged();
							}
						});
					}

					// 对象内容发生变化时启动
					@Override
					public void factChanged(ChangeInfo<Object> object) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								getTotalMoney();
								shopmodel.fireTableDataChanged();
							}
						});
					}
				});
				return IFuture.DONE;
			}
		});

		// 充值金钱
		chargeMoneyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RechargeJDialog cJDialog = new RechargeJDialog(shopFrame);
				final double chargemoney = cJDialog.value;
				if (cJDialog.isOk) {
					capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
						@Override
						public IFuture<Void> execute(IInternalAccess ia) {
							AddMoneyGoal goal = new AddMoneyGoal(chargemoney);
							IFuture<AddMoneyGoal> rs = ia.getComponentFeature(IBDIAgentFeature.class)
									.dispatchTopLevelGoal(goal);
							// 添加结果监听器
							rs.addResultListener(
									new SwingResultListener<AddMoneyGoal>(new IResultListener<AddMoneyGoal>() {
								@Override
								public void resultAvailable(AddMoneyGoal result) {
									String text = SUtil.wrapText("成功冲入:$" + result.getMoney());
									JOptionPane.showMessageDialog(SGUI.getWindowParent(ShopPanel.this), text, "充值成功",
											JOptionPane.INFORMATION_MESSAGE);
								}

								@Override
								public void exceptionOccurred(Exception exception) {
									String text = SUtil.wrapText("充值失败:" + exception.getMessage());
									JOptionPane.showMessageDialog(SGUI.getWindowParent(ShopPanel.this), text, "充值失败",
											JOptionPane.WARNING_MESSAGE);
								}
							}));
							return IFuture.DONE;
						}
					});
				}
			}
		});

		purchaseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
					@Override
					public IFuture<Void> execute(IInternalAccess ia) {
						PurchaseGoal goal = new PurchaseGoal();
						IFuture<PurchaseGoal> rs = ia.getComponentFeature(IBDIAgentFeature.class)
								.dispatchTopLevelGoal(goal);
						// 添加结果监听器
						rs.addResultListener(new SwingResultListener<PurchaseGoal>(new IResultListener<PurchaseGoal>() {
							@Override
							public void resultAvailable(PurchaseGoal result) {
								JOptionPane.showMessageDialog(SGUI.getWindowParent(ShopPanel.this), "采购成功", "成功",
										JOptionPane.INFORMATION_MESSAGE);
							}

							@Override
							public void exceptionOccurred(Exception exception) {
								JOptionPane.showMessageDialog(SGUI.getWindowParent(ShopPanel.this),
										exception.getLocalizedMessage(), "失败", JOptionPane.WARNING_MESSAGE);
							}
						}));
						return IFuture.DONE;
					}
				});
			}
		});

		autoPurchaseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AutoPurchaseJDialog cJDialog = new AutoPurchaseJDialog(shopFrame, new ArrayList<>(commodityList));
				if (cJDialog.isOk) {
					capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
						@Override
						public IFuture<Void> execute(IInternalAccess ia) {
							List<CommodityPo> newCommodityList = cJDialog.commodityList;
							CommodityPurchaseGoal goal = new CommodityPurchaseGoal(newCommodityList);
							IFuture<CommodityPurchaseGoal> rs = ia.getComponentFeature(IBDIAgentFeature.class)
									.dispatchTopLevelGoal(goal);
							// 添加结果监听器
							rs.addResultListener(new SwingResultListener<CommodityPurchaseGoal>(
									new IResultListener<CommodityPurchaseGoal>() {
								@Override
								public void resultAvailable(CommodityPurchaseGoal result) {
									JOptionPane.showMessageDialog(SGUI.getWindowParent(ShopPanel.this), "设置成功", "成功",
											JOptionPane.INFORMATION_MESSAGE);
								}

								@Override
								public void exceptionOccurred(Exception exception) {
									JOptionPane.showMessageDialog(SGUI.getWindowParent(ShopPanel.this),
											exception.getLocalizedMessage(), "失败", JOptionPane.WARNING_MESSAGE);
								}
							}));
							return IFuture.DONE;
						}
					});
				}
			}
		});

	}

	/**
	 * 计算总资产.
	 */
	public void getTotalMoney() {
		final NumberFormat df = NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		Double total = money;
		for (CommodityPo po : commodityList) {
			Double orignPrice = purchaseMap.get(po.getCommodityname());
			if (orignPrice != null) {
				total += orignPrice * po.getQuantity();
			} else {
				total += po.getSingleprice() * po.getQuantity();
			}
		}
		totalMoney.setText(df.format(total));
	}

	/**
	 * 初始化采购列表.(用于计算总资产的成本价)
	 */
	private void initPurchaseList() {
		purchaseMap = new HashMap<>();
		SuperDaoProxy daoProxy = SuperDaoProxy.getInstance();
		try {
			List<PurchasePo> purchaseList = daoProxy.queryList(new PurchasePo());
			for (PurchasePo po : purchaseList) {
				purchaseMap.put(po.getCommodityname(), po.getSingleprice());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刷新顾客商品列表.
	 */
	private void refreshCustomer() {
		//全局
		IFuture<Collection<ICustService>> ret2 = capa.getAgent().getExternalAccess()
				.scheduleStep(new IComponentStep<Collection<ICustService>>() {
					public IFuture<Collection<ICustService>> execute(IInternalAccess ia) {
						Future<Collection<ICustService>> ret = new Future<Collection<ICustService>>();
						IFuture<Collection<ICustService>> fut = capa.getAgent()
								.getComponentFeature(IRequiredServicesFeature.class)
								.getRequiredServices("glcustservices");
						fut.addResultListener(new DelegationResultListener<Collection<ICustService>>(ret) {
							public void exceptionOccurred(Exception exception) {
								super.exceptionOccurred(exception);
							}
						});
						return ret;
					}
				});
		ret2.addResultListener(new SwingDefaultResultListener<Collection<ICustService>>(ShopPanel.this) {
			public void customResultAvailable(Collection<ICustService> coll) {
				if (coll != null && coll.size() > 0) {
					for (ICustService cust : coll) {
						cust.refresh();
					}
				}
			}
			
			public void customExceptionOccurred(Exception exception) {
				super.customExceptionOccurred(exception);
			}
		});
	}

}
