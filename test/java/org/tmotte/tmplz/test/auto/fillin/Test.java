package org.tmotte.tmplz.test.auto.fillin;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public void test() throws Exception {
    System.out.println("\n\n***Test template show/fill tags: ");
    getAutoTemplate().appendTo(System.out);

    System.out.println("\n\n***Test java show/fill methods: ");
    Section s=getAutoTemplate("include.html");
    s.show("Foo").fillin("Bar", "XXX");
    s.appendTo(System.out);
  }
  public static void main(String[] args) throws Exception {
    new Test().test();
  }
}
