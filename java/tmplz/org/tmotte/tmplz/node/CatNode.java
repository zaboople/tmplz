package org.tmotte.tmplz.node;
import org.tmotte.common.text.Appender;
/** 
 * This is a base class for template nodes. 
 *   I made this an abstract class because otherwise
 *    cloneSelf becomes public. I don't want that. Actually just a matter of preference, really...
 */
public abstract class CatNode implements Appender {
  protected abstract CatNode cloneSelf(boolean forShow);
  public abstract String getName();
}
