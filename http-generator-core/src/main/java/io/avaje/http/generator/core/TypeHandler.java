package io.avaje.http.generator.core;

/**
 * Handles type conversion for path and query parameters.
 */
interface TypeHandler {

  /**
   * Return the non-nullable type conversion method.
   */
  String asMethod();

  /**
   * Return the nullable type conversion method.
   */
  String toMethod();

  /**
   * The type for adding to imports.
   */
  String importType();

  /**
   * The short name.
   */
  String shortName();

  /**
   * Return true if this is a primitive type.
   */
  boolean isPrimitive();
}
