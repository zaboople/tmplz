package org.tmotte.tmplz.test.auto.performance;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.load.builtin.FileTextLoaderFactory;

public class Test extends AbstractTest {

  public void test(Appendable a, String file) throws Exception {
    TemplateManager tm=new TemplateManager();
    tm.getTextLoadMgr().register(new FileTextLoaderFactory());
    for (int i=0; i<30; i++)
      new Thread(
        new Threader(tm, file, a)
      ).start();
  }
  static int ids=0;
  private static class Threader implements Runnable {
    String file;
    TemplateManager tm;
    Appendable a;
    String id="<"+(ids++)+">";
    public Threader(TemplateManager tm, String file, Appendable a){
      this.file=file;
      this.tm=tm;
      this.a=a;
    }
    public void run() {
      for (long i=0; i<32000; i++){
        tm.getTemplate(file).appendTo(a);
        if (i % 1000==1){
          System.err.print(id);
        }
      }
    }
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out, args[0]);
  }
}
