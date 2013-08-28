package org.tmotte.tmplz.test;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.load.builtin.FileTextLoaderFactory;
import org.tmotte.tmplz.load.builtin.ClassLoaderTextLoaderFactory;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.util.Log;

class Global {
  static {
    Log.bufferEverything();
  }
 
 
  static TemplateManager tmgr=new TemplateManager();
  static {
    tmgr.getTextLoadMgr().register(new FileTextLoaderFactory("./java/"));
  }
  
  static TemplateManager classTemplateManager=new TemplateManager();
  static {
    classTemplateManager.getTextLoadMgr().register(
      new ClassLoaderTextLoaderFactory(new Global().getClass().getClassLoader(), (String)null, true)
    );
  }
  
  public static TemplateManager getFileTemplateManager(){
    return tmgr;
  }
  public static TemplateManager getClassTemplateManager() {
    return classTemplateManager;
  }
}