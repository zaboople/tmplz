package org.tmotte.common.text;
import java.util.List;
import java.util.ArrayList;

public class Base32 {
  long[] numbers;
  char[] represent={
    '0','1','2','3','4','5','6','7','8','9',
    'A','B','C','D','E','F','G','H','I','J',
    'K','M','N','P','Q','R','S','T','V','W',
    'X','Y'
  };
  public Base32() {
    List<Long> nums=new ArrayList();
    {
      long i=1, prev=0;
      while (prev<i) {
        nums.add(0, i);
        prev=i;
        i*=32;
      }
    }
    numbers=new long[nums.size()];
    for (int i=0; i<nums.size(); i++) 
      numbers[i]=nums.get(i);
  }
  public String encode(long val){
    StringBuilder sb=new StringBuilder();
    boolean ever=false;
    for (long x: numbers) {
      int temp=(int)(val/x);
      //System.out.println("EH "+val+"/"+x+"="+temp);
      if (temp>0) {
        ever=true;
        val-=((long)temp)*x;
      }
      if (ever)
        sb.append(represent[temp]);
    };
    return sb.toString();
  }
  public static void main(String[] args) {
    Base32 b32=new Base32();
    test(b32, System.currentTimeMillis());
    /*
    for (long n=Long.parseLong(args[0]); n>=Long.parseLong(args[1]); n--)
      System.out.println(n+"="+b32.encode(n)+" ");
    */
  }  
  private static void test(Base32 b32, long n) {
    System.out.println(n+"="+b32.encode(n)+" "+Long.toHexString(n));
  }
}