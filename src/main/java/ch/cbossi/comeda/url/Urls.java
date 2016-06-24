package ch.cbossi.comeda.url;

public class Urls {

  private static final String PATH_VARIABLE_REGEX = "\\{.+?\\}";
  private static final String REDIRECT_PREFIX = "redirect:";

  private Urls() {}

  public static Url url(final String urlPattern, final Object... pathVariableValues) {
    String url = urlPattern;
    for (Object pathVariableValue : pathVariableValues) {
      url = url.replaceFirst(PATH_VARIABLE_REGEX, pathVariableValue.toString());
    }
    return new Url(url);
  }

  public static String redirectTo(final Url url) {
    return redirectTo(url.toString());
  }

  public static String redirectTo(final String url) {
    return REDIRECT_PREFIX + url;
  }

}
