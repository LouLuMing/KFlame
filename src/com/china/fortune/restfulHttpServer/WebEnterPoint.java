package com.china.fortune.restfulHttpServer;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.mySql.MySqlManager;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassRraverse;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.restfulHttpServer.action.*;
import com.china.fortune.restfulHttpServer.annotation.AsComponent;
import com.china.fortune.restfulHttpServer.annotation.AsSchedule;
import com.china.fortune.restfulHttpServer.annotation.AsServlet;
import com.china.fortune.restfulHttpServer.base.IPAllowAction;
import com.china.fortune.restfulHttpServer.base.IPFrequentAction;
import com.china.fortune.restfulHttpServer.config.WebProp;
import com.china.fortune.restfulHttpServer.msgSystem.MsgActionInterface;
import com.china.fortune.restfulHttpServer.msgSystem.MsgInterface;
import com.china.fortune.restfulHttpServer.msgSystem.MsgSystem;
import com.china.fortune.restfulHttpServer.schedule.ScheduleAction;
import com.china.fortune.restfulHttpServer.schedule.ScheduleManager;
import com.china.fortune.xml.XmlNode;

import java.util.List;

public class WebEnterPoint extends WebServer implements DataSaveInterface, TargetInterface {
    protected MySqlManager mySqlManager = new MySqlManager();
    protected BeansFamily beansFamily = new BeansFamily();
    private ScheduleManager scheduleManager = new ScheduleManager() {
        @Override
        public void addMessage(ScheduleAction saObj) {
            msgServer.addObject(saObj);
        }
    };

    protected MsgSystem msgServer = new MsgSystem() {
        @Override
        protected Object onCreate() {
            return createObjectInThread();
        }

        @Override
        protected void onDestroy(Object objForThread) {
            destroyObjectInThread(objForThread);
        }

        @Override
        protected void onTimer(Object dbObj) {
            scheduleManager.doAction();
        }

        @Override
        public void addMsgSerlvet(MsgActionInterface mai, Class<?> cls) {
            beansFamily.injectFieldAutowired(mai);
            lsMsgAction.put(cls, mai);
        }
    };

    @Override
    protected void onMissResource(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
        hRes.setBody(ResultJson.sJsonNotFoundResource, "application/json");
    }

    protected void onException(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
        hRes.setBody(ResultJson.sJsonException, "application/json");
    }

    @Override
    protected Object createObjectInThread() {
        return mySqlManager.get();
    }

    @Override
    protected void destroyObjectInThread(Object objForThread) {
        mySqlManager.free((MySqlDbAction) objForThread);
    }

    protected void scanServlet(String packagePath) {
        List<String> lsData = ClassRraverse.getClassName(packagePath);
        for (String clsName : lsData) {
            addServlet(clsName);
        }
    }

    protected void scanSchedule(String packagePath) {
        List<String> lsData = ClassRraverse.getClassName(packagePath);
        for (String clsName : lsData) {
            addSchedule(clsName);
        }
    }

    protected void scanComponent(String packagePath) {
        List<String> lsData = ClassRraverse.getClassName(packagePath);
        for (String clsName : lsData) {
            addComponent(clsName);
        }
    }

    protected void addServlet(String clsName) {
        try {
            Class<?> cls = Class.forName(clsName);
            if (cls != null) {
                if (cls.isAnnotationPresent(AsServlet.class)) {
                    if (ServletInterface.class.isAssignableFrom(cls)) {
                        ServletInterface si = (ServletInterface)cls.newInstance();
                        AsServlet kf = cls.getAnnotation(AsServlet.class);
                        addServlet(si);
                        if (kf.ipFrequent()) {
                            addFilter(si, IPFrequentAction.class);
                        }
                        if (kf.ipAllow()) {
                            addFilter(si, IPAllowAction.class);
                        }
                    } else {
                        Log.logError(clsName + " is not instanceof ServletInterface");
                    }
                }
            }
        } catch (Error e) {
            Log.logException(e);
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    public void addComponent(String clsName) {
        try {
            Class<?> cls = Class.forName(clsName);
            if (cls != null) {
                if (cls.isAnnotationPresent(AsComponent.class)) {
                    Object obj = cls.newInstance();
                    beansFamily.put(clsName, obj);
                }
            }
        } catch (Error e) {
            Log.logException(e);
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    protected void addSchedule(String clsName) {
        try {
            Class<?> cls = Class.forName(clsName);
            if (cls != null) {
                if (cls.isAnnotationPresent(AsSchedule.class)) {
                    if (MsgInterface.class.isAssignableFrom(cls)) {
                        AsSchedule as = cls.getAnnotation(AsSchedule.class);
                        Object obj = cls.newInstance();
                        scheduleManager.addSchedule((MsgInterface) obj, as.cron());
                    } else {
                        Log.logError(clsName + " is not instanceof MsgInterface");
                    }
                }
            }
        } catch (Error e) {
            Log.logException(e);
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    protected void injectAutowired(BeansFamily bf) {
        for (int i = lsServlet.size() - 1; i >= 0; i--) {
            ServletInterface servlet = lsServlet.get(i).getHost();
            bf.injectFieldAutowired(servlet);
        }
    }

    @Override
    public void stop() {
        super.stop();
        mySqlManager.clear();
    }

    protected void addIPAllowServelt(ServletInterface si) {
        addServlet(si);
        addFilter(si, IPAllowAction.class);
    }

    protected void addIPFrequentServelt(ServletInterface si) {
        addServlet(si);
        addFilter(si, IPFrequentAction.class);
    }

    protected void addAllServlet(String sServlet) {
        addServlet(null, new IPAllowAction());
        addServlet(null, new IPFrequentAction());

        addServlet(new ShowHttpAction());
        addServlet(new AddAllowIPAction());
        addIPAllowServelt(new ShowStatisticsAction(this));
        addIPAllowServelt(new ResetStatisticsAction(this));
        addIPAllowServelt(new SaveToFileAction(this));

        addIPFrequentServelt(new InterfaceAction(this));

        if (sServlet != null) {
            scanServlet(sServlet);
        }

        initHitCache();
        addStatisticsServlet();
    }

    public boolean startWeb(int iPort, String sScanPath) {
        beansFamily.put(beansFamily);
        beansFamily.put(mySqlManager);
        beansFamily.put(msgServer);

        beansFamily.initHitCache();
        beansFamily.injectSelfAutowired();
        if (sScanPath != null) {
            scanComponent(sScanPath);
        }

        addAllServlet(sScanPath);
        injectAutowired(beansFamily);

        if (sScanPath != null) {
            scanSchedule(sScanPath);
        }
        scheduleManager.injectBeans(beansFamily);

        msgServer.setLoop(scheduleManager.getMiniLoop());
        msgServer.start(1);

        return startAndBlock(iPort);
    }

    public boolean initDatabase(String sServer, String sDBName, String sUser, String sPasswd) {
        return mySqlManager.init(sServer, sDBName, sUser, sPasswd);
    }

    public boolean doAction(XmlNode cfg, ProcessAction self) {
        WebProp wc = new WebProp();
        ClassXml.toObject(cfg, wc);
        if (ClassUtils.checkNoNull(wc)) {
            if (initDatabase(wc.MySqlIP, wc.MySqlDBName, wc.MySqlUser, wc.MySqlPassword)) {
                return startWeb(wc.WebPort, wc.ScanPath);
            }
        } else if (wc.WebPort > 0) {
            return startWeb(wc.WebPort, wc.ScanPath);
        }
        return false;
    }


}
