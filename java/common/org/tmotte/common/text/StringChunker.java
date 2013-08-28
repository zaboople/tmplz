package org.tmotte.common.text;
import java.util.regex.*;

/**
 * Provides the ability to &quot;walk&quot; through a String
 * using <code>find()</code> methods, parsing found &quot;chunks&quot; one at a time. 
 * Interally, there is a text buffer and a pointer to a position in that buffer;
 * the pointer moves as <code>find()</code> executes.
 */
public class StringChunker{

  private String text;
  private int index=0;
  private int foundAt=-1;
  private String upTo, found;
  private StringChunker parentChunker;

  //////////////////////////
  // CONSTRUCTORS AND     //
  // PSEUDO-CONSTRUCTORS: //
  //////////////////////////

  /** 
   * Creates a StringChunker ready to use. 
   * @param text The String to be parsed.
   */
  public StringChunker(String text){
    reset(text);
  }
  /**
   * Creates an uninitialized StringChunker. Invoke <code>reset(String)</code> to
   * initialize it.
   */
  public StringChunker(){
  }

  /**
   * Sets the internal index back to 0, as if find()
   * had never been invoked.
   */
  public StringChunker reset(){
    index=0;
    upTo=null;
    found=null;
    return this;
  }
  /**
   * Completely reinitializes this StringChunker. Simply avoids
   * creating a new instance when an old one is laying around.
   */
  public StringChunker reset(String text){
    reset();
    if (text==null)
      throw new IllegalStateException("Text provided was null");
    this.text=text;
    upTo=null;
    found=null;
    return this;
  }

  ///////////////////////
  // BASIC PROPERTIES: //
  ///////////////////////

  /** 
   * @return The entire internal buffer
   */
  public String getText(){
    return text;
  }

  ////////////////////////////
  // INDEX STATE RETRIEVAL: //
  ////////////////////////////
  
  /** 
   * @return The current index into the internal buffer.
   *   When getRest() is invoked, this will be the
   *   buffer length.
   */
  public int getIndex(){
    return index;
  }
  /** 
   * @return The index of where the last find() found
   *   something.
   */
  public int foundAt(){
    return foundAt;
  }
  /**
   * @return Whether the internal index has advanced
   *   to the end of the internal buffer.
   */
  public boolean finished(){
    return index>=text.length();
  }  

  /////////////////////////
  // INDEX MANIPULATION: //
  /////////////////////////

  /** Moves the internal buffer index forward the specified distance.*/
  public void move(int distance){
    index+=distance;
  }
  /** Sets the internal buffer index to a specific position.*/
  public void setIndex(int index){
    this.index=index;
  }

  ///////////////////
  // FIND METHODS: //
  ///////////////////
  
  /** 
   * Finds the given substring, starting at the current index, and sets
   * the internal index to the location directly following the text found.
   * Invoke getUpTo(), getIncluding(), getFound() etc. to retrieve
   * the text found and text relative to its location.
   * @return true if lookFor was located. 
   */    
  public boolean find(String lookFor) {
    upTo=null;
    found=null;
    if (!rangeCheck())
      return false;
    else {      
      foundAt=text.indexOf(lookFor, index);      
      if (foundAt<0) {
        upTo=null;
        found=null;
      } else {
        upTo=text.substring(index,foundAt);
        int endAt=foundAt+lookFor.length();
        index=endAt;
        found=text.substring(foundAt,endAt);
      }
      return foundAt > -1;
    }
  } 
  /**
   * Makes it easier to deal with end-of-String vs. text-found conditions in loops.
   * @return true if lookFor is found, or if  
   * not found but the internal index was still before the end of the
   * text. In the latter case, the next call to getUpTo() will return 
   * the remaining text, and further calls to findOrFinish() will return false.
   */
  public boolean findOrFinish(String lookFor) {
    if (finished())
      return false;
    else
    if (find(lookFor))
      return true;
    else{
      upTo=text.substring(index, text.length());
      index=text.length();
      found="";
      return true;
    }
  }
  /**
   * Does the same as <code>find(String)</code>, but using a regular expression.
   */
  public boolean find(java.util.regex.Pattern regex){
    upTo=null;
    found=null;
    foundAt=-1;
    if (!rangeCheck())
      return false;
    else {    
      Matcher matcher=regex.matcher(text);      
      boolean worked=matcher.find(index);
      if (worked) {
        foundAt=matcher.start();
        int endAt=matcher.end();
        upTo=text.substring(index,foundAt);
        found=text.substring(foundAt,endAt);
        index=endAt;
      } else {
        upTo=null;
        found=null;
      }
      return worked;
    }
  }
  /**
   * Does the same as <code>findOrFinish(String)</code>, but using a regular expression.
   */
  public boolean findOrFinish(java.util.regex.Pattern regex){
    if (finished())
      return false;
    else
    if (find(regex))
      return true;
    else{
      upTo=text.substring(index, text.length());
      index=text.length();
      found="";
      return true;
    }
  }

  
  ////////////////////////
  // "CHUNK" RETRIEVAL: //
  ////////////////////////
  
  /** 
   * Obtains the text <i>before</i> the text found by <code>find()</code>.
   * @return The text between text found by the last successful find() and the successful 
   *   find() before that; or, if there has been only one successful find(), the text between 
   *   position 0 and the text found by that find(). 
   */
  public String getUpTo(){
    return upTo;
  }
  /** 
   * Obtains the text that was found by <code>find()</code>.
   * @return text located during last execution of find() 
   */
  public String getFound() {
    return found;
  }
  /** 
   * Combines the results of <code>getUpTo()</code. and <code>getFound()</code>.
   * @return getUpTo() + getFound() 
   */
  public String getIncluding(){
    return getUpTo()+getFound();
  }
  /** 
   * Provides all the text up to the current position of the internal pointer; i.e. everything
   * up to and <i>including</i> the last item found.
   */
  public String getEverythingSoFar(){
    StringBuffer sb=new StringBuffer();
    getEverythingSoFar(sb);
    return sb.toString();
  }
  private void getEverythingSoFar(StringBuffer sb){    
    if (parentChunker!=null)
      parentChunker.getEverythingSoFar(sb);
    sb.append(
      finished() ?text :text.substring(0, index)
    );    
  }

  ////////////////////////////
  // COMBINATION FIND/CHUNK //
  // STATE RETRIEVAL:       //
  ////////////////////////////

  /** 
   * Combines find() and getUpTo(). 
   * @return text located during the find() 
   */
  public String getUpTo(String lookFor) {
    find(lookFor);
    return getUpTo();
  }
  /** 
   * Combines find() and getIncluding(). 
   * @return text located during the find() 
   */
  public String getIncluding(String lookFor) {
    find(lookFor);
    return getIncluding();
  }
  /**  
   * Moves index to end of buffer and retrieves rest of text.
   * @return Remainder of buffer, or "" if nothing is left. 
   */
  public String getRest(){
    if (finished())
      return "";
    else {
      String result=text.substring(index);
      index=text.length();
      return result;
    }
  }
  /** 
   * If last find() failed, invokes getRest(), else getUpTo().
   */
  public String getUpToOrGetRest(){
    if (upTo==null)
      return getRest();
    else
      return upTo;
  }
  /** 
   * Combines find() with getUpToOrGetRest(). 
   */
  public String getUpToOrGetRest(String val){
    find(val);
    return getUpToOrGetRest();
  }


  ////////////////
  // INTERNALS: //
  ////////////////

  private boolean rangeCheck(){
    return text!=null && index<text.length();    
  } 


  ///////////////////
  // TEST HARNESS ///
  ///////////////////


  /** Call with two args: 1) A string to search 2) A regex to search with. */
  public static void main(String[] args)throws Exception{
    StringChunker sc=new StringChunker(args[0]);
    System.out.println("Upto/Found:");
    Pattern regex=Pattern.compile(args[1]);
    System.out.println("\nTest 1:");
    while (sc.find(regex)){
      rip("upto:", sc.getUpTo());
      rip("found:", sc.getFound());
    }
    System.out.println("Leftovers:");
    rip("rest:", sc.getRest());

    System.out.println("\nTest 2:");
    sc.reset();
    while (sc.findOrFinish(regex)){
      rip("upto:", sc.getUpTo());
      rip("found:", sc.getFound());
    }
  }  

  private static void rip(String s1, String s2){
    System.out.println(s1+">"+s2+"<");
  }

}