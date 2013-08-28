package org.tmotte.tmplz.exception.template;
import org.tmotte.tmplz.exception.TmplzException;
import org.tmotte.tmplz.node.CatNodeSection;

/** 
 * Thrown when <code>Section.appendTo()</code> catches an Exception. Use <code>Throwable.getCause()</code> to get the Exception that
 * caused SectionRenderingException. Note that SectionRenderingExceptions may be nested several deep, once for each
 * sub-section.
 */
public class SectionRenderingException extends TmplzException {
  final String section;
  /** 
   * Gets the name of the Section that had the error.
   */
  public String getSectionName(){
    return section;
  }
  public SectionRenderingException(CatNodeSection s, Exception e){
    super("Error rendering "+(s.isTemplate() ?"section "+s.getName() :"template"), e);
    this.section=s.getName();
  }
}