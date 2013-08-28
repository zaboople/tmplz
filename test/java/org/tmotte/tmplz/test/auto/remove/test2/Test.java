package org.tmotte.tmplz.test.auto.remove.test2;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.apps.TemplateLister;

public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    TemplateManager t=getTemplateManager();
    Section s=t.getTemplate(getAutoTemplateName());
    a.append("\n\n** Printing template:\n");
    s.appendTo(System.out);
    a.append("---------------\n** Listing template:\n");
    TemplateLister.list(s, a);    
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}

