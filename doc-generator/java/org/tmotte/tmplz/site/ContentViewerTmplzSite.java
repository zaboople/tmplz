package org.tmotte.tmplz.site;
import org.tmotte.tmplz.apps.ContentViewer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.net.URL;
import java.util.Properties;
import java.io.InputStream;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.util.TemplateInterceptor;

public class ContentViewerTmplzSite extends ContentViewer {

  
  public TemplateInterceptor getTemplateInterceptor(ServletConfig config){
    Properties props=new java.util.Properties();
    try {
      URL url=getClass().getResource("/fillin.properties");
      InputStream instr=url.openStream();
      try {
        props.load(instr);
      } finally {
        instr.close();
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to load fillin.properties from root of classpath. Where is it?", e);
    }

    final String urlContext=config.getInitParameter("Context");
    
    java.util.Set<Object> keys=props.keySet();
    final String[] 
      fillKeys=new String[keys.size()],
      fillVals=new String[keys.size()];
    int i=0;
    for (Object o: keys){
      String s=o.toString();
      fillKeys[i]=s;
      fillVals[i]=props.getProperty(s);
      i++;
    }
          
    return new TemplateInterceptor() {
      public void postLoad(Section template){
        if (template.hasSlot("SiteContext"))
          template.replace("SiteContext", urlContext, false);
        if (template.hasSlot("Context"))
          template.replace("Context", urlContext, false);
        for (int i=0; i<fillKeys.length; i++)
          if (template.hasSlot(fillKeys[i]))
            template.replace(fillKeys[i], fillVals[i], false);
      }
      public void preRender(Section template){}
    };
  }

}