package org.tmotte.tmplz.test;
import org.tmotte.tmplz.apps.TemplateLister;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer;
import org.tmotte.tmplz.parse.tokenize.TokenList;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.util.Log;
import org.tmotte.tmplz.util.ObjectFormatter;
import org.tmotte.common.io.StringLoader;

public abstract class AbstractTest{

  protected static int FILE_TEMPLATE=1, CLASS_TEMPLATE=2;
  protected int getTemplateType(){
    return CLASS_TEMPLATE;
  }

  public TemplateManager getTemplateManager() {
    TemplateManager tm=null;
    int foo=getTemplateType();
    if (foo==FILE_TEMPLATE)
      tm=Global.getFileTemplateManager();
    else
    if (foo==CLASS_TEMPLATE)
      tm=Global.getClassTemplateManager();
    else
      throw new RuntimeException("Invalid template type "+foo);
    return tm;
  }
  public String getAutoTemplateNameFile() {
    return "./java/"+getAutoTemplateName();
  }
  public String getAutoTemplateName() {
    return getAutoTemplateName("template.html");
  }
  public String getAutoTemplateName(String file) {
    String className=getClass().getName();
    className=className.replaceAll("(.*\\.).*", "$1");
    className=className.replace(".", "/")+file;
    return className;
  }
  public void doListing(Appendable a) throws Exception {
    doListing(a, getAutoTemplateName());
  }
  public void doListing(Appendable a, String templateName) throws Exception {
    TemplateManager t=getTemplateManager();
    Section s=t.getTemplate(templateName);
    System.out.println("\n** Template Listing: **");
    TemplateLister.list(s, a);    
    System.out.println("\n** Template Result: **");
    s.appendTo(System.out);  
  }
  public void test(String templateName, Appendable a) {
    getAutoTemplate(templateName).appendTo(a);
  }
  public void test(Appendable a) throws Exception {
    getAutoTemplate().appendTo(a);
  }
  public void test() throws Exception {
    test(System.out);
  }
  public Section getAutoTemplate() {
    return getTemplateManager().getTemplate(getAutoTemplateName());
  }
  public Section getAutoTemplate(String name) {
    return getTemplateManager().getTemplate(getAutoTemplateName(name));
  }
  public void reportError(Throwable e, Appendable a) throws java.io.IOException {
    a.append(e.getMessage()+"\n");
    Throwable e2=e.getCause();
    if (e2!=null){
      a.append("  Caused by: ");
      reportError(e2, a);
    }
  }
}