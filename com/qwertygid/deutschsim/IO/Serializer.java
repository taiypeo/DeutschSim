package com.qwertygid.deutschsim.IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashSet;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldMatrixChangingVisitor;
import org.apache.commons.math3.linear.FieldMatrixPreservingVisitor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.qwertygid.deutschsim.Logic.Circuit;
import com.qwertygid.deutschsim.Logic.Gate;
import com.qwertygid.deutschsim.Logic.Table;

public class Serializer {
	public Serializer(final String qubits, final Circuit circuit) {
		this.qubits = qubits;
		this.circuit = circuit;
		
		if (!valid())
			throw new IllegalArgumentException("Cannot save an invalid state");
		
		set_up_gson();
	}
	
	public Serializer(final String filename) throws IOException {
		set_up_gson();
		deserialize(filename);
	}
	
	public void serialize(final String filename) throws IOException {
		if (!valid())
			throw new RuntimeException("Tried to save an invalid state");
		
		String json = gson.toJson(this);
		
		PrintWriter fileout = new PrintWriter(filename);
		fileout.println(json);
		fileout.close();
	}
	
	public void deserialize(final String filename) throws IOException{
		BufferedReader filein = new BufferedReader(new FileReader(filename));
		
		StringBuilder json = new StringBuilder();
		String line = filein.readLine();
		
		while (line != null) {
			json.append(line);
			json.append('\n');
			line = filein.readLine();
		}
		
		filein.close();
		
		Serializer s = gson.fromJson(json.toString(), Serializer.class);
		if (!s.valid())
			throw new IllegalArgumentException("Loaded circuit is corrupted");
		
		qubits = s.qubits;
		circuit = s.circuit;
	}
	
	public boolean valid() {
		return circuit.valid() && circuit.valid_qubit_sequence(qubits);
	}
	
	public HashSet<Gate> get_used_gates() {
		HashSet<Gate> used_gates = new HashSet<Gate>();
		
		Table<Gate> gates = circuit.get_gates_table();
		
		for (int row = 0; row < gates.get_row_count(); row++)
			for (int col = 0; col < gates.get_col_count(); col++) {
				Gate current = gates.get_element(row, col);
				if (current != null)
					used_gates.add(current);
			}
		
		return used_gates;
	}
	
	public String get_qubit_sequence() {
		return qubits;
	}
	
	public Circuit get_circuit() {
		return circuit;
	}
	
	private void set_up_gson() {
		GsonBuilder builder = new GsonBuilder();
		
		JsonSerializer<FieldMatrix<Complex>> matrix_serializer = new JsonSerializer<FieldMatrix<Complex>>() {
			@Override
			public JsonElement serialize(FieldMatrix<Complex> src, Type typeOfSrc,
					JsonSerializationContext context) {			
				JsonArray arr = new JsonArray();				
				src.walkInRowOrder(new MatrixSerializingWalker(arr));
				
				JsonObject obj = new JsonObject();
				obj.add("data", arr);
				
				return obj;
			}
		};
		builder.registerTypeAdapter(FieldMatrix.class, matrix_serializer);
		
		JsonDeserializer<FieldMatrix<Complex>> matrix_deserializer = new JsonDeserializer<FieldMatrix<Complex>>() {

			@Override
			public FieldMatrix<Complex> deserialize(JsonElement json,
					Type type, JsonDeserializationContext context)
					throws JsonParseException {
				JsonArray json_mat = json.getAsJsonObject().get("data").getAsJsonArray();
				
				if (!valid_json_matrix(json_mat))
					throw new JsonParseException("Matrix data in the provided JSON file is corrupted");
				
				final int rows = json_mat.size(), cols = json_mat.get(0).getAsJsonArray().size();
				
				FieldMatrix<Complex> mat = new Array2DRowFieldMatrix<Complex>(ComplexField.getInstance(),
						rows, cols);
				mat.walkInRowOrder(new MatrixDeserializingWalker(json_mat));
				
				return mat;
			}
			
		};
		builder.registerTypeAdapter(FieldMatrix.class, matrix_deserializer);
		
		gson = builder.create();
	}
	
	private boolean valid_json_matrix(final JsonArray mat) {
		if (mat.size() == 0)
			return false;
		
		int row_length = mat.get(0).getAsJsonArray().size();
		for (int row = 1; row < mat.size(); row++)
			if (mat.get(row).getAsJsonArray().size() != row_length)
				return false;
		
		return true;
	}
	
	private String qubits;
	private Circuit circuit;
	
	private transient Gson gson;
	
	private static class MatrixSerializingWalker implements FieldMatrixPreservingVisitor<Complex> {
		public MatrixSerializingWalker(JsonArray arr) {
			this.arr = arr;
			last_row = 0;
			row_array = new JsonArray();
		}
		
		@Override
		public Complex end() {
			arr.add(row_array);
			return null;
		}

		@Override
		public void start(int rows, int cols, int start_row, int end_row, int start_col, int end_col) {
			
		}

		@Override
		public void visit(int row, int col, Complex value) {
			if (row != last_row) {
				arr.add(row_array);
				row_array = new JsonArray();
				last_row++;
			}
			
			JsonObject cell = new JsonObject();
			cell.addProperty("imaginary", value.getImaginary());
			cell.addProperty("real", value.getReal());
			
			row_array.add(cell);
		}
		
		private JsonArray arr, row_array;
		private int last_row;
	}
	
	private static class MatrixDeserializingWalker implements FieldMatrixChangingVisitor<Complex> {
		public MatrixDeserializingWalker(JsonArray data) {
			this.data = data;
			last_row = 0;
			row_arr = data.get(0).getAsJsonArray();
		}
		
		@Override
		public Complex end() {
			return null;
		}

		@Override
		public void start(int rows, int cols, int start_row, int end_row, int start_col, int end_col) {
			
		}

		@Override
		public Complex visit(int row, int col, Complex value) {
			if (row != last_row) {
				row_arr = data.get(row).getAsJsonArray();
				last_row++;
			}
			
			JsonObject cell = row_arr.get(col).getAsJsonObject();
			return new Complex(cell.get("real").getAsDouble(), cell.get("imaginary").getAsDouble());
		}
		
		private int last_row = 0;
		private JsonArray data, row_arr;
	}
}
