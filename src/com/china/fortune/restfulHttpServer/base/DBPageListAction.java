package com.china.fortune.restfulHttpServer.base;

import com.china.fortune.database.DatabaseJson;
import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.mySql.MySqlLimit;
import com.china.fortune.database.sql.OrderBy;
import com.china.fortune.database.sql.WhereSql;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.string.StringUtils;

public class DBPageListAction extends RestfulStringServlet {
	protected String sSelect = null;
	private String sSelectCount = null;
	protected int iPageSize = 10;
	protected String[] lsFields = { "pageNo" };

	protected void appendWhere(String[] lsValues, WhereSql ws, OrderBy ob) {
	};

	public DBPageListAction(String select) {
		sSelect = select;
		sSelectCount = createSelectCout();
		ksUnKey.append(lsFields);
	}

	protected JSONArray getList(JSONObject json) {
		JSONObject data = json.optJSONObject("data");
		if (data != null) {
			return data.optJSONArray("list");
		}
		return null;
	}

	protected void addEmptyList(JSONObject json) {
		JSONArray jarr = new JSONArray();
		JSONObject data = new JSONObject();
		data.put("list", jarr);
		ResultJson.fillData(json, 0, "ok", data);
	}

	private String createSelectCout() {
		int index = sSelect.toLowerCase().indexOf(" from ");
		if (index > 0) {
			int iLen = sSelect.length();
			return "select count(1)" + sSelect.substring(index, iLen);
		} else {
			return null;
		}
	}

    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, String[] lsValues) {
        MySqlDbAction dbObj = (MySqlDbAction)objForThread;
		OrderBy ob = new OrderBy();

		WhereSql ws = new WhereSql();
		appendWhere(lsValues, ws, ob);

		StringBuilder sb = new StringBuilder();
		sb.append(sSelect);
		sb.append(ws.toSql());
		if (ob.size() > 0) {
			sb.append(ob.toSql());
		}
		String pageNo = getString(lsValues, "pageNo");
		if (StringUtils.length(pageNo) > 0) {
			sb.append(MySqlLimit.toSql(StringUtils.toInteger(pageNo) * iPageSize, iPageSize));
		} else {
			sb.append(MySqlLimit.toSql(iPageSize * 10));
		}

		String sSql = sb.toString();
		if (StringUtils.length(sSql) > 0) {
			JSONArray jarr = DatabaseJson.toJSONArray(dbObj, sSql);
			JSONObject data = new JSONObject();
			data.put("list", jarr);
			if (sSelectCount != null) {
                data.put("total", dbObj.selectInt(sSelectCount + ws.toSql()));
            }
			ResultJson.fillData(json, 0, "ok", data);
		}
		return RunStatus.isOK;
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
        ResultJson.fillError(json, sKey + " is miss");
	}
}
