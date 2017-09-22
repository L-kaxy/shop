package jadex.bdiv3.examples.shop.agent.customer;

import jadex.bdiv3.examples.shop.agent.customer.CustomerCapability.RefreshGoal;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ICapability;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 顾客服务.
 */
@Service
public class CustService implements ICustService {
	// -------- attributes --------

	@ServiceComponent
	protected ICapability capa;

	/**
	 * 顾客名.
	 */
	protected String name;

	// -------- constructors --------

	public CustService(String name) {
		this.name = name;
	}

	// -------- methods --------

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

	/**
	 * 刷新商品列表.
	 */
	@Override
	public IFuture<String> refresh() {
		final Future<String> ret = new Future<>();
		RefreshGoal sell = new RefreshGoal();
		capa.getAgent().getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(sell);
		ret.setResult("refresh success");
		return ret;
	}

}
