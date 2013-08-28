package org.tmotte.tmplz.test.auto.show.test3;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    Section s=getAutoTemplate();
    Section t=s.show("XYZ");
    t.show("A").show("C").fillin("B", "hello");
    t.show("A").show("C").fillin("B", "hello");
    s.appendTo(a);
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
