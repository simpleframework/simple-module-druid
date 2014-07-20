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
		return new Module().setName(MODULE_NAME).setText("Druid").setOrder(36)
				.setDefaultFunction(FUNC_DATASOURCE_MONITOR);
	}

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions.of(FUNC_DATASOURCE_MONITOR);
	}

	public static final WebModuleFunction FUNC_DATASOURCE_MONITOR = (WebModuleFunction) new WebModuleFunction(
			DataSourceMonitorPage.class).setName(MODULE_NAME + "-DataSourceMonitorPage").setText(
			$m("DruidWebContext.0"));
}
