package io.dinject.javalin.generator.openapi;

import io.dinject.javalin.generator.ElementReader;
import io.dinject.javalin.generator.ParamType;
import io.dinject.javalin.generator.javadoc.Javadoc;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;

import javax.lang.model.element.Element;

/**
 * Build the OpenAPI for a method parameter.
 */
public class MethodParamDocBuilder {

  private final DocContext ctx;
  private final Javadoc javadoc;
  private final Operation operation;
  private final String paramName;
  private final String varName;
  private final String rawType;
  private final ParamType paramType;
  private final Element element;

  public MethodParamDocBuilder(MethodDocBuilder methodDoc, ElementReader param) {

    this.ctx = methodDoc.getContext();
    this.javadoc = methodDoc.getJavadoc();
    this.operation = methodDoc.getOperation();

    this.paramType = param.getParamType();
    this.paramName = param.getParamName();
    this.varName = param.getVarName();
    this.rawType = param.getRawType();
    this.element = param.getElement();
  }

  /**
   * Build the OpenAPI documentation for the method parameter.
   */
  public void build() {

    if (paramType == ParamType.FORM || paramType == ParamType.BODY) {
      addMetaRequestBody(ctx, javadoc, operation);

    } else {
      Parameter param = new Parameter();
      param.setName(varName);
      param.setDescription(javadoc.getParams().get(paramName));

      Schema schema = ctx.toSchema(rawType, element);
      if (paramType == ParamType.FORMPARAM) {
        ctx.addFormParam(operation, varName, schema);

      } else {
        param.setSchema(schema);
        param.setIn(paramType.getType());
        operation.addParametersItem(param);
      }
    }
  }

  private void addMetaRequestBody(DocContext ctx, Javadoc javadoc, Operation operation) {

    Schema schema = ctx.toSchema(rawType, element);
    String description = javadoc.getParams().get(paramName);

    boolean asForm = (paramType == ParamType.FORM);
    ctx.addRequestBody(operation, schema, asForm, description);
  }

}
