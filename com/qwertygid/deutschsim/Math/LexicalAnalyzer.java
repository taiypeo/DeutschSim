package com.qwertygid.deutschsim.Math;

public class LexicalAnalyzer {
	public LexicalAnalyzer(final String expression) {
		this.expression = expression.toLowerCase();
		current_position = 0;
	}
	
	public Token get_next_token() {
		while (current_position < expression.length()) {
			final char current_char = expression.charAt(current_position);
			
			if (Character.isWhitespace(current_char)) {
				skip_whitespace();
				continue;
			} else if (Character.isDigit(current_char))
				return integer_token();
			else if (is_string_token("sqrt")) {
				current_position += "sqrt".length();
				return new Token(Token.Type.SQRT);
			} else if (is_string_token("sin")) {
				current_position += "sin".length();
				return new Token(Token.Type.SIN);
			} else if (is_string_token("cos")) {
				current_position += "cos".length();
				return new Token(Token.Type.COS);
			} else if (is_string_token("tan")) {
				current_position += "tan".length();
				return new Token(Token.Type.TAN);
			} else if (is_string_token("cot")) {
				current_position += "cot".length();
				return new Token(Token.Type.COT);
			} else if (is_string_token("pi")) {
				current_position += "pi".length();
				return new Token(Token.Type.PI);
			} else {
				current_position++;
				
				if (current_char == '.')
					return new Token(Token.Type.DOT);
				else if (current_char == '+')
					return new Token(Token.Type.PLUS);
				else if (current_char == '-')
					return new Token(Token.Type.MINUS);
				else if (current_char == '*')
					return new Token(Token.Type.MUL);
				else if (current_char == '/')
					return new Token(Token.Type.DIV);
				else if (current_char == '^')
					return new Token(Token.Type.POW);
				else if (current_char == '(')
					return new Token(Token.Type.LPAREN);
				else if (current_char == ')')
					return new Token(Token.Type.RPAREN);
				else if (current_char == 'i')
					return new Token(Token.Type.I);
				else if (current_char == 'e')
					return new Token(Token.Type.E);
			}
		}
		
		return new Token(Token.Type.END);
	}
	
	private void skip_whitespace() {
		while (current_position < expression.length() &&
				Character.isWhitespace(expression.charAt(current_position)))
				current_position++;
	}
	
	private Token integer_token() {
		StringBuilder result = new StringBuilder();
		for (; current_position < expression.length() &&
				Character.isDigit(expression.charAt(current_position));
				current_position++)
			result.append(expression.charAt(current_position));
		
		return new Token(Token.Type.INTEGER, Integer.parseInt(result.toString()));
	}
	
	private boolean is_string_token(final String str) {
		return expression.substring(current_position).startsWith(str.toLowerCase());
	}
	
	private final String expression;
	private int current_position;
}
