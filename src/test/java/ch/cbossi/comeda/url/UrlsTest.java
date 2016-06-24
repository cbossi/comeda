package ch.cbossi.comeda.url;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class UrlsTest {

  @Test
  public void testUrlWithoutPathVariables() {
    String url = "test";

    Url resultingUrl = Urls.url(url);

    assertThat(resultingUrl.toString()).isEqualTo(url);
  }

  @Test
  public void testUrlWithOnePathVariable() {
    String url = "test/{pathVariable}";
    Integer param1 = new Integer(1);

    Url resultingUrl = Urls.url(url, param1);

    assertThat(resultingUrl.toString()).isEqualTo("test/1");
  }

  @Test
  public void testUrlWithMultiplePathVariables() {
    String url = "test/{var1}/{var2}/test/{var3}";
    Integer param1 = new Integer(1);
    Integer param2 = new Integer(2);
    Integer param3 = new Integer(3);

    Url resultingUrl = Urls.url(url, param1, param2, param3);

    assertThat(resultingUrl.toString()).isEqualTo("test/1/2/test/3");
  }

  @Test
  public void testRedirectToWithUrl() {
    Url url = new Url("test");

    String redirectUrl = Urls.redirectTo(url);

    assertThat(redirectUrl).isEqualTo("redirect:test");
  }

  @Test
  public void testRedirectToWithString() {
    String url = "test";

    String redirectUrl = Urls.redirectTo(url);

    assertThat(redirectUrl).isEqualTo("redirect:test");
  }

}
