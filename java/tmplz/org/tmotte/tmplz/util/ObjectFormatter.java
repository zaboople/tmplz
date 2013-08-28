package org.tmotte.tmplz.util;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;

/**
 * A convenience interface that allows one to intercept Objects passed to Section.fillin() and
 * format them.
 * @see org.tmotte.tmplz.TemplateManager#setObjectFormatter
 * @see org.tmotte.tmplz.Section#setObjectFormatter
 */
public interface ObjectFormatter {
  /** 
   * @param slotValue The Object inserted into the slot.
   * @param slotName The name of the slot affected.
   */
  public String format(Object slotValue, String slotName);
}