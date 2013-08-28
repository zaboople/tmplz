package org.tmotte.tmplz.parse;
public class Types {
  public final static int

    //Base types:
    STATIC
      =1,
    SECTION
      =2,
    SLOT
      =2*2,

    //Include types:
    INCLUDE
      =2*2*2,
    FILLIN
      =2*2*2*2,
    SHOW
      =2*2*2*2*2,
    RENAME
      =2*2*2*2*2*2,
    REMOVE
      =2*2*2*2*2*2*2,

    //OTHER
    TRIM
      =2*2*2*2*2*2*2*2,
    TAG_WITH
      =2*2*2*2*2*2*2*2*2,
    ATTR_SECTION_2
      =2*2*2*2*2*2*2*2*2*2,
    ATTR_TARGET_2
      =2*2*2*2*2*2*2*2*2*2*2,
    BETWEEN
      =2*2*2*2*2*2*2*2*2*2*2*2;

  /** 
   * Rudimentary set logic, intended to test if a number is a member of a set of others. 
   * Normally invoked as <code>memberOf(aType, anotherType|another|yetAnother)</code>
   */
  public static boolean memberOf(int i, int c){
    return c==(i|c);
  }

  public static void main(String[] args) {
    int[] every={STATIC,SECTION,SLOT,INCLUDE,FILLIN,SHOW,RENAME,REMOVE,TRIM,TAG_WITH,ATTR_SECTION_2,ATTR_TARGET_2,BETWEEN};
    for (int i: every)
      System.out.print(i+" ");
    System.out.println();

    int all=0;
    for (int i: every)
      all|=i;
    System.out.println("ALL="+all+", but -1|all="+(all|-1));
    System.out.println(memberOf(-1, -1|all));
    System.out.println(memberOf(SECTION, -1|all));    
    {
      int x=SHOW|ATTR_SECTION_2;
      System.out.println(memberOf(SHOW,x)+" "+memberOf(REMOVE,x)+" "+memberOf(STATIC,x)+" "+memberOf(-1,x));
    }
    {
      int x=Types.SECTION|Types.FILLIN|Types.TRIM|Types.STATIC;
      System.out.println(memberOf(SECTION, x)+" "+memberOf(FILLIN, x)+" "+memberOf(REMOVE, x));
    }
    System.out.println(memberOf(SLOT, -1|SLOT));
    System.out.println(memberOf(-1, -1|SLOT));
  }
  
}