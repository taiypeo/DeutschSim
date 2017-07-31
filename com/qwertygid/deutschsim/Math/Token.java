package com.qwertygid.deutschsim.Math;

public class Token {
	enum Type {
		INTEGER, SQRT, SIN, COS,
		TAN, COT, PI, DOT, PLUS,
		MINUS, MUL, DIV, POW,
		LPAREN, RPAREN,	I, E, END
	}
	
	public Token(final Type type) {
		this.type = type;
		value = "";
	}
	
	public Token(final Type type, final String value) {
		this.type = type;
		this.value = value;
	}
	
	public final Type type;
	public final String value;
}
