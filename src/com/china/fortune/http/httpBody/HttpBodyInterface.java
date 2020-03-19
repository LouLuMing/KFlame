package com.china.fortune.http.httpBody;

import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.socket.LineSocketAction;

public interface HttpBodyInterface {
	public abstract boolean onContentRecv(HttpHeader hh, LineSocketAction lSA);
}
