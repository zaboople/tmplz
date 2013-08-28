package org.tmotte.tmplz.apps;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import org.tmotte.tmplz.load.builtin.FileTextLoaderFactory;
import org.tmotte.tmplz.load.builtin.URLTextLoaderFactory;
import org.tmotte.tmplz.load.builtin.URLTextLoaderFactory;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.node.CatNode;
import org.tmotte.tmplz.node.CatNodeSection;
import org.tmotte.tmplz.node.CatNodeSlot;
import org.tmotte.tmplz.node.CatNodeStatic;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.util.Log;

/**
 * Obtains a code &quot;skeleton&quot; or Section/Slot listing from a template. Designed to allow command-line use;
 * Run &quot;java org.tmotte.tmplz.apps.TemplateLister&quot; to get help and options.
 */
public class TemplateLister {


  /** 
   * Prints out the tags in the template, in order, indented according to what sections they are nested in. 
   */
  public static void list(Section template, Appendable out) throws Exception {
    listRecurse(getUnique(template), out, "");
  }
  private static void listRecurse(Uniquer u, Appendable writer, String indent) throws Exception {
    for (String slot:u.slots) {
      writer.append(indent);
      writer.append("Slot: ");
      writer.append(slot);
      writer.append("\n");
    }
    for (String section: u.sections) {
      writer.append(indent);
      writer.append("Section: ");
      writer.append(section);
      writer.append("\n");
      listRecurse(u.getUniquer(section), writer, indent+"  ");
    }
  }




  /**
   * Generates a java &quot;skeleton&quot; from a given Template.
   */
  public static void generateCode(Section template, Appendable out) throws Exception {
    out.append("\n\n");
    out.append("  Section template=null;");
    out.append("\n");
    recurseCode(getUnique(template), out, "  ", "template");
  }
  private static void recurseCode(
      Uniquer u, Appendable writer, String indent, String parent
    ) throws Exception {
    for (String slot: u.slots){
      writer.append(indent);
      writer.append(parent+".fillin(\""+slot+"\", \"XXXX\");");
      writer.append("\n");
    }
    for (String section: u.sections) {
      writer.append(indent+"{\n");
      writer.append(indent);
      writer.append("  Section section"+section+"="+parent+".show(\""+section+"\");");
      writer.append("\n");
      recurseCode(u.getUniquer(section), writer, indent+"  ", "section"+section);
      writer.append(indent+"}\n");
    }
  }


  /////////////////////////
  // INTERNAL UTILITIES: //
  /////////////////////////

  private static Uniquer getUnique(Section template) {
    Uniquer u=new Uniquer();
    recurse(template, u);
    return u;
  }
  private static void recurse(Section section, Uniquer u) {
    for (int i=0; i<section.size(); i++) {
      CatNode n=section.getNode(i);
      if (n instanceof CatNodeSlot) 
        u.addSlot(n.getName());
      else
      if (n instanceof CatNodeSection) {
        u.addSection(n.getName());
        recurse((Section)n, u.getUniquer(n.getName()));
      }
    }
  }
  private static class Uniquer {
    List<String> sections=new LinkedList<String>(),
                 slots   =new LinkedList<String>();
    Set<String> slotSet=new HashSet<String>();
    Map<String,Uniquer> uniquers=new HashMap<String,Uniquer>();
    void addSection(String name) {
      if (uniquers.containsKey(name))
        return;
      sections.add(name);
      uniquers.put(name, new Uniquer());
    }
    void addSlot(String name) {
      if (slotSet.contains(name))
        return;
      slots.add(name);
      slotSet.add(name);
    }
    Uniquer getUniquer(String name) {
      return uniquers.get(name);
    }
  }


  ///////////////////////////////
  // COMMAND-LINE INTERACTION: //
  ///////////////////////////////


  /** 
   * Provides command-line functionality. 
   * Run &quot;java org.tmotte.tmplz.TemplateLister&quot; to get help and options.
   */
  public static void main(String[] args) throws Exception {
    String file=null;
    boolean code=false, help=args.length==0, quiet=true, destructiveShowTags=false;
    String basedir="";
    for (int i=0; i<args.length; i++) {
      String s=args[i].toLowerCase();
      if (s.startsWith("-c"))
        code=true;
      else
      if (s.startsWith("-f"))
        file=args[++i];
      else
      if (s.startsWith("-h"))
        help=true;
      else
      if (s.startsWith("-l"))
        quiet=false;
      else
      if (s.startsWith("-dest"))
        destructiveShowTags=true;
      else {
        help("Unexpected parameter: "+s);
        return;
      }
    }
    if (file==null) 
      help("No file specified");
    else
    if (help)
      help(null);
    else try {
      if (quiet)
        Log.bufferEverything();
      TemplateManager templateMgr=new TemplateManager();
      templateMgr.getTextLoadMgr().register(
        new FileTextLoaderFactory()
      );
      templateMgr.setDestructiveShowTags(destructiveShowTags);
      if (code)
        generateCode(templateMgr.getTemplate(file), System.out);
      else
        list(templateMgr.getTemplate(file), System.out);
    } catch (Exception e) {
      if (quiet)
        for (Object o: Log.getBuffer())
          System.out.println(o);
      throw e;
    }
  }
  private static void help(String message) {
    if (message!=null)
      System.err.println("Error: "+message);
    System.out.println(
      "\nUsage: ...TemplateLister (-help | -f inputfile [-code] [-log] [-dest])"
     +"\n  -code Generates java code skeleton; otherwise you get a hierarchical listing of Slot and Section names. "
     +"\n  -log Turns on verbose logging info " 
     +"\n  -dest Turns on \"destructive\" show tags in TemplateManager " 
     +"\n"
    );
    if (message!=null)
      System.exit(2);
  }
}