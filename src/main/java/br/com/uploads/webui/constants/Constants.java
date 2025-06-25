package br.com.uploads.webui.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Constants {

  public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  public static final String UPLOADS_BUCKET_NAME = "gf-hack-bucket";
  public static final String EMAIL_CONTEXT_KEY = "email";

}
