package com.china.fortune.json;

import com.china.fortune.string.StringAction;

public class JSONBase {
	public Object copy(Object o) {
		if (o instanceof JSONObject) {
			JSONObject json = new JSONObject();
			json.copy((JSONObject) o);
			return json;
		} else if (o instanceof JSONArray) {
			JSONArray jarr = new JSONArray();
			jarr.copy((JSONArray) o);
			return jarr;
		} else {
			return o;
		}
	}

	protected int optInt(Object o) {
		if (o != null) {
			Class<?> cls = o.getClass();
			if (cls == char[].class) {
				return StringAction.toInteger((char[]) o);
			} else if (cls == Integer.class || cls == int.class) {
				return (int) o;
			} else if (cls == String.class) {
				return StringAction.toInteger((String) o);
			} else if (cls == Long.class || cls == long.class) {
				return (int) ((long) o);
			}
		}
		return 0;
	}

	protected long optLong(Object o) {
		if (o != null) {
			Class<?> cls = o.getClass();
			if (cls == char[].class) {
				return StringAction.toLong((char[]) o);
			} else if (cls == Long.class || cls == long.class) {
				return (Long) o;
			} else if (cls == String.class) {
				return StringAction.toInteger((String) o);
			} else if (cls == Integer.class || cls == int.class) {
				return (long) ((int) o);
			}
		}
		return 0;
	}

	protected String optString(Object o) {
		if (o != null) {
			Class<?> cls = o.getClass();
			if (cls == String.class) {
				return (String) o;
			} else if (cls == char[].class) {
				return new String((char[]) o);
			} else if (cls == int.class || cls == Integer.class || cls == long.class || cls == Long.class) {
				return String.valueOf(o);
			} else {
				return o.toString();
			}
		}
		return null;
	}

	protected JSONObject optJSONObject(Object o) {
		if (o != null) {
			if (o.getClass() == JSONObject.class) {
				return (JSONObject) o;
			}
		}
		return null;
	}

	protected JSONArray optJSONArray(Object o) {
		if (o != null) {
			if (o.getClass() == JSONArray.class) {
				return (JSONArray) o;
			}
		}
		return null;
	}

	protected void toString(StringBuilder sb, Object o) {
		Class<?> cls = o.getClass();
		if (cls == String.class) {
			sb.append('"');
			// sb.append((String) o);
			JSONString.quote(sb, (String) o);
			sb.append('"');
			sb.append(',');
		} else if (cls == Integer.class || cls == int.class) {
			sb.append((int) o);
			sb.append(',');
		} else if (cls == Long.class || cls == long.class) {
			sb.append((long) o);
			sb.append(',');
		} else if (cls == Boolean.class) {
			if (o == Boolean.TRUE) {
				sb.append("true");
			} else {
				sb.append("false");
			}
			sb.append(',');
		} else if (cls == JSONObject.class) {
			((JSONObject) o).toString(sb);
			sb.append(',');
		} else if (cls == JSONArray.class) {
			((JSONArray) o).toString(sb);
			sb.append(',');
		} else if (cls == char[].class) {
			sb.append((char[]) o);
			sb.append(',');
		} else {
			sb.append(o);
			sb.append(',');
		}
	}

}
