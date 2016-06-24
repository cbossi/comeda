package ch.cbossi.comeda.url;

import static java.util.Objects.hash;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Joiner;

public class Url {

  private final String url;
  private final Map<String, String> requestParameters;

  private Optional<String> composedUrl;

  public Url(final String url) {
    this(url, new HashMap<>());
  }

  public Url(final String url, final Map<String, String> requestParameters) {
    this.url = url;
    this.requestParameters = requestParameters;
    composedUrl = empty();
  }

  public Url parameter(final String name, final String value) {
    requestParameters.put(name, value);
    return new Url(url, requestParameters);
  }

  public Url parameters(final Map<String, String> parameters) {
    requestParameters.putAll(parameters);
    return new Url(url, requestParameters);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj != null && this.getClass() == obj.getClass()) {
      Url that = (Url) obj;
      return Objects.equals(this.url, that.url) && Objects.equals(this.requestParameters, that.requestParameters);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return hash(url, requestParameters);
  }

  @Override
  public String toString() {
    if (!composedUrl.isPresent()) {
      composedUrl = Optional.of(composeUrl());
    }
    return composedUrl.get();
  }

  private String composeUrl() {
    return requestParameters.isEmpty() ? url : composeUrlWithParameters();
  }

  private String composeUrlWithParameters() {
    List<String> keyValueStrings = requestParameters.entrySet().stream()
        .map(parameter -> parameter.getKey() + "=" + parameter.getValue())
        .collect(toList());
    return withoutTrailingSlash(url) + "?" + Joiner.on("&").join(keyValueStrings);
  }

  private static final String withoutTrailingSlash(final String url) {
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }
}
