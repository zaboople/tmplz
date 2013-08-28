package org.tmotte.tmplz.exception.template;
import org.tmotte.tmplz.exception.TmplzException;
import org.tmotte.tmplz.node.CatNodeSection;

/** 
 * Thrown when <code>Section.show()</code> cannot find the specified sub-Section.
 */
public class NoSuchSectionException extends TmplzException {
  final String subSection, section;
  public NoSuchSectionException(String subSection, CatNodeSection section){
    super("Section "+subSection+" not found in Section "+section.getName());
    this.subSection=subSection;
    this.section=section.getName();
  }
  /** 
   * Gets the name of the Section that could not be found.
   */
  public String getSectionNotFound(){
    return subSection;
  }
  /** 
   * Gets the name of the Section that did not contain the named sub-Section.
   */
  public String getSectionSearched(){
    return section;
  }
}