package org.tmotte.common.text;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Locale;

/** 
 *  This is a wrapper for DateFormat. It uses a WeakHashMap as a pool for DateFormat objects,
 *  which are expensive to create and garbage collect. 
 */
public abstract class DateFormatPool {
 
  public String format(Date date){
    DateFormat formatter=getDF();
    try {
      return formatter.format(date);
    } finally {
      returnDF(formatter);
    }    
  }
  public Date parse(String date) throws ParseException {
    DateFormat sdf=getDF();
    try {
      return sdf.parse(date);
    } finally {
      returnDF(sdf);
    }
  }
  /** You implement this part. */
  protected abstract DateFormat create();
  
  private Stack<DateFormat> sdfStack=new Stack<DateFormat>();
  private synchronized DateFormat getDF(){
    DateFormat sdf;
    if (sdfStack.empty()) 
      sdf=create();
    else
      sdf=sdfStack.pop();      
    return sdf;
  }
  private synchronized void returnDF(DateFormat f){
    sdfStack.push(f);
  }

}