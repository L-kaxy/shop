package jadex.bdiv3.examples.shop.view.customer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import jadex.bdiv3.examples.shop.customer.CustomerCapability;
import jadex.bdiv3.examples.shop.customer.CustomerCapability.AddMoneyGoal;
import jadex.bdiv3.examples.shop.customer.CustomerCapability.BuyItem;
import jadex.bdiv3.examples.shop.customer.CustomerCapability.GetInventoryGoal;
import jadex.bdiv3.examples.shop.entity.view.ItemInfo;
import jadex.bdiv3.examples.shop.shop.IShopService;
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
 * Customer gui that allows buying items at different shops.
 */
public class CustomerPanel extends JPanel {
	// -------- attributes --------

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ICapability capa;
	protected List<ItemInfo> shoplist = new ArrayList<>();
	protected JCheckBox remote;
	protected JTable shoptable;
	protected AbstractTableModel shopmodel = new ItemTableModel(shoplist);

	protected List<ItemInfo> invlist;
	protected AbstractTableModel invmodel;
	protected JTable invtable;
	protected Map<String, IShopService> shops;

	protected JButton rechargeBtn;
	// -------- constructors --------
	private JComboBox<String> shopscombo;

	/**
	 * Create a new gui.
	 */
	public CustomerPanel(final ICapability capa, final JFrame parent) {
		this.capa = capa;
		this.shops = new HashMap<>();

		shopscombo = new JComboBox<>();
		shopscombo.addItem("无");
		shopscombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (shops.get(shopscombo.getSelectedItem()) instanceof IShopService) {
					refresh((IShopService) shops.get(shopscombo.getSelectedItem()));
				}
			}
		});

		remote = new JCheckBox("远程");
		remote.setToolTipText("搜索远程平台的商店.");
		final JButton searchbut = new JButton("搜索");
		searchbut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchbut.setEnabled(false);

				// SServiceProvider.getServices(agent.getServiceProvider(),
				// IShop.class, remote.isSelected(), true)
				IFuture<Collection<IShopService>> ret = capa.getAgent().getExternalAccess()
						.scheduleStep(new IComponentStep<Collection<IShopService>>() {
					public IFuture<Collection<IShopService>> execute(IInternalAccess ia) {
						Future<Collection<IShopService>> ret = new Future<Collection<IShopService>>();
						if (remote.isSelected()) {
							IFuture<Collection<IShopService>> fut = capa.getAgent()
									.getComponentFeature(IRequiredServicesFeature.class)
									.getRequiredServices("remoteshopservices");
							fut.addResultListener(new DelegationResultListener<Collection<IShopService>>(ret) {
								public void exceptionOccurred(Exception exception) {
									super.exceptionOccurred(exception);
								}
							});
						} else {
							IFuture<Collection<IShopService>> fut = capa.getAgent()
									.getComponentFeature(IRequiredServicesFeature.class)
									.getRequiredServices("localshopservices");
							fut.addResultListener(new DelegationResultListener<Collection<IShopService>>(ret) {
								public void exceptionOccurred(Exception exception) {
									super.exceptionOccurred(exception);
								}
							});
						}
						return ret;
					}
				});

				ret.addResultListener(new SwingDefaultResultListener<Collection<IShopService>>(CustomerPanel.this) {
					public void customResultAvailable(Collection<IShopService> coll) {
						searchbut.setEnabled(true);
						// System.out.println("Customer search result:
						// "+result);
						((DefaultComboBoxModel<String>) shopscombo.getModel()).removeAllElements();
						shops.clear();
						if (coll != null && coll.size() > 0) {
							for (Iterator<IShopService> it = coll.iterator(); it.hasNext();) {
								IShopService shop = it.next();
								shops.put(shop.getName(), shop);
								((DefaultComboBoxModel<String>) shopscombo.getModel()).addElement(shop.getName());
							}
						} else {
							((DefaultComboBoxModel<String>) shopscombo.getModel()).addElement("无");
						}
					}

					public void customExceptionOccurred(Exception exception) {
						searchbut.setEnabled(true);
						super.customExceptionOccurred(exception);
					}
				});
			}
		});

		final NumberFormat df = NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		final JTextField money = new JTextField(5);

		// 初始化用户现金
		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			@Classname("initialMoney")
			public IFuture<Void> execute(IInternalAccess ia) {
				CustomerCapability cust = (CustomerCapability) capa.getPojoCapability();
				final double mon = cust.getMoney();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						money.setText(df.format(mon));
					}
				});
				return IFuture.DONE;
			}
		});
		money.setEditable(false);

		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			@Classname("money")
			public IFuture<Void> execute(IInternalAccess ia) {
				capa.addBeliefListener("money", new BeliefAdapter<Object>() {
					public void beliefChanged(final ChangeInfo<Object> info) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								money.setText(df.format(info.getValue()));
							}
						});
					}
				});
				return IFuture.DONE;
			}
		});

		rechargeBtn = new JButton("充值");

		rechargeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RechargeJDialog cJDialog = new RechargeJDialog(parent);
				final double rechargemoney = cJDialog.value;
				if (cJDialog.isOk) {
					capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
						@Override
						public IFuture<Void> execute(IInternalAccess ia) {
							AddMoneyGoal goal = new AddMoneyGoal(rechargemoney);
							IFuture<AddMoneyGoal> rs = ia.getComponentFeature(IBDIAgentFeature.class)
									.dispatchTopLevelGoal(goal);
							// 添加结果监听器
							rs.addResultListener(
									new SwingResultListener<AddMoneyGoal>(new IResultListener<AddMoneyGoal>() {
								@Override
								public void resultAvailable(AddMoneyGoal result) {
									String text = SUtil.wrapText("成功: $" + result.getMoney());
									JOptionPane.showMessageDialog(SGUI.getWindowParent(CustomerPanel.this), text,
											"Success", JOptionPane.INFORMATION_MESSAGE);
								}

								@Override
								public void exceptionOccurred(Exception exception) {
									String text = SUtil.wrapText("失败:" + exception.getMessage());
									JOptionPane.showMessageDialog(SGUI.getWindowParent(CustomerPanel.this), text,
											"Fail", JOptionPane.WARNING_MESSAGE);
								}
							}));
							return IFuture.DONE;
						}
					});
				}
			}
		});

		JPanel selpanel = new JPanel(new GridBagLayout());
		selpanel.setBorder(new TitledBorder(new EtchedBorder(),
				"顾客  :  " + ((CustomerCapability) capa.getPojoCapability()).getCustomerName()));
		int x = 0;
		int y = 0;

		selpanel.add(new JLabel("现金: "), new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		x++;
		selpanel.add(money, new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));
		x++;
		selpanel.add(rechargeBtn, new GridBagConstraints(x, y, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		x++;
		selpanel.add(new JLabel("商店: "), new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		x++;
		selpanel.add(shopscombo, new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		x++;
		selpanel.add(searchbut, new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		x++;
		selpanel.add(remote, new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));

		JPanel shoppanel = new JPanel(new BorderLayout());
		shoppanel.setBorder(new TitledBorder(new EtchedBorder(), "商店商品"));
		shoptable = new JTable(shopmodel);
		shoptable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		shoptable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		shoppanel.add(BorderLayout.CENTER, new JScrollPane(shoptable));

		JPanel invpanel = new JPanel(new BorderLayout());
		invpanel.setBorder(new TitledBorder(new EtchedBorder(), "顾客资产"));
		invlist = ((CustomerCapability) capa.getPojoCapability()).getInventory();
		invmodel = new ItemTableModel(invlist);
		invtable = new JTable(invmodel);
		invtable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		invpanel.add(BorderLayout.CENTER, new JScrollPane(invtable));

		// 获取用户资产列表
		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			public IFuture<Void> execute(IInternalAccess ia) {
				GetInventoryGoal goal = new GetInventoryGoal();
				ia.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal);
				return IFuture.DONE;
			}
		});

		// 资产变化监听
		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			@Classname("inventory")
			public IFuture<Void> execute(IInternalAccess ia) {
				try {
					capa.addBeliefListener("inventory", new BeliefAdapter<Object>() {
						public void factRemoved(final ChangeInfo<Object> value) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									invmodel.fireTableDataChanged();
								}
							});
						}

						public void factAdded(final ChangeInfo<Object> value) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									invmodel.fireTableDataChanged();
								}
							});
						}

						public void factChanged(ChangeInfo<Object> object) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									invmodel.fireTableDataChanged();
								}
							});
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				return IFuture.DONE;
			}
		});

		JPanel butpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		// butpanel.setBorder(new TitledBorder(new EtchedBorder(), "Actions"));
		JButton buy = new JButton("购买");
		final JTextField item = new JTextField(8);
		item.setEditable(false);
		butpanel.add(new JLabel("选择商品:"));
		butpanel.add(item);
		butpanel.add(buy);
		buy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sel = shoptable.getSelectedRow();
				if (sel != -1) {
					final String name = (String) shopmodel.getValueAt(sel, 0);
					final Double price = (Double) shopmodel.getValueAt(sel, 1);
					final IShopService shop = (IShopService) shops.get(shopscombo.getSelectedItem());
					capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
						@Classname("buy")
						public IFuture<Void> execute(IInternalAccess ia) {
							BuyItem big = new BuyItem(name, shop, price.doubleValue());
							IFuture<BuyItem> ret = capa.getAgent().getComponentFeature(IBDIAgentFeature.class)
									.dispatchTopLevelGoal(big);
							ret.addResultListener(new SwingResultListener<BuyItem>(new IResultListener<BuyItem>() {
								public void resultAvailable(BuyItem result) {
									// Update number of available items
									refresh(shop);
								}

								public void exceptionOccurred(Exception exception) {
									// Update number of available items
									refresh(shop);

									String text = SUtil.wrapText("商品无法购买. " + exception.getMessage());
									JOptionPane.showMessageDialog(SGUI.getWindowParent(CustomerPanel.this), text,
											"Buy problem", JOptionPane.INFORMATION_MESSAGE);
								}
							}));
							return IFuture.DONE;
						}
					});
				}
			}
		});

		shoptable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int sel = shoptable.getSelectedRow();
				if (sel != -1) {
					item.setText("" + shopmodel.getValueAt(sel, 0));
				}
			}
		});

		setLayout(new GridBagLayout());
		x = 0;
		y = 0;
		add(selpanel, new GridBagConstraints(x, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		add(shoppanel, new GridBagConstraints(x, y++, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		add(invpanel, new GridBagConstraints(x, y++, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		add(butpanel, new GridBagConstraints(x, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));

		addRefreshListener();
	}

	/**
	 * 刷新商品列表监听器.
	 */
	private void addRefreshListener() {
		capa.getAgent().getExternalAccess().scheduleStep(new IComponentStep<Void>() {
			@Classname("refresh")
			public IFuture<Void> execute(IInternalAccess ia) {
				capa.addBeliefListener("refreshFlag", new BeliefAdapter<Object>() {
					public void beliefChanged(final ChangeInfo<Object> info) {
						refresh((IShopService) shops.get(shopscombo.getSelectedItem()));
					}
				});
				return IFuture.DONE;
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void refresh(IShopService shop) {
		if (shop != null) {

			shop.getCatalog().addResultListener(new SwingDefaultResultListener(CustomerPanel.this) {
				public void customResultAvailable(Object result) {
					int sel = shoptable.getSelectedRow();
					ItemInfo[] aitems = (ItemInfo[]) result;
					shoplist.clear();
					for (int i = 0; i < aitems.length; i++) {
						if (!shoplist.contains(aitems[i])) {
							shoplist.add(aitems[i]);
						}
					}
					shopmodel.fireTableDataChanged();
					if (sel != -1 && sel < aitems.length)
						((DefaultListSelectionModel) shoptable.getSelectionModel()).setSelectionInterval(sel, sel);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					shoplist.clear();
					shopmodel.fireTableDataChanged();
				}
			});
		}
	}

}