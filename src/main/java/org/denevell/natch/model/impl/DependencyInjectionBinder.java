package org.denevell.natch.model.impl;

import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.model.interfaces.PostFindModel;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class DependencyInjectionBinder extends AbstractBinder {
	@Override
	protected void configure() {
		bind(PostAddModelImpl.class).to(PostAddModel.class);
		bind(PostFindModelImpl.class).to(PostFindModel.class);
	}
}