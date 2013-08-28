package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;

/** 
 * Thrown when an AttrSection tag does not specify a Section name or an attribute name. Example:
 * <pre>  [$AttrSection]</pre>
 */
public class AttrNameBlankException extends TemplateContentException {
  public final String tag;
  public AttrNameBlankException(String tag, StringChunker upTo){
    super("No attribute name after \""+tag+"\"", upTo);
    this.tag=tag;
  }
}