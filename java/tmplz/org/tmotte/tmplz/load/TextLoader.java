package org.tmotte.tmplz.load;

/**
 * <p>TextLoaders are responsible for loading the actual text of templates. TextLoaders exist 1-to-1 with templates, so a TextLoader
 * will only be asked to handle requests for a specific Path (different Path instances may be used, but all will match
 * according to <code>Path.equals()</code>).
 * </p>
 */
public interface TextLoader {
  
  /** 
   * The TextLoader should check its source and return text if any changes have occurred since the last
   * call to <code>check()</code> (or obviously if <code>check()</code> has never been called before).
   * Note that there is no synchronization around the invocation of this method, so one may need to implement such
   * to prevent two threads from accessing it at the same time.
   */
  public String check();

  /**
   * When the parser encounters an <code>Include tag</code> in a template, the path referenced by the Include will be 
   * passed to the template's TextLoader's <code>getAbsolutePath()</code> for resolution. It is up to the TextLoader to
   * decide whether the referenced path is absolute or relative; if it is not relative, the TextLoader can return null, 
   * and a Path will instead be created via <code>new Path(mayBeRelativePath)</code>. 
   * Either way, the resulting Path will be handed to TextLoadMgr, which will in turn ask its TextLoaderFactory
   * instance(s) to produce a TextLoader that can handle that Path.
   *
   * @return A Path object representing the absolute path corresponding to <code>mayBeRelativePath</code>, or null if it 
   *         does not appear to be relative.
   */ 
  public Path getAbsolutePath(String mayBeRelativePath);
  
} 
