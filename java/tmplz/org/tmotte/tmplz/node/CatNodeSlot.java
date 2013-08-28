package org.tmotte.tmplz.node;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import org.tmotte.common.text.Appender;

/** Corresponds to a Slot in a template.*/
public class CatNodeSlot extends CatNode{
  private String name=null;
  private boolean isStatic=false;
  private Object toRender=null;
  List<CatNodeSlot> doppleGangers=null;
  
  public CatNodeSlot(String name){    
    super();
    this.name=name;
  }
  protected CatNode cloneSelf(boolean forShow){
    //Note: Dopplegangers do not need to be copied, on the assumption
    //that when the original Section is copied, addDoppleGanger will be
    //invoked when they are encountered.
    return isStatic
      ?new CatNodeStatic(0, toRender==null ?"" :toRender.toString())
      :new CatNodeSlot(name);
  }
  public void addDoppleGanger(CatNodeSlot cns) {
    if (doppleGangers==null)
      doppleGangers=new LinkedList<CatNodeSlot>();
    doppleGangers.add(cns);
  }
  

  /////////////////
  // PROPERTIES: //
  /////////////////

  public String getName(){
    return name;
  }   
  protected void setFill(Object toRender, boolean asStatic){
    this.toRender=toRender;
    this.isStatic=asStatic;
    if (doppleGangers!=null)
      for (CatNodeSlot c: doppleGangers)
        c.setFill(toRender, asStatic);
  }

  /////////////////////
  // Output methods: //
  /////////////////////

  public String toString(){
    if (toRender==null)
      return null;
    else
    if (toRender instanceof Appender){
      StringBuffer sb=new StringBuffer();
      try {
        ((Appender)toRender).appendTo(sb);
      } catch (java.io.IOException e) {
        throw new RuntimeException(e);
      }
      return sb.toString();
    }
    else 
      return toRender.toString();
  }
  public void appendTo(Appendable writer) throws java.io.IOException{
    if (toRender!=null){
      if (toRender instanceof Appender)
        ((Appender)toRender).appendTo(writer);
      else
        writer.append(toRender.toString());
    }
  }
}