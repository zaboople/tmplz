package org.tmotte.tmplz.test.auto.textload.classloader;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.load.builtin.FileTextLoaderFactory;
import org.tmotte.tmplz.load.builtin.ClassLoaderTextLoaderFactory;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.test.AbstractTest;

public class Test extends AbstractTest {
  public static void main(String[] args) throws Exception {
    new Test().test(System.out);
  }
}
