package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" " 	{ }
"\b" 	{ }
"\t" 	{ }
"\r\n" 	{ }
"\f" 	{ }

"program"   { return new_symbol(sym.PROG, yytext());}
"print" 	{ return new_symbol(sym.PRINT, yytext()); }
"read"		{ return new_symbol(sym.READ, yytext()); }
"const"		{ return new_symbol(sym.CONST, yytext()); }
"break"		{ return new_symbol(sym.BREAK, yytext()); }
"continue"	{ return new_symbol(sym.CONTINUE, yytext()); }
"return" 	{ return new_symbol(sym.RETURN, yytext()); }
"if" 		{ return new_symbol(sym.IF, yytext()); }
"else" 		{ return new_symbol(sym.ELSE, yytext()); }
"while" 	{ return new_symbol(sym.WHILE, yytext()); }
"new" 		{ return new_symbol(sym.NEW, yytext()); }
"findAndReplace" 		{ return new_symbol(sym.FINDANDREPLACE, yytext()); }
"findAny" 				{ return new_symbol(sym.FINDANY, yytext()); }
"foreach" 				{ return new_symbol(sym.FOREACH, yytext()); }
"void" 		{ return new_symbol(sym.VOID, yytext()); }
"true" 		{ return new_symbol(sym.TRUE, yytext()); }
"false" 	{ return new_symbol(sym.FALSE, yytext()); }
"void" 		{ return new_symbol(sym.VOID, yytext()); }
"++"		{ return new_symbol(sym.INC, yytext()); }
"+" 		{ return new_symbol(sym.PLUS, yytext()); }
"--"		{ return new_symbol(sym.DEC, yytext()); }
"-" 		{ return new_symbol(sym.MINUS, yytext()); }
"*" 		{ return new_symbol(sym.MULTIPLY, yytext()); }
"/" 		{ return new_symbol(sym.DIVIDE, yytext()); }
"%" 		{ return new_symbol(sym.MOD, yytext()); }
"=>" 		{ return new_symbol(sym.LAMBDA, yytext()); }
"==" 		{ return new_symbol(sym.ISEQUAL, yytext()); }
"=" 		{ return new_symbol(sym.EQUAL, yytext()); }
">=" 		{ return new_symbol(sym.GEQUAL, yytext()); }
"<=" 		{ return new_symbol(sym.LEQUAL, yytext()); }
">" 		{ return new_symbol(sym.GREATER, yytext()); }
"<" 		{ return new_symbol(sym.LESS, yytext()); }
"!=" 		{ return new_symbol(sym.NEQ, yytext()); }
"&&" 		{ return new_symbol(sym.AND, yytext()); }
"||" 		{ return new_symbol(sym.OR, yytext()); }
":" 		{ return new_symbol(sym.COLON, yytext()); }
";" 		{ return new_symbol(sym.SEMI, yytext()); }
"," 		{ return new_symbol(sym.COMMA, yytext()); }
"(" 		{ return new_symbol(sym.LPAREN, yytext()); }
")" 		{ return new_symbol(sym.RPAREN, yytext()); }
"{" 		{ return new_symbol(sym.LBRACE, yytext()); }
"}"			{ return new_symbol(sym.RBRACE, yytext()); }
"[" 		{ return new_symbol(sym.LBRACKET, yytext()); }
"]" 		{ return new_symbol(sym.RBRACKET, yytext()); }
"."			{ return new_symbol(sym.DOT, yytext()); }	
	
<YYINITIAL> "//" 		     { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }
<COMMENT> .      { yybegin(COMMENT); }

[0-9]+  { return new_symbol(sym.NUMBER, new Integer (yytext())); }
"'"."'" { return new_symbol(sym.PRINTCHAR, yytext().charAt(1)); }
[a-zA-Z][a-zA-Z0-9_]* 	{return new_symbol (sym.IDENT, yytext()); }

. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)); }