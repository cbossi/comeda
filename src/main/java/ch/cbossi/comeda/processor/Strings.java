package ch.cbossi.comeda.processor;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Character.toUpperCase;

final class Strings {

  private Strings() {}

  public static String capitalize(final String word) {
    if (isNullOrEmpty(word)) {
      return word;
    }
    else if (word.length() == 1) {
      return word.toUpperCase();
    }
    return toUpperCase(word.charAt(0)) + word.substring(1);
  }
}
