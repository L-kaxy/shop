package jadex.bdiv3.examples.shop.customer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.shop.entity.ResultMessage;
import jadex.bdiv3.examples.shop.entity.po.InventoryPo;
import jadex.bdiv3.examples.shop.entity.view.ItemInfo;
import jadex.bdiv3.examples.shop.service.ICustomerService;
import jadex.bdiv3.examples.shop.service.impl.CustomerSerivce;
import jadex.bdiv3.examples.shop.shop.IShopService;
import jadex.bdiv3.examples.shop.view.customer.CustomerFrame;
import jadex.bdiv3.runtime.ICapability;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * Customer capability.
 */
@Capability
@Properties(@NameValue(name = "componentviewer.viewerclass", value = "\"jadex.bdi.examples.shop.CustomerViewerPanel\""))
@ProvidedServices(@ProvidedService(type = ICustService.class, implementation = @Implementation(expression = "new CustService($pojocapa.getCustomerName())") ))
@RequiredServices({
		@RequiredService(name = "localshopservices", type = IShopService.class, multiple = true, binding = @Binding(scope = Binding.SCOPE_PLATFORM) ),
		@RequiredService(name = "remoteshopservices", type = IShopService.class, multiple = true, binding = @Binding(scope = Binding.SCOPE_GLOBAL) ), })
public class CustomerCapability {
	// -------- attributes --------

	/** The capability. */
	@Agent
	protected ICapability capa;

	/** The inventory. */
	@Belief
	protected List<ItemInfo> inventory;

	/**
	 * 刷新信号.
	 */
	@Belief
	protected boolean refreshFlag;

	/**
	 * 顾客 Service.
	 */
	protected ICustomerService customerService;

	/**
	 * 顾客名字.
	 */
	protected String customerName;

	// -------- constructors --------

	/**
	 * Called when the agent is started.
	 */
	public CustomerCapability(String customerName) {

		// 初始化参数
		this.customerName = customerName;
		this.inventory = new ArrayList<ItemInfo>();

		customerService = CustomerSerivce.getInstance();
		// 若用户存在, 则取数据库数据; 用户不存在, 初始化一个新的用户, 初始金钱100.00
		ResultMessage rs = customerService.initUserMoney(customerName);

		if (rs.getServiceResult() == 1L) {
			// 查询成功
			setMoney((Double) rs.getResultParm().get("money"));

			// 启动界面
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new CustomerFrame(capa);
				}
			});
		} else {
			// 查询失败
		}
	}

	/**
	 * Get the money.
	 */
	@Belief
	public native double getMoney();

	/**
	 * Set the money.
	 */
	@Belief
	public native void setMoney(double money);

	/*
	 * 获取名字
	 */
	public String getCustomerName() {
		return this.customerName;
	}

	/*
	 * 获取资产
	 */
	public List<ItemInfo> getInventory() {
		return this.inventory;
	}

	// -------- goals --------

	/**
	 * Goal to buy an item.
	 */
	@Goal
	public static class BuyItem {
		// -------- attributes --------

		/** The item name. */
		public String name;

		/** The shop. */
		public IShopService shop;

		/** The price. */
		public double price;

		// -------- constructors --------

		/**
		 * Create a buy item goal.
		 */
		public BuyItem(String name, IShopService shop, double price) {
			this.name = name;
			this.shop = shop;
			this.price = price;
		}
	}

	/**
	 * 添加现金
	 * 
	 * @author 罗佳欣
	 */
	@Goal
	public static class AddMoneyGoal {
		public Double money;

		public AddMoneyGoal(Double money) {
			this.money = money;
		}

		public Double getMoney() {
			return money;
		}

		public void setMoney(Double money) {
			this.money = money;
		}
	}

	/**
	 * 获取资产
	 * 
	 * @author 罗佳欣
	 */
	@Goal
	public static class GetInventoryGoal {
	}

	// -------- plans --------

	/**
	 * Plan for buying an item.
	 */
	@Plan(trigger = @Trigger(goals = BuyItem.class) )
	public void buyItem(BuyItem big) {
		// Check if enough money to buy the item
		if (getMoney() < big.price)
			throw new RuntimeException("Not enough money to buy: " + big.name);

		// Buy the item at the shop (the shop is a service at another agent)
		System.out.println(capa.getAgent().getComponentIdentifier().getName() + " buying item: " + big.name);
		IFuture<ItemInfo> future = big.shop.buyItem(big.name, big.price);
		System.out.println(capa.getAgent().getComponentIdentifier().getName() + " getting item: " + future);
		ItemInfo item = (ItemInfo) future.get();
		System.out.println(capa.getAgent().getComponentIdentifier().getName() + " bought item: " + item);

		// Update the customer inventory
		InventoryPo inventoryPo = new InventoryPo();
		inventoryPo.setUsername(customerName);
		inventoryPo.setInventoryname(big.name);
		inventoryPo.setPrice(big.price);
		inventoryPo.setQuantity(1L);

		ResultMessage rs = customerService.buyItem(inventoryPo);

		if (rs.getServiceResult() == 1L) {
			setMoney((Double) rs.getResultParm().get("money"));
			inventory.clear();

			@SuppressWarnings("unchecked")
			List<InventoryPo> inventoryList = (List<InventoryPo>) rs.getResultParm().get("inventoryList");
			ItemInfo tempItem = null;
			for (InventoryPo po : inventoryList) {
				tempItem = new ItemInfo(po.getInventoryname(), po.getPrice(), po.getQuantity());
				inventory.add(tempItem);
			}
		} else {
			// 查询失败
		}
	}

	/**
	 * 添加现金
	 * 
	 * @param goal
	 */
	@Plan(trigger = @Trigger(goals = AddMoneyGoal.class) )
	public void addMoney(AddMoneyGoal goal) {
		ResultMessage rs = customerService.addMoney(customerName, goal.getMoney());
		if (rs.getServiceResult() == 1L) {
			setMoney((Double) rs.getResultParm().get("money"));
		} else {
			// 查询失败
		}
	}

	/**
	 * 获取资产列表
	 * 
	 * @param goal
	 */
	@Plan(trigger = @Trigger(goals = GetInventoryGoal.class) )
	public void getInventory(GetInventoryGoal goal) {
		ResultMessage rs = customerService.getInventoryList(customerName);
		if (rs.getServiceResult() == 1L) {
			inventory.clear();
			@SuppressWarnings("unchecked")
			List<InventoryPo> inventoryList = (List<InventoryPo>) rs.getResultParm().get("inventoryList");
			ItemInfo tempItem = null;
			for (InventoryPo po : inventoryList) {
				tempItem = new ItemInfo(po.getInventoryname(), po.getPrice(), po.getQuantity());
				inventory.add(tempItem);
			}
		} else {
			// 查询失败
		}
	}

	/**
	 * 刷新商品目标.
	 * 
	 * @author 罗佳欣
	 */
	@Goal
	public static class RefreshGoal {
	}

	/**
	 * 刷新商品
	 * 
	 * @param goal
	 */
	@Plan(trigger = @Trigger(goals = RefreshGoal.class) )
	public void refresh(RefreshGoal goal) {
		refreshFlag = !refreshFlag;
	}

}
