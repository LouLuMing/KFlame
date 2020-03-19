package com.china.fortune.struct;

import java.util.ArrayList;

@SuppressWarnings("serial")
public abstract class SortArrayList<E> extends ArrayList<E> {
	abstract protected int compare(E o1, E o2);

	abstract protected boolean equals(E o1, E o2);

	private int iMaxArrayListSize = 100;

	public SortArrayList(int iMax) {
		iMaxArrayListSize = iMax;
	}

	public void addAndSort(E v) {
		int iSize = size();
		if (iSize > 0) {
			E max = get(0);
			E min = get(iSize - 1);
			if (compare(v, min) <= 0) {
				if (iSize < iMaxArrayListSize) {
					add(v);
				}
			} else if (compare(v, max) >= 0) {
				add(0, v);
			} else {
				int iFound = findPosition(0, iSize - 1, v);
				iFound++;
				if (iFound < iSize) {
					add(iFound, v);
				} else {
					add(v);
				}
			}
		} else {
			add(v);
		}
		if (size() > iMaxArrayListSize) {
			remove(size() - 1);
		}
	}

	public int findPosition(E v) {
		return findPosition(0, size() - 1, v);
	}

	private int findPositionOrNot(int iStart, int iEnd, E v) {
		if (iEnd > iStart + 1) {
			int iMid = (iStart + iEnd) / 2;
			int iCompare = compare(v, get(iMid));
			if (iCompare > 0) {
				return findPositionOrNot(iStart, iMid - 1, v);
			} else if (iCompare < 0) {
				return findPositionOrNot(iMid + 1, iEnd, v);
			} else {
				return iMid;
			}
		} else {
			if (compare(get(iEnd), v) == 0) {
				return iEnd;
			} else if (compare(get(iStart), v) == 0) {
				return iStart;
			} else {
				return -1;
			}
		}
	}

	private int findPosition(int iStart, int iEnd, E v) {
		if (iEnd > iStart + 1) {
			int iMid = (iStart + iEnd) / 2;
			int iCompare = compare(v, get(iMid));
			if (iCompare > 0) {
				return findPosition(iStart, iMid, v);
			} else if (iCompare < 0) {
				return findPosition(iMid, iEnd, v);
			} else {
				return iMid;
			}
		} else {
			if (compare(v, get(iEnd)) <= 0) {
				return iEnd;
			} else {
				return iStart;
			}
		}
	}

	public E findAndDel(E item) {
		E v = null;
		int iIndex;
		int iSize = size();
		if (iSize > 0) {
			E min = get(iSize - 1);
			E max = get(0);
			if (compare(item, min) >= 0 && compare(item, max) <= 0) {
				int iFound = findPositionOrNot(0, iSize - 1, item);
				if (iFound >= 0) {
					iIndex = iFound;
					while (iIndex < iSize) {
						E found = get(iIndex);
						if (compare(item, found) == 0) {
							if (equals(item, found)) {
								v = remove(iIndex);
								break;
							}
						} else {
							break;
						}
						iIndex++;
					}
					if (v == null) {
						iIndex = iFound - 1;
						while (iIndex >= 0) {
							E found = get(iIndex);
							if (compare(item, found) == 0) {
								if (equals(item, found)) {
									v = remove(iIndex);
									break;
								}
							} else {
								break;
							}
							iIndex--;
						}
					}
				}
			}
		}
		return v;
	}
}
