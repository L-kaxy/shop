package jadex.bdiv3.examples.shop.agent.shop;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.shop.agent.customer.ICustService;
import jadex.bdiv3.examples.shop.entity.ResultMessage;
import jadex.bdiv3.examples.shop.entity.po.CommodityPo;
import jadex.bdiv3.examples.shop.entity.view.ItemInfo;
import jadex.bdiv3.examples.shop.service.IStoreService;
import jadex.bdiv3.examples.shop.service.impl.StoreSerivce;
import jadex.bdiv3.examples.shop.view.shop.ShopFrame;
import jadex.bdiv3.runtime.ICapability;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Capability
@ProvidedServices(@ProvidedService(type = IShopService.class, implementation = @Implementation(expression = "new ShopService($pojocapa.getShopSeriverName())") ))
@RequiredServices({
		@RequiredService(name = "custservices", type = ICustService.class, multiple = true, binding = @Binding(scope = Binding.SCOPE_PLATFORM) ),
		@RequiredService(name = "glcustservices", type = ICustService.class, multiple = true, binding = @Binding(scope = Binding.SCOPE_GLOBAL) ), })
public class ShopCapa {
	@Agent
	private ICapability capa;

	@Belief
	public native double getMoney();

	@Belief
	public native void setMoney(double money);

	/** The shop name. */
	protected String shopname;

	/** The shop catalog. */
	@Belief
	protected List<CommodityPo> catalog;

	protected IStoreService storeService;

	/**
	 * Create a shop capability.
	 */
	@SuppressWarnings("unchecked")
	public ShopCapa(String shopname) {
		this.shopname = shopname;
		this.catalog = new ArrayList<>();

		storeService = StoreSerivce.getInstance();
		// 若用户存在, 则取数据库数据; 用户不存在, 初始化一个新的用户, 初始金钱100.00
		ResultMessage rs = storeService.initStore(shopname);

		if (rs.getServiceResult() == 1L) {
			// 查询成功
			setMoney((Double) rs.getResultParm().get("money"));

			catalog.clear();
			catalog.addAll((List<CommodityPo>) rs.getResultParm().get("commodityList"));

			// 启动界面
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new ShopFrame(capa);
				}
			});

		} else {
			// 查询失败
		}

	}

	/**
	 * Get the shop name.
	 */
	public String getShopname() {
		return shopname;
	}

	/**
	 * 获取商店服务名字.
	 */
	public String getShopSeriverName() {
		return shopname + "," + capa.getAgent().getComponentIdentifier().getPlatformName();
	}

	/**
	 * Get the catalog.
	 */
	public List<CommodityPo> getCatalog() {
		return catalog;
	}

	@Goal
	public class SellGoal {
		/** The text. */
		protected String name;

		/** The price. */
		protected double price;

		/** The result. */
		@GoalResult
		protected ItemInfo result;

		/**
		 * Create a new SellGoal.
		 */
		public SellGoal(String name, double price) {
			this.name = name;
			this.price = price;
		}

		/**
		 * Get the name.
		 * 
		 * @return The name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the price.
		 * 
		 * @return The price.
		 */
		public double getPrice() {
			return price;
		}

		/**
		 * Get the result.
		 * 
		 * @return The result.
		 */
		public ItemInfo getResult() {
			return result;
		}

		/**
		 * Set the result.
		 * 
		 * @param result
		 *            The result to set.
		 */
		public void setResult(ItemInfo result) {
			this.result = result;
		}
	}

	/**
	 * Plan for handling a sell goal.
	 * 
	 * @param goal
	 *            The goal.
	 */
	@SuppressWarnings("unchecked")
	@Plan(trigger = @Trigger(goals = SellGoal.class) )
	public void sell(SellGoal goal) {
		CommodityPo ii = null;
		int pos = 0;
		for (; pos < catalog.size(); pos++) {
			CommodityPo tmp = catalog.get(pos);
			if (tmp.getCommodityname().equals(goal.getName())) {
				ii = tmp;
				break;
			}
		}

		// Check if enough money is given and it is in stock.
		if (ii == null || ii.getQuantity() == 0) {
			throw new RuntimeException("Item not in store: " + goal.getName());
		} else if (ii.getQuantity() > 0 && ii.getSingleprice() <= goal.getPrice()) {
			// Sell item by updating catalog and account
			//// System.out.println(getComponentName()+" sell item: "+name+"
			//// for: "+price);
			ii.setQuantity(ii.getQuantity() - 1);
			goal.setResult(new ItemInfo(goal.getName(), ii.getSingleprice(), 1));
			// getBeliefbase().getBeliefSet("catalog").modified(ii);

			CommodityPo commodityPo = new CommodityPo();
			commodityPo.setCommodityname(ii.getCommodityname());
			commodityPo.setUsername(shopname);
			commodityPo.setQuantity(1L);

			ResultMessage rs = storeService.sellCommodity(commodityPo);

			if (rs.getServiceResult() == 1L) {
				setMoney((Double) rs.getResultParm().get("money"));
				catalog.clear();

				List<CommodityPo> commodityPos = (List<CommodityPo>) rs.getResultParm().get("commodityList");

				// 自动采购
				ResultMessage rs2 = storeService.purchaseCommodity(shopname, commodityPo);
				if (rs2.getServiceResult() == 1L) {
					setMoney((Double) rs2.getResultParm().get("money"));
					commodityPos = (List<CommodityPo>) rs2.getResultParm().get("commodityList");
				}

				catalog.addAll(commodityPos);

			} else if (rs.getServiceResult() == 2L) {
				// 商品数量不足
				throw new RuntimeException("Commodity quentity no enough : " + goal.getName());
			} else if (rs.getServiceResult() == 3L) {
				// 商品不存在
				throw new RuntimeException("Commodity not in store : " + goal.getName());
			}
		} else {
			throw new RuntimeException("Payment not sufficient: " + goal.getPrice());
		}
	}

	/**
	 * 获取商品列表目标.
	 * 
	 * @author 罗佳欣
	 *
	 */
	@Goal
	public static class GetCommodityGoal {
	}

	/**
	 * 获取商品列表
	 * 
	 * @param goal
	 */
	@SuppressWarnings("unchecked")
	@Plan(trigger = @Trigger(goals = GetCommodityGoal.class) )
	public void getCommoditys(GetCommodityGoal goal) {
		ResultMessage rs = storeService.getCommodityList(shopname);

		if (rs.getServiceResult() == 1L) {
			catalog.clear();

			catalog.addAll((List<CommodityPo>) rs.getResultParm().get("commodityList"));
		} else {
			// 查询失败
			throw new RuntimeException("获取商品列表失败");
		}
	}

	/**
	 * 添加现金目标.
	 * 
	 * @author 罗佳欣
	 */
	@Goal
	public static class AddMoneyGoal {
		public double money;

		public AddMoneyGoal(double money) {
			this.money = money;
		}

		public double getMoney() {
			return money;
		}

		public void setMoney(double money) {
			this.money = money;
		}
	}

	/**
	 * 添加现金.
	 * 
	 * @param goal
	 */
	@Plan(trigger = @Trigger(goals = AddMoneyGoal.class) )
	public void addMoney(AddMoneyGoal goal) {
		ResultMessage rs = storeService.addMoney(shopname, goal.getMoney());

		if (rs.getServiceResult() == 1L) {
			setMoney((Double) rs.getResultParm().get("money"));
		} else {
			// 查询失败
			throw new RuntimeException("添加现金失败");
		}
	}

	/**
	 * 商品采购目标.
	 * 
	 * @author 罗佳欣
	 */
	@Goal
	public static class CommodityPurchaseGoal {

		private List<CommodityPo> commodityList;

		public CommodityPurchaseGoal(List<CommodityPo> commodityList) {
			this.commodityList = commodityList;
		}

		/**
		 * @return the commodityList
		 */
		public List<CommodityPo> getCommodityList() {
			return commodityList;
		}

		/**
		 * @param commodityList
		 *            the commodityList to set
		 */
		public void setCommodityList(List<CommodityPo> commodityList) {
			this.commodityList = commodityList;
		}

	}

	/**
	 * 商品采购规划.
	 * 
	 * @param goal
	 */
	@SuppressWarnings("unchecked")
	@Plan(trigger = @Trigger(goals = CommodityPurchaseGoal.class) )
	public void commodityPurchase(CommodityPurchaseGoal goal) {
		// 保存采购规划
		List<CommodityPo> commodityList = goal.getCommodityList();

		ResultMessage rs = storeService.updateCommodity(shopname, commodityList);

		if (rs.getServiceResult() == 1L) {
			// 保存成功
			catalog.clear();
			catalog.addAll((List<CommodityPo>) rs.getResultParm().get("commodityList"));
			setMoney((Double) rs.getResultParm().get("money"));
		} else {
			// 保存失败
			throw new RuntimeException("采购规划失败");
		}
	}

	/**
	 * 采购目标.
	 * 
	 * @author 罗佳欣
	 */
	@Goal
	public static class PurchaseGoal {
	}

	/**
	 * 手动采购.
	 * 
	 * @param goal
	 */
	@SuppressWarnings("unchecked")
	@Plan(trigger = @Trigger(goals = PurchaseGoal.class) )
	public void purchase(PurchaseGoal goal) {
		ResultMessage rs = storeService.purchase(shopname);

		if (rs.getServiceResult() == 1L) {
			// 保存成功
			catalog.clear();
			catalog.addAll((List<CommodityPo>) rs.getResultParm().get("commodityList"));
			setMoney((Double) rs.getResultParm().get("money"));
		} else if (rs.getServiceResult() == 2L) {
			// 金钱不足
			throw new RuntimeException("金钱不足");
		} else {
			// 保存失败
			throw new RuntimeException("采购失败");
		}
	}

}
