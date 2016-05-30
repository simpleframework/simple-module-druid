package net.simpleframework.module.druid.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Properties;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.ctx.InjectCtx;
import net.simpleframework.module.druid.web.IDruidContext;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractMonitorPage extends T1ResizedTemplatePage {

	@InjectCtx
	protected static IDruidContext context;

	protected static Properties properties;
	static {
		try {
			properties = new Properties();
			properties.load(ClassUtils.getResourceAsStream(DataSourceMonitorPage.class,
					"stat.properties"));
		} catch (final IOException e) {
		}
	}

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(AbstractMonitorPage.class, "/druid.css");
	}

	@Override
	public String getPageRole(final PageParameter pp) {
		return getPageManagerRole(pp);
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return TabButtons.of(new TabButton($m("AbstractMonitorPage.0"),
				url(DataSourceMonitorPage.class)), new TabButton($m("AbstractMonitorPage.1"),
				url(ConnectionMonitorPage.class)), new TabButton($m("AbstractMonitorPage.2"),
				url(SqlMonitorPage.class)));
	}
}
