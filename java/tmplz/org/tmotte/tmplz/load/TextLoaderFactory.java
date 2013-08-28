package org.tmotte.tmplz.load;

/**
 * Implement this interface to supply TextLoaders to TextLoadMgr.
 * @see org.tmotte.tmplz.load.TextLoadMgr#register(org.tmotte.tmplz.load.TextLoaderFactory)
 */
public interface TextLoaderFactory {  
  /** 
   * <p>
   * Requests a TextLoader instance; invoked by TextLoadMgr.
   * TextLoadMgr will cache the TextLoader and ask it to check any Path that matches the Path it was
   * created for (as defined by <code>Path.equals()</code>).
   * </p>
   * @return <p>A TextLoader that can handle the specified Path, or null if the Path is not a good match
   * for this TextLoaderFactory. When null is returned, TextLoadMgr will try the next registered TextLoaderFactory
   * to see if it can return a TextLoader, or give up if no more are available.
   * </p>
   */
  public TextLoader create(Path path) throws Exception;   
}