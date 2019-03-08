package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays){
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	StringTokenizer st = new StringTokenizer(expr, delims, true);
    	ArrayList<String> token = new ArrayList<>();
    	while(st.hasMoreTokens()) {
    		token.add(st.nextToken());
    	}
    	
    	for(int i = 1; i < token.size(); i++) {
    		if(token.get(i).contains("[")){
    			if(isArray(arrays, token.get(i-1)) == -1){
    				arrays.add(new Array(token.get(i-1)));
    			}
    		}else if(Pattern.matches("[a-zA-Z]+",token.get(i-1)) && isArray(arrays, token.get(i-1)) == -1){
    			if(isVariable(vars, token.get(i-1)) == -1){
    				vars.add(new Variable(token.get(i-1)));
    			}
    		}
    	}
    	if(Pattern.matches("[a-zA-Z]+",token.get(token.size()-1)) && isArray(arrays, token.get(token.size()-1)) == -1){
    		if(isVariable(vars, token.get(token.size()-1)) == -1){
    			vars.add(new Variable(token.get(token.size()-1)));
        	}
    	}
    }

 
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    /* helper methods */
    private static boolean isNumber(String input){//checks whether something is a constant
       try
       {
          Integer.parseInt(input);
          return true;
       }
       catch(Exception e)
       {
          return false;
       }
    }
    private static boolean OOO(String current, String top){//for order of operations
    	if((current.equals("*") || current.equals("/")) && (top.equals("+") || top.equals("-"))) {
    		return false;
    	}if(top.equals("(")) {
    		return false;
    	}if(top.equals("[")){
    		return false;
    	}
    	return true;
    	}
    private static int isVariable(ArrayList<Variable> vars, String name){//checks if the token is a variable or an array name
    	for(int i = 0; i < vars.size(); i++) {
    		if(vars.get(i).name.equals(name)) {
    			return i;
    		}
    	}
    	return -1;
    		
    }
    private static int isArray(ArrayList<Array> arrays, String name){//checks if the token is an array name or variable
    	for (int i = 0; i < arrays.size(); i++) {
    		if(arrays.get(i).name.equals(name)) {
    			return i;
    		}
    	}
    	return -1;
    }
    private static float getValue(ArrayList<Array> arrays, float index, String name) {//returns the value at index i of an array
    	int index1= (int) index;
    	for(int i = 0; i < arrays.size(); i++){
    		if(arrays.get(i).name.equals(name)){
    			Array arrs = arrays.get(i);
    			return (float)arrs.values[index1];
    		}
    	}
    	return -1;
    }
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	Stack<Float> value = new Stack<>();
    	Stack<String> operator = new Stack<>();
    	StringTokenizer st = new StringTokenizer(expr, delims, true);
    	while(st.hasMoreTokens()) {//reads through expression
    		String current = st.nextToken();
    		if(current.equals(" ") || current.equals("\t")){
    		}else if(isNumber(current)){//adds constant to stack
    			value.push(Float.parseFloat(current));
    			continue;
    		}else if(isVariable(vars, current) != -1){//adds the variable value to stack //horrendous code
    			value.push((float)vars.get(isVariable(vars,current)).value);//horrendous code
    		}else if(current.equals("(")){//deals with parenthesis
    			operator.push("(");
    		}else if(current.equals(")")){//reach end of subexpression
    			while(operator.peek() != "("){
    				float pop2 = value.pop();
    				float pop1 = value.pop();
    				String sign = operator.pop();
    				switch(sign){
    					case "+": 
    						value.push(pop1 + pop2);
    						continue;
    					case "-":
    						value.push(pop1 - pop2);
    						continue;
    					case "*":
    						value.push(pop1 * pop2);
    						continue;
    					case "/":
    						value.push(pop1 / pop2);
    						continue;
    				}
    			}
    			operator.pop();
    		}else if(current.equals("[")){//deals with brackets
    			operator.push("[");
    		}else if(isArray(arrays, current) != -1){//deals with names of arrays
    			operator.push(current);
    		}else if(current.equals("]")) {//reach end of subexpression and evaluate the array at index ____
    			while(operator.peek() != "["){
    				float pop2 = value.pop();
    				float pop1 = value.pop();
    				String sign = operator.pop();
    				switch(sign){
    					case "+": 
    						value.push(pop1 + pop2);
    						continue;
    					case "-":
    						value.push(pop1 - pop2);
    						continue;
    					case "*":
    						value.push(pop1 * pop2);
    						continue;
    					case "/":
    						value.push(pop1 / pop2);
    						continue;
    				}
    			}
    			operator.pop();
    			String ArrayName = operator.pop();
    			float ArrayIndex = value.pop();
    			value.push(getValue(arrays, ArrayIndex, ArrayName));
    		}else if(current.equals("+") || current.equals("-") || current.equals("*") || current.equals("/")){//deals with operators and pemdas
    			while(operator.isEmpty() != true && OOO(current, operator.peek())){
    				float pop2 = value.pop();
    				float pop1 = value.pop();
    				String sign = operator.pop();
    				switch(sign){
					case "+": 
						value.push(pop1 + pop2);
						continue;
					case "-":
						value.push(pop1 - pop2);
						continue;
					case "*":
						value.push(pop1 * pop2);
						continue;
					case "/":
						value.push(pop1 / pop2);
						continue;
				}
			}
    			operator.push(current);
    		}
    	}
    	while(operator.isEmpty() != true) {//evaluate the end of the expression when there are no more tokens
    		float pop2 = value.pop();
			float pop1 = value.pop();
			String sign = operator.pop();
			switch(sign){
			case "+": 
				value.push(pop1 + pop2);
				continue;
			case "-":
				value.push(pop1 - pop2);
				continue;
			case "*":
				value.push(pop1 * pop2);
				continue;
			case "/":
				value.push(pop1 / pop2);
				continue;
			}
    	}
    	return value.pop();//return final result
    }
}
