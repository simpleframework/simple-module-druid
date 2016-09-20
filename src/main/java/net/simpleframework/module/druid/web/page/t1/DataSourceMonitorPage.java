package net.simpleframework.module.druid.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;

import com.alibaba.druid.stat.JdbcStatManager;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.NumberUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.web.html.HtmlConst;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentHandlerException;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.IGroupTablePagerHandler;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.struct.NavigationButtons;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/druid/datasource")
public class DataSourceMonitorPage extends AbstractMonitorPage {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp,
				"DataSourceMonitorPage_tbl", TablePagerBean.class).setGroupColumn("g")
						.setShowVerticalLine(true).setShowCheckbox(false).setScrollHead(false)
						.setShowLineNo(true).setPageItems(100).setPagerBarLayout(EPagerBarLayout.none)
						.setContainerId("idDataSourceMonitorPage_tbl")
						.setHandlerClass(DataSourceMonitorTable.class);

		tablePager
				.addColumn(new TablePagerColumn("key").setColumnText($m("DataSourceMonitorPage.0"))
						.setNowrap(false).setWidth(300))
				.addColumn(new TablePagerColumn("val").setColumnText($m("DataSourceMonitorPage.1"))
						.setNowrap(false))
				.addColumn(new TablePagerColumn("g").setVisible(false));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div align='center' class='DataSourceMonitorPage'>");
		sb.append("  <div id='idDataSourceMonitorPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	public static class DataSourceMonitorTable extends AbstractTablePagerHandler
			implements IGroupTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final CompositeData val;
			try {
				val = (CompositeData) JdbcStatManager.getInstance().getDataSourceList().values()
						.iterator().next();
			} catch (final JMException e) {
				throw ComponentHandlerException.of(e);
			}

			final ArrayList<Map<?, ?>> data = new ArrayList<>();

			for (final String g : new String[] { "DataSource.Base", "DataSource.Raw",
					"DataSource.Connection", "DataSource.Statement", "DataSource.ResultSet" }) {
				for (String k : StringUtils.split(properties.getProperty(g))) {
					k = k.trim();
					final Map<String, Object> row = new HashMap<>();
					row.put("g", g);
					row.put("key", k);
					row.put("val", val.get(k));
					data.add(row);
				}
			}
			return new ListDataQuery<>(data);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp,
				final Object dataObject) {
			@SuppressWarnings("unchecked")
			final KVMap kv = new KVMap().addAll((Map<String, Object>) dataObject);
			final String k = (String) kv.get("key");
			final StringBuilder sb = new StringBuilder();
			sb.append("<div class='k'>").append(k).append("</div>");
			final String desc = properties.getProperty("DataSource.Key." + k);
			if (StringUtils.hasText(desc)) {
				sb.append("<div class='desc'>").append(desc).append("</div>");
			}
			kv.put("key", sb.toString());

			sb.setLength(0);
			Object v = kv.get("val");
			if (v instanceof Date) {
				v = Convert.toDateString((Date) v, "yyyy-MM-dd HH:mm:ss");
			} else if (k.contains("Millis") && v instanceof Long) {
				final double l = ((Long) v).doubleValue() / 1000.0;
				sb.append(NumberUtils.format(l)).append("s");
				if (l > 60.0) {
					sb.append(HtmlConst.NBSP).append("(").append(NumberUtils.format(l / 60.0))
							.append("m)");
				}
				v = sb.toString();
			} else if ("ConnectionHistogram".equals(k)) {
				final long[] l = (long[]) v;
				if (l.length > 0) {
					sb.append("<table>");
					sb.append("<tr><td class='h'>0-1s:</td><td>").append(l[0]).append("</td></tr>");
					sb.append("<tr><td class='h'>1s-5s:</td><td>").append(l[1]).append("</td></tr>");
					sb.append("<tr><td class='h'>5s-15s:</td><td>").append(l[2]).append("</td></tr>");
					sb.append("<tr><td class='h'>15s-60s:</td><td>").append(l[3]).append("</td></tr>");
					sb.append("<tr><td class='h'>60s-5m:</td><td>").append(l[4]).append("</td></tr>");
					sb.append("<tr><td class='h'>5m-30m:</td><td>").append(l[5]).append("</td></tr>");
					sb.append("<tr><td class='h'>&gt30m:</td><td>").append(l[6]).append("</td></tr>");
					sb.append("</table>");
				}
				v = sb.toString();
			} else if ("StatementHistogram".equals(k)) {
				final long[] l = (long[]) v;
				if (l.length > 0) {
					sb.append("<table>");
					sb.append("<tr><td class='h'>0-10ms:</td><td>").append(l[0]).append("</td></tr>");
					sb.append("<tr><td class='h'>10ms-100ms:</td><td>").append(l[1])
							.append("</td></tr>");
					sb.append("<tr><td class='h'>100ms-1s:</td><td>").append(l[2]).append("</td></tr>");
					sb.append("<tr><td class='h'>1s-10s:</td><td>").append(l[3]).append("</td></tr>");
					sb.append("<tr><td class='h'>&gt10s:</td><td>").append(l[4]).append("</td></tr>");
					sb.append("</table>");
				}
				v = sb.toString();
			}
			kv.put("val", v);
			return kv;
		}
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement($m("AbstractMonitorPage.0")));
	}
}
