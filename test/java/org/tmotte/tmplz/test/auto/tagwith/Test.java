package org.tmotte.tmplz.test.auto.tagwith;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  protected int getTemplateType() {
    return CLASS_TEMPLATE;
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
