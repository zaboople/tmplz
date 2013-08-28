package org.tmotte.tmplz.test.auto.objectformatter;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.util.ObjectFormatter;
import org.tmotte.tmplz.Section;

public class Test extends org.tmotte.tmplz.test.AbstractTest {
  public void test(Appendable a) {
    TemplateManager tm=getTemplateManager();
    tm.setObjectFormatter(formatter);
    Section s=getAutoTemplate();
    s.fillin("Foo", "YES");
    s.show("Bar").fillin("Foox", "YESAGAIN");
    s.appendTo(a);
  }
  ObjectFormatter formatter=
    new ObjectFormatter() {
      public String format(Object slotValue, String slotName){
        return slotValue+" **** ObjectFormatter modifies slot"+slotName+" *** ";
      }
    };

  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}