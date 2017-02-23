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
package com.nominanuda.urispec;

import java.util.HashMap;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;

import com.nominanuda.urispec.UriSpecParser.AnonExprContext;
import com.nominanuda.urispec.UriSpecParser.AnonExprTokens1Context;
import com.nominanuda.urispec.UriSpecParser.AnonExprTokens2Context;
import com.nominanuda.urispec.UriSpecParser.AnonExprTokens3Context;
import com.nominanuda.urispec.UriSpecParser.AnonExprTokensContext;
import com.nominanuda.urispec.UriSpecParser.AnyCharsContext;
import com.nominanuda.urispec.UriSpecParser.AnyChars_tContext;
import com.nominanuda.urispec.UriSpecParser.AnySequenceContext;
import com.nominanuda.urispec.UriSpecParser.AnySequence_tContext;
import com.nominanuda.urispec.UriSpecParser.Fragment_Context;
import com.nominanuda.urispec.UriSpecParser.NamedExprContext;
import com.nominanuda.urispec.UriSpecParser.NamedExprTokensContext;
import com.nominanuda.urispec.UriSpecParser.PDeclContext;
import com.nominanuda.urispec.UriSpecParser.PExprContext;
import com.nominanuda.urispec.UriSpecParser.PSeqContext;
import com.nominanuda.urispec.UriSpecParser.PSeqTokensContext;
import com.nominanuda.urispec.UriSpecParser.ParamNameContext;
import com.nominanuda.urispec.UriSpecParser.ParamSepContext;
import com.nominanuda.urispec.UriSpecParser.ParamsGroupContext;
import com.nominanuda.urispec.UriSpecParser.ProgramContext;
import com.nominanuda.urispec.UriSpecParser.QueryContext;
import com.nominanuda.urispec.UriSpecParser.SchemeAuthorityAndPathContext;
import com.nominanuda.urispec.UriSpecParser.SequenceContext;
import com.nominanuda.urispec.UriSpecParser.Sequence_tContext;
import com.nominanuda.urispec.UriSpecParser.UriCharsContext;
import com.nominanuda.urispec.UriSpecParser.UriChars_tContext;
import com.nominanuda.urispec.UriSpecParser.UrispecContext;
import com.nominanuda.urispec.UriSpecParser.VarNameContext;

import static com.nominanuda.urispec.UriSpecParser.*;

public class AstCompatGeneratingVisitor<T> extends UriSpecBaseVisitor<T> {
	private final NodeAdapter nodeAdapter;// = new NodeAdapter();
	private CommonTree root = new CommonTree();
	private HashMap<ParserRuleContext, CommonTree> m = new HashMap<>();

	public AstCompatGeneratingVisitor(NodeAdapter treeAdaptor) {
		this.nodeAdapter = treeAdaptor;
	}
	public CommonTree getRoot() {
		return root;
	}
	private CommonTree create(ParserRuleContext ctx) {
		return nodeAdapter.create(new CommonToken(ctx.getStart().getType(), ctx.getText()));
	}

	private CommonTree getAstParent(ParserRuleContext ctx) {
		ParserRuleContext pCtx = ctx.getParent();
		if(pCtx == null) return root;
		CommonTree t = null;
		while(t == null) {
			t = m.get(pCtx);
			pCtx = pCtx.getParent();
		}
		return t;
	}
	private void on(ParserRuleContext ctx) {
		createAndAppendAst(ctx);
		//System.err.println(ctxName(ctx)+" -> "+ctx.getText() + " => "+ctxName(ctx.getParent()));
	}

//	private String ctxName(ParserRuleContext ctx) {
//		return ctx.getClass().getSimpleName();//.substring("UriSpecParser$".length());
//	}

	private void createAndAppendAst(ParserRuleContext ctx) {
		CommonTree t = create(ctx);
		appendAst(ctx, t);
	}

	private void appendAst(ParserRuleContext ctx, CommonTree t) {
		getAstParent(ctx).addChild(t);
		m.put(ctx, t);
	}
	private CommonTree tok(int type) {
		ParserRuleContext ctx = new ParserRuleContext();
		ctx.start = new CommonToken(type);
		return create(ctx);// new CommonTree(new CommonToken(type));
	}
	private CommonTree tok(int type, String txt) {
		return new CommonTree(new CommonToken(type, txt));
	}


//	program	:	urispec EOF 
//	//-> 	^( URI_SPEC urispec )
	@Override
	public T visitProgram(ProgramContext ctx) {
		m.put(ctx, root);
		//on(ctx);
		return super.visitProgram(ctx);
	}
	//	urispec	:	schemeAuthorityAndPath (QMARK! query?)? (POUND! fragment_?)?;
	@Override
	public T visitUrispec(UrispecContext ctx) {
		//on(ctx);
		appendAst(ctx, tok(URI_SPEC));
		return super.visitUrispec(ctx);
	}
//	schemeAuthorityAndPath 	:	sequence
//	//->	^(SCHEME_AUTH_PATH_PART sequence)
	@Override
	public T visitSchemeAuthorityAndPath(SchemeAuthorityAndPathContext ctx) {
		//on(ctx);
		appendAst(ctx, tok(SCHEME_AUTH_PATH_PART));
		return super.visitSchemeAuthorityAndPath(ctx);
	}
//	fragment_ 	:	sequence
//	//->	^(FRAGMENT_PART sequence)
	@Override
	public T visitFragment_(Fragment_Context ctx) {
		//on(ctx);
		appendAst(ctx, tok(FRAGMENT_PART));
		return super.visitFragment_(ctx);
	}
//	sequence	:	sequence_t
	@Override
	public T visitSequence(SequenceContext ctx) {
		//on(ctx);
		return super.visitSequence(ctx);
	}
//	//->	^(SEQUENCE sequence_t)
//	sequence_t	:	(namedExpr | anonExpr|uriChars)+;
	@Override
	public T visitSequence_t(Sequence_tContext ctx) {
		//on(ctx);
		appendAst(ctx, tok(SEQUENCE));
		return super.visitSequence_t(ctx);
	}

//	anySequence_t:	(namedExpr | anonExpr|anyChars)+;
	@Override
	public T visitAnySequence_t(AnySequence_tContext ctx) {
//		on(ctx);
		return super.visitAnySequence_t(ctx);
	}
//	anySequence	:	anySequence_t
//	//->	^(SEQUENCE anySequence_t)
	@Override
	public T visitAnySequence(AnySequenceContext ctx) {
//		on(ctx);
		appendAst(ctx, tok(SEQUENCE));
		return super.visitAnySequence(ctx);
	}
//	uriChars	:	uriChars_t
//		//-> 	^(CHARACTERS uriChars_t)
	@Override
	public T visitUriChars(UriCharsContext ctx) {
//		on(ctx);
		appendAst(ctx, tok(CHARACTERS));
		return super.visitUriChars(ctx);
	}
//	uriChars_t:	(URI_CHARS)+;
	@Override
	public T visitUriChars_t(UriChars_tContext ctx) {
		on(ctx);
		return super.visitUriChars_t(ctx);
	}
//	anyChars:	anyChars_t 
//		//->	^(CHARACTERS anyChars_t)
	@Override
	public T visitAnyChars(AnyCharsContext ctx) {
//		on(ctx);
		appendAst(ctx, tok(CHARACTERS));
		return super.visitAnyChars(ctx);
	}
//	anyChars_t:	(URI_CHARS|MATCHER)+;
	@Override
	public T visitAnyChars_t(AnyChars_tContext ctx) {
		on(ctx);
		return super.visitAnyChars_t(ctx);
	}
//	namedExpr	:	namedExprTokens
//		//->	^(NAMED_EXPRESSION namedExprTokens)
	@Override
	public T visitNamedExpr(NamedExprContext ctx) {
		//on(ctx);
		appendAst(ctx, tok(NAMED_EXPRESSION));
		return super.visitNamedExpr(ctx);
	}
//	namedExprTokens:	BEGIN_NAMED_EXPR! varName (WS! anySequence ( ALTERNATIVE! anySequence )*)? (ALTERNATIVE!)? END_NAMED_EXPR!;
	@Override
	public T visitNamedExprTokens(NamedExprTokensContext ctx) {
		//on(ctx);
		return super.visitNamedExprTokens(ctx);
	}
//	anonExpr	:	anonExprTokens
//		//->	^(ANONYMOUS_EXPRESSION anonExprTokens )
	@Override
	public T visitAnonExpr(AnonExprContext ctx) {
//		on(ctx);
		appendAst(ctx, tok(ANONYMOUS_EXPRESSION));
		return super.visitAnonExpr(ctx);
	}
//	anonExprTokens:	anonExprTokens1 | anonExprTokens2 | anonExprTokens3; 
	@Override
	public T visitAnonExprTokens(AnonExprTokensContext ctx) {
		//on(ctx);
		return super.visitAnonExprTokens(ctx);
	}
//	anonExprTokens1:	(BEGIN_ANON_EXPR! sequence ( ALTERNATIVE! sequence )* END_ANON_EXPR!);
	@Override
	public T visitAnonExprTokens1(AnonExprTokens1Context ctx) {
		//on(ctx);
		return super.visitAnonExprTokens1(ctx);
	}
//	anonExprTokens2
//		:		
//			(BEGIN_ANON_EXPR sequence ( ALTERNATIVE sequence )* ALTERNATIVE END_ANON_EXPR)
//		//-> sequence* ^(SEQUENCE ^(CHARACTERS URI_CHARS[""]))
	@Override
	public T visitAnonExprTokens2(AnonExprTokens2Context ctx) {
		//on(ctx);
		T res = super.visitAnonExprTokens2(ctx);
		appendAst(ctx, tok(SEQUENCE));
		appendAst(ctx, tok(CHARACTERS));
		appendAst(ctx, tok(UriSpecParser.URI_CHARS, ""));
		return res;
	}
//	anonExprTokens3
//		:		
//			(BEGIN_ANON_EXPR ALTERNATIVE sequence ( ALTERNATIVE sequence )* END_ANON_EXPR)
//		//-> ^(SEQUENCE ^(CHARACTERS URI_CHARS[""])) sequence* 
	@Override
	public T visitAnonExprTokens3(AnonExprTokens3Context ctx) {
//		on(ctx);
		appendAst(ctx, tok(SEQUENCE));
		appendAst(ctx, tok(CHARACTERS));
		appendAst(ctx, tok(UriSpecParser.URI_CHARS, ""));
		T res = super.visitAnonExprTokens3(ctx);
		return res;
	}
//	query	:	pSeq paramSep //-> ^(QUERY_PART pSeq STRICT_PARAMS)
//		|	pSeq //-> ^(QUERY_PART pSeq)
	@Override
	public T visitQuery(QueryContext ctx) {
		//on(ctx);
		appendAst(ctx, tok(QUERY_PART));
		return super.visitQuery(ctx);
	}
//	pSeq	:	pSeqTokens
//		//->	^(PARAM_SEQUENCE pSeqTokens)
	@Override
	public T visitPSeq(PSeqContext ctx) {
//		on(ctx);
		appendAst(ctx, tok(PARAM_SEQUENCE));
		return super.visitPSeq(ctx);
	}
//	pSeqTokens	:	pExpr (paramSep! pExpr)*
	@Override
	public T visitPSeqTokens(PSeqTokensContext ctx) {
//		on(ctx);
		return super.visitPSeqTokens(ctx);
	}
//	pExpr	:	(pDecl | paramsGroup)
//	//->	^(PARAM_DECL pDecl)? ^(PARAM_GROUP paramsGroup)?
	@Override
	public T visitPExpr(PExprContext ctx) {
//		on(ctx);
		return super.visitPExpr(ctx);
	}
//	pDecl	:	namedExpr |paramName EQUALS! sequence?;
	@Override
	public T visitPDecl(PDeclContext ctx) {
		//on(ctx);
		appendAst(ctx, tok(PARAM_DECL));
		return super.visitPDecl(ctx);
	}
//	paramsGroup	: 	BEGIN_ANON_EXPR! pSeq ( ALTERNATIVE! pSeq )* END_ANON_EXPR!;
	@Override
	public T visitParamsGroup(ParamsGroupContext ctx) {
//		on(ctx);
		appendAst(ctx, tok(PARAM_GROUP));
		return super.visitParamsGroup(ctx);
	}

//	paramName	:	URI_CHARS;
	@Override
	public T visitParamName(ParamNameContext ctx) {
		on(ctx);
		return super.visitParamName(ctx);
	}

	// noRule....
	@Override
	public T visitParamSep(ParamSepContext ctx) {
		//on(ctx);
		return super.visitParamSep(ctx);
	}

	//varName 	:	URI_CHARS;
	@Override
	public T visitVarName(VarNameContext ctx) {
		on(ctx);
//		createAndAppendAst(ctx);
		return super.visitVarName(ctx);
	}
///////////////////////////////////
///////////////////////////////////



}
