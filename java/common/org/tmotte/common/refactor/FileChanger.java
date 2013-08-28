package org.tmotte.common.refactor;
import java.io.File;
import java.io.BufferedReader;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.LinkedList;
import org.tmotte.common.io.Passthru;
import org.tmotte.common.io.Loader;
import org.tmotte.common.text.DelimitedString;

public class FileChanger{
  static String newName="tmplz";
  static String oldDir="../tmplz";
  public static void main(String[] args) throws Exception {
    FileChanger converter=new FileChanger();
    boolean test=false;
    boolean interactRead=true, interactWrite=true;
    for (int i=0; i<args.length; i++){
      String arg=args[i], argLo=args[i].toLowerCase();
      if (argLo.startsWith("-noask"))
        interactRead=false;
      else
      if (argLo.startsWith("-notell"))
        interactWrite=false;
      else
      if (argLo.startsWith("-from")){
        String s=getParam(args, ++i);
        if (s!=null)
          converter.setInputDir(new File(s));
      }
      else
      if (argLo.startsWith("-to")){
        String s=getParam(args, ++i);
        if (s!=null)
          converter.setOutputDir(new File(s));
      }
      else
      if (argLo.startsWith("-test"))
        test=true;
      else
      if (argLo.startsWith("-help")){
        help(System.out);
        return;
      }
      else
      if (argLo.equals("-replace")){
        String s1=getParam(args, ++i),
               s2=getParam(args, ++i);
        if (s1==null || s2==null)
          throw new UserInputException("-replace requires 2 arguments after it");
        converter.addFindReplaceContentPattern(s1, s2);
      }
      else
      if (argLo.startsWith("-replacefile")){
        String s1=getParam(args, ++i),
               s2=getParam(args, ++i);
        if (s1==null || s2==null)
          throw new UserInputException("-replacefile requires 2 arguments after it");
        converter.addFindReplaceFileNamePattern(s1, s2);
      }
      else
      if (argLo.startsWith("-x") || argLo.startsWith("-ext")){
        DelimitedString ds=new DelimitedString(".*\\.(", "|", ")$");
        while (++i<args.length && !args[i].startsWith("-"))
          ds.add(args[i]);        
        converter.setTextFilePattern(ds.toString());
        i--;
      }
    }
    converter.setInteractive(
      interactRead ?new BufferedReader(new InputStreamReader(System.in)) :null,
      interactWrite || interactRead ?new OutputStreamWriter(System.out) :null
    );
    if (converter.inputDir==null)
      throw new UserInputException("No input directory specified");
    if (converter.outputDir==null)
      throw new UserInputException("No output directory specified");
    if (test)
      System.out.println(
        "Interactive Read: "+(converter.userReader!=null)
       +"\nInteractive Write: "+(converter.userWriter!=null)
       +"\nFrom: "+converter.inputDir.getCanonicalPath()
       +"\nTo:   "+converter.outputDir.getCanonicalPath()
       +"\nText files match on: "+(
           converter.textFilePattern!=null 
             ? converter.textFilePattern.pattern() 
             : (converter.findReplaceContentPatterns.size()>0 ?"ALL" : "none")
         )
       +"\nFind/Replace content with: "+converter.findReplaceContentPatterns
       +"\nFind/Replace file names with: "+converter.findReplaceFileNamePatterns
      );
    else
      converter.go();
    System.out.flush();
  }
  public static void help(Appendable a) throws Exception {
    a.append(
      "Usage:\n"
     +"  java org.tmotte.apps.FileChanger -from <fromdir> -to <todir> \\\n"
     +"     [-replace <content regex> <replacement>]*                 \\\n"
     +"     [-replacefile <file name regex> <replacement>]*           \\\n"
     +"     [-ext <file extension>*]                                  \\\n"
     +"     [-test] [-noask] [-notell]                                \\\n"
     +"  -from, -to: Directory to read from and directory to write to \n"
    );
  }
  private static String getParam(String[] args, int index) {
    return index<args.length ?args[index] :null;
  }
  
  /////////////////////////////////
  // SETUP & INSTANCE VARIABLES: //
  /////////////////////////////////

  BufferedReader userReader;
  Appendable userWriter;
  File inputDir, outputDir;
  boolean inPlace=false, interactiveRead=false, interactiveWrite=false, test=false;
  Pattern textFilePattern;
  List<FindReplacePattern> 
    findReplaceContentPatterns=new java.util.LinkedList<FindReplacePattern>(),
    findReplaceFileNamePatterns=new java.util.LinkedList<FindReplacePattern>();

  public void setInputDir(File inputDir) throws Exception {
    if (!inputDir.exists())
      throw new UserInputException("Does not exist: "+inputDir);
    if (!inputDir.isDirectory())
      throw new UserInputException("Not a directory: "+inputDir);
    this.inputDir=inputDir;
    inPlace=inputDir.equals(outputDir);
  }
  public void setOutputDir(File outputDir) throws Exception {
    if (outputDir.exists() && !outputDir.isDirectory())
      throw new UserInputException("Not a directory: "+outputDir);
    this.outputDir=outputDir;
    inPlace=outputDir.equals(inputDir);
  }
  public void setTextFilePattern(String pattern) {
    textFilePattern=Pattern.compile(pattern);
  }
  public void setInteractive(BufferedReader reader, Appendable writer) {
    userReader=reader;
    userWriter=writer;
    interactiveRead=userReader!=null;
    interactiveWrite=userWriter!=null;
  }
  public void addFindReplaceContentPattern(String find, String replace) {
    FindReplacePattern frp=new FindReplacePattern();
    frp.find=Pattern.compile(find);
    frp.replace=replace;
    findReplaceContentPatterns.add(frp);
  }
  public void addFindReplaceFileNamePattern(String find, String replace) {
    FindReplacePattern frp=new FindReplacePattern();
    frp.find=Pattern.compile(find);
    frp.replace=replace;
    findReplaceFileNamePatterns.add(frp);
  }
  class FindReplacePattern {
    public Pattern find;
    public String replace;
    public String toString() {
      return "--> FIND: "+find.pattern()+" REPLACE: "+replace;
    }
  }
  

  //////////
  // RUN: //
  //////////
  
  public void go() throws Exception {
    if (!inputDir.exists())
       throw new UserInputException("Does not exist: "+inputDir);
    if (!clear(outputDir))
      return;
    copy(outputDir, inputDir.listFiles());
  }
  

  private void copy(File newDir, File... oldFiles) throws Exception{
    if (interactiveWrite) 
      log("Copying to: "+newDir.getCanonicalPath());
    newDir.mkdir();
    String basePath=newDir.getCanonicalPath()+File.separator;
    for (File oldFile: oldFiles) {
      if (interactiveWrite)
        log("  Copying from: "+oldFile.getCanonicalPath());
      String oldFileName=oldFile.getName();
      if (findReplaceFileNamePatterns.size()>0) 
         oldFileName=replaceAll(oldFileName, findReplaceFileNamePatterns);
      File newFile=new File(basePath+oldFileName);
      boolean fileNameChanged=!oldFile.getCanonicalPath().equals(newFile.getCanonicalPath());
      if (interactiveWrite)
        log("    Changed to name: "+newFile.getCanonicalPath());
      
      if (oldFile.isDirectory()){
        //Create new directory:
        if (fileNameChanged || !inPlace) 
          newFile.mkdir();
        copy(newFile, oldFile.listFiles());
      }
      else {
        //Copy file contents:
        FileInputStream oldStr=new FileInputStream(oldFile);
        
        boolean rewriteContent=
          findReplaceContentPatterns.size()>0 
          && 
          (textFilePattern==null || textFilePattern.matcher(oldFileName).matches());
        if (rewriteContent) {
          if (interactiveWrite)
            log("    Rewriting content");
          String fileContent=replaceAll(
            Loader.loadString(oldStr), findReplaceContentPatterns
          );
          FileOutputStream newStr=new FileOutputStream(newFile);
          Writer w=new OutputStreamWriter(newStr);
          w.write(fileContent);
          w.flush();
          newStr.close();
        }
        else 
        if (fileNameChanged)
          Passthru.pass(oldStr, new FileOutputStream(newFile), 512);
      }
      
      if (inPlace && fileNameChanged && oldFile.exists()){
        if (interactiveWrite)
          log("  Deleting "+oldFile);
        delete(oldFile);
      }
    }
  }
  
  private String replaceAll(String text, List<FindReplacePattern> patterns){
    for (FindReplacePattern fr: patterns) 
      text=fr.find.matcher(text).replaceAll(fr.replace);
    return text;
  }

  private boolean clear(File newDir) throws Exception {
    if (newDir.exists()) {
      if (interactiveRead && interactiveWrite && newDir.isDirectory()) {
        if (newDir.listFiles().length>0) {
          userWriter.append("Please confirm ");
          if (inPlace) 
            userWriter.append("IN-PLACE update to: "); 
          else
            userWriter.append("DELETE: ");
          userWriter.append(newDir.getCanonicalPath()+" Y/N: ");
          if (userWriter instanceof Writer) 
            ((Writer)userWriter).flush();
          else
          if (userWriter instanceof OutputStream) 
            ((OutputStream)userWriter).flush();
          String yesno=userReader.readLine();
          if (!yesno.toLowerCase().startsWith("y"))
            return false;
        }
      }  
      if (!inPlace)    
        delete(newDir);
    }  
    return true;
  }

  private void delete(File... files) throws Exception {
    for (File f: files) {
      if (f.isDirectory())
        delete(f.listFiles());
      if (!f.delete())
        throw new UserInputException("Could not delete: "+f);
    }
  }
  
  private void log(String s) throws IOException {
    userWriter.append(s);
    userWriter.append("\n");
    if (userWriter instanceof OutputStream) 
      ((OutputStream)userWriter).flush();
    if (userWriter instanceof Writer) 
      ((Writer)userWriter).flush();
  }
  
  private static class UserInputException extends Exception {
    public UserInputException(String s) {
      super(s);
    }
  }
}