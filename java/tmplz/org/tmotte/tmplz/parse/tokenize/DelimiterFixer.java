package org.tmotte.tmplz.parse.tokenize;
import java.util.regex.Pattern;
import org.tmotte.common.text.DelimitedString;

public class DelimiterFixer {
  static String[] items={
    "[",
    "]",
    "*",
    ".",
    "$",
    "^",
    "%",
    "(",
    ")",
    "?",
    "!",
    "+",
    "-",
    "\\"
  };
  static String allItems=new DelimitedString("(", "|", ")", "\\", "").addEach(items).toString();
  static Pattern p=Pattern.compile(allItems);
  public static String fix(String s) {
    return p.matcher(s).replaceAll("\\\\$1");
  }
  public static void main(String[] args) {
    System.out.println(allItems);
    Pattern p=Pattern.compile(fix(args[0]));
    System.out.println(p.matcher(args[1]).replaceAll("->Replaced<-"));    
  }

}