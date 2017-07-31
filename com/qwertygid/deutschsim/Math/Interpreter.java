package com.qwertygid.deutschsim.Math;

import org.apache.commons.math3.complex.Complex;

import com.qwertygid.deutschsim.Miscellaneous.Tools;

public class Interpreter {
	public Interpreter(final LexicalAnalyzer analyzer) {
		this.analyzer = analyzer;
		current = analyzer.get_next_token();
	}
	
	public Complex interpret() {
		Complex value = sum();
		
		if (current.type != Token.Type.END)
			throw new RuntimeException("Given expression has an invalid syntax");
		
		double real = value.getReal(), imag = value.getImaginary();
		
		if (Tools.equal(real, Math.round(real)))
			real = Math.round(real);
		if (Tools.equal(imag, Math.round(imag)))
			imag = Math.round(imag);
		
		return new Complex(real, imag);
	}
	
	private void eat(final Token.Type type) {
		if (current.type == type)
			current = analyzer.get_next_token();
		else
			throw new RuntimeException("Given expression has an invalid syntax");
	}
	
	private Complex decimal_number() {
		StringBuilder number = new StringBuilder();
		
		number.append(current.value);
		eat(Token.Type.INTEGER);
		
		if (current.type == Token.Type.DOT) {
			number.append('.');
			eat(Token.Type.DOT);
			
			number.append(current.value);
			eat(Token.Type.INTEGER);
		}
		
		if (current.type == Token.Type.E) {
			number.append('e');
			eat(Token.Type.E);
			
			if (current.type == Token.Type.MINUS) {
				number.append('-');
				eat(Token.Type.MINUS);
			}
			
			number.append(current.value);
			eat(Token.Type.INTEGER);
		}
		
		return new Complex(Double.parseDouble(number.toString()));
	}
	
	private Complex number() {
		switch (current.type) {
		case I:
			eat(Token.Type.I);
			return new Complex(0, 1);
		case E:
			eat(Token.Type.E);
			return new Complex(Math.E);
		case PI:
			eat(Token.Type.PI);
			return new Complex(Math.PI);
		default:
			return decimal_number();	
		}
	}
	
	private Complex function() {
		final Token.Type func = current.type;
		
		switch (func) {
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
		
		Complex value = sum();
		switch (func) {
		case SQRT:
			value = value.sqrt();
			break;
		case SIN:
			value = value.sin();
			break;
		case COS:
			value = value.cos();
			break;
		case TAN:
			value = value.tan();
			break;
		default:
			value = value.tan().reciprocal();	
		}
		
		eat(Token.Type.RPAREN);
		
		return value;
	}
	
	private Complex top_level() {
		switch (current.type) {
		case INTEGER: // Fall-through
		case I:
		case E:
		case PI:
			return number();
		case MINUS:
			eat(Token.Type.MINUS);
			return top_level().negate();
		case LPAREN:
			eat(Token.Type.LPAREN);
			Complex value = sum();
			eat(Token.Type.RPAREN);
			return value;
		default:
			return function();
		}
	}
	
	private Complex power() {
		Complex lhs = top_level();
		
		if (current.type == Token.Type.POW) {
			eat(Token.Type.POW);
			return lhs.pow(power());
		}
		
		return lhs;
	}
	
	private Complex product() {
		Complex lhs = power();
		
		while (current.type == Token.Type.MUL ||
				current.type == Token.Type.DIV)
			if (current.type == Token.Type.MUL) {
				eat(Token.Type.MUL);
				lhs = lhs.multiply(power());
			} else {
				eat(Token.Type.DIV);
				lhs = lhs.divide(power());
			}
		
		return lhs;
	}
	
	private Complex sum() {
		Complex lhs = product();
		
		while (current.type == Token.Type.PLUS ||
				current.type == Token.Type.MINUS)
			if (current.type == Token.Type.PLUS) {
				eat(Token.Type.PLUS);
				lhs = lhs.add(product());
			} else {
				eat(Token.Type.MINUS);
				lhs = lhs.subtract(product());
			}
		
		return lhs;
	}
	
	private final LexicalAnalyzer analyzer;
	private Token current;
}
