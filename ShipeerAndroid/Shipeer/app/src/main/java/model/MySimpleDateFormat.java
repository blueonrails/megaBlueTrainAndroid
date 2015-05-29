package model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mifercre on 07/05/15.
 */
public class MySimpleDateFormat extends SimpleDateFormat {

    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String MY_FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm";
    private static final String MY_FORMAT_ONLY_DATE = "dd/MM/yyyy";
    private static final String ANDROID_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    private SimpleDateFormat ISOformat = new SimpleDateFormat(ISO_FORMAT, Locale.US);
    private SimpleDateFormat myFormatDateTime = new SimpleDateFormat(MY_FORMAT_DATE_TIME, Locale.US);
    private SimpleDateFormat myFormatOnlyDate = new SimpleDateFormat(MY_FORMAT_ONLY_DATE, Locale.US);
    private SimpleDateFormat androidFormat = new SimpleDateFormat(ANDROID_FORMAT, Locale.US);

    private static MySimpleDateFormat mySimpleDateFormat;

    public static MySimpleDateFormat getMySimpleDateFormat() {
        if (mySimpleDateFormat == null) {
            mySimpleDateFormat = new MySimpleDateFormat();
            mySimpleDateFormat.ISOformat = new SimpleDateFormat(ISO_FORMAT, Locale.US);
            mySimpleDateFormat.myFormatDateTime = new SimpleDateFormat(MY_FORMAT_DATE_TIME, Locale.US);
            //myFormatDateTime.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            mySimpleDateFormat.myFormatOnlyDate = new SimpleDateFormat(MY_FORMAT_ONLY_DATE, Locale.US);
            mySimpleDateFormat.androidFormat = new SimpleDateFormat(ANDROID_FORMAT, Locale.US);
        }
        return mySimpleDateFormat;
    }

    public static String convertFromAndroidToISO(String androidDateString) {
        if (mySimpleDateFormat == null) getMySimpleDateFormat();
        Log.d("convtFromAndroidToISO 1", androidDateString);

        Date androidDate = null;
        try {
            androidDate = mySimpleDateFormat.androidFormat.parse(androidDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("convtFromAndroidToISO 2", androidDate.toString());

        String isoRes = mySimpleDateFormat.ISOformat.format(androidDate);
        Log.d("convtFromAndroidToISO 3", isoRes);

        return isoRes;
    }

    public static String convertFromISOtoAndroid(String ISODateString) {
        if (mySimpleDateFormat == null) getMySimpleDateFormat();
        Log.d("convtFromISOtoAndroid 1", ISODateString);
        Calendar cal = Calendar.getInstance();
        Date currentDatetime = cal.getTime();

        String currentDatetimeISO = mySimpleDateFormat.ISOformat.format(currentDatetime);
        String currentTimeZone = currentDatetimeISO.substring(23, currentDatetimeISO.length());

        if (ISODateString.endsWith("Z")) {
            ISODateString = ISODateString.substring(0, ISODateString.length() - 1) + currentTimeZone;//"+0000";
        }

        Log.d("convtFromISOtoAndroid 2", ISODateString);
        Date ISODate = null;
        try {
            ISODate = mySimpleDateFormat.ISOformat.parse(ISODateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("convtFromISOtoAndroid 3", ISODate.toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        calendar.setTime(ISODate);
        calendar.add(Calendar.HOUR, 2); // Spain is at GMT+2. Correct this in the future.

        String res = calendar.getTime().toString();
        Log.d("convtFromISOtoAndroid 4", res);

        return res;
    }

    public static Date parseAndroidDate(String androidDateString) {
        if (mySimpleDateFormat == null) getMySimpleDateFormat();
        Log.d("parseAndroidDate 1", androidDateString);
        Date androidDate = null;
        try {
            androidDate = mySimpleDateFormat.androidFormat.parse(androidDateString.replace("CEST", "GMT+2"));
            return androidDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(androidDate != null) Log.d("parseAndroidDate 2", androidDate.toString());
        return null;
    }

    public static String formatAndroidDateTime(Date androidDate) {
        /**String[] ids = TimeZone.getAvailableIDs();
        Arrays.sort(ids);
        for (String id : ids) {
            System.out.println(id);
        }**/
        Date androidDate2 = null;
        if(androidDate != null) {
            try {
                androidDate2 = mySimpleDateFormat.androidFormat.parse(androidDate.toString().replace("CEST", "GMT+2"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (mySimpleDateFormat == null) getMySimpleDateFormat();
            return mySimpleDateFormat.myFormatDateTime.format(androidDate2);
        }
        return null;
    }

    public static String formatAndroidOnlyDate(Date androidDate) {
        if (mySimpleDateFormat == null) getMySimpleDateFormat();
        return mySimpleDateFormat.myFormatOnlyDate.format(androidDate);
    }

/**public Date parseISODateString(String ISODateString) throws ParseException {
 Log.d("DATE ZONE", ISODateString);
 Calendar cal = Calendar.getInstance();
 Date currentDatetime = cal.getTime();

 String currentDatetimeISO = ISOformat.format(currentDatetime);
 String currentTimeZone = currentDatetimeISO.substring(23, currentDatetimeISO.length());

 if(ISODateString.endsWith("Z")) {
 ISODateString = ISODateString.substring(0, ISODateString.length()-1) + currentTimeZone;//"+0000";
 }

 //TimeZone tz = cal.getTimeZone();
 //Log.d("Time zone","="+tz.getDisplayName() + ", " + tz.toString() + ", " + tz.getID());

 Log.d("DATE ZONE", ISODateString);
 Date ISODate = ISOformat.parse(ISODateString);

 return ISODate;
 }**/


    /**public static String formatFromISOtoMyFormatDateTime(String ISODateString) {
     if(ISOformat == null) ISOformat = new SimpleDateFormat(ISO_FORMAT);
     try {
     Log.d("DATE ZONE", ISODateString);
     Calendar cal = Calendar.getInstance();
     Date currentDatetime = cal.getTime();

     String currentDatetimeISO = ISOformat.format(currentDatetime);
     String currentTimeZone = currentDatetimeISO.substring(23, currentDatetimeISO.length());

     if(ISODateString.endsWith("Z")) {
     ISODateString = ISODateString.substring(0, ISODateString.length()-1) + currentTimeZone;//"+0000";
     }

     //TimeZone tz = cal.getTimeZone();
     //Log.d("Time zone","="+tz.getDisplayName() + ", " + tz.toString() + ", " + tz.getID());

     Log.d("DATE ZONE", ISODateString);
     Date ISODate = ISOformat.parse(ISODateString);

     Calendar calendar = Calendar.getInstance();
     calendar.setTime(ISODate);
     calendar.add(Calendar.HOUR, 2); // Spain is at GMT+2. Correct this in the future.

     String res = myFormatDateTime.format(calendar.getTime());
     Log.d("DATE ZONE", res);
     return res;
     } catch (ParseException e) {
     e.printStackTrace();
     }
     return null;
     }

     public static String formatOnlyDate(Date date) {
     if(myFormatOnlyDate == null) myFormatOnlyDate = new SimpleDateFormat(MY_FORMAT_ONLY_DATE);
     return myFormatOnlyDate.format(date);
     }

     public static String formatDateTime(Date date) {
     if(myFormatDateTime == null) myFormatDateTime = new SimpleDateFormat(MY_FORMAT_DATE_TIME);
     return myFormatDateTime.format(date);
     }

     public static Date parseDateTime(String dateTime) {
     try {
     return myFormatDateTime.parse(dateTime);
     } catch (ParseException e) {
     e.printStackTrace();
     }
     return null;
     }**/

    /**public String getESDate(Date date) {
     //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
     String dateFormatted = MySimpleDateFormat.formatOnlyDate(date);
     Log.d("DATE TO FORMAT", dateFormatted);
     return dateFormatted;
     String res = "";
     String[] parts = dateFormatted.split("/");
     switch (parts[0].toLowerCase()) {
     case "lun":
     res = getString(R.string.monday) + " ";
     break;
     case "mar":
     res = getString(R.string.tuesday) + " ";
     break;
     case "mié":
     res = getString(R.string.wednesday) + " ";
     break;
     case "jue":
     res = getString(R.string.thursday) + " ";
     break;
     case "vie":
     res = getString(R.string.friday) + " ";
     break;
     case "sáb":
     res = getString(R.string.saturday) + " ";
     break;
     case "dom":
     res = getString(R.string.sunday) + " ";
     break;
     }

     res = res + parts[1] + " " + getString(R.string.of) + " ";

     switch (parts[2].toLowerCase()) {
     case "01":
     res += getString(R.string.january) + " ";
     break;
     case "02":
     res += getString(R.string.february) + " ";
     break;
     case "03":
     res += getString(R.string.march) + " ";
     break;
     case "04":
     res += getString(R.string.april) + " ";
     break;
     case "05":
     res += getString(R.string.may) + " ";
     break;
     case "06":
     res += getString(R.string.june) + " ";
     break;
     case "07":
     res += getString(R.string.july) + " ";
     break;
     case "08":
     res += getString(R.string.august) + " ";
     break;
     case "09":
     res += getString(R.string.september) + " ";
     break;
     case "10":
     res += getString(R.string.october) + " ";
     break;
     case "11":
     res += getString(R.string.november) + " ";
     break;
     case "12":
     res += getString(R.string.december) + " ";
     break;
     }

     return res;

     }**/


}
