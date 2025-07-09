package br.com.uploads.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static br.com.uploads.webui.constants.Constants.YYYY_MM_DD_HH_MM_SS_SSS;

@UtilityClass
public class Dates {

  public static String format(LocalDateTime value) {
    return value.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS));
  }

}
