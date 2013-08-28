package org.tmotte.tmplz.util;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.Section;

/**
 * This can be assigned to a TemplateManager so that one can do any necessary manipulations to a template before
 * it is released for use, or just prior to rendering.
 * @see org.tmotte.tmplz.TemplateManager#setTemplateInterceptor
 */
public interface TemplateInterceptor {
  /** 
   * Invoked once by TemplateManager after a template is parsed. Changes made to <code>template</code> affect all copies of the template
   * obtained via TemplateManager.getTemplate(). 
   */
  public void postLoad(Section template);
  /** 
   * Invoked every time Section.appendTo() is invoked on a template. Changes made to the template will only 
   * affect the copy of the template that is being printed, unlike changes made in postLoad(). 
   */
  public void preRender(Section template);
}