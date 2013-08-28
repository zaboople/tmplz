package org.tmotte.tmplz;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.parse.TextSource;
import org.tmotte.tmplz.parse.TemplateSource;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer;
import org.tmotte.tmplz.util.Log;
import org.tmotte.tmplz.util.ObjectFormatter;
import org.tmotte.tmplz.util.TemplateInterceptor;
import org.tmotte.tmplz.parse.TemplateBuilder;
import org.tmotte.tmplz.parse.preprocess.Preprocessor;

/**
 * This class is used to obtain templates (instances of Section) ready for processing. These instances
 * are not to be thought of as "reuseable"; for example, if a web application uses templates to generate dynamic
 * HTML, every time it responds to an HTTP request it should invoke TemplateManager.getTemplate() to obtain a 
 * new template instance. <br>
 * It is of course completely reasonable to cache & reuse the text output from a template (e.g. from
 * Section.toString()) in a multithreaded manner, since it's just a String.
 */
public class TemplateManager {

  private TextLoadMgr textLoadMgr=new TextLoadMgr();
  private Map<Path,TemplateSource> templateSources=new HashMap<Path,TemplateSource>();
  private TemplateInterceptor templateInterceptor;
  private ObjectFormatter objFormatter;
  private Preprocessor preprocessor;
  private TemplateBuilder templateBuilder;
  private DocumentTokenizer tokenizer;
  private boolean alwaysCheckForChanges=true;
  

  ////////////////////
  // CONFIGURATION: //
  ////////////////////
  
  /** <b>Creates a TemplateManager for obtaining templates.</b> */
  public TemplateManager() {
    templateBuilder=new TemplateBuilder();
    preprocessor=new Preprocessor();    
    templateBuilder.setPreprocessor(preprocessor);
    setTokenizer(new DocumentTokenizer());
  }
  
  private void setTokenizer(DocumentTokenizer dt) {
    templateBuilder.setTokenizer(dt);
    preprocessor.setTokenizer(dt);
    tokenizer=dt;
  }
  
  /**
   * This can be used to intercept templates on creation and/or print.
   */
  public void setTemplateInterceptor(TemplateInterceptor ti){
    this.templateInterceptor=ti;
  }
  /**
   * <b>Before using TemplateManager, its TextLoadMgr must be configured
   * with at least one TextLoaderFactory instance.</b>
   * @see TextLoadMgr#register(org.tmotte.tmplz.load.TextLoaderFactory)
   */
  public TextLoadMgr getTextLoadMgr() {
    return textLoadMgr;
  }
  /**
   * Sets the default ObjectFormatter for all templates produced by this TemplateManager. Whenever a Slot is filled in, the ObjectFormatter will be invoked 
   * with the new Slot value (and name) and allowed to format the object. Note that <code>Section.setObjectFormatter()</code> will override
   * this setting. 
   * @see Section#setObjectFormatter(ObjectFormatter)
   */
  public void setObjectFormatter(ObjectFormatter obj) {
    this.objFormatter=obj;
  }
  /**
   * Changes the default tag delimiters from <code>[$</code> and <code>]</code> to <code>startTag</code> and <code>endTag</code>. 
   * Another alternative is to use the <code>TagWith</code> tag to set the start/end delimiters on a template-by-template basis.
   */
  public void setTagDelimiters(String startTag, String endTag) {
    setTokenizer(new DocumentTokenizer(startTag, endTag));
  }
  /**
   * Turns on/off auto-trim, which attempts to trim line breaks and extra spaces resulting 
   * from tags placed on lines by themselves (presumably for clarity). Defaults to true.
   * Does not trim very aggressively; for that, use the Trim template tag.
   * @param autoTrim If true, tags on lines by themselves are trimmed.
   */
  public void setAutoTrim(boolean autoTrim){
    tokenizer.setAutoTrim(autoTrim);
  }
  /**
   * <p>
   * This controls whether Show tag "destroys" the corresponding Section so that 
   * it cannot be shown again, by parent templates or Java code. When destruction
   * is turned off (the default), there may be an excessive proliferation of leftover Section
   * elements from included templates. Note that these extra Sections can be removed 
   * using the <code>Remove</code> tag, however.
   * <p>
   * The default value of this property is false.
   * @param tf If true, a Show tag "destroys" the Section; if false, it does not.
   */
  public void setDestructiveShowTags(boolean tf) {
    preprocessor.setDestructiveShow(tf);
  }
  /** 
   * @see setDestructiveShowTags
   */
  protected boolean getDestructiveShowTags() {
    return preprocessor.getDestructiveShow();
  }
  /**
   * Allows one to customize the Maps used for caching data. By default, HashMaps are used everywhere.        
   * @param templateCache This will hold the actual template (Section) objects.
   * @param textLoadMgrCache This can also be set via getTextLoadMgr().setCache(), but is included here
   *        for completeness. 
   */
  public synchronized void setCaches(Map templateCache, Map textLoadMgrCache) {
    templateSources=templateCache;
    textLoadMgr.setCache(textLoadMgrCache);
  }
  /**
   * Normally TemplateManager will check the underlying template to see if it's changed 
   * when a template is requested via <code>getTemplate(path)</code>. This is slightly
   * inefficient if one knows that the templates will not change (e.g. in a production
   * system). Invoking <code>setCheckForChanges(false)</code> will turn the check off.
   */
  public synchronized void setCheckForChanges(boolean alwaysCheckForChanges) {
    this.alwaysCheckForChanges=alwaysCheckForChanges;
  }
  

  ///////////////////
  // GET TEMPLATE: // 
  ///////////////////
    
  /**
   * <b>The easiest way to get a template.</b>
   * Invokes <code>getTemplate(new Path(path), alwaysCheckForChanges);</code>
   * @see #setCheckForChanges(boolean)
   */
  public Section getTemplate(String path){
    if (path==null)
      throw new NullPointerException("Name of template was null");
    return getTemplate(new Path(path), alwaysCheckForChanges);
  }
  /**
   * This gets a template ready for threadsafe content generation. 
   * @param path A string or URL indicating a source for the content. This will be logged to the Log class
   *    at the "FINEST" setting.
   * @param checkForChanges 
   *    If true, the underlying source content provider will be asked to check the source content for
   *    changes to the template. If false and the parsed template already exists, the template
   *    will be assumed up to date. This is useful when a call has already been made to 
   *    getLastModified(String) or getETag(String), both of which check for changes to the template, 
   *    such that we don't need to check a second time when we already know the template has changed.
   * @return A template ready for use.
   */  
  public Section getTemplate(Path path, boolean checkForChanges){
    Log.finest("TemplateManager.getTemplate(): "+path);
    Section template=getTemplateSource(path).getTemplate(checkForChanges);
    return template;
  }
  /**
   * This just invokes <code>getTemplate(new Path(path), checkForChanges);</code>
   */
  public Section getTemplate(String path, boolean checkForChanges){
    return getTemplate(new Path(path), checkForChanges);
  }


  /**
   * <p>
   * Obtains an "entity tag" for a template, which in HTTP parlance is a text
   * code designating the current version of a resource. Uses a simple checksum of the template source 
   * (<code>String.hashCode()+String.length()</code>, combined additively for all included templates). 
   * The checksum is only recalculated when the template changes. 
   * </p>
   * <p>
   * This also checks the template for changes, in which case the template will be reloaded. Thus <code>getTemplate()</code> can be 
   * invoked afterwards
   * with its <code>checkForChanges</code> parameter set to <code>false</code> (otherwise one would 
   * end up checking the source twice, once each for <code>getETag()</code> &amp; <code>getTemplate()</code>).
   * </p>
   */
  public long getETag(Path path){
    TemplateSource ts=getTemplateSource(path);
    ts.check();
    return ts.getETag();
  }
  /**
   * For convenience, this invokes <code>getETag(new Path(path))</code>. 
   */
  public long getETag(String path){
    return getETag(new Path(path));
  }
  /**
   * <p>
   * Obtains a timestamp for the most current version of a template. This is useful in caching
   * scenarios where one simply wants to know if the template has changed. Note that for templates
   * with Include tags, the timestamp returned will be newest timestamp found among the main
   * template and all its included templates, recursively.
   * </p>
   * <p>
   * This also checks the template for changes, in which case the template will be reloaded. Thus <code>getTemplate()</code> can be 
   * invoked afterwards
   * with its <code>checkForChanges</code> parameter set to <code>false</code> (otherwise one would 
   * end up checking the source twice, once each for <code>getLastModified()</code> &amp; <code>getTemplate()</code>).
   * </p>
   * <p>Note that this is <i>not</i> a good value for the HTTP <code>last-modified</code> header when using load-balanced
   *  web servers, because it uses <code>System.currentTimeMillis()</code>, which will be different on every server; also,
   *  the value will change after every reboot. It is better to use Entity Tags, i.e. <code>getETag()</code>.
   * </p>
   * 
   * @param path 
   *   Represents the path to the template.
   * @return The last time that the source for the template changed. For templates that have
   *   Include tags, it will be the most recent timestamp from all templates involved.
   * @see #getETag(String)
   */
  public long getLastModified(Path path){
    TemplateSource ts=getTemplateSource(path);
    ts.check();
    return ts.getLastModified();
  }
  /**
   * For convenience, this invokes <code>getLastModified(new Path(path))</code>. 
   */
  public long getLastModified(String path){
    return getLastModified(new Path(path));
  }


  //////////////
  // VARIOUS: //
  //////////////
  
  /**
   * This clears the cache of all templates.
   */
  public synchronized void forceReload(){
    templateSources.clear();
  }

  ////////////////
  // INTERNALS: //
  ////////////////

  private synchronized TemplateSource getTemplateSource(Path path){
    if (path==null)
      throw new IllegalArgumentException("Null path");
    TemplateSource ts=(TemplateSource)templateSources.get(path);
    if (ts!=null)
      return ts;
    ts=new TemplateSource(
      textLoadMgr.getSource(path),
      templateBuilder, 
      objFormatter, 
      templateInterceptor
    );
    templateSources.put(path, ts);
    return ts;
  }

}