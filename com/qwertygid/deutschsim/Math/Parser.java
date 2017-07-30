package com.qwertygid.deutschsim.Math;

public class Parser {
	public Parser(final LexicalAnalyzer analyzer) {
		this.analyzer = analyzer;
		current = analyzer.get_next_token();
	}
	
	public void parse() {
		sum();
		
		if (current.type != Token.Type.END)
			throw new RuntimeException("Given expression has an invalid syntax");
	}
	
	private void eat(final Token.Type type) {
		if (current.type == type) {
			System.out.println(current.type + "\t" + current.value);
			current = analyzer.get_next_token();
		}
		else
			throw new RuntimeException("Given expression has an invalid syntax");
	}
	
	private void decimal_number() {		
		eat(Token.Type.INTEGER);
		
		if (current.type == Token.Type.DOT) {
			eat(Token.Type.DOT);
			eat(Token.Type.INTEGER);
		}
		
		if (current.type == Token.Type.E) {
			eat(Token.Type.E);
			
			if (current.type == Token.Type.MINUS)
				eat(Token.Type.MINUS);
			
			eat(Token.Type.INTEGER);
		}
	}
	
	private void number() {
		switch (current.type) {
		case I:
			eat(Token.Type.I);
			break;
		case E:
			eat(Token.Type.E);
			break;
		case PI:
			eat(Token.Type.PI);
			break;
		default:
			decimal_number();	
		}
	}
	
	private void function() {
		switch (current.type) {
		case SQRT:
			eat(Token.Type.SQRT);
			break;
		case SIN:
			eat(Token.Type.SIN);
			break;
		case COS:
			eat(Token.Type.COS);
			break;
		case TAN:
			eat(Token.Type.TAN);
			break;
		default:
			eat(Token.Type.COT);	
		}
		
		eat(Token.Type.LPAREN);
		sum();
		eat(Token.Type.RPAREN);
	}
	
	private void top_level() {
		switch (current.type) {
		case INTEGER: // Fall-through
		case I:
		case E:
		case PI:
			number();
			break;
		case MINUS:
			eat(Token.Type.MINUS);
			top_level();
			break;
		case LPAREN:
			eat(Token.Type.LPAREN);
			sum();
			eat(Token.Type.RPAREN);
		default:
			function();
		}
	}
	
	private void power() {
		top_level();
		
		if (current.type == Token.Type.POW) {
			eat(Token.Type.POW);
			power();
		}
	}
	
	private void product() {
		power();
		
		while (current.type == Token.Type.MUL ||
				current.type == Token.Type.DIV)
			if (current.type == Token.Type.MUL) {
				eat(Token.Type.MUL);
				power();
			} else {
				eat(Token.Type.DIV);
				power();
			}
			
	}
	
	private void sum() {
		product();
		
		while (current.type == Token.Type.PLUS ||
				current.type == Token.Type.MINUS)
			if (current.type == Token.Type.PLUS) {
				eat(Token.Type.PLUS);
				product();
			} else {
				eat(Token.Type.MINUS);
				product();
			}
	}
	
	private final LexicalAnalyzer analyzer;
	private Token current;
}
