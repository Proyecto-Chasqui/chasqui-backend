package chasqui.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;

public class DateRender
{
  public static SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
  public static SimpleDateFormat formatterDateTime = new SimpleDateFormat("dd/MM/yyyy k:mm");

  public static String renderDate(DateTime value) {
    if(value == null) {
      return "";
    }

    Date d = new Date(value.getMillis());
    return formatterDate.format(d);
  }

  public static String renderDate(Date value) {
    if(value == null) {
      return "";
    }
    return formatterDate.format(value);
  }

  public static String renderDateTime(DateTime value) {
    if(value == null) {
      return "";
    }

    Date d = new Date(value.getMillis());
    return formatterDateTime.format(d);
  }

  public static String renderDateTime(Date value) {
    if(value == null) {
      return "";
    }
    return formatterDateTime.format(value);
  }
}