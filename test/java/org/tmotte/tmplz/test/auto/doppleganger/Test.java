package org.tmotte.tmplz.test.auto.doppleganger;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public void test(Appendable a) {
    Section t=getAutoTemplate();
    Section x21a=
      t.show("X1").fillin("X1s", "a filled in X1s")
        .show("X11")
        .show("X111")
        .show("X2")
        .show("X21");
    Section x211b=
      t.show("X1").fillin("X1s", "b filled in X1s")
        .show("X11")
        .show("X111")
        .show("X2")
        .show("X21")
        .show("X211").fillin("X211s", "b Filled in X211s");
    t.appendTo(a);
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
