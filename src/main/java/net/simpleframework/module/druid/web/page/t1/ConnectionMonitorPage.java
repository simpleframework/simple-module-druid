package net.simpleframework.module.druid.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import com.alibaba.druid.stat.JdbcStatManager;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ado.query.ListDataQuery;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.I18n;
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
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.template.struct.NavigationButtons;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/druid/connection")
public class ConnectionMonitorPage extends AbstractMonitorPage {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp,
				"ConnectionMonitorPage_tbl", TablePagerBean.class).setScrollHead(false)
						.setDetailField("connDetail").setShowCheckbox(false).setShowLineNo(true)
						.setPageItems(999).setPagerBarLayout(EPagerBarLayout.none)
						.setContainerId("idConnectionMonitorPage_tbl")
						.setHandlerClass(ConnectionMonitorTable.class);

		tablePager.addColumn(new TablePagerColumn("ID").setWidth(80))
				.addColumn(new TablePagerColumn("ConnectTime", $m("ConnectionMonitorPage.ConnectTime"))
						.setFormat(DATE_FORMAT).setPropertyClass(Date.class))
				.addColumn(new TablePagerColumn("ConnectTimespan",
						I18n.$m("ConnectionMonitorPage.ConnectTimespan")))
				.addColumn(new TablePagerColumn("EstablishTime",
						I18n.$m("ConnectionMonitorPage.EstablishTime")).setFormat(DATE_FORMAT)
								.setPropertyClass(Date.class))
				.addColumn(new TablePagerColumn("AliveTimespan",
						I18n.$m("ConnectionMonitorPage.AliveTimespan")))
				.addColumn(new TablePagerColumn("LastErrorTime",
						I18n.$m("ConnectionMonitorPage.LastErrorTime")).setFormat(DATE_FORMAT)
								.setPropertyClass(Date.class));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div align='center' class='ConnectionMonitorPage'>");
		sb.append("  <div id='idConnectionMonitorPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	public static class ConnectionMonitorTable extends AbstractTablePagerHandler {

		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			TabularData coll;
			try {
				coll = JdbcStatManager.getInstance().getConnectionList();
			} catch (final JMException e) {
				throw ComponentHandlerException.of(e);
			}

			final ArrayList<Map<?, ?>> data = new ArrayList<Map<?, ?>>();
			final Iterator<?> it = coll.values().iterator();
			while (it.hasNext()) {
				final Map<String, Object> row = new HashMap<String, Object>();
				final CompositeData val = (CompositeData) it.next();

				for (final String k : new String[] { "AliveTimespan", "ID", "ConnectStatckTrace",
						"ConnectTime", "ConnectTimespan", "EstablishTime", "LastError", "LastErrorTime",
						"LastSql", "LastStatementStackTrace" }) {
					row.put(k, val.get(k));
				}
				data.add(row);
			}
			return new ListDataQuery<Map<?, ?>>(data);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp,
				final Object dataObject) {
			@SuppressWarnings("unchecked")
			final KVMap kv = new KVMap().addAll((Map<String, Object>) dataObject);

			double l = ((Long) BeanUtils.getProperty(dataObject, "AliveTimespan")).doubleValue()
					/ 1000.0;
			String ts = NumberUtils.format(l) + "s";
			if (l > 60) {
				ts += HtmlConst.NBSP + "(" + NumberUtils.format(l / 60.0) + "m)";
			}
			kv.put("AliveTimespan", ts);

			l = ((Long) BeanUtils.getProperty(dataObject, "ConnectTimespan")).doubleValue() / 1000.0;
			kv.put("ConnectTimespan", NumberUtils.format(l) + "s");

			final StringBuilder sb = new StringBuilder();
			final String lastSql = (String) BeanUtils.getProperty(dataObject, "LastSql");
			if (StringUtils.hasText(lastSql)) {
				sb.append("<tr><td class='l'>").append($m("ConnectionMonitorPage.0"));
				sb.append("</td><td class='v'>");
				sb.append(lastSql).append("</td></tr>");
			}
			final CompositeData lastError = ((CompositeData) BeanUtils.getProperty(dataObject,
					"LastError"));
			if (lastError != null) {
				sb.append("<tr><td class='l'>").append($m("ConnectionMonitorPage.1"));
				sb.append("</td><td class='v'>").append(lastError.get("stackTrace"));
				sb.append("</td></tr>");
			}
			final Object lastStatementStackTrace = BeanUtils.getProperty(dataObject,
					"LastStatementStackTrace");
			if (lastStatementStackTrace != null) {
				sb.append("<tr><td class='l'>").append($m("ConnectionMonitorPage.2"));
				sb.append("</td><td class='v'>").append(lastStatementStackTrace);
				sb.append("</td></tr>");
			}
			if (sb.length() > 0) {
				sb.insert(0, "<table class='form_tbl'>");
				sb.append("</table>");
				kv.put("connDetail", sb.toString());
			}
			return kv;
		}
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement($m("AbstractMonitorPage.1")));
	}
}
