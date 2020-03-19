package com.china.fortune.verifyCode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.china.fortune.data.CacheClass;
import com.china.fortune.file.ReadFileAction;
import com.china.fortune.file.WriteFileAction;
import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.timecontrol.TimeoutMapActionThreadSafe;

public class VerifyCodeCreater extends CacheClass {
	private TimeoutMapActionThreadSafe<String, String> tmats = new TimeoutMapActionThreadSafe<String, String>(2, 17) {
		@Override
		public void onTimeout(ConcurrentHashMap<String, String> map) {
			map.clear();
		}
	};

	public VerifyCodeCreater() {
		loadData();
	}

	public String createVerifyCode(String sPhone) {
		String sCode = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
		tmats.add(sPhone, sCode);
		return sCode;
	}

	public String getVerifyCode(String sPhone) {
		tmats.checkTimeout();
		return tmats.get(sPhone);
	}

	public void removeVerifyCode(String sPhone) {
		tmats.remove(sPhone);
	}

    @Override
    protected void onSave(WriteFileAction wfa) {
		HashMap<String, String> allData = tmats.getAll();
		wfa.writeInt(allData.size());
		for (Map.Entry<String, String> en : allData.entrySet()) {
			wfa.writeString(en.getKey());
			wfa.writeString(en.getValue());
		}
    }

    @Override
    protected void onLoad(ReadFileAction rfa) {
		int iSize = rfa.readInt();
		for (int i = 0; i < iSize; i++) {
			String sKey = rfa.readString();
			String sValue = rfa.readString();
			tmats.add(sKey, sValue);
		}
    }

	static public void main(String[] args) {
		String sPhone = "18258448718";
		VerifyCodeCreater obj = new VerifyCodeCreater();
		obj.createVerifyCode(sPhone);
		while (true) {
			Log.log(obj.getVerifyCode(sPhone));
			ThreadUtils.sleep(1000);
		}
	}
}
