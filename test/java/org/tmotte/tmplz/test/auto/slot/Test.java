package org.tmotte.tmplz.test.auto.slot;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    super.test(a);
    Section s=getTemplateManager().getTemplate("org/tmotte/tmplz/test/auto/slot/include.html");
    s.fillin("A", "Did A")
     .fillin("B", "Did B") 
     .fillin("C", "Did C");
    s.appendTo(a);
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
