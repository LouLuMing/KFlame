package com.china.fortune.yaml;

import com.china.fortune.file.FileUtils;
import com.china.fortune.global.Log;
import com.china.fortune.json.JSONObject;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.statemachine.LogStateMachine;
import com.china.fortune.statemachine.PathInterface;
import com.china.fortune.statemachine.StateAction;
import com.china.fortune.statemachine.StateMachine;

public class YamlStateMachine {
    static private StateMachine yamlSM = new LogStateMachine();
    static {
        class GetChar extends StateAction {
            protected boolean onAction(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.nextChar();
                return true;
            }
        }

        PathInterface pifIsSpace = new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return ji.isSpace();
            }
        };

        PathInterface pifIsChar = new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return ji.isChar();
            }
        };

        yamlSM.addState("StartYaml", new StateAction() {
            protected boolean onAction(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                int space = ji.getSpaceCount();
                if (space >= 0) {
                    ji.pop(space);
                    if (ji.isChar()) {
                        ji.pushJSONObjectIfNeed(space);
                    } else if (ji.currentChar() == '-') {
                        ji.pushJSONArrayIfNeed(space);
                    }
                }
                return true;
            }
        });
        yamlSM.addState("GetKey", new GetChar());
        yamlSM.addState("GetArray", new GetChar());
        yamlSM.addState("NextLine", new GetChar());
        yamlSM.addState("StarValue", new GetChar());
        yamlSM.addState("EndValue", new StateAction() {
            protected boolean onAction(Object owner) {
                return true;
            }
        });

        yamlSM.addState("EndYaml", new StateAction() {
            protected boolean onAction(Object owner) {
                return true;
            }
        });

        yamlSM.addPath("StartYaml", "GetKey", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.setSubStringStart(0);
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return ji.isChar();
            }
        });

        yamlSM.addPath("StartYaml", "GetArray", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.filterSpace();
                ji.setSubStringStart(1);
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return (ji.currentChar() == '-');
            }
        });

        yamlSM.addPath("GetKey", "GetKey", pifIsChar);

        yamlSM.addPath("GetKey", "NextLine", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.saveKey();
                ji.filterSpace();
                return (ji.currentChar() == ':');
            }
        });

        yamlSM.addPath("StarValue", "StarValue", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return ji.currentChar() != '\n' && ji.currentChar() != 0;
            }
        });

        yamlSM.addPath("NextLine", "NextLine", pifIsSpace);

        yamlSM.addPath("NextLine", "StartYaml", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.setNeedPush();
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.filterSpace();
                if (ji.currentChar() == '\r') {
                    ji.nextChar();
                }
                return ji.currentChar() == '\n';
            }
        });

        yamlSM.addPath("NextLine", "StarValue", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.setSubStringStart(0);
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                return true;
            }
        });

        yamlSM.addPath("StarValue", "EndValue", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.saveValue();
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return ji.currentChar() == '\n' || ji.currentChar() == 0;
            }
        });

        yamlSM.addPath("GetArray", "EndValue", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                ji.saveArrayValue();
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return ji.currentChar() == '\n' || ji.currentChar() == 0;
            }
        });
        yamlSM.addPath("GetArray", "GetArray", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return ji.currentChar() != '\n' && ji.currentChar() != 0;
            }
        });

        yamlSM.addPath("EndValue", "StartYaml");

        yamlSM.addPath("StartYaml", "EndYaml", new PathInterface() {
            @Override
            public boolean onAction(Object owner) {
                return true;
            }

            @Override
            public boolean onCondition(Object owner) {
                YamlDataCenter ji = (YamlDataCenter) owner;
                return ji.currentChar() == 0;
            }
        });

        yamlSM.setStart("StartYaml");
        yamlSM.setStop("EndYaml");
    }

    public Object parseJSONObject(String sJson) {
        YamlDataCenter jsonDC = new YamlDataCenter(sJson);
        if (yamlSM.doAction(jsonDC) != null) {
            return jsonDC.getRootObject();
        }
        return null;
    }

    public static void main(String[] args) {
        String sPath = PathUtils.getCurrentDataPath(false);
        sPath += "\\show.yml";
        String sJson = FileUtils.readSmallFile(sPath, "utf-8");
        if (sJson != null) {
            YamlStateMachine ysm = new YamlStateMachine();

            JSONObject jarr = (JSONObject)ysm.parseJSONObject(sJson);
            if (jarr != null) {
                Log.log(jarr.toString());
                Log.logNoDate(jarr.toYaml());
            }
        }
    }
}
