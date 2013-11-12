package net.simpleframework.module.druid.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.AbstractModuleContext;
import net.simpleframework.ctx.Module;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.module.druid.web.page.t1.DataSourceMonitorPage;
import net.simpleframework.mvc.ctx.WebModuleFunction;

public class DruidWebContext extends AbstractModuleContext implements IDruidContext {

	@Override
	protected Module createModule() {
		return new Module()
				.setName("simple-module-druid")
				.setText("Druid")
				.setDefaultFunction(
						new WebModuleFunction(DataSourceMonitorPage.class).setName(
								"simple-module-druid-DataSourceMonitorPage").setText(
								$m("DruidWebContext.0"))).setOrder(36);
	}

	@Override
	public String getManagerRole() {
		return IPermissionConst.ROLE_MANAGER;
	}
}
