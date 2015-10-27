package org.outing.medicine.tools;

import java.util.ArrayList;

/**
 * Created by apple on 15/10/27.
 */
public class GetStringBetweenComma {

    public static String getStrArr(String inString,int number){
        ArrayList<String> outArray=new ArrayList<String>();
        int x=0;
        for (int i = 0; i < inString.length(); i++) {
          if (inString.charAt(i)==','||inString.charAt(i)=='，'){
              outArray.add( inString.substring(x,i));
              x=i+1;
          }
        }
        outArray.add(inString.substring(x,inString.length()));
        return outArray.get(number);
    }

}
