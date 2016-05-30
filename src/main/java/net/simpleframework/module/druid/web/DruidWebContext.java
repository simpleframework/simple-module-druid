package net.simpleframework.module.druid.web;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.ctx.AbstractModuleContext;
import net.simpleframework.ctx.Module;
import net.simpleframework.ctx.ModuleFunctions;
import net.simpleframework.module.druid.web.page.t1.DataSourceMonitorPage;
import net.simpleframework.mvc.ctx.WebModuleFunction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DruidWebContext extends AbstractModuleContext implements IDruidContext {

	@Override
	protected Module createModule() {
		return super.createModule().setName(MODULE_NAME).setText("Druid").setOrder(36);
	}

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions
				.of((WebModuleFunction) new WebModuleFunction(this, DataSourceMonitorPage.class)
						.setName(MODULE_NAME + "-DataSourceMonitorPage")
						.setText($m("DruidWebContext.0")));
	}
}
