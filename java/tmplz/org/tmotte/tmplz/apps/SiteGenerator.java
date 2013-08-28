package org.tmotte.tmplz.apps;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;
import org.tmotte.common.io.Passthru;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.builtin.FileTextLoaderFactory;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.util.Log;

/** 
 * <p>This class was built for instances where a servlet engine to run ContentViewer isn't available, so it
 * is preferable to generate a "static" site from template content. It is primarily designed to be executed as a command-line
 * program, so that most of its property methods have corresponding command-line parameters. 
 * <p>General operation: SiteGenerator takes as its input a directory of template files, runs them through TemplateManager 
 * for parsing, and generates one file for each input file using Section.appendTo() on the parsed templates. </p> 
 */
public class SiteGenerator {

  /////////////////////////
  // INSTANCE VARIABLES: //
  /////////////////////////

  TemplateManager tm=new TemplateManager();
  TextLoadMgr textLoadMgr=tm.getTextLoadMgr();
  Map fillins;
  File indir;
  String outdirstr;
  int indirstrlength=-1;
  String contextPath;
  FileFilter 
    acceptFileFilter=new FileFilter(){
      public boolean accept(File file) {
        return true;
      }
    },
    templateFileFilter=new FileFilter() {
      public boolean accept(File file) {
        String fileName=file.getName();
        return fileName.endsWith(".html")||fileName.endsWith(".htm")||fileName.endsWith(".js")||fileName.endsWith(".css");
      }
    };
    

  ////////////
  // SETUP: //
  ////////////
  
  public SiteGenerator() {
    final FileTextLoaderFactory fac=new FileTextLoaderFactory();
    textLoadMgr.register(fac);
  }
  /** 
   * Sets the input directory. The application will not run without an input directory.
   */
  public void setInputDir(String dir) {
    this.indir=new File(dir);
    this.indirstrlength=indir.getPath().length();
  }
  /** 
   * Sets the output directory. The application will not run without an output directory.
   */
   public void setOutputDir(String dir) {
    this.outdirstr=new File(dir).getPath();
  }
  /**
   * Controls whether we fill any Slot named "Context" with a relative dotted-slashed path according
   * to the position of the containing file in the hierarchy, starting with "./" at the root.
   */
  public void setRelativeContext(boolean rel) {
    this.contextPath=rel ?"./" :null;
  }
  /**
   * Use this to customize the internal TextLoadMgr.
   */
  public TextLoadMgr getTextLoadMgr(){
    return textLoadMgr;
  }
  /**
   * The filter will be used to determine whether files should be included. This should not restrict
   * itself to templates if there are also image files, javascript, css etc.. Use setTemplateFileFilter()
   * to determine what files are templates.
   */
  public void setFileFilter(FileFilter filter){
    this.acceptFileFilter=filter;
  }
  /**
   * This filter determines what files are treated as templates. All other files will simply be copied
   * as-is if the target & source timestamps don't match.
   */
  public void setTemplateFileFilter(FileFilter filter){
    this.templateFileFilter=filter;
  }
  /**
   * This will invoke <code>Section.fillin(Map, true)</code> against every template. This gives us
   * a way to fillin an arbitrary set of Slots, recursively, in all templates. 
   */
  public void setFillins(Map fillins) {
    this.fillins=fillins;
  }



  ////////////////
  // EXECUTION: //
  ////////////////
  
  /**
   * This executes a single update.
   */
  public void process() throws Exception {
    process(indir, contextPath);
  }
  private void process(File inputdir, String context) throws Exception {
    File[] list=inputdir.listFiles();
    if (list==null)//Yeah, stupid, but this can happen
      throw new RuntimeException(inputdir+" does not appear to exist ");
    String nextLevelContext=null;
    if (context!=null) 
      nextLevelContext=context.startsWith(".")
        ?"../"+context
        :context;
    for (File fromFile: list){
      if (!acceptFileFilter.accept(fromFile))
        continue;
      String fromPath=fromFile.getPath();
      String newName=outdirstr+fromPath.substring(indirstrlength);
      File toFile=new File(newName);
      makeParents(toFile);
      if (fromFile.isDirectory()){
        toFile.mkdir();
        process(fromFile, nextLevelContext);
      }  
      else 
      if (templateFileFilter.accept(fromFile)){
        FileOutputStream fos=new FileOutputStream(toFile);
        try {
          logFileChange(toFile, false);
          PrintWriter pw=new PrintWriter(fos);
          Section section=tm.getTemplate(fromPath);
          if (fillins!=null)
            section.fillin(fillins, true);
          if (context!=null && section.hasSlot("Context"))
            section.fillin("Context", context, false);
          section.appendTo(pw);
          pw.flush();
          fos.flush();
        } finally {
          fos.close();
        }
      }
      else {
        //Treat as non-template:
        long fromLastModified=fromFile.lastModified();
        logFileChange(toFile, true);
        Passthru.pass(new FileInputStream(fromFile), new FileOutputStream(toFile));
        toFile.setLastModified(fromLastModified);
      }

    }
  }
  private void logFileChange(File toFile, boolean plain) {
    Log.info(
      (toFile.exists() ?"Updating " :"Creating ")
     +(plain           ?"plain file: " : "template: ")
     +toFile
    );
  }
  private static void makeParents(File newFile){
    //Yeah sometimes parentDir comes out null. Quirky Java API's are quirky.
    File parentDir=newFile.getParentFile();
    if (parentDir!=null && !parentDir.exists()){
      makeParents(parentDir);
      parentDir.mkdir();
    }
  }

  ///////////////////////////
  // COMMAND-LINE CONTROL: //
  ///////////////////////////
  
  /**
   * This is intended for use from a typical public static void main(String[]). Null values
   * in the args array will be ignored (in case you have custom parameters). At the command line,
   * add "-help" as a parameter to get a full list of command-line options.
   */
  public void runCommandLine(String[] args, Appendable out) throws Exception {
    String fromDir=null, toDir=null, context="", filterAll=null, filterTemplates=null, error=null;
    Map doFills=null;
    boolean help=false, loop=false, relativeContext=false;
    for (int i=0; i<args.length; i++) {
      String arg=args[i];
      if (arg==null)
        continue;
      arg=arg.trim().toLowerCase();
      if (arg.equals("-help"))
        help=true;
      else
      if (arg.equals("-repeat"))
        loop=true;
      else
      if (arg.equals("-from"))
        fromDir=expect(args, ++i);
      else
      if (arg.equals("-to"))
        toDir=expect(args, ++i);
      else
      if (arg.equals("-relativecontext"))
        relativeContext=true;
      else
      if (arg.equals("-filter"))
        filterAll=expect(args, ++i);
      else
      if (arg.equals("-filtertemplates"))
        filterTemplates=expect(args, ++i);
      else
      if (arg.equals("-fillin")){
        String name=expect(args, ++i);
        String val=expect(args, ++i);
        if (doFills==null)
          doFills=new HashMap();
        doFills.put(name, val);
      }
      else
      if (arg.equals("-fillin.props")){
        String filename=expect(args, ++i);
        if (doFills==null)
          doFills=new HashMap();
        InputStream instr=new java.io.FileInputStream(filename);
        Properties props=new Properties();
        try {
          props.load(instr);
        } finally {
          instr.close();
        }
        for (Object o: props.keySet()){
          String name=o.toString();
          doFills.put(name, props.getProperty(name));
        }
      }
      else
        error=arg;
    }

    //Errors, help:
    if (error!=null)
      System.out.println("Unexpected: "+error);
    if (help || toDir==null || fromDir==null || error!=null){
      help(out);
      if (!help)
        System.exit(1);
      return;
    }
    
    //Run configuration we've received:
    setInputDir(fromDir);
    setOutputDir(toDir);
    setRelativeContext(relativeContext);
    if (filterAll!=null)
      setFileFilter(new MyFileFilter(filterAll));
    if (filterTemplates!=null)
      setTemplateFileFilter(new MyFileFilter(filterTemplates));
    if (doFills!=null)
      setFillins(doFills);

    //Run processing:
    java.io.BufferedReader br=loop ?new java.io.BufferedReader(new java.io.InputStreamReader(System.in)) :null;
    do {
      process();
      System.out.print("Build complete... ");
      if (loop){
        String s=br.readLine();
        if (s==null || s.toLowerCase().startsWith("q"))
          break;
      }  
    } while (loop);
  }
  private static String expect(String[] args, int index) {
    if (index>=args.length)
      return null;
    return args[index];
  }
  private static class MyFileFilter implements FileFilter {
    final Pattern p;
    public MyFileFilter(String filter) {
      p=Pattern.compile(".*"+filter+".*");
    }
    public boolean accept(File file) {
      return p.matcher(file.getPath()).matches();
    }
  }
  
  
  private void help(Appendable a) throws java.io.IOException {
    a.append(
      "\nUsage: java ...SiteGenerator -from <input directory> -to <output directory>  \\"
      +"\n    [-help] [-repeat] [-filter <str>] [-filtertemplates <str>] [-context <c>]>"
      +"\n    [-fillin <name> <val>]* [-fillin.props <filename>]"
      +"\n"
      +"\n  -help "
      +"\n     Prints this message. "
      +"\n"
      +"\n  -repeat "
      +"\n     Run once every time the <enter> key is pressed, until \"q\" is typed. "
      +"\n"
      +"\n  -filter <str>"
      +"\n     Only copy files that match the regular expression <str>. "
      +"\n"
      +"\n  -fillin <name> <val>"
      +"\n     Will recursively fill in all Slots of name <name> with <val> in every template."
      +"\n      This parameter can be used more than once, to fill in multiple slots. "
      +"\n"
      +"\n  -fillin.props <filename>"
      +"\n     Similar to -fillin, but loads all the fillin slot names and values from a file"
      +"\n     named <filename>, which should be in the standard java properties format, e.g."
      +"\n     \"myslot=myvalue\", with one line per key/value pair. "
      +"\n"
      +"\n  -filtertemplates <str>"
      +"\n     Only files that match the regular expression <str> will be treated as"
      +"\n     tmplz templates. Other files will be copied as is. By default, .html, "
      +"\n     .htm, .css & .js file extensions are assumed to be templates."
      +"\n"
      +"\n  -relativeContext "
      +"\n     When a slot named \"Context\" (e.g. [$SlotContext]) is found, it will be "
      +"\n     replaced with a dotted-slashed path according to the depth of the file in the "
      +"\n     directory hierarchy, starting with \"./\" at the root, and prepending \"../\""
      +"\n     as we descend. This allows one to generate paths (such as for html attributes like \"src\","
      +"\n     \"href\", etc.) without knowing the root path of the web site, so that all paths are relative. "
      +"\n"
   ); 
  }
  
  /** This is of course the method invoked when this class is executed from the command line. */
  public static void main(String[] args) throws Exception {
    Log.getLogger().setLevel(java.util.logging.Level.INFO);
    new SiteGenerator().runCommandLine(args, System.out);
  }
}
