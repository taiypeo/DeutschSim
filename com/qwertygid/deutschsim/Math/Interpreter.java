package com.qwertygid.deutschsim.Math;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import com.qwertygid.deutschsim.Miscellaneous.Tools;

public class Interpreter {
	public Interpreter(final LexicalAnalyzer analyzer, final Tools.AngleType angle_type) {
		this.analyzer = analyzer;
		current = analyzer.get_next_token();
		
		this.angle_type = angle_type;
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
			if (Tools.equal(value.getImaginary(), 0.0) &&
					FastMath.copySign(1.0, value.getImaginary()) < 0.0) // check for negative zero in the imaginary part
				value = new Complex(value.getReal());
			
			value = value.sqrt();
			break;
		case SIN:
			value = convert_to_radians(value).sin();
			break;
		case COS:
			value = convert_to_radians(value).cos();
			break;
		case TAN:
			value = convert_to_radians(value).tan();
			break;
		default:
			value = convert_to_radians(value).tan().reciprocal();	
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
	
	private Complex convert_to_radians(final Complex angle) {
		if (angle_type == Tools.AngleType.DEGREES)
			return new Complex(Math.toRadians(angle.getReal()),
					Math.toRadians(angle.getImaginary()));
		
		return angle;
	}
	
	private final LexicalAnalyzer analyzer;
	private final Tools.AngleType angle_type;
	private Token current;
}
