package org.tmotte.tmplz.test.auto.templateinterceptor;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.util.TemplateInterceptor;
import org.tmotte.tmplz.Section;

public class Test extends org.tmotte.tmplz.test.AbstractTest {
  public void test(Appendable a) {
    TemplateManager tm=getTemplateManager();
    tm.setTemplateInterceptor(
      new TemplateInterceptor(){
        public void postLoad(Section template){
          template.replace("PostLoad", "PostLoad Succeded", true);
        }
        public void preRender(Section template){}
      }
    );
    Section s=getAutoTemplate();
    for (int i=0; i<2; i++){
      Section x=s.show("X");
      x.show("Y");
      x.show("Z");
    }
    s.appendTo(a);
  }

  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}