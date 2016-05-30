package net.simpleframework.module.druid.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.ArrayList;
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
import net.simpleframework.common.NumberUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
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
@PageMapping(url = "/druid/sql")
public class SqlMonitorPage extends AbstractMonitorPage {

	private static final String DATE_FORMAT = "MM-dd HH:mm:ss";

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp, "SqlMonitorPage_tbl",
				TablePagerBean.class).setDetailField("sqlDetail").setHeadHeight(54)
						.setShowCheckbox(false).setShowLineNo(true)
						.setPagerBarLayout(EPagerBarLayout.bottom).setContainerId("idSqlMonitorPage_tbl")
						.setHandlerClass(SqlMonitorTable.class);

		tablePager.addColumn(new TablePagerColumn("ExecuteCount", "执行次数", 70))
				.addColumn(new TablePagerColumn("FetchRowCount", "读取行数", 70))
				.addColumn(new TablePagerColumn("EffectedRowCount", "影响行数", 70))
				.addColumn(new TablePagerColumn("RunningCount", "正在执\n行次数", 70))
				.addColumn(new TablePagerColumn("TotalTime", "总共时间", 70))
				.addColumn(new TablePagerColumn("MaxTimespan", "最慢消\n耗时间", 70).setFormat("#ms"))
				.addColumn(
						TablePagerColumn.DATE("MaxTimespanOccurTime", "最慢发生时间").setFormat(DATE_FORMAT))
				.addColumn(new TablePagerColumn("ConcurrentMax", "最大并\n发数量 ", 70))
				.addColumn(new TablePagerColumn("ErrorCount", "错误次数", 70))
				.addColumn(new TablePagerColumn("BatchSizeMax", "最大\nBatch", 70))
				.addColumn(new TablePagerColumn("BatchSizeTotal", "所有\nBatch", 70))
				.addColumn(new TablePagerColumn("ResultSetHoldTime", "ResultSet\n持有时间", 100)
						.setFormat("#ms"))
				.addColumn(
						new TablePagerColumn("ExecuteAndResultSetHoldTime", "ResultSet\n执行及持有时间", 110)
								.setFormat("#ms"))
				.addColumn(new TablePagerColumn("InTransactionCount", "事务中\n运行数", 70))
				.addColumn(TablePagerColumn.DATE("LastTime", "最后执行时间").setFormat(DATE_FORMAT))
				.addColumn(TablePagerColumn.DATE("LastErrorTime", "最后错误时间").setFormat(DATE_FORMAT))
				.addColumn(TablePagerColumn.BLANK());

		// for (final TablePagerColumn c : tablePager.getColumns()) {
		// c.setTooltip(c.getColumnName());
		// }
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div align='center' class='SqlMonitorPage'>");
		sb.append("  <div id='idSqlMonitorPage_tbl'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	public static class SqlMonitorTable extends AbstractTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			TabularData sqls;
			try {
				sqls = JdbcStatManager.getInstance().getSqlList();
			} catch (final JMException e) {
				throw ComponentHandlerException.of(e);
			}

			// EffectedRowCountHistogram, ,
			// ExecuteAndResultHoldTimeHistogram, ,
			// FetchRowCountHistogram, ,
			// Histogram, ID
			// LastSlowParameters,
			final ArrayList<Map<?, ?>> data = new ArrayList<Map<?, ?>>();
			final Iterator<?> it = sqls.values().iterator();
			while (it.hasNext()) {
				final CompositeData val = (CompositeData) it.next();
				final Map<String, Object> row = new HashMap<String, Object>();

				for (final String k : new String[] { "SQL", "ExecuteCount", "FetchRowCount",
						"EffectedRowCount", "RunningCount", "TotalTime", "ErrorCount", "ConcurrentMax",
						"MaxTimespan", "MaxTimespanOccurTime", "BatchSizeMax", "BatchSizeTotal",
						"ResultSetHoldTime", "ExecuteAndResultSetHoldTime", "InTransactionCount",
						"LastTime", "LastError", "LastErrorTime" }) {
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

			final double l = ((Long) BeanUtils.getProperty(dataObject, "TotalTime")).doubleValue()
					/ 1000.0;
			kv.put("TotalTime", NumberUtils.format(l) + "s");

			final StringBuilder sb = new StringBuilder();
			final String sql = (String) BeanUtils.getProperty(dataObject, "SQL");
			if (StringUtils.hasText(sql)) {
				sb.append("<tr><td class='l'>SQL</td>");
				sb.append("<td class='v'>").append(sql).append("</td></tr>");
			}
			final CompositeData lastError = (CompositeData) kv.get("LastError");
			if (lastError != null) {
				sb.append("<tr><td class='l'>").append("最后一次错误栈");
				sb.append("</td><td class='v'>").append(lastError.get("stackTrace"));
				sb.append("</td></tr>");
			}
			if (sb.length() > 0) {
				sb.insert(0, "<table class='form_tbl'>");
				sb.append("</table>");
				kv.put("sqlDetail", sb.toString());
			}
			return kv;
		}
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement($m("AbstractMonitorPage.2")));
	}
}
