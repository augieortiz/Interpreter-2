import java.io.*;

import java.util.ArrayList;

import javax.xml.stream.events.NotationDeclaration;

/*
 * Written and Coded by Agustin Ortiz
 * 
 * CSE 6341: Lisp Interpreter Project, Part 2
 * Due: Tuesday August 6th at 23:59
 * Java Implementation of Lexical Analyzer & Parser
 * 
 * */
public class Interpreter {
	
	public static Token current; //Current Token for Parser
	public static InputStream scan = System.in; //Input must be stdin
	public static BinaryTree exp; // Global Tree to pass to printer
	public static class Token 
	{
		String type; // Five types of Tokens:  Atom, OpenParenthesis, ClosingParenthesis, ERROR, and EOF
		String atomType; // Atom Categories: LiteralAtom, NumericAtom.
		String value; 
	}

	public static ArrayList<Token> tokenList = new ArrayList<Token>(); //List to track all tokens returned by getNextToken
	
	public static void printer()
	{
		//Token Counts
		int op = 0;
		int cp = 0;
		int na = 0;
		int la = 0;
		int naTotal = 0;
	
		//Token Value Strings
		String naValues = "";
		String laValues = "";
		
		//Iterate over list and get stats & concatenate value strings
		for(Token element : tokenList) {
			  if (element.type == "OpenParenthesis")
			  {
				  op++;
				  
			  }
			  else if (element.type == "ClosedParenthesis")
			  {
				  cp++;
			  }
			  else if(element.type == "Atom")
			  {
				  if(element.atomType == "LiteralAtom")
				  {
					la++;  
					laValues += ", " + element.value;
				  }
				  else if(element.atomType == "NumericAtom")
				  {
					na++;  
					naValues += ", " + element.value;
					naTotal += Integer.parseInt(element.value);
				  }
			  }
			}
		
		//Output must be stdout
		System.out.println("LITERAL ATOMS: " + la + laValues);
		System.out.println("NUMERIC ATOMS: " + na + ", " + naTotal);
		System.out.println("OPEN PARENTHESES: " + op);
		System.out.println("CLOSING PARENTHESES: " + cp);
	}
	
	public static void printError(Token errorToken)
	{
		//Output must be stdout
		System.out.println(errorToken.type + " Invalid token: " + errorToken.value );
		System.exit(1);
	}
	
	//Helper function returns token 
	public static Token getNextToken()
	{
		Token token = new Token(); //Create single token object
		try
		{
			int tempInput = 1;
			//Loop to read input, exit when there nothing available in the stream
			while(scan.available() != 0)
			{
				tempInput = scan.read();
				
				//1. If the input is empty, returns token EOF
				if((char)tempInput == '\0')
				{
					token.type = "EOF";
					token.value = "\0";
					return token;
				}
				//2. If the current character is a white space, consumes it and any white spaces that follow it; if the input
				//is empty after this, returns EOF
				else if(tempInput == 10 || tempInput == 13 || tempInput == 32 )
				{
					scan.mark(1);
					if(scan.available() == 0)
					{
						token.type = "EOF";
						token.value = "  ";
						return token;
					}
					else
					{
						int doubleSpace = scan.read();
						if(doubleSpace == 10 || doubleSpace == 13 || doubleSpace == 32)
						{
							//token.type = "EOF";
							//token.value = "  ";
							//return token;
						}
						else
						{
							scan.reset();
						}
					}
				}
				//3. If the current character is ‘(‘ it consumes it and returns token OpenParenthesis
				else if ((char)tempInput == '(')
				{
					token.value = "(";
					token.type = "OpenParenthesis";
					return token;
				}
				//4. If the current character is ‘)’ it consumes it and returns token ClosingParenthesis
				else if ((char)tempInput == ')')
				{
					token.value = ")";
					token.type = "ClosedParenthesis";
					return token;
				}
				
				//5. If the current character is alphabetic;
				else if ((char)tempInput >= 'A' && (char)tempInput <= 'Z')
				{
					String literalAtom = "";
					scan.mark(1);
					
					//Check if stream continues to be alphanumeric
					while(((char)tempInput >= '0' && (char)tempInput <= '9') || ((char)tempInput >= 'A' && (char)tempInput <= 'Z'))
					{
						scan.mark(1);
						literalAtom += String.valueOf((char)tempInput);
						if(scan.available() > 0)
							tempInput = scan.read();
						else
						{
							tempInput = '\0';
						}
					}
					
					//Atom Token created
					scan.reset();
					token.type = "Atom";
					token.atomType = "LiteralAtom";
					token.value = literalAtom;
					return token;
				}
				
				//6. If the current character is numeric;
				else if ((char)tempInput >= '0' && (char)tempInput <= '9')
				{
					String numeric = "";
					scan.mark(1);
					
					//Check if stream continues to be numeric
					while((char)tempInput >= '0' && (char)tempInput <= '9')
					{
						scan.mark(1);
						numeric += String.valueOf((char)tempInput);
						if(scan.available() > 0)
							tempInput = scan.read();
						else
						{
							tempInput = '\0';
						}
					}
					
					//Check if digit is followed by a digit, error occurs.
					if((char)tempInput >= 'A' && (char)tempInput <= 'Z')
					{
						//ERROR - continue to gather string until character is not alpha-numeric
						while(((char)tempInput >= '0' && (char)tempInput <= '9') || ((char)tempInput >= 'A' && (char)tempInput <= 'Z'))
						{
							scan.mark(1);
							numeric += String.valueOf((char)tempInput);
							if(scan.available() > 0)
								tempInput = scan.read();
						}
						
						//Error Token created.
						scan.reset();
						token.type = "ERROR";
						token.value = numeric;
						return token;
					}
					
					//Atom Token created
					scan.reset();
					token.type = "Atom";
					token.atomType = "NumericAtom";
					token.value = numeric;
					return token;						
				}
			}
		}
		catch(Exception e){
	         
	         // if any I/O error occurs
	         e.printStackTrace();
	      }
		
		//Stream has completed, return EOF token
		token.type = "EOF";
		return token;
	}
	public static void main(String[] args) 
	{
		
		//Main function modified to handle parser organization
		
		init(); //Start the Scanner
		
		while(current.type != "EOF")
		{
			if(current.type == "Error")
				printError(getCurrent());
			
			
			exp = Express(); //Main recursive decent parser
			preetyPrint(exp); //Print the returned tree;
			System.out.println("");
		}
		
		System.exit(1);
		
	}
	
	
	//New Functions for parser
	
	//Gets the next token from the scanner
	public static void MoveToNext()
	{
		current = getNextToken(); 
		tokenList.add(current);
	}
	
	//Get's current token
	public static Token getCurrent()
	{
		return current;
	}
	
	//Starts the Scanner
	public static void init()
	{
		MoveToNext();
	}
	
	//This simple input language is easy to parse using the standard technique of recursive-descent parsing. The
	//high-level structure of the parser is the following:
	public static BinaryTree Express()
	{
		BinaryTree root, node;
		node = root = new BinaryTree(); //Move to the root of the tree to the last returned node.
		if(getCurrent().type == "Atom")
		{

			
			node.isList = false;
			node.value = current.value;
			MoveToNext(); //Consume atom
			
		}
		else if(getCurrent().type == "OpenParenthesis")
		{
			MoveToNext(); //Consume opening parenthesis
			node.isList = true; //Start of new list

			while(getCurrent().type != "ClosedParenthesis")
			{
					node.left = Express();
					node.right = new BinaryTree();
					node = node.right;
			}
			
			if(current.value != ")")
			{
				System.out.println("End Parenthesis expected..."); //Error on parse, exit program
				System.exit(0);
			}
			else
			{
				node.value = "NIL"; //End parenthesis found and end of tree, set to NIL
				node.isList = false;
			}

			MoveToNext(); //Consume closing parenthesis
		}
		else
		{
			//
			// All Error output based on current token
			//
			
			if(current.type == "ERROR")
			{
				System.out.println("ERROR: Sanner error:" + current.value);
				System.exit(0);
			}
			else if(current.value == ")")
			{
				System.out.println("ERROR: Closed parenthesis unexpected.");
				System.exit(0);
			}
			else if(current.value == "(")
			{
				System.out.println("ERROR: Open parenthesis unexpected.");
				System.exit(0);
			}
			else 
			{
				System.out.println("ERROR: Unknon Parse Error.  Posible pre-condition not satisfied.");
				System.exit(0);
			}
			
		}
		return root; 
	}
	
	public static void preetyPrint(BinaryTree exp)
	{
		//Find leaf node;
		if(exp == null) {
			return;
		}
		//Node has children
		else if(exp.isList) {
			System.out.print("(");
			preetyPrint(exp.left);
			System.out.print(" . ");
			preetyPrint(exp.right); 
			System.out.print(")");
		}
		//Node has no children and has value
		else {
			System.out.print(exp.value);
		}	
	}
}


