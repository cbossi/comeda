package ch.cbossi.comeda.generator;

final class UrlDelimiters {

  private static final String URL_DELIMITER = "/";

  private UrlDelimiters() {}

  public static String concatenate(final String leftUrl, final String rightUrl) {
    if (leftUrl.endsWith(URL_DELIMITER) && rightUrl.startsWith(URL_DELIMITER)) {
      return leftUrl.substring(0, leftUrl.length() - 1) + rightUrl;
    }
    else if (!leftUrl.endsWith(URL_DELIMITER) && !rightUrl.startsWith(URL_DELIMITER)) {
      return leftUrl + URL_DELIMITER + rightUrl;
    }
    return leftUrl + rightUrl;
  }

  public static String absolute(final String url) {
    return url.startsWith(URL_DELIMITER) ? url : URL_DELIMITER + url;
  }
}
