package io.avaje.http.generator.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {

  @Test
  void combinePath() {

    assertEquals(Util.combinePath("/hello", null), "/hello");

    assertEquals(Util.combinePath(null, "/hello"), "/hello");
    assertEquals(Util.combinePath(null, "/hello/"), "/hello");
    assertEquals(Util.combinePath(null, "hello"), "/hello");
    assertEquals(Util.combinePath(null, "hello/"), "/hello");

    assertEquals(Util.combinePath(null, "/hello/:id"), "/hello/:id");
    assertEquals(Util.combinePath(null, "/hello/:id/:gid"), "/hello/:id/:gid");

    assertEquals(Util.combinePath("/a", "/hello"), "/a/hello");
    assertEquals(Util.combinePath("/a/b", "/hello"), "/a/b/hello");
    assertEquals(Util.combinePath("/a/b", "/hello"), "/a/b/hello");
    assertEquals(Util.combinePath("/a", ""), "/a");
    assertEquals(Util.combinePath("/a", "/"), "/a");
  }

  @Test
  void combinePath_forRoot() {
    assertEquals(Util.combinePath("/", ""), "/");
    assertEquals(Util.combinePath("", "/"), "");
  }

  @Test
  void trimPath() {
    assertEquals(Util.trimPath("/"), "/");
    assertEquals(Util.trimPath("/foo"), "/foo");
    assertEquals(Util.trimPath("/foo/"), "/foo");
  }

  @Test
  void snakeCase() {

    assertThat(Util.snakeCase("lower")).isEqualTo("lower");
    assertThat(Util.snakeCase("fooId")).isEqualTo("foo-id");
    assertThat(Util.snakeCase("_fooId")).isEqualTo("_foo-id");
    assertThat(Util.snakeCase("fooBarBazUuid")).isEqualTo("foo-bar-baz-uuid");
    assertThat(Util.snakeCase("aDTo")).isEqualTo("a-d-to");
    assertThat(Util.snakeCase("DTo")).isEqualTo("d-to");
    assertThat(Util.snakeCase("_DTo")).isEqualTo("_-d-to");
  }

  @Test
  void initcap() {
    assertThat(Util.initcapSnake("lower")).isEqualTo("Lower");
    assertThat(Util.initcapSnake("a")).isEqualTo("A");
    assertThat(Util.initcapSnake("myFoo")).isEqualTo("MyFoo");
  }

  @Test
  void initcapSnake() {
    assertThat(Util.initcapSnake("lower")).isEqualTo("Lower");
    assertThat(Util.initcapSnake("foo-id")).isEqualTo("Foo-Id");
    assertThat(Util.initcapSnake("foo-bar-baz-uuid")).isEqualTo("Foo-Bar-Baz-Uuid");
    assertThat(Util.initcapSnake("a-d-to")).isEqualTo("A-D-To");
    assertThat(Util.initcapSnake("proxy-authenticate")).isEqualTo("Proxy-Authenticate");
  }

  @Test
  void propertyName() {
    assertThat(Util.propertyName("setLower")).isEqualTo("lower");
    assertThat(Util.propertyName("setFooBar")).isEqualTo("fooBar");
  }

  @Test
  void parse_basic() {
    UType type = Util.parse("org.example.Repo");

    assertThat(type.importTypes()).containsExactly("org.example.Repo");
    assertThat(type.shortType()).isEqualTo("Repo");
  }

  @Test
  void parse_generic() {
    UType type = Util.parse("java.util.List<org.example.Repo>");

    assertThat(type.importTypes()).containsExactly("java.util.List", "org.example.Repo");
    assertThat(type.shortType()).isEqualTo("List<Repo>");
  }

  @Test
  void parse_generic_twoParams() {
    UType type = Util.parse("java.util.List<org.example.Repo, foo.Other>");

    assertThat(type.importTypes()).containsExactly("java.util.List", "org.example.Repo", "foo.Other");
    assertThat(type.shortType()).isEqualTo("List<Repo,Other>");
  }

  @Test
  void parse_CompletableFutureHttpVoid() {
    UType type = Util.parse("java.util.concurrent.CompletableFuture<java.net.http.HttpResponse<java.lang.Void>>");

    assertThat(type.importTypes()).containsExactly("java.util.concurrent.CompletableFuture", "java.net.http.HttpResponse");
    assertThat(type.shortType()).isEqualTo("CompletableFuture<HttpResponse<Void>>");
  }

  @Test
  void parse_CompletableFutureBean() {
    UType type = Util.parse("java.util.concurrent.CompletableFuture<org.example.Repo>");

    assertThat(type.importTypes()).containsExactly("java.util.concurrent.CompletableFuture", "org.example.Repo");
    assertThat(type.shortType()).isEqualTo("CompletableFuture<Repo>");
  }

  @Test
  void parse_CompletableFutureListBean() {
    UType type = Util.parse("java.util.concurrent.CompletableFuture<java.util.List<org.example.Repo>>");

    assertThat(type.importTypes()).containsExactly("java.util.concurrent.CompletableFuture", "java.util.List", "org.example.Repo");
    assertThat(type.shortType()).isEqualTo("CompletableFuture<List<Repo>>");
  }

  @Test
  void parse_CompletableFutureStreamBean() {
    UType type = Util.parse("java.util.concurrent.CompletableFuture<java.util.Stream<org.example.Repo>>");

    assertThat(type.importTypes()).containsExactly("java.util.concurrent.CompletableFuture", "java.util.Stream", "org.example.Repo");
    assertThat(type.shortType()).isEqualTo("CompletableFuture<Stream<Repo>>");
  }
}
