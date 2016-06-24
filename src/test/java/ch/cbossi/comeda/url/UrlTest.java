package ch.cbossi.comeda.url;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class UrlTest {

  @Test
  public void checkUrlWithoutParameters() {
    Url url = new Url("base");

    String actualUrl = url.toString();

    assertThat(actualUrl).isEqualTo("base");
  }

  @Test
  public void checkUrlWithOneParameter() {
    Url url = new Url("base").parameter("key1", "value1");

    String actualUrl = url.toString();

    assertThat(actualUrl).isEqualTo("base?key1=value1");
  }

  @Test
  public void checkUrlWithMultipleParametersAddedIndividually() {
    Url url = new Url("base")
        .parameter("key1", "value1")
        .parameter("key2", "value2")
        .parameter("key3", "value3");

    String actualUrl = url.toString();

    assertThat(actualUrl).isEqualTo("base?key1=value1&key2=value2&key3=value3");
  }

  @Test
  public void checkUrlWithMultipleParametersAddedTogether() {
    Url url = new Url("base")
        .parameters(ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3"));

    String actualUrl = url.toString();

    assertThat(actualUrl).isEqualTo("base?key1=value1&key2=value2&key3=value3");
  }

  @Test
  public void checkUrlWithMultipleParametersAddedIndividuallyAndTogether() {
    Url url = new Url("base")
        .parameter("key1", "value1")
        .parameters(ImmutableMap.of("key2", "value2", "key3", "value3"))
        .parameter("key4", "value4");

    String actualUrl = url.toString();

    assertThat(actualUrl).isEqualTo("base?key1=value1&key2=value2&key3=value3&key4=value4");
  }

  @Test
  public void checkUrlWithMultipleParametersInConstructor() {
    Url url = new Url("base", ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3"));

    String actualUrl = url.toString();

    assertThat(actualUrl).isEqualTo("base?key1=value1&key2=value2&key3=value3");
  }

  @Test
  public void checkUrlWithTrailingSlashWithoutParameters() {
    Url url = new Url("base/");

    String actualUrl = url.toString();

    assertThat(actualUrl).isEqualTo("base/");
  }

  @Test
  public void checkUrlWithTrailingSlashWithParameters() {
    Url url = new Url("base/").parameter("key1", "value1");

    String actualUrl = url.toString();

    assertThat(actualUrl).isEqualTo("base?key1=value1");
  }

}
