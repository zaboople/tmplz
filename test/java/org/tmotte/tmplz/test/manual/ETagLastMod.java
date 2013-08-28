package org.tmotte.tmplz.test.manual;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.load.builtin.FileTextLoaderFactory;

public class ETagLastMod {
  public static void main(String[] args) throws Exception {
    TemplateManager tm=new TemplateManager();
    tm.getTextLoadMgr().register(new FileTextLoaderFactory());
    if (args.length==0){
      System.err.println("Need a list of file names. Press <enter> to repeat verification.");
      System.exit(1);
      return;
    }
    while (true) 
      for (String templateName: args) {
        try {
          System.out.println(
            templateName
            +" ETag:"+Long.toHexString(tm.getETag(templateName))
            +" Last Mod:"+tm.getLastModified(templateName)
          );
        } catch (Throwable e) {
          e.printStackTrace(System.out);
        }
        System.in.read();
      }
  }
}