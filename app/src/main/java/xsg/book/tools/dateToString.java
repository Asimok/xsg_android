package xsg.book.tools;


import java.text.SimpleDateFormat;
import java.util.Date;

public class dateToString {


    public static String nowdateToString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String reStr = sdf.format(date);
        System.out.println(reStr);
        return reStr;
    }

    public static String nowdateToString2() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String reStr = sdf.format(date);
        System.out.println(reStr);
        return reStr;
    }

    public static String nowdateToString3() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String reStr = sdf.format(date);
        System.out.println(reStr);
        return reStr;
    }

    public static String nowdateToString4() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String reStr = sdf.format(date);
        System.out.println(reStr);
        return reStr;
    }
}
