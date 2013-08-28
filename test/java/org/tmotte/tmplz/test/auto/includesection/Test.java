package org.tmotte.tmplz.test.auto.includesection;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  protected int getTemplateType() {
    return CLASS_TEMPLATE;
  }
  public void test(Appendable a) {
    Section s=getTemplateManager().getTemplate(getAutoTemplateName());
    s.appendTo(a);
  }
  public static void main(String[] args) throws Exception {
    System.out.println("Starting...");
    new Test().test(System.out);
  }
}
