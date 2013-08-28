package org.tmotte.tmplz.test;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.load.builtin.FileTextLoaderFactory;
public class DocExample {
  public static void main(String[] args) {
    TemplateManager templateMgr=new TemplateManager();
    templateMgr.getTextLoadMgr().register(new FileTextLoaderFactory());
    Section template=templateMgr.getTemplate("test.html");
    for (int i=0; i<5; i++){
      Section s=template.show("item");
      s.fillin("number", i);
    }
    template.appendTo(System.out);
  }
}
/***
 Template
[$Section item]
  [$Slot number]
[$Section]
****/