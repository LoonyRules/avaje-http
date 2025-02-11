package io.avaje.http.generator.core;

import io.avaje.http.generator.core.openapi.DocContext;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProcessingContext {

  private final PlatformAdapter readAdapter;
  private final Messager messager;
  private final Filer filer;
  private final Elements elements;
  private final Types types;
  private final boolean openApiAvailable;
  private final DocContext docContext;
  private final boolean useComponent;
  private final boolean useJavax;
  private final String diAnnotation;

  public ProcessingContext(ProcessingEnvironment env, PlatformAdapter readAdapter) {
    this.readAdapter = readAdapter;
    this.messager = env.getMessager();
    this.filer = env.getFiler();
    this.elements = env.getElementUtils();
    this.types = env.getTypeUtils();
    this.openApiAvailable = isTypeAvailable(Constants.OPENAPIDEFINITION);
    this.docContext = new DocContext(env, openApiAvailable);

    final var options = env.getOptions();
    final var singletonOverride = options.get("useSingleton");
    if (singletonOverride != null) {
      this.useComponent = !Boolean.parseBoolean(singletonOverride);
    } else {
      this.useComponent = isTypeAvailable(Constants.COMPONENT);
    }
    this.diAnnotation = useComponent ? "@Component" : "@Singleton";

    final var javax = isTypeAvailable(Constants.SINGLETON_JAVAX);
    final var jakarta = isTypeAvailable(Constants.SINGLETON_JAKARTA);
    final var override = env.getOptions().get("useJavax");
    if (override != null || (javax && jakarta)) {
      this.useJavax = Boolean.parseBoolean(override);
    } else {
      this.useJavax = javax;
    }
  }

  private boolean isTypeAvailable(String canonicalName) {
    return null != typeElement(canonicalName);
  }

  public TypeElement typeElement(String canonicalName) {
    return elements.getTypeElement(canonicalName);
  }

  public boolean isOpenApiAvailable() {
    return openApiAvailable;
  }

  public boolean useJavax() {
    return useJavax;
  }

  public boolean useComponent() {
    return useComponent;
  }

  public void logError(Element e, String msg, Object... args) {
    messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
  }

  /**
   * Create a file writer for the given class name.
   */
  public JavaFileObject createWriter(String cls, Element origin) throws IOException {
    return filer.createSourceFile(cls, origin);
  }

  /**
   * Create a file writer for the META-INF services file.
   */
  public FileObject createMetaInfWriter(String target) throws IOException {
    return filer.createResource(StandardLocation.CLASS_OUTPUT, "", target);
  }

  public String docComment(Element param) {
    return elements.getDocComment(param);
  }

  public DocContext doc() {
    return docContext;
  }

  public Element asElement(TypeMirror typeMirror) {
    return types.asElement(typeMirror);
  }

  public TypeMirror asMemberOf(DeclaredType declaredType, Element element) {
    return types.asMemberOf(declaredType, element);
  }

  public List<ExecutableElement> superMethods(Element element, String methodName) {
    return types.directSupertypes(element.asType()).stream()
      .filter(type -> !type.toString().contains("java.lang.Object"))
      .map(
        superType -> {
          final var superClass = (TypeElement) types.asElement(superType);
          for (final ExecutableElement method : ElementFilter.methodsIn(elements.getAllMembers(superClass))) {
            if (method.getSimpleName().contentEquals(methodName)) {
              return method;
            }
          }
          return null;
        })
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  public PlatformAdapter platform() {
    return readAdapter;
  }

  public String diAnnotation() {
    return diAnnotation;
  }
}
