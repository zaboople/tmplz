package org.tmotte.tmplz.parse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.exception.load.CircularPathException;
import org.tmotte.tmplz.exception.TmplzException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.parse.tokenize.TokenList;
import org.tmotte.tmplz.util.Log;
import org.tmotte.tmplz.exception.load.DependencyCheckException;

/**
 * <p>
 * For internal use. Does the following:
 * <ul>
 * <li>Holds & caches the template text.
 * <li>For top-level templates (i.e. not included templates), caches Tokens produced by the DocumentTokenizer.
 * <li>Holds references to all the nested TextSource instances Included by the underlying template, so that all can be checked
 *    for changes when this template is checked for changes.
 * <li>Maintains a last-modified timestamp for the template, as the maximum timestamp of the main template and everything it Includes.
 * </ul>
 */
public class TextSource {
  //FixLater: move to load package.
  private long allLastModified=0;
  private long myLastModified=0;
  private long etag=0;
  private String text=null;
  private Map<String,TextSource> depLoaderMap=null;
  private TextLoader textLoader;
  private Path path;
  private TokenList cachedTokens;
  private TmplzException failed=null;
  private TextLoadMgr textLoadMgr;

  ////////////
  // SETUP: //
  ////////////

  public TextSource(TextLoader textLoader, TextLoadMgr textLoadMgr, Path path){
    this.textLoader=textLoader;
    this.textLoadMgr=textLoadMgr;
    this.path=path;
  }

  ////////////////////////
  // PROPERTY GET/SETS: //
  ////////////////////////

  public Exception getError() {
    return failed;
  }
  public boolean hasError() {
    return failed!=null;
  }
  public Path getPath(){
    return this.path;
  }
  public String getText(){
    return text;
  }
  public TokenList getCachedTokens() {
    return cachedTokens;
  }
  public void setCachedTokens(TokenList tokens) {
    this.cachedTokens=tokens;
  }


  ////////////
  // RESET: //
  ////////////

  /** Invoked by TemplateBuilder */
  public synchronized void resetOnError() {
    resetDependencies();
  }
  protected void resetDependencies() {
    changed();
    //Can't lose my own last modified or text
    //because I can't reload, gotta hang on to it.
    allLastModified=myLastModified;
  }
  private synchronized void reset() {
    changed();
    allLastModified=0;
    myLastModified=0;
    text=null;
    failed=null;
    etag=0;
  }
  private synchronized void changed() {
    cachedTokens=null;
    if (depLoaderMap!=null)
      depLoaderMap.clear();
  }


  //////////////////
  // Dependency: //
  //////////////////

  /** This is used by the Preprocessor */
  public synchronized TextSource getInThisContext(String anotherPath, Set<TextSource> parents) throws Exception {
    if (depLoaderMap==null)
      depLoaderMap=new HashMap<String,TextSource>();
    TextSource d=(TextSource)depLoaderMap.get(anotherPath);
    parents.add(this);
    if (d==null){
      d=textLoadMgr.getSource(textLoader.getAbsolutePath(anotherPath));
      d.check();
      depLoaderMap.put(anotherPath, d);
      setLastModified(d.getLastModified());
    }
    if (parents!=null && parents.contains(d))
      throw new CircularPathException();
    parents.add(d);
    //If d already exists, there is no need to run check() because the containing TextSource
    //already check()'s d when the container check()'s itself. This way we don't get punished for
    //including a template several times.
    return d;
  }


  ////////////
  // CHECK: //
  ////////////

  /**
   * The "parents" used here is different from the one used in getInThisContext(). This one
   * is just used to prevent a stack overflow when we have a child in our dependency map
   * and it (or a child of it) has us in its own dependency map. An actual circular
   * include will be detected by getInThisContext().
   */
  Set<TextSource> spareAlreadyChecker;
  synchronized long check() {
    if (depLoaderMap!=null && !depLoaderMap.isEmpty()){
      if (spareAlreadyChecker==null)
        spareAlreadyChecker=new HashSet<TextSource>();
      spareAlreadyChecker.clear();
    }
    else
      spareAlreadyChecker=null;
    return check(spareAlreadyChecker);
  }
  /** This is only invoked by checkDependencies against other TextSources. */
  synchronized long check(Set<TextSource> already) {
    checkSelf(already);
    checkDependencies(already);
    return getLastModified();
  }
  private synchronized void checkDependencies(Set<TextSource> already) {
    if (depLoaderMap!=null && !depLoaderMap.isEmpty()) {
      for (TextSource ts: depLoaderMap.values()){
        if (!already.contains(ts))
          try {
            ts.check(already);
          } catch (Exception e) {
            throw new DependencyCheckException(this, e);
          }
        //It may have been checked elsewhere, but we still need to know its getLastModified().
        setLastModified(ts.getLastModified());
      }
    }
  }
  private synchronized void checkSelf(Set<TextSource> already) {
    //System.out.println(" self check "+this);
    if (already!=null) {
      if (already.contains(this)){
        //System.out.println(" self check "+this+" DO NOT NEED TO ALREADY CONTAINS ");
        return;
      }
      already.add(this);
    }
    try {
      String newText=textLoader.check();
      if (newText!=null){
        reset();
        myLastModified=System.currentTimeMillis();
        setLastModified(myLastModified);
        this.text=newText;
        long len=this.text.length(),
             hash=this.text.hashCode();
        this.etag=((long)this.text.length())+((long)this.text.hashCode());
      }
      else
      if (text==null)
        throw new InternalException("No text found for path: "+path+" from TextLoader "+textLoader.getClass());
    } catch (TmplzException e) {
      failed=e;
      throw e;
    }
  }


  ///////////////////////////
  // LAST MODIFIED & ETAG: //
  ///////////////////////////

  private synchronized void setLastModified(long possible) {
    if (possible>allLastModified){
      Log.finest("TextSource", "check()", "Detected change for "+path+" last mod "+possible);
      allLastModified=possible;
    }
  }
  synchronized long getLastModified() {
    return allLastModified;
  }
  synchronized long computeETag() {
    long e=etag+1;
    int offset=1;
    if (depLoaderMap!=null)
      for (TextSource t: depLoaderMap.values())
        e+=t.computeETag()*(offset++);
    return e;
  }

  //////////
  // ETC: //
  //////////

  public String toString() {
    return path.toString();
  }
}
