package com.china.fortune.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;
import com.china.fortune.struct.IntObject;

public class CountMap {
	private HashMap<String, IntObject> mapCount = new HashMap<String, IntObject>();
	private int iTotal = 0;

	public int getSummary() {
		return iTotal;
	}

	public void add(String s, int c) {
		IntObject obj = mapCount.get(s);
		if (obj == null) {
			mapCount.put(s, new IntObject(c));
		} else {
			obj.add(c);
		}
		iTotal+=c;
	}

	public void add(String s) {
		IntObject obj = mapCount.get(s);
		if (obj == null) {
			mapCount.put(s, new IntObject(1));
		} else {
			obj.incrementAndGet();
		}
		iTotal++;
	}

	public void set(String s, int i) {
		IntObject obj = mapCount.get(s);
		if (obj == null) {
			mapCount.put(s, new IntObject(i));
			iTotal += i;
		} else {
			int iOld = obj.get();
			obj.set(i);
			iTotal += (i - iOld);
		}
	}

	public int get(String s) {
		IntObject obj = mapCount.get(s);
		if (obj != null) {
			return obj.get();
		}
		return 0;
	}

	public int size() {
		return mapCount.size();
	}

	public List<Entry<String, IntObject>> sortHashMap() {
		return sortHashMap(true);
	}

	public List<Entry<String, IntObject>> sortHashMap(final boolean bAsc) {
		List<Entry<String, IntObject>> lsNodes = new ArrayList<Entry<String, IntObject>>(mapCount.entrySet());
		if (lsNodes.size() > 0) {
			Collections.sort(lsNodes, new Comparator<Entry<String, IntObject>>() {
				public int compare(Entry<String, IntObject> o1, Entry<String, IntObject> o2) {
					if (bAsc) {
						return (o1.getValue().get() - o2.getValue().get());
					} else {
						return (o2.getValue().get() - o1.getValue().get());
					}
				}
			});
		}
		return lsNodes;
	}

	public void showSortLog() {
		showSortLogTail(true, 50);
	}

	private void showItem(Entry<String, IntObject> e) {
		int iCount = e.getValue().get();
		Log.log(e.getKey() + "\t" + String.valueOf(iCount) + "\t" + StringAction.toPercent(iCount, iTotal));
	}

	public void showSortLogHead(boolean bAsc, int iLimit) {
		int iSize = mapCount.size();
		if (iSize > 0) {
			List<Entry<String, IntObject>> lsNodes = sortHashMap(bAsc);
			int iLoop;
			if (iLimit > 0) {
				iLoop = Math.min(iLimit, lsNodes.size());
			} else {
				iLoop = lsNodes.size();
			}
			for (int i = 0; i < iLoop; i++) {
				Entry<String, IntObject> e = lsNodes.get(i);
				showItem(e);
			}
		}
		Log.log("Count:" + iSize + " Total:" + iTotal);
	}

	public void showSortLogTail(boolean bAsc, int iLimit) {
		int iSize = mapCount.size();
		if (iSize > 0) {
			List<Entry<String, IntObject>> lsNodes = sortHashMap(bAsc);
			int iStart = 0;
			if (iLimit > 0) {
				iStart = lsNodes.size() - iLimit;
				if (iStart < 0) {
					iStart = 0;
				}
			}

			for (int i = iStart; i < lsNodes.size(); i++) {
				Entry<String, IntObject> e = lsNodes.get(i);
				showItem(e);
			}
		}
		Log.log("Count:" + iSize + " Total:" + iTotal);
	}

	public void showSortLogLargeThan(boolean bAsc, int iMinCount) {
		int iSize = mapCount.size();
		int iFound = 0;
		if (iSize > 0) {
			List<Entry<String, IntObject>> lsNodes = sortHashMap(bAsc);
			for (int i = 0; i < lsNodes.size(); i++) {
				Entry<String, IntObject> e = lsNodes.get(i);
				int iCount = e.getValue().get();
				if (iCount > iMinCount) {
					iFound++;
					showItem(e);
				}
			}
		}
		Log.log("Count:" + iSize + " Total:" + iTotal + " Found:" + iFound);
	}

	public void showSortLogSmallThan(boolean bAsc, int iMaxCount) {
		int iSize = mapCount.size();
		int iFound = 0;
		if (iSize > 0) {
			List<Entry<String, IntObject>> lsNodes = sortHashMap(bAsc);
			for (int i = 0; i < lsNodes.size(); i++) {
				Entry<String, IntObject> e = lsNodes.get(i);
				int iCount = e.getValue().get();
				if (iCount < iMaxCount) {
					iFound++;
					showItem(e);
				}
			}
		}
		Log.log("Count:" + iSize + " Total:" + iTotal + " Found:" + iFound);
	}

	public List<Entry<String, IntObject>> sortHashMapByKey(final boolean bAsc) {
		List<Entry<String, IntObject>> lsNodes = new ArrayList<Entry<String, IntObject>>(mapCount.entrySet());
		if (lsNodes.size() > 0) {
			Collections.sort(lsNodes, new Comparator<Entry<String, IntObject>>() {
				public int compare(Entry<String, IntObject> o1, Entry<String, IntObject> o2) {
					if (bAsc) {
						return StringAction.compareTo(o1.getKey(), o2.getKey());
					} else {
						return StringAction.compareTo(o2.getKey(), o1.getKey());
					}
				}
			});
		}
		return lsNodes;
	}

	public void showSortKeyLogHead(boolean bAsc, int iLimit) {
		int iSize = mapCount.size();
		if (iSize > 0) {
			List<Entry<String, IntObject>> lsNodes = sortHashMapByKey(bAsc);
			int iLoop;
			if (iLimit > 0) {
				iLoop = Math.min(iLimit, lsNodes.size());
			} else {
				iLoop = lsNodes.size();
			}
			for (int i = 0; i < iLoop; i++) {
				Entry<String, IntObject> e = lsNodes.get(i);
				showItem(e);
			}
		}
		Log.log("Count:" + iSize + " Total:" + iTotal);
	}
}
