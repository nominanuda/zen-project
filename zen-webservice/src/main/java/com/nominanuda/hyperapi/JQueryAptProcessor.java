package com.nominanuda.hyperapi;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager.Location;
import javax.tools.FileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.nominanuda.lang.Collections;

public class JQueryAptProcessor implements Processor {
	private ProcessingEnvironment env;
	String prologue = "(function() {var dummy = function(){};var uritpl = function(spec, model) {var pathAndQuery = spec.split('?');var re = /\\{[^\\}]+\\}/g;var result = pathAndQuery[0].replace(re,function(str, p1, p2, offset, s) {return model[str.replace(/\\{(\\w+).*/, '$1')];});if(pathAndQuery.length > 1) {result += '?' + pathAndQuery[1].replace(re,function(str, offset) {var x = str.replace(/\\{(\\w+).*/, '$1');var p = model[x];var isPname = offset === 0 || pathAndQuery[1].charAt(offset - 1) ==! '=';return ''+ (isPname ? x+'='+p : p);});};return result;};";
	String epilogue = "})();";

	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		log("process");
		for(Element el :  roundEnv.getElementsAnnotatedWith(HyperApi.class)) {
			processApi((TypeElement)el, roundEnv);
			//log(el.getSimpleName().toString());
		}
		return true;
	}
	//		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	//		StandardJavaFileManager fileManager = compiler.
	//		        getStandardFileManager(null, null, null);


	//window.hyperapi_<type> = {
//  <method> : function(x,y,z,okcb,errcb) {
//    var uri = uritpl('<tpl>',{x:x,y:y...});//TODO nested...
//    var _errcb = errcb || dummy;
////TODO uri PREFIX
//    jQuery.ajax({
//      url:uri,
//      
//    });
//  },
//}
	private void processApi(TypeElement el, RoundEnvironment roundEnv) {
		StringBuilder sb = new StringBuilder();
		try {
			String targetType = el.getQualifiedName().toString();
			log("targetType:"+targetType);
			String pkg = targetType.substring(0, targetType.lastIndexOf('.'));
			log("detected package:"+pkg);
			FileObject jsSrc = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, pkg, "foobar", el);
			Writer w = jsSrc.openWriter();
			w.write("la vispa teresa");
			for(Element member : el.getEnclosedElements()) {
				if(member.getKind() == ElementKind.METHOD) {
					log(member.getSimpleName().toString());
				}
			}
			w.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void init(ProcessingEnvironment processingEnv) {
		log("init");
		env = processingEnv;
	}

	public Set<String> getSupportedOptions() {
		log("getSupportedOtions");
		return Collections.hashSet();
	}

	public Set<String> getSupportedAnnotationTypes() {
		log("getSupportedAnnotationTypes");
		return Collections.hashSet(HyperApi.class.getName());
	}

	public SourceVersion getSupportedSourceVersion() {
		log("getSupportedSourceVersion");
		return SourceVersion.RELEASE_6;
	}

	public Iterable<? extends Completion> getCompletions(Element element,
			AnnotationMirror annotation, ExecutableElement member,
			String userText) {
		log("getCompletions");
		return Collections.hashSet();
	}

	private void log(String msg) {
		System.err.println(msg);
		//System.out.println(msg);
	}
}
//(function() {
//var dummy = function(){};
//var uritpl = function(spec, model) {
//  var pathAndQuery = spec.split('?');
//  var re = /\{[^\}]+\}/g;
//  var result = pathAndQuery[0].replace(re,function(str, p1, p2, offset, s) {
//    return model[str.replace(/\{(\w+).*/, '$1')];
//  });
//  if(pathAndQuery.length > 1) {
//    result += '?' + pathAndQuery[1].replace(re,function(str, offset) {
//      var x = str.replace(/\{(\w+).*/, '$1');
//      var p = model[x];
//      var isPname = offset === 0 || pathAndQuery[1].charAt(offset - 1) ==! '=';
//      return ''+ (isPname ? x+'='+p : p);
//    });
//  }
//  return result;
//};
//window.hyperapi_<type> = {
//  <method> : function(x,y,z,okcb,errcb) {
//    var uri = uritpl('<tpl>',{x:x,y:y...});//TODO nested...
//    var _errcb = errcb || dummy;
////TODO uri PREFIX
//    jQuery.ajax({
//      url:uri,
//      
//    });
//  },
//}
//})();
