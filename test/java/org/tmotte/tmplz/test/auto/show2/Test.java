package org.tmotte.tmplz.test.auto.show2;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public void test(Appendable a) {
    getAutoTemplate("xx/t1.html").appendTo(a);
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
