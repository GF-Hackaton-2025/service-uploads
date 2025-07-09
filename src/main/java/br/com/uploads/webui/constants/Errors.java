package br.com.uploads.webui.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Errors {

  public static final String FIELD_REQUIRED = "Field is required";
  public static final String HEADER_REQUIRED = "Header is required";
  public static final String ERROR_UNAUTHORIZED = "Unauthorized access";
  public static final String ERROR_NO_JWT_TOKEN = "No jwt token";
  public static final String ERROR_MESSAGE = "Error";
  public static final String VALIDATION_FIELD_ERROR = "Validation field with error";

  // Pagination
  public static final String PAGE_MIN = "Minimum page number is 1";
  public static final String LIMIT_MAX = "Maximum limit is 50";

}
