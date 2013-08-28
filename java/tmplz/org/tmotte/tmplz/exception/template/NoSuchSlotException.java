package org.tmotte.tmplz.exception.template;
import org.tmotte.tmplz.exception.TmplzException;
import org.tmotte.tmplz.node.CatNodeSection;

/** 
 * Thrown when <code>Section.fillin()</code> cannot find the specified Slot.
 */
public class NoSuchSlotException extends TmplzException {
  final String slotName, sectionName;
  public String getSlotName(){
    return slotName;
  }
  public String getSectionName(){
    return sectionName;
  }
  public NoSuchSlotException(String slotName, CatNodeSection section){
    super("No such slot: \""+slotName+"\" in section "+section.getName());
    this.slotName=slotName;
    this.sectionName=section.getName();
  }
}