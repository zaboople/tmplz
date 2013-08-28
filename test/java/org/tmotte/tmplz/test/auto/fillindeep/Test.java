package org.tmotte.tmplz.test.auto.fillindeep;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  protected int getTemplateType() {
    return CLASS_TEMPLATE;
  }
  public void test(Appendable a) throws Exception {
    a.append("\n\n***Testing template.html with tag-based show/fillin:\n");
    getAutoTemplate().appendTo(a);

    a.append("\n\n***Testing include2.html with java-based show/fillin:\n");
    Section s=getAutoTemplate("include2.html");
    s.show("Foo2").show("Foo2.1");
    s.show("Foo").show("Deep2.x.x.x").show("Deep3.y.y.y").show("Deep4");
    s.fillin("Bar", "Bar value", true)
     .fillin("Bar2", "Bar2 value", true)
     .fillin("ASlot.Deepest", "ASlot.Deepest value", true);
    s.appendTo(a);
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
