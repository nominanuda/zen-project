/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mozilla.javascript;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

import org.mozilla.javascript.regexp.RegExpImpl;
import org.mozilla.javascript.tools.debugger.Main;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.stereotype.Initializable;

@ThreadSafe
public class RhinoEmbedding extends ContextFactory implements Initializable, MethodArgCoercer {
	private static final AtomicBoolean exclusiveContextMaker = new AtomicBoolean(false);
	private boolean debug = false;
	@SuppressWarnings("unused")
	private Object securityDomain = null;
	private RegExpProxy regExpProxy = new HtmlUnitRegExpProxy(new RegExpImpl());

	public void installAsGlobalContextFactory() {
		ContextFactory.initGlobal(this);
	}

	public void init() {
		wrapFactory.setJavaPrimitiveWrap(javaPrimitiveWrap);
	}
	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		cx.setWrapFactory(wrapFactory);
		cx.setLanguageVersion(languageVersion);
		if(regExpProxy != null) {
			ScriptRuntime.setRegExpProxy(cx, regExpProxy);
		}
		if(debug) {
			getDebugger();
		}
//		//cx.setOptimizationLevel(-1);//disallow compilation
//		cx.setGeneratingDebug(true);
//		//cx.setWrapFactory(WrapFactory wrapFactory);
//		cx.setGeneratingSource(true);
//		cx.setErrorReporter(new ToolErrorReporter(true));
		return cx;
	}
	@Override
	protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		return super.doTopCall(callable, cx, scope, thisObj, args);
	}
	public static void setExclusiveJvmContextMaker() {
		if(! exclusiveContextMaker.getAndSet(true)) {
			ContextFactory.getGlobal().addListener(new Listener() {
				public void contextReleased(Context cx) {
				}
				public void contextCreated(Context cx) {
					Check.illegalstate.assertTrue(
						cx.getFactory() instanceof RhinoEmbedding);
				}
			});
		}
	}
	public void setRegExpProxy(RegExpProxy regExpProxy) {
		this.regExpProxy = regExpProxy;
	}
	public void setSecurityDomain(Object securityDomain) {
		this.securityDomain = securityDomain;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	///// Java <--> Javascript conversions
	private MethodArgCoercer methodArgsCoercer = new PluggableMethodArgCoercer();
	private boolean javaPrimitiveWrap = false;
	private WrapFactory wrapFactory = new PluggableWrapFactory(this);

	public void setWrapFactory(WrapFactory wf) {
		this.wrapFactory = wf;
	}

	public void setMethodArgCoercer(MethodArgCoercer coercer) {
		this.methodArgsCoercer = coercer;
	}

	public int getConversionWeight(Object value, Class<?> type) {
		return methodArgsCoercer.getConversionWeight(value, type);
	}

	public Object coerceTypeImpl(Class<?> type, Object value) {
		return methodArgsCoercer.coerceTypeImpl(type, value);
	}
	/**
	 * @see {@link WrapFactory#setJavaPrimitiveWrap(boolean)}.
	 */
	public void setJavaPrimitiveWrap(
			boolean wrapFactoryWrapJavaPrimitives) {
		this.javaPrimitiveWrap = wrapFactoryWrapJavaPrimitives;
	}
	// Language Settings
	private int languageVersion = Context.VERSION_1_7;

	private boolean featureMemberAsFunctionName = true;
	/**FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER Control if reserved keywords are treated as identifiers.*/
	private boolean featureReservedKeywordAsIdentifier = false;
	//FEATURE_DYNAMIC_SCOPE Control if dynamic scope should be used for name access.
	private boolean featureDynamicScope = true;
	//FEATURE_STRICT_VARS Control if strict variable mode is enabled.
	private boolean featureStrictVars = true;
	//FEATURE_E4X Control if support for E4X(ECMAScript for XML) extension is available.
	private boolean featureE4X = true;
	//FEATURE_ENHANCED_JAVA_ACCESS Enables enhanced access to Java.
	private boolean featureEnhancedJavaAccess = true;
	//FEATURE_LOCATION_INFORMATION_IN_ERROR When the feature is on Rhino will add 
	//a "fileName" and "lineNumber" properties to Error objects automatically.
	private boolean featureLocationInformationInError = true;
	//FEATURE_NON_ECMA_GET_YEAR Controls behaviour of Date.prototype.getYear().
	private boolean featureNonEcmaGetYear = false;
	//FEATURE_PARENT_PROTO_PROPERTIES Control if properties __proto__ and __parent__ are treated specially.
	private boolean featureParentProtoProperties = false;
	//FEATURE_STRICT_EVAL Control if strict eval mode is enabled.
	private boolean featureStrictEval = true;
	//FEATURE_STRICT_MODE Controls whether JS 1.5 'strict mode' is enabled.
	private boolean featureStrictMode = false;
	//FEATURE_TO_STRING_AS_SOURCE Control if toString() should returns 
	//the same result as toSource() when applied to objects and arrays.
	private boolean featureToStringAsSource = true;
	//FEATURE_WARNING_AS_ERROR Controls whether a warning should be treated as an error.	
	private boolean featureWarningAsError = true;

	@Override
	protected boolean hasFeature(Context cx, int featureIndex) {
		switch (featureIndex) {
		case Context.FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER:
			return featureReservedKeywordAsIdentifier;
		case Context.FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME:
			return featureMemberAsFunctionName;
		case Context.FEATURE_DYNAMIC_SCOPE:
			return featureDynamicScope;
		case Context.FEATURE_STRICT_VARS:
			return featureStrictVars;
		case Context.FEATURE_E4X:
			return featureE4X;
		case Context.FEATURE_ENHANCED_JAVA_ACCESS:
			return featureEnhancedJavaAccess;
		case Context.FEATURE_LOCATION_INFORMATION_IN_ERROR:
			return featureLocationInformationInError;
		case Context.FEATURE_NON_ECMA_GET_YEAR:
			return featureNonEcmaGetYear;
		case Context.FEATURE_PARENT_PROTO_PROPERTIES:
			return featureParentProtoProperties;
		case Context.FEATURE_STRICT_EVAL:
			return featureStrictEval;
		case Context.FEATURE_STRICT_MODE:
			return featureStrictMode;
		case Context.FEATURE_TO_STRING_AS_SOURCE:
			return featureToStringAsSource;
		case Context.FEATURE_WARNING_AS_ERROR:
			return featureWarningAsError;
		}
		return super.hasFeature(cx, featureIndex);
	}

	public void setFeatureReservedKeywordAsIdentifier(boolean featureReservedKeywordAsIdentifier) {
		this.featureReservedKeywordAsIdentifier = featureReservedKeywordAsIdentifier;
	}
	public void setFeatureMemberAsFunctionName(boolean featureMemberAsFunctionName) {
		this.featureMemberAsFunctionName = featureMemberAsFunctionName;
	}
	public void setFeatureDynamicScope(boolean featureDynamicScope) {
		this.featureDynamicScope = featureDynamicScope;
	}
	public void setFeatureStrictVars(boolean featureStrictVars) {
		this.featureStrictVars = featureStrictVars;
	}
	public void setFeatureE4X(boolean featureE4X) {
		this.featureE4X = featureE4X;
	}
	public void setLanguageVersion(int languageVersion) {
		this.languageVersion = languageVersion;
	}
	public void setFeatureEnhancedJavaAccess(boolean featureEnhancedJavaAccess) {
		this.featureEnhancedJavaAccess = featureEnhancedJavaAccess;
	}
	public void setFeatureLocationInformationInError(boolean featureLocationInformationInError) {
		this.featureLocationInformationInError = featureLocationInformationInError;
	}
	public void setFeatureNonEcmaGetYear(boolean featureNonEcmaGetYear) {
		this.featureNonEcmaGetYear = featureNonEcmaGetYear;
	}
	public void setFeatureParentProtoProperties(boolean featureParentProtoProperties) {
		this.featureParentProtoProperties = featureParentProtoProperties;
	}
	public void setFeatureStrictEval(boolean featureStrictEval) {
		this.featureStrictEval = featureStrictEval;
	}
	public void setFeatureStrictMode(boolean featureStrictMode) {
		this.featureStrictMode = featureStrictMode;
	}
	public void setFeatureToStringAsSource(boolean featureToStringAsSource) {
		this.featureToStringAsSource = featureToStringAsSource;
	}
	public void setFeatureWarningAsError(boolean featureWarningAsError) {
		this.featureWarningAsError = featureWarningAsError;
	}
	///////// DEBUGGER STUFF
	private Main _debugger;

	private Main getDebugger() {
		if (_debugger == null) {
			_debugger = new Main("rhino debugger");
			_debugger.attachTo(ContextFactory.getGlobal());
			_debugger.setExitAction(new Runnable() {
				public void run() {
					debug = false;
					_debugger.detach();
					_debugger = null;
				}
			});
			_debugger.pack();
			_debugger.setSize(600, 460);
			_debugger.setVisible(true);
		}
		return _debugger;
	}

}
