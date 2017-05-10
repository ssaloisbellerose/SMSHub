package com.hub;

public class Notification {  
   private int count = 0;
   public Notification(){} 
    
   public Notification(int count){  
      this.count = count;
   }  
   public int getCount() { 
      return count; 
   }  

   public void setCount(int count) { 
      this.count = count; 
   }
} 
