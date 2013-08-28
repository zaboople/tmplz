package org.tmotte.tmplz.test.auto.includespaces;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public static void main(String[] args) throws Exception {
    System.out.println("Starting...");
    new Test().test("template with spaces.html", System.out);
  }
}
