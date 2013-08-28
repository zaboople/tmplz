package org.tmotte.tmplz.test.auto.section;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;


public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    doListing(a);
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}

