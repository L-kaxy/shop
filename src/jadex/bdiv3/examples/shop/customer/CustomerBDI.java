package jadex.bdiv3.examples.shop.customer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

/**
 * Customer capability.
 */
@Agent
@Arguments({
		@Argument(name = "customername", clazz = String.class, defaultvalue = "jadex.commons.SUtil.createUniqueId(\"Customer\",2)") })
public class CustomerBDI {
	// -------- attributes --------
	/** The agent. */
	@Agent
	@SuppressFBWarnings(value = "UR_UNINIT_READ", justification = "Agent field injected by interpreter")
	protected IInternalAccess agent;

	/** The customer capability. */
	@Capability(beliefmapping = @Mapping("money") )
	protected CustomerCapability cap = new CustomerCapability(
			(String) agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("customername"));

	/** The money. */
	@Belief
	protected double money = 100;
}
