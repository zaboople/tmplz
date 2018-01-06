package org.tmotte.tmplz.parse;
import java.util.HashMap;
import java.util.Map;
import org.tmotte.tmplz.exception.TmplzException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.parse.dum.DocumentBuilder;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.node.CatNodeSection;
import org.tmotte.tmplz.util.Log;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.util.ObjectFormatter;
import org.tmotte.tmplz.util.TemplateInterceptor;

/**
 * This holds the current version of a template. TemplateSource really could be combined with TextSource to
 * simplify the code, even though TextSource instances do not always correspond to a template/Section instance
 * (included templates do not need to be parsed into actual Section instances).
 */
public class TemplateSource {

  private Section baseTemplate=CatNodeSection.createTemplate();
  private TextSource textSource;
  private TemplateBuilder templateBuilder;
  private ObjectFormatter objFormatter;
  private TemplateInterceptor interceptor;
  private long lastModified=-2;
  private long etag=-2;

  ////////////////////
  // INITIALIZATION //
  ////////////////////

  public TemplateSource(
      TextSource textSource,
      TemplateBuilder templateBuilder,
      ObjectFormatter objFormatter,
      TemplateInterceptor interceptor
    ) {
    this.textSource=textSource;
    this.templateBuilder=templateBuilder;
    this.objFormatter=objFormatter;
    this.interceptor=interceptor;
  }

  ////////////////////
  // GET() METHODS: //
  ////////////////////

  /**
   * Gets a thread-safe, up-to-date template instance:
   * <ol>
   * <li> If checkForChanges==true, checks the template's source to find out
   *    if the template source has changed and rebuilds as necessary.
   * <li> Makes a copy of the internal template and returns it.
   * </ol>
   */
  public Section getTemplate(boolean checkForChanges) {
    if (checkForChanges || lastModified==-2)
      check();
    Section template=(Section)baseTemplate.cloneSelf(false);
    template.setTemplateInterceptor(interceptor);
    return template;
  }

  ////////////
  //  LOAD  //
  ////////////

  /**
   * @return true if the underlying content changed.
   */
  public synchronized boolean check(){
    if (textSource.check()!=lastModified) {
      Log.info("TemplateSource.check(): reloading template for "+textSource.getPath());
      templateBuilder.load(textSource, baseTemplate);
      //Unfortunately we have to get last modified after template load, because the TextSource
      //doesn't know what all of its included templates are until then.
      lastModified=textSource.getLastModified();
      etag=textSource.computeETag();
      if (objFormatter!=null)
        baseTemplate.setObjectFormatter(objFormatter);
      if (interceptor!=null)
        interceptor.postLoad(baseTemplate);
      return true;
    }
    return false;
  }
  public synchronized long getLastModified(){
    return lastModified;
  }
  public synchronized long getETag(){
    return etag;
  }

}
