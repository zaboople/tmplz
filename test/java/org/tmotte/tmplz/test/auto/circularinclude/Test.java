package org.tmotte.tmplz.test.auto.circularinclude;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    a.append("\n** TEST 1 **\n");
    try {
      getAutoTemplate("test1/template.html");
    } catch (Exception e) {
      reportError(e, a);
    }
    a.append("\n** TEST 2 **\n");
    try {
      getAutoTemplate("test2/template.html");
    } catch (Exception e) {
      reportError(e, a);
    }
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
