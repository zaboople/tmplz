package org.tmotte.tmplz.load;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import org.tmotte.tmplz.exception.TmplzException;
import org.tmotte.tmplz.exception.load.NoTextLoaderException;
import org.tmotte.tmplz.exception.load.TextLoaderCreateException;
import org.tmotte.tmplz.exception.load.TextLoaderFailureException;
import org.tmotte.tmplz.util.Log;
import org.tmotte.tmplz.parse.TextSource;

/**
 * <p>
 * As per its name, this class manages the process of loading text data from a multitude of 
 * possible sources. TextLoadMgr maintains a list of TextLoaderFactory instances and uses the TextLoader
 * provided by the first TextLoaderFactory that returns a non-null TextLoader instance. 
 * </p>
 * <p>
 *  Every TemplateManager maintains an internal TextLoadMgr instance.
 * </p>
 * <p>
 * After a TextLoader is first obtained, TextLoadMgr keeps the TextLoader in HashMap cache
 * for later requests to the same Path.
 * </p>
 */
public class TextLoadMgr {

  /** This is the list of paths-to-TextLoaderFactories: */
  private List<TextLoaderFactory> factories=new ArrayList<TextLoaderFactory>();
  
  /** This is a cache of absolute paths to actual TextSources */
  private Map<Path,TextSource> textSources=new HashMap<Path,TextSource>();
  
  
  ///////////
  // init: //
  ///////////

  /**
   * This needs to be invoked at least once so that the TextLoadMgr has a factory that can 
   * generate TextLoaders to obtain text data. If multiple TextLoaderFactory's are registered,
   * they will be asked in order of registration to provide a suitable TextLoader for a given
   * Path. 
   * @return The same object, for convenience.
   */
  public synchronized TextLoadMgr register(TextLoaderFactory factory){
    factories.add(factory);
    resetCache();
    return this;
  }
  /**
   * Unregisters the given factory so that it is no longer used to obtain TextLoaders.
   */
   public synchronized void unregister(TextLoaderFactory factory){
    if (factory==null)
      throw new IllegalArgumentException("Null factory");
    factories.remove(factory);
    resetCache();
  }
  /**
   * Clears the internal Map object that caches TextLoaders.
   */
  public synchronized void resetCache(){
    textSources.clear();
  }
  /**
   * Allows one to provide a custom Map implementation for the TextLoader cache.
   * @param cache The new cache.
   */
  public void setCache(Map cache) {
    textSources=cache;
  }



  ////////////////
  // INTERNALS: //
  ////////////////
  

  /**
   * @return null if no TextLoaderFactory can be found to create the TextLoader.
   */
  public synchronized TextSource getSource(Path path){
  
    //0. Input checks:
    if (path==null)
      throw new IllegalArgumentException("Internal failure - please contact us - path was null!");
    
    //1. If we already have it, just return it:
    //Log.finest("TextLoadMgr", "getLoader()", path.toString());
    TextSource tl=(TextSource)textSources.get(path);
    if (tl!=null)
      return tl;
     
    //2. Obtain a factory to create it, and return it:
    for (int i=0; i<factories.size(); i++){
      TextLoaderFactory factory=factories.get(i);
      TextLoader loader;
      try {
        loader=factory.create(path);
      } catch (TmplzException e) {
        throw e;
      } catch (Exception e2) {
        throw new TextLoaderCreateException(path, e2);
      }
      if (loader!=null){
        tl=new TextSource(loader, this, path);
        textSources.put(path, tl);
        return tl;
      }
    }
  
    return null;
  }

}
