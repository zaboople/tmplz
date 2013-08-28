package org.tmotte.tmplz.test.auto.tokenizer;
import org.tmotte.common.io.StringLoader;
import org.tmotte.tmplz.test.AbstractTest;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer;
import org.tmotte.tmplz.parse.tokenize.TokenList;

public class Test extends AbstractTest {
  public void test(Appendable a) throws Exception {
    DocumentTokenizer dt=new DocumentTokenizer();
    String s=new StringLoader(new java.io.FileInputStream(getAutoTemplateNameFile())).load();
    TokenList t=dt.parse(s);
    dt.produce(a, t, null);
  }
  public static void main(String[] args) throws Exception {
    new Test().test((Appendable)System.out);
  }
}
