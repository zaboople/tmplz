package org.tmotte.tmplz.node;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import org.tmotte.common.text.Appender;
import org.tmotte.tmplz.util.ObjectFormatter;
import org.tmotte.tmplz.util.TemplateInterceptor;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.exception.template.NoSuchSlotException;
import org.tmotte.tmplz.exception.template.SectionRenderingException;

/**
 * This exists primarily to reduce method clutter in the Section class.
 */
public abstract class CatNodeSection extends CatNode{

  private Map<String, CatNodeSection> sectionMap;
  private Map<String, CatNodeSlot> slotMap;
  private List<CatNode> childNodes=new ArrayList<CatNode>();
  private List<CatNodeSection> copies;
  private List<CatNodeSection> doppleGangers;
  protected TemplateInterceptor interceptor;
  protected ObjectFormatter objFormatter;  
  protected boolean isVisible=false;
  protected boolean isTemplate=false;
  protected String name;
  
  
  /////////////////
  // INITIALIZE: //
  /////////////////
  
  protected CatNodeSection(String name){
    this.name=name;
  }
  public static Section createTemplate(){
    Section s=new Section("Template");
    s.isVisible=true;
    s.isTemplate=true;
    return s;
  }

  /** For internal use */
  public void clear(){
    childNodes.clear();
    if (copies!=null)
      copies.clear();
    if (sectionMap!=null)
      sectionMap.clear();
    if (slotMap!=null)
      slotMap.clear();
    if (doppleGangers!=null)
      doppleGangers.clear();
  }
  
  /////////////////
  // PROPERTIES: //
  /////////////////

  public boolean isTemplate(){
    return isTemplate;
  }

  protected void setObjectFormatter(ObjectFormatter obj) {
    this.objFormatter=obj;
    if (sectionMap!=null)
      for (CatNodeSection c: sectionMap.values())
        c.setObjectFormatter(obj);
    if (doppleGangers!=null)
      for (CatNodeSection dopple: doppleGangers)
        dopple.setObjectFormatter(obj);
  }
  
  //////////////////////
  // FILLIN INTERNALS //
  //////////////////////
  
  protected void doFillin(String name, Object value, boolean throwErrorWhenMissing, boolean recursive, boolean asStatic){
    if (!doFillin(name, value, recursive, asStatic) && throwErrorWhenMissing)
      throw new NoSuchSlotException(name, this);
  }
  private boolean doFillin(String name, Object value, boolean recursive, boolean asStatic){
    boolean found=false;
    if (doppleGangers!=null)
      for (int i=0; i<doppleGangers.size(); i++)
        found|=doppleGangers.get(i).doFillin(name, value, recursive, asStatic);
    found|=doFillin(name, value, asStatic);
    if (recursive && childNodes!=null)
      for (int i=0; i<childNodes.size(); i++){
        CatNode x=(CatNode)childNodes.get(i);
        if (x instanceof CatNodeSection) {
          CatNodeSection cns=(CatNodeSection)x;
          if (cns.isVisible || asStatic)
            found|=cns.doFillin(name, value, true, asStatic);
          if (cns.copies!=null)
            for (int j=0; j<cns.copies.size(); j++){
              found|=((CatNodeSection)cns.copies.get(j)).doFillin(name, value, true, asStatic);
            }
        }
      }
    return found;
  }
  private boolean doFillin(String name, Object value, boolean asStatic){
    if (objFormatter!=null)
      value=objFormatter.format(value, name);
    if (slotMap==null)
      return false;
    CatNodeSlot s=slotMap.get(name);
    if (s!=null) {    
      s.setFill(value, asStatic);
      return true;
    }
    return false;
  }
  protected boolean containsSlot(String name) {
    if (slotMap!=null && slotMap.containsKey(name))
      return true;
    else
    if (doppleGangers!=null)
      for (CatNodeSection c: doppleGangers)
        if (c.containsSlot(name))
          return true;
    return false;
  }


  
  /////////////
  // LOOPING //
  /////////////

  public int size(){
    return childNodes.size();
  }
  public CatNode getNode(int i){
    return (CatNode)childNodes.get(i);
  }
  
  ///////////////
  // ADD NODE: //
  ///////////////
  
  /** This is used by copyTo(); the type-specific add()'s are used by TemplateBuilder. */
  public void add(CatNode c){
    if (c instanceof CatNodeSection)
      add((CatNodeSection)c);
    else
    if (c instanceof CatNodeSlot)
      add((CatNodeSlot)c);
    else
      doAdd(c);
  }
  public void add(CatNodeStatic c){
    doAdd(c);
  } 
  public void add(CatNodeBetween c){
    doAdd(c);
  } 
  public void add(CatNodeSection c){    
    doAdd(c);
    if (sectionMap==null)
      sectionMap=new HashMap<String,CatNodeSection>(4);
    CatNodeSection old=sectionMap.get(c.name);
    if (old==null)
      sectionMap.put(c.name, c);
    else 
      old.addDoppleganger(c);
  }
  private void addDoppleganger(CatNodeSection newDopple){
    if (doppleGangers==null) 
      doppleGangers=new ArrayList<CatNodeSection>();
    doppleGangers.add(newDopple);  

    //Now we need to make sure that every section represented in the doppleganger is
    //represented in the main section; if it isn't, we create a dummy Section. Then
    //we make all the subsections of the doppleganger dopplegangers of the same
    //subsections of this section. Woo.
    if (newDopple.sectionMap!=null) 
      for (CatNodeSection doppleChild: newDopple.sectionMap.values()){      
        CatNodeSection mainChild=getCatNodeSection(doppleChild.name);
        if (mainChild==null) {
          mainChild=new Section(doppleChild.name);
          add(mainChild);
        }
        mainChild.addDoppleganger(doppleChild);
      }
  }
  public void add(CatNodeSlot c){
    doAdd(c);
    if (slotMap==null)
      slotMap=new HashMap<String,CatNodeSlot>();
    CatNodeSlot already=slotMap.get(c.getName());
    if (already==null)
      slotMap.put(c.getName(), c);
    else         
      already.addDoppleGanger(c);
  } 

  private void doAdd(CatNode c){
    childNodes.add(c);
  }
  
  ////////////////
  // DUPLICATE: //
  ////////////////

  public CatNode cloneSelf(boolean forShow){
    CatNodeSection newSection=new Section(name);
    copyTo(newSection, forShow);
    return newSection;
  }

  protected CatNodeSection doShow(String name) {
    CatNodeSection section=getCatNodeSection(name);
    return section==null ?null :section.showSelf();  
  }
  private CatNodeSection showSelf(){
    CatNodeSection section=this;
    if (section.isVisible){
      CatNodeSection original=section;
      section=(CatNodeSection)original.cloneSelf(true);
      if (original.copies==null)
        original.copies=new ArrayList<CatNodeSection>();
      original.copies.add(section);
    }
    if (doppleGangers!=null)
      for (CatNodeSection dopple: doppleGangers){
        CatNodeSection doppleCopy=dopple.showSelf();
        if (section!=this)
          section.addDoppleganger(doppleCopy);
      }
    section.isVisible=true; 
    return section;
  }

  protected void copyTo(CatNodeSection newSection, boolean forShow){
    newSection.isTemplate=isTemplate;
    newSection.objFormatter=objFormatter;
    if (newSection.isTemplate)
      newSection.isVisible=true;
    if (childNodes!=null)
      for (int i=0; i<childNodes.size(); i++){
        CatNode original=(CatNode)childNodes.get(i);
        original=original.cloneSelf(forShow);
        newSection.add(original);
      }
  }
  
  //////////
  // ETC. //
  //////////
  
  private CatNodeSection getCatNodeSection(String name){
    if (sectionMap==null)
      return null;
    return sectionMap.get(name);
  }
  protected boolean hasCatNodeSection(String name) {
    return sectionMap!=null && sectionMap.containsKey(name);
  }
  /**
   * For internal use.
   * @see org.tmotte.tmplz.TemplateManager#setTemplateInterceptor
   */
  public void setTemplateInterceptor(TemplateInterceptor interceptor) {
    this.interceptor=interceptor;
  }
  protected Collection<String> getSectionNamesInternal() {
    if (sectionMap==null)
      return Collections.emptySet();
    return sectionMap.keySet();
  }
  protected Collection<String> getSlotNamesInternal() {
    if (slotMap==null)
      return Collections.emptySet();
    return slotMap.keySet();
  }

  ////////////
  // PRINT: //
  ////////////
  
  protected void doAppendTo(Appendable a) {
    if (isVisible) 
      try {
        for (CatNode cn:childNodes)
          cn.appendTo(a);
        if (copies!=null)
          for (CatNodeSection cns: copies)
            cns.appendTo(a);
      } catch (Exception e){
        throw new SectionRenderingException(this, e);
      }
  }
}
