package cn.iocoder.yudao.module.business.util;

import jep.Interpreter;
import jep.MainInterpreter;
import jep.PyConfig;
import jep.SharedInterpreter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PollinationsAI {

    private static String jepLibraryPath = "C:/Users/CuiMa/anaconda3/envs/gpt4free/Lib/site-packages/jep/jep.dll";
    private static String pythonHome = "C:/Users/CuiMa/anaconda3/envs/gpt4free";
    private static String sysPathAppend = "sys.path.append('C:/Code/gpt4free')";

    static {
        try {
            PyConfig pyConfig = new PyConfig();
            MainInterpreter.setJepLibraryPath(jepLibraryPath);
            pyConfig.setPythonHome(pythonHome);
        } catch (Exception e) {
            log.error("PollinationsAI static init error", e);
        }
    }

    public static String getCommentByPrompt(String userPrompt) {
        try (Interpreter interpreter = new SharedInterpreter()) {
            interpreter.exec("import sys");
            interpreter.exec(sysPathAppend);
            interpreter.exec("from g4f.test import test11");
            Object javaResult = interpreter.invoke("test11.get_message_by_qwen_qwen_3", userPrompt);
            return javaResult != null ? javaResult.toString() : null;
        } catch (Exception e) {
            log.error("Jep 执行出错", e);
            return null;
        }
    }
}
