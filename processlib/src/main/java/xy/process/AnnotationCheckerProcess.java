package xy.process;


import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by Administrator on 2016/9/1.
 */
//@AutoService(Processor.class)
@SupportedAnnotationTypes("xy.annotation.SaveState")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationCheckerProcess extends AbstractProcessor {
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement serializable = elementUtils.getTypeElement("java.io.Serializable");
        TypeElement string = elementUtils.getTypeElement("java.lang.String");
        TypeElement list = elementUtils.getTypeElement("java.util.List");
        TypeElement sparseArray = elementUtils.getTypeElement("android.util.SparseArray");
        TypeElement parcelable = elementUtils.getTypeElement("android.os.Parcelable");
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                if (e instanceof VariableElement) {
                    TypeMirror mirror = e.asType();
                    //if the mirror is assignable form list or sparseArray , or is itself , it must declare a generic paradigm
                    if (typeUtils.isAssignable(mirror, list.asType())
                            || typeUtils.isSameType(mirror, list.asType())
                            || typeUtils.isAssignable(mirror, sparseArray.asType())
                            || typeUtils.isSameType(mirror, sparseArray.asType())) {
                        throw new IllegalArgumentException("generic paradigm must be declared in " + e.toString() + " at the class " + e.getEnclosingElement().toString());
                    } else {
                        //if the mirror is not primitive or String , we need more information
                        if (!mirror.getKind().isPrimitive() &&
                                (!typeUtils.isSameType(mirror, string.asType()) || !typeUtils.isAssignable(mirror, string.asType()))) {
                            if (mirror.getKind() != TypeKind.ARRAY) {
                                if (mirror.toString().contains("android.util.SparseArray")
                                        || mirror.toString().contains("java.util.ArrayList")
                                        || mirror.toString().contains("java.util.List")) {
                                    int start = mirror.toString().indexOf("<");
                                    int end = mirror.toString().indexOf(">");
                                    TypeMirror generic = elementUtils.getTypeElement(mirror.toString().substring(start + 1, end)).asType();
                                    if (!typeUtils.isAssignable(generic, parcelable.asType())) {
                                        throw new IllegalArgumentException("the element " + mirror.toString().substring(start + 1, end) + "in the field " + e.toString() + " at the class " + e.getEnclosingElement().toString() + " must be parcelable");
                                    }
                                } else {
                                    //now we know the mirror is an object, if is not serializable or parcelable,throws exception;
                                    if (!typeUtils.isAssignable(mirror, parcelable.asType())
                                            && !typeUtils.isAssignable(mirror, serializable.asType())) {
                                        throw new IllegalArgumentException("field " + e.toString() + " at the class " + e.getEnclosingElement().toString() + " must be serializable or parcelable");
                                    }
                                }
                            } else {
                                // if the mirror is array but not a string array or parcelable array, throws exception;
                                if (!"java.lang.String[]".equals(mirror.toString())) {
                                    String elementName = mirror.toString().replace("[]", "");
                                    TypeMirror arrayElement = elementUtils.getTypeElement(elementName).asType();
                                    if (!typeUtils.isAssignable(arrayElement, parcelable.asType())) {
                                        throw new IllegalArgumentException("the element " + elementName + "in the array field " + e.toString() + " at the class " + e.getEnclosingElement().toString() + " must be parcelable");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
