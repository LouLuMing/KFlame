package com.china.fortune.json;

import com.china.fortune.statemachine.PathInterface;
import com.china.fortune.statemachine.StateAction;
import com.china.fortune.statemachine.StateMachine;

public class JSONStateMachine {
	static private StateMachine jsonSM = new StateMachine();
	static private StateMachine jarrSM = new StateMachine();

	static {
		class GetChar extends StateAction {
			protected boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.goAndFilter();
			}
		};

		jsonSM.addState("StartObject", new StateAction() {
			protected boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				char c = ji.getAndFilter();
				if (c == '{') {
					if (ji.goAndFilter()) {
						JSONObject json = new JSONObject();
						ji.addChild(json);
						return true;
					}
				}
				return false;
			}
		});

		jsonSM.addState("GetKey", new GetChar());
		jsonSM.addState("SaveKey", new StateAction() {
			protected boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.saveKey();
				char c = ji.getAndFilter();
				if (c == ':') {
					return ji.goAndFilter();
				} else {
					return false;
				}
			}
		});

		jsonSM.addState("GetString", new GetChar());
		jsonSM.addState("Loop", new GetChar());
		jsonSM.addState("GetOther", new GetChar());
		jsonSM.addState("EndObject", new StateAction() {
			protected boolean onAction(Object owner) {
				return true;
			}
		});
		jsonSM.addState("JSONObject", jsonSM);
		PathInterface pifNotNull = new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.getChar() != 0;
			}
		};

		PathInterface pifNotNullAndFindSlash = new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.getCharAndSetQuote() != 0;
			}
		};

		jsonSM.addState("JSONArray", jarrSM);

		PathInterface pifIsDoubleQuotes = new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.isDoubleQuotes();
			}
		};

		PathInterface pifIsDoubleQuotesAndSetStart = new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.setSubStringStart(1);
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.getChar() == '"';
			}
		};

		PathInterface pifIsDoubleQuotesAndSaveKeyAndValue = new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.saveStringValue();
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				// return ji.getChar() == '"';
				return ji.isDoubleQuotes();
			}
		};

		jsonSM.addPath("StartObject", "GetKey", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.setSubStringStart(1);
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.getChar() == '"';
			}
		});

		jsonSM.addPath("GetKey", "SaveKey", pifIsDoubleQuotes);
		jsonSM.addPath("GetKey", "GetKey", pifNotNull);
		jsonSM.addPath("SaveKey", "GetString", pifIsDoubleQuotesAndSetStart);

		jsonSM.addPath("SaveKey", "JSONObject", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.backChar();
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.getChar() == '{';
			}
		});

		jsonSM.addPath("SaveKey", "JSONArray", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.backChar();
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.getChar() == '[';
			}
		});

		PathInterface pifToOtherAndSetStart = new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.setSubStringStart(0);
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				return true;
			}
		};

		jsonSM.addPath("SaveKey", "GetOther", pifToOtherAndSetStart);

		jsonSM.addPath("GetString", "Loop", pifIsDoubleQuotesAndSaveKeyAndValue);
		jsonSM.addPath("GetString", "GetString", pifNotNullAndFindSlash);

		jsonSM.addPath("JSONObject", "Loop");
		jsonSM.addPath("JSONArray", "Loop");

		jsonSM.addPath("GetOther", "Loop", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.saveOtherValue();
				ji.backChar();
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				char c = ji.getChar();
				return c == ',' || c == '}';
			}
		});
		jsonSM.addPath("GetOther", "GetOther", pifNotNull);

		PathInterface pifEndObject = new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.popJSON();
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.getChar() == '}';
			}
		};
		jsonSM.addPath("StartObject", "EndObject", pifEndObject);
		jsonSM.addPath("Loop", "EndObject", pifEndObject);
		jsonSM.addPath("Loop", "GetKey", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				if (ji.getChar() == ',') {
					if (ji.getAndFilter() == '"') {
						ji.setSubStringStart(1);
						return true;
					}
				}
				return false;
			}
		});

		jsonSM.setStart("StartObject");
		jsonSM.setStop("EndObject");

		jarrSM.addState("StartArray", new StateAction() {
			protected boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				char c = ji.getAndFilter();
				if (c == '[') {
					JSONArray jarr = new JSONArray();
					ji.addChild(jarr);
					return true;
				}
				return false;
			}
		});

		jarrSM.addState("EndArray", new StateAction() {
			protected boolean onAction(Object owner) {
				return true;
			}
		});
		jarrSM.addState("GetChar", new GetChar());
		jarrSM.addState("JSONObject", jsonSM);
		jarrSM.addState("JSONArray", jarrSM);
		jarrSM.addState("GetString", new GetChar());
		jarrSM.addState("GetOther", new GetChar());
		jarrSM.addState("AddChild", new GetChar());

		jarrSM.addPath("StartArray", "GetChar");
		jarrSM.addPath("GetChar", "JSONObject", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				if (ji.getChar() == '{') {
					ji.backChar();
					return true;
				}
				return false;
			}
		});
		jarrSM.addPath("GetChar", "JSONArray", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				if (ji.getChar() == '[') {
					ji.backChar();
					return true;
				}
				return false;
			}
		});
		jarrSM.addPath("GetChar", "AddChild", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				if (ji.getChar() == ']') {
					ji.backChar();
					return true;
				}
				return false;
			}
		});
		jarrSM.addPath("GetChar", "GetString", pifIsDoubleQuotesAndSetStart);
		jarrSM.addPath("GetChar", "GetOther", pifToOtherAndSetStart);

		jarrSM.addPath("JSONObject", "AddChild");
		jarrSM.addPath("JSONArray", "AddChild");
		jarrSM.addPath("GetString", "AddChild", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.saveArrayString();
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				return ji.isDoubleQuotes();
			}
		});

		jarrSM.addPath("GetString", "GetString", pifNotNullAndFindSlash);
		jarrSM.addPath("GetOther", "AddChild", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.saveArrayOther();
				ji.backChar();
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				char c = ji.getChar();
				return c == ',' || c == ']';
			}
		});
		jarrSM.addPath("GetOther", "GetOther", pifNotNull);
		jarrSM.addPath("AddChild", "EndArray", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				ji.popJSON();
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				if (ji.getChar() == ']') {
					return true;
				}
				return false;
			}
		});
		jarrSM.addPath("AddChild", "GetChar", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				return true;
			}

			@Override
			public boolean onCondition(Object owner) {
				JSONDataCenter ji = (JSONDataCenter) owner;
				if (ji.getChar() == ',') {
					return true;
				}
				return false;
			}
		});

		jarrSM.setStart("StartArray");
		jarrSM.setStop("EndArray");
	}

	public Object parseJSONObject(String sJson) {
		JSONDataCenter jsonDC = new JSONDataCenter(sJson);
		if (jsonSM.doAction(jsonDC)) {
			return jsonDC.getRootObject();
		}
		return null;
	}

	public void parseJSONObject(String sJson, Object o) {
		JSONDataCenter jsonDC = new JSONDataCenter(sJson, o);
		jsonSM.doAction(jsonDC);
	}

	public Object parseJSONArray(String sJson) {
		JSONDataCenter jsonDC = new JSONDataCenter(sJson);
		if (jarrSM.doAction(jsonDC)) {
			return jsonDC.getRootObject();
		}
		return null;
	}

	public void parseJSONArray(String sJson, Object o) {
		JSONDataCenter jsonDC = new JSONDataCenter(sJson, o);
		jarrSM.doAction(jsonDC);
	}
}
