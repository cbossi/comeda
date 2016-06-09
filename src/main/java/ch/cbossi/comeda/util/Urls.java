package ch.cbossi.comeda.util;

public final class Urls {

  private static final String URL_SEPARATOR = "/";

  private Urls() {}

  public static String concatenate(final String leftUrl, final String rightUrl) {
    if (leftUrl.endsWith(URL_SEPARATOR) && rightUrl.startsWith(URL_SEPARATOR)) {
      return leftUrl.substring(0, leftUrl.length() - 1) + rightUrl;
    }
    else if (!leftUrl.endsWith(URL_SEPARATOR) && !rightUrl.startsWith(URL_SEPARATOR)) {
      return leftUrl + URL_SEPARATOR + rightUrl;
    }
    return leftUrl + rightUrl;
  }

  public static String absolute(final String url) {
    return url.startsWith(URL_SEPARATOR) ? url : URL_SEPARATOR + url;
  }
}
