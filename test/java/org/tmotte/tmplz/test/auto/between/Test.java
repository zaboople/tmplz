package org.tmotte.tmplz.test.auto.between;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    System.out.println("Testing against Show tag: ");
    super.test(a);
    System.out.println("Testing against java show(): ");
    Section s=getAutoTemplate();
    s.show("Y");
    s.show("Z");
    s.show("Z");
    s.show("Z");
    s.appendTo(System.out);
    System.out.flush();
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
