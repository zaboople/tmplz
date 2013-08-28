package org.tmotte.tmplz.util;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.LinkedList;
import org.tmotte.common.text.StackTracer;
import org.tmotte.common.text.DelimitedString;

/** 
 * For internal use. This is the passthru point for all logging. It looks for a java.util.logging.Logger
 * mapped to "org.tmotte.tmplz". No, we aren't using log4j because it's not a J2EE standard. Meh.
 */
public class Log {

  private static List buffer=null;
  private static int maxBuffer=600;
  private final static Log myLog=new Log();
  private final Logger logger=Logger.getLogger("org.tmotte.tmplz");  

  public static Logger getLogger(){
    return myLog.logger;
  }


  //////////////////////
  // BUFFERED LOGGING://
  //////////////////////


  /** 
   * If this is invoked, messages don't go to the log, but instead to an internal static buffer.
   * Use getBuffer() to retrieve the messages.
   */
  public static synchronized void bufferEverything(int maxBufferSize){
    buffer=new LinkedList();
    maxBuffer=maxBufferSize;
  }
  public static synchronized void bufferEverything(){
    bufferEverything(600);
  }
  public static synchronized List getBuffer(){
    return buffer;
  }
  public static synchronized String dumpBuffer(String delimiter){
    if (buffer==null)
      return "";
    return new DelimitedString(delimiter).add(buffer).toString();
  }
  public static void clearBuffer() {
    if (buffer!=null)
      buffer.clear();
  }
  private static void buffer(String message){
    if (buffer.size()>1000)
      buffer.remove(0);
    buffer.add(message);
  }


  public static void info(String aclass, String amethod, String message) {
    if (buffer!=null)
      buffer.add(aclass+amethod+message);
    else
      getLogger().logp(Level.INFO, aclass, amethod, message);
  }
  public static void info(String aclass, String amethod, String message, Object messageX) {
    if (buffer!=null)
      buffer.add(aclass+amethod+message+messageX);
    else
      getLogger().logp(Level.INFO, aclass, amethod, message, messageX);
  }
  public static void info(String message) {
    if (buffer!=null)
      buffer.add(message);
    else
      getLogger().log(Level.INFO, message);
  }
  

  public static void fine(String aclass, String amethod, String message) {
    if (buffer!=null)
      buffer.add(aclass+amethod+message);
    else
      getLogger().logp(Level.FINE, aclass, amethod, message);
  }
  public static void fine(String message) {
    if (buffer!=null)
      buffer.add(message);
    else
      getLogger().log(Level.FINE, message);
  }


  public static void finest(String aclass, String amethod, String message) {
    if (buffer!=null)
      buffer.add(aclass+amethod+message);
    else
      getLogger().logp(Level.FINEST, aclass, amethod, message);
  }
  public static void finest(String message) {
    if (buffer!=null)
      buffer.add(message);
    else
      getLogger().log(Level.FINEST, message);
  }


  public static void warning(String message) {
    if (buffer!=null)
      buffer.add(message);
    else
      getLogger().log(Level.WARNING, message);
  }

  public static void error(String message, Exception e){
    if (buffer!=null)
      buffer.add(message+" "+StackTracer.getStackTrace(e));
    else
      getLogger().log(Level.SEVERE, message, e);
  }
  public static void error(String message, String m2){
    if (buffer!=null)
      buffer.add(message+" "+m2);
    else
      getLogger().log(Level.SEVERE, message+ m2);
  }

    
}