package jadex.bdiv3.examples.shop.customer;

import jadex.commons.future.IFuture;

public interface ICustService {

	public String getName();

	public IFuture<String> refresh();

}
