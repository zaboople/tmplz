package org.tmotte.tmplz;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.io.Writer;
import java.io.OutputStream;
import org.tmotte.common.text.Appender;
import org.tmotte.tmplz.node.CatNodeSection;
import org.tmotte.tmplz.node.CatNode;
import org.tmotte.tmplz.util.ObjectFormatter;
import org.tmotte.tmplz.exception.template.NoSuchSectionException;

/**
 * <p>
 * Section is the main class used to manipulate Tmplz templates. A Section contains Slots and other Sections. Templates themselves are
 * instances of Section; use TemplateManager to obtain a template.
 * <p>
 * Slots in a template are filled in via the
 * <code>fillin()</code> methods, and Sections are shown via the <code>show()</code> method. 
 * </p>
 */
public class Section extends CatNodeSection implements Appender {
  
  /////////////////
  // INITIALIZE: //
  /////////////////
  
  
  /**
   * For internal use.
   */
  public Section(String name){
    super(name);
  }
  /**
   * <p>
   * As a convenience feature, ObjectFormatter.format() will be invoked every time Section.fillin() is invoked. This allows one to 
   * intercept Slot values provided to fillin() and format them. setObjectFormatter() is recursive, so that it will be invoked on all
   * Sections contained by this Section, whether they are currently visible or not.
   * </p>
   * <p>
   *  Note that this can also be done for all templates using <code>TemplateManager.setObjectFormatter</code>
   * @param obj An ObjectFormatter that can format Slot values.
   * @see TemplateManager#setObjectFormatter
   */
  public void setObjectFormatter(ObjectFormatter obj) {
    super.setObjectFormatter(obj);
  }

  
  /////////////////
  // PROPERTIES: //
  /////////////////

  /** 
   * Provides the name of the Section as defined in its template; e.g., for
   * <code>[$Section Foo]</code> returns "Foo".
   * @return The section name.
   */
  public String getName(){
    return name;
  }

  /////////////////////////////////
  // GET SECTION / SHOW SECTION: //
  /////////////////////////////////
  

  /** 
   *  <b>Obtains a named Section from this Section, makes it visible and returns it.</b>
   *  This method may be invoked with the same Section name any number of times; each time after the first,
   *  a copy is made of the original Section and appended to it. <br><br>
   *  It makes no difference what order different Sections are shown in; for example, this:<pre> 
        mySection.show("A"); 
        mySection.show("B");</pre><br/>
   *  has the same result as this:<br/><br/><pre>
        mySection.show("B"); 
        mySection.show("A");</pre>
   * @param name The name of the contained Section to show.
   * @throws NoSuchSectionException if the named Section is not found.
   * @return The contained Section.
   */
  public Section show(String name){
    Section s=(Section) super.doShow(name);
    if (s==null)
      throw new NoSuchSectionException(name, this);
    return s;
  }
  /**
   * Finds out if this Section contains another Section with the specified <code>name</code>.
   * @param name Name of a Section to find.
   * @return <code>true</code> If this Section contains another Section named <code>name</code>.
   */
  public boolean hasSection(String name) {
    return super.hasCatNodeSection(name);
  }

  /////////////
  // FILLIN: //
  /////////////


  /** 
   * <p>
   * Sets the value of a Slot in this Section. If the Slot is defined in a template as 
   * <code>[$Slot Foo]</code>, then <code>fillin("Foo", "bar", true, false)</code>
   * will put "bar" into the Slot. 
   * </p>
   * <p>
   * If multiple Slots have the same name, all will be filled in.
   * </p>
   * @param name The name of the Slot, minus the &quot;Slot&quot; part.
   * @param value Will be placed in the Slot. If this is an <b>Appender</b>, its <code>appendTo()</code>
   *   will be invoked when the template is printed, allowing greater efficiency; otherwise, it will be <code>toString()</code>'d.
   * @param recursive If true, will drill down into contained Sections and look for Slots with the specified name.
   *   Sections that are not set to visible (ref. <code>Section.show()</code>) will not be drilled into.
   * @return The same Section object, for convenience (Section is nonetheless &quot;mutable&quot;).
   * @throws org.tmotte.tmplz.exception.template.NoSuchSlotException When no matching Slot is found and <code>recursive</code> is false.
   * @see Appender
   */
  public Section fillin(String name, Object value, boolean recursive){
    super.doFillin(name, value, !recursive, recursive, false);
    return this;
  }
  /**
   * <b>The most commonly used version of fillin().</b> Does the same as <code>fillin(name, value, false)</code>.
   */
  public Section fillin(String name, Object value){
    super.doFillin(name, value, true, false, false);
    return this;
  }
  /**
   * For each key and value in the given Map, invokes 
   * <code>fillin(key.toString(), value, recursive)</code> using the key
   * as the name parameter. This method will <i>not</i> throw an exception if the key
   * is not found in this Section or its sub-Sections.
   * @param namesValues Keys are names of Slots to fill in; values are what to fill them in with.
   */
  public Section fillin(Map namesValues, boolean recursive){
    for (Iterator iter=namesValues.keySet().iterator(); iter.hasNext();){
      Object key=iter.next();
      Object value=namesValues.get(key);
      if (value==null)
        value="";
      super.doFillin(key.toString(), value, !recursive, recursive, false);
    }
    return this;
  }
  /**
   * Invokes <code>fillin(namesValues, false)</code>.
   */
  public Section fillin(Map namesValues){
    return fillin(namesValues, false);
  }
  /**
   * Queries the template for the existence of a named Slot.
   * @param name The name of the Slot to find.
   * @return true if this Section contains a Slot named <code>name</code>
   */
  public boolean hasSlot(String name){
    return super.containsSlot(name);
  }

  /**
   * <p>
   * This provides a slightly different functionality from fillin(). It was designed primarily for use by TemplateInterceptor 
   * instances that want to fill in a Slot "permanently", so that all copies of the template have the same value in the specified Slot. 
   * </p>
   * <p>
   * Normally, when a Section is duplicated via Section.show(), all of the 
   * Slot values in the copied Section are reset to null, even if fillin() has been used to set values
   * on the original Section. The replace() method acts just like fillin(), but the Slot is 
   * converted into a static text element (CatNodeStatic) that retains its value when copied. 
   * </p><p>
   * When <code>recursive</code> is <code>true</code>, Slots in sub-sections will be filled in even if the Section is
   * not visible (note that <code>fillin()</code> does not do this).
   * </p><p>
   * The replace() method can only be invoked once against a Slot of a given name. 
   * </p><p>
   * Values passed into the Section via replace() will not be sent to the Section's ObjectFormatter (if any exists) 
   * for modification.
   * </p>
   * @see org.tmotte.tmplz.util.TemplateInterceptor
   * @param name The name of the slot.
   * @param value The value to place in the Slot
   * @param recursive If true, will drill down into contained Sections and look for Slots with the specified name.
   * @return Section The same Section, for convenience.
   * @throws org.tmotte.tmplz.exception.template.NoSuchSlotException When no matching Slot is found and <code>recursive</code> is false.
   */
  public Section replace(String name, Object value, boolean recursive){
    super.doFillin(name, value, !recursive, recursive, true);
    return this; 
  }


  ////////////
  // PRINT: //
  ////////////

  /** 
   * <p>
   * <b>Prints the Section and all of its child nodes.</b> Does not render the Section if it is
   * not "visible"; the root Section of a template is automatically visible, and so are
   * any Sections within it that have been "shown" via the show() method. Empty Slots
   * are not rendered. 
   * </p><p>
   * If a TemplateInterceptor has been assigned, <code>TemplateInterceptor.preRender(Section)</code> will be invoked with this Section
   * before rendering starts.
   * </p>
   * @see org.tmotte.tmplz.TemplateManager#setTemplateInterceptor
   * @param a The Appendable to print to.
   */
  public void appendTo(Appendable a) {
    if (isVisible && interceptor!=null) 
      interceptor.preRender(this);
    super.doAppendTo(a);
  }
  /**
   * Invokes <code>appendTo()</code> with an internal buffer, and returns the result.
   * @return The rendered Section.
   */
  public String toString(){
    StringBuilder sb=new StringBuilder();
    appendTo(sb);
    return sb.toString();
  }

  /**
   * Obtains a list of the names of all Sections in this Section.
   */
  public Collection<String> getSectionNames() {
    return super.getSectionNamesInternal();
  }

  /**
   * Obtains a list of the names of all Slots in this Section.
   */
   public Collection<String> getSlotNames() {
    return super.getSlotNamesInternal();
  }
  

}
