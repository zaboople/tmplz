package org.tmotte.tmplz.test.auto.templatelister;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer;
import org.tmotte.tmplz.apps.TemplateLister;

public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    TemplateLister.list(getAutoTemplate(), a);
    TemplateLister.generateCode(getAutoTemplate(), a);
  }
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
    System.out.flush();
  }
}
