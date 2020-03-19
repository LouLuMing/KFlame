package com.china.fortune.data;

import java.io.File;

import com.china.fortune.file.ReadFileAction;
import com.china.fortune.file.WriteFileAction;
import com.china.fortune.os.file.PathUtils;

public abstract class CacheClass {
	static private String sPath = PathUtils.getCurrentDataPath(true) + "ramData" + File.separator;

	protected abstract void onSave(WriteFileAction wfa);
	protected abstract void onLoad(ReadFileAction rfa);

	static {
		PathUtils.create(sPath);
	}

	private String sTag = null;

	public void setTag(String s) {
		sTag = s;
	}

	static public void setPath(String path) {
		sPath = PathUtils.addSeparator(path);
		PathUtils.create(sPath);
	}

	public void saveData() {
		if (sTag != null) {
			saveDataRaw(this.getClass().getSimpleName() + "." + sTag);
		} else {
			saveDataRaw(this.getClass().getSimpleName());
		}
	}

	public void loadData() {
		if (sTag != null) {
			loadDataRaw(this.getClass().getSimpleName() + "." + sTag);
		} else {
			loadDataRaw(this.getClass().getSimpleName());
		}
	}

	public void saveData(Object o) {
		if (sTag != null) {
			saveDataRaw(o.getClass().getSimpleName() + "." + sTag);
		} else {
			saveDataRaw(o.getClass().getSimpleName());
		}
	}

	public void loadData(Object o) {
		if (sTag != null) {
			loadDataRaw(o.getClass().getSimpleName() + "." + sTag);
		} else {
			loadDataRaw(o.getClass().getSimpleName());
		}
	}

	protected void saveDataRaw(String sFileName) {
		WriteFileAction wfa = new WriteFileAction();
		if (wfa.open(sPath + sFileName)) {
			onSave(wfa);
			wfa.close();
		}
	}

	protected void loadDataRaw(String sFileName) {
		ReadFileAction rfa = new ReadFileAction();
		if (rfa.open(sPath + sFileName)) {
			onLoad(rfa);
			rfa.close();
		}
	}
}
