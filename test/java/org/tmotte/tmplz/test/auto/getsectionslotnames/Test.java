package org.tmotte.tmplz.test.auto.getsectionslotnames;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.Section;

public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    TemplateManager tm=getTemplateManager();
    {
      Section s=tm.getTemplate(getAutoTemplateName("empty.html"));
      a.append("Should be no sections: ");
      a.append(s.getSectionNames().toString());
      a.append(" Should be no slots: ");
      a.append(s.getSlotNames().toString());
    }
    a.append("\n");
    {
      Section s=tm.getTemplate(getAutoTemplateName("template.html"));
      a.append("Now we get sections: ");
      a.append(s.getSectionNames().toString());
      a.append(" and slots: ");
      a.append(s.getSlotNames().toString());
    }
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
