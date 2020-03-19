package com.china.fortune.http.webservice.servlet;

public abstract class ShellServlet implements ServletInterface {
    protected ServletInterface svHost;

    public ShellServlet(ServletInterface st) {
        svHost = st;
    }

    public ServletInterface getHost() {
        return svHost.getHost();
    };

}
