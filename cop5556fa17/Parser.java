package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;

//import com.sun.tracing.dtrace.DependencyClass;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
//import cop5556fa17.SimpleParser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;
import cop5556fa17.AST.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
//	public void parse() throws SyntaxException {
//		program();
//		
//		matchEOF();
//	}
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}


	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		//TODO  implement this
		Token first=t,name;
		ArrayList<ASTNode> decsAndStatements=new ArrayList<ASTNode>();
		
		//System.out.println("---------------------Program--------------------------");
		if(t.kind==Kind.IDENTIFIER){
			name=t;
			match();
			//if(t.kind==Kind.KW_url||t.kind==Kind.KW_file||t.kind==Kind.KW_image||t.kind==Kind.IDENTIFIER||t.kind==Kind.KW_int||t.kind==Kind.KW_boolean||t.kind==Kind.IDENTIFIER)
			while(t.kind==Kind.KW_url||t.kind==Kind.KW_file||t.kind==Kind.KW_image||t.kind==Kind.IDENTIFIER||t.kind==Kind.KW_int||t.kind==Kind.KW_boolean){
				if(t.kind==Kind.IDENTIFIER)
				{
					
					Statement s=Statement();
					decsAndStatements.add(s);
					if(t.kind==Kind.SEMI)
						{match();}
					else
						throw new SyntaxException(t,"Error");
				}
				else if(t.kind==Kind.KW_url||t.kind==Kind.KW_file||t.kind==Kind.KW_image||t.kind==Kind.KW_int||t.kind==Kind.KW_boolean)
				{
					Declaration d=Declaration();
					decsAndStatements.add(d);
					if(t.kind==Kind.SEMI)
						{match();}
					else
						throw new SyntaxException(t,"Syntax Error");
				}
				
			}
		
		}
	else
			throw new SyntaxException(t,"Syntax Error");
		
	return new Program(first, name, decsAndStatements);
		
	}
	Declaration Declaration()throws SyntaxException{
		//System.out.println("---------------------Declaration--------------------------");
		if(t.kind==Kind.KW_url||t.kind==Kind.KW_file){
			Declaration_SourceSink d=SourceSinkDeclaration();
			return d;
		}
		else if(t.kind==Kind.KW_image){
			
			Declaration_Image d=  ImageDeclaration();
			return d;
		}
		else if(t.kind==Kind.KW_int||t.kind==Kind.KW_boolean){
			Declaration_Variable d= VariableDeclaration();
			return d;
		}
		else
			throw new SyntaxException(t,"Syntax Error");
		
	}
	Declaration_Variable VariableDeclaration() throws SyntaxException{
		//System.out.println("---------------------VariableDeclaration--------------------------");
		Token firstToken=t;
		Token type;
		Token name;
		Expression e = null;
		type=t;
		VarType();
		if(t.kind==Kind.IDENTIFIER)
			{name=t;
			match();}
		else
			throw new SyntaxException(t,"Syntax Error");
		if(t.kind==Kind.OP_ASSIGN)
		{	match();
		    e=expression();
		}
		return new Declaration_Variable(firstToken, type, name, e);
	}
	Declaration_SourceSink SourceSinkDeclaration () throws SyntaxException{
		//System.out.println("---------------------SourceSinkDeclaration--------------------------");
		Token firstToken=t;
		Token type;
		Token name;
		Source source;
		type=t;
		SourceSinkType();
		if(t.kind==Kind.IDENTIFIER)
			{ name=t;
			  match();}
		else
			throw new SyntaxException(t,"Syntax Error");
		if(t.kind==Kind.OP_ASSIGN)
			match();
		else
			throw new SyntaxException(t,"Syntax Error");
		source=Source();
		
		return new Declaration_SourceSink(firstToken, type, name, source); 
	}
	Declaration_Image ImageDeclaration() throws SyntaxException{
		//System.out.println("---------------------ImageDeclaration--------------------------");
		Token firstToken=t;
		Expression xSize = null; 
		Expression ySize = null;
		Token name;
		Source source = null;
		if(t.kind==Kind.KW_image)
		{
			
			match();
			if(t.kind==Kind.LSQUARE)
			{	match();
			    xSize= expression();
			   // System.out.println("llllllllllllllllllllllll");
			if(t.kind==Kind.COMMA)
				match();
			else
				throw new SyntaxException(t,"Syntax Error");
			 ySize=expression();
			if(t.kind==Kind.RSQUARE)
				match();
			else
				throw new SyntaxException(t,"Syntax Error");
			}
			if(t.kind==Kind.IDENTIFIER){
				//System.out.println("uuuuuuuuuuuuuuuuuu"+t.getText());
				name=t;
				match();
				if(t.kind==Kind.OP_LARROW)
				{	match();
				   source=Source();
				}
			}
			else
				throw new SyntaxException(t,"Syntax Error");
		}
		
		else
			throw new SyntaxException(t,"Syntax Error");
		return new Declaration_Image(firstToken, xSize, ySize, name, source);
	}
	Statement Statement() throws SyntaxException{
		//System.out.println("---------------------Statement--------------------------");
		
		if(t.kind==Kind.IDENTIFIER){
			if(scanner.peek().kind==Kind.OP_RARROW)
				{Statement_Out s=ImageOutStatement();return s;}
			else if (scanner.peek().kind==Kind.OP_LARROW) {
				{Statement_In s=ImageInStatement();return s;}
			}
			else
				{Statement_Assign s=AssignmentStatement();return s;}
		}
		else
			throw new SyntaxException(t,"Syntax Error");
	}
	Statement_Out ImageOutStatement() throws SyntaxException{
		//System.out.println("----------------------ImageOutStatement-------------------------");
		Token firstToken=t,  name;
		Sink sink;
		if(t.kind==Kind.IDENTIFIER)
			{name=t;match();}
		else
			throw new SyntaxException(t,"Syntax Error");
		if(t.kind==Kind.OP_RARROW)
			match();
		else
			throw new SyntaxException(t,"Syntax Error");
		sink=Sink();
		//System.out.println("SInk finished");
		return new Statement_Out(firstToken, name, sink);
	}
	Sink Sink() throws SyntaxException{
		//System.out.println("----------------------Sink-------------------------");
		if(t.kind==Kind.IDENTIFIER||t.kind==Kind.KW_SCREEN)
			{
			  if(t.kind==Kind.IDENTIFIER){
				  Token firstToken=t, name;
				  name=t;
				  match();
				  return new Sink_Ident(firstToken, name);
			  }
			  else{
				  Token firstToken=t;
				  match();
				  return new Sink_SCREEN(firstToken);
			  }
			 }
		else
			throw new SyntaxException(t,"Syntax Error");
		
	}
	Statement_In ImageInStatement() throws SyntaxException{
		//System.out.println("----------------------ImageInStatement-------------------------");
		Token firstToken=t,  name;
		Source source;
		if(t.kind==Kind.IDENTIFIER)
			{name=t;match();}
		else
			throw new SyntaxException(t,"Syntax Error");
		if(t.kind==Kind.OP_LARROW)
			match();
		else
			throw new SyntaxException(t,"Syntax Error");
		source = Source();
		return new Statement_In(firstToken, name, source);
	}
	Statement_Assign AssignmentStatement() throws SyntaxException{
		//System.out.println("----------------------AssignmentStatement-------------------------");
		Token firstToken=t; LHS lhs; Expression e;
		lhs=Lhs();
		if(t.kind==Kind.OP_ASSIGN)
			match();
		else 
			throw new SyntaxException(t,"Syntax Error");
		e=expression();
		return new Statement_Assign(firstToken, lhs, e);
	}
	
	Expression OrExpression() throws SyntaxException{
		//System.out.println("----------------------OrExpression-------------------------");
		Token firstToken=t; 
		Expression e0; 
		Token op = null; 
		Expression e1 = null;
		e0=AndExpression();
		while(t.kind==Kind.OP_OR){
			op=t;
			match();
			e1=AndExpression();
			e0=new Expression_Binary(firstToken, e0, op, e1);
		}
		//System.out.println("llllllllllllllllhhhhhh");
		return e0;
	}
	Expression AndExpression() throws SyntaxException{
		//System.out.println("----------------------AndExpression-------------------------");
		Token firstToken=t; 
		Expression e0; 
		Token op = null; 
		Expression e1 = null;
		e0=EqExpression();
		while(t.kind==Kind.OP_AND){
			op=t;
			match();
			e1=EqExpression();
			e0=new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	Expression EqExpression() throws SyntaxException{
		//System.out.println("----------------------EqExpression-------------------------");
		Token firstToken=t; 
		Expression e0; 
		Token op = null; 
		Expression e1 = null;
		e0=RelExpression();
		while(t.kind==Kind.OP_EQ||t.kind==Kind.OP_NEQ){
			op=t;
			match();
			e1=RelExpression();
			e0=new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	Expression RelExpression() throws SyntaxException{
		//System.out.println("----------------------RelExpression-------------------------");
		Token firstToken=t; 
		Expression e0; 
		Token op = null; 
		Expression e1 = null;
		e0=AddExpression();
		while(t.kind==Kind.OP_LT||t.kind==Kind.OP_GT||t.kind==Kind.OP_LE||t.kind==Kind.OP_GE){
			op=t;
			match();
			e1=AddExpression();
			e0=new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	Expression AddExpression() throws SyntaxException{
		//System.out.println("----------------------AddExpression-------------------------");
		Token firstToken=t; 
		Expression e0; 
		Token op = null; 
		Expression e1 = null;
		e0=MultExpression();
		//System.out.println("::::::::::::::");
		while(t.kind==Kind.OP_PLUS||t.kind==Kind.OP_MINUS){
			op=t;
			match();
			e1=MultExpression();
			e0=new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	Expression MultExpression() throws SyntaxException {
		//TODO  implement this
		//System.out.println("----------------------MultExpression-------------------------");
		Token firstToken=t; 
		Expression e0; 
		Token op = null; 
		Expression e1 = null;
			e0=UnaryExpression();
			//System.out.println("&&&&&&&&&&&&&&&&77");
			while(t.kind==Kind.OP_TIMES||t.kind==Kind.OP_DIV||t.kind==Kind.OP_MOD){
				op=t;
				match();
				e1=UnaryExpression();
				e0= new Expression_Binary(firstToken, e0, op, e1);
			}
			//System.out.println("&&&&&&&&&&&&&&&&77");
			return e0;
	}
	Expression UnaryExpression() throws SyntaxException{
		Token firstToken=t; 
		Token op; 
		Expression e;
		//System.out.println("----------------------UnaryExpression-------------------------");
		if(t.kind==Kind.OP_PLUS){
			op=t;
			match();
			e=UnaryExpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else if (t.kind==Kind.OP_MINUS) {
			op=t;
			match();
			e=UnaryExpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else if (t.kind==Kind.OP_EXCL
				||t.kind==Kind.BOOLEAN_LITERAL||t.kind==Kind.INTEGER_LITERAL||t.kind==Kind.LPAREN||t.kind==Kind.KW_sin||t.kind==Kind.KW_cos||t.kind==Kind.KW_atan||t.kind==Kind.KW_abs||t.kind==Kind.KW_cart_x||t.kind==Kind.KW_cart_y||t.kind==Kind.KW_polar_a||t.kind==Kind.KW_polar_r
				||t.kind==Kind.IDENTIFIER
				||t.kind==Kind.KW_x||t.kind==Kind.KW_y||t.kind==Kind.KW_r||t.kind==Kind.KW_a||t.kind==Kind.KW_X||t.kind==Kind.KW_Y||t.kind==Kind.KW_Z||t.kind==Kind.KW_A||t.kind==Kind.KW_R||t.kind==Kind.KW_DEF_X||t.kind==Kind.KW_DEF_Y) 
		{
			//System.out.println("*****************");
			 return UnaryExpressionNotPlusMinus();
		}
		else {
			throw new SyntaxException(t,"Syntax Error");
		}
	}
	Expression UnaryExpressionNotPlusMinus()throws SyntaxException{
		//System.out.println("----------------------UnaryExpressionNotPlusMinus-------------------------");
		Token firstToken=t; 
		Token op; 
		Expression e;
		if(t.kind==Kind.OP_EXCL){
			op=t;
			match();
			e=UnaryExpression();
			return new Expression_Unary(firstToken, op, e);
		}
		else if (t.kind==Kind.BOOLEAN_LITERAL||t.kind==Kind.INTEGER_LITERAL||t.kind==Kind.LPAREN||t.kind==Kind.KW_sin||t.kind==Kind.KW_cos||t.kind==Kind.KW_atan||t.kind==Kind.KW_abs||t.kind==Kind.KW_cart_x||t.kind==Kind.KW_cart_y||t.kind==Kind.KW_polar_a||t.kind==Kind.KW_polar_r) {
			//op=t;
			//match();
			//System.out.println("^^^^^^^^^^^^^^^^^^666"+t.getText());
			e=Primary();
			return e;
		}
		else if (t.kind==Kind.IDENTIFIER) {
			return IdentOrPixelSelectorExpression();
		}
		else if (t.kind==Kind.KW_x||t.kind==Kind.KW_y||t.kind==Kind.KW_r||t.kind==Kind.KW_a||t.kind==Kind.KW_X||t.kind==Kind.KW_Y||t.kind==Kind.KW_Z||t.kind==Kind.KW_A||t.kind==Kind.KW_R||t.kind==Kind.KW_DEF_X||t.kind==Kind.KW_DEF_Y) {
            Kind k=t.kind;
			match();
			return new Expression_PredefinedName(firstToken, k);
		}
		else {
			throw new SyntaxException(t,"Syntax Error");
		}
	}
	Expression Primary() throws SyntaxException{
		System.out.println("----------------------Primary-------------------------");
		Token firstToken=t;
		int value;
		if(t.kind==Kind.INTEGER_LITERAL){
			String val=t.getText();
			match();
			return new Expression_IntLit(firstToken, Integer.parseInt(val));
		}
		else if(t.kind==Kind.BOOLEAN_LITERAL){
			String val=t.getText();
			match();
			return new Expression_BooleanLit(firstToken, Boolean.parseBoolean(val));
		}
		else if(t.kind==Kind.KW_sin||t.kind==Kind.KW_cos||t.kind==Kind.KW_atan||t.kind==Kind.KW_abs||t.kind==Kind.KW_cart_x||t.kind==Kind.KW_cart_y||t.kind==Kind.KW_polar_a||t.kind==Kind.KW_polar_r)
		{
			return FunctionApplication();
		}	
		else if (t.kind==Kind.LPAREN) {
			match();
			/* Expression ----------------------------------*/
			Expression e=expression();
			if(t.kind==Kind.RPAREN)
				match();
			else 
				throw new SyntaxException(t,"Syntax Error");
			return e;
		}
		else 
			throw new SyntaxException(t,"Syntax Error");

	}
    Expression IdentOrPixelSelectorExpression() throws SyntaxException{
//    	System.out.println("----------------------IdentOrPixelSelectorExpression-------------------------");
//    	System.out.println("oooo");
    	Token firstToken=t;
    	Token name;
    	Index index;
    	if(t.kind==Kind.IDENTIFIER){
    		name=t;
    		match();
    		//System.out.println("iiiiii");
    		if(t.kind==Kind.LSQUARE){
    			match();
    		//	System.out.println("ppppppppp");
    			index =Selector();
    			if(t.kind==Kind.RSQUARE)
    				match();
    			else
    				throw new SyntaxException(t,"Syntax Error");
    		 return new Expression_PixelSelector(firstToken, name, index);
    		}
    		//System.out.println("aaaaaaaaa");
    		return new Expression_Ident(firstToken, name);
    	}
    	else
    		throw new SyntaxException(t,"Syntax Error");
    }
	Index Selector() throws SyntaxException{
		Token firstToken=t; Expression e0; Expression e1;
		/*Expression()-----------------------------------------------------------*/
		//System.out.println("----------------------Selector-------------------------");
		e0=expression();
		if(t.kind==Kind.COMMA)
			match();
		else
			throw new SyntaxException(t,"Syntax Error");
		/*Expression()-----------------------------------------------------------*/
		e1=expression();
		return new Index(firstToken,e0,e1);
	}
	Expression FunctionApplication() throws SyntaxException{
		//System.out.println("----------------------FunctionApplication-------------------------");
		Token firstToken=t; 
		Kind function; 
		Expression arg;
		if(t.kind==Kind.KW_sin||t.kind==Kind.KW_cos||t.kind==Kind.KW_atan||t.kind==Kind.KW_abs||t.kind==Kind.KW_cart_x||t.kind==Kind.KW_cart_y||t.kind==Kind.KW_polar_a||t.kind==Kind.KW_polar_r)
			{
			    function=t.kind;
				FunctionName();
				switch (t.kind) {
				case LPAREN:
					if(t.kind==Kind.LPAREN)
						match();
					else
						throw new SyntaxException(t,"Syntax Error");
					/*Expression()-----------------------------------*/
					arg=expression();
					if(t.kind==Kind.RPAREN)
						match();
					else
						throw new SyntaxException(t,"Syntax Error");
					return new Expression_FunctionAppWithExprArg(firstToken, function, arg);
					
				case LSQUARE:
					if(t.kind==Kind.LSQUARE)
						match();
					else
						throw new SyntaxException(t,"Syntax Error");
					Index c=Selector();
					if(t.kind==Kind.RSQUARE)
						match();
					else
						throw new SyntaxException(t,"Syntax Error");
					return new Expression_FunctionAppWithIndexArg(firstToken, function, c);
					
				default:
					throw new SyntaxException(t,"Syntax Error");
				}
			}
		else
			throw new SyntaxException(t,"Syntax Error");
	}
	void FunctionName()throws SyntaxException{
		//System.out.println("----------------------FunctionName-------------------------");
		if(t.kind==Kind.KW_sin||t.kind==Kind.KW_cos||t.kind==Kind.KW_atan||t.kind==Kind.KW_abs||t.kind==Kind.KW_cart_x||t.kind==Kind.KW_cart_y||t.kind==Kind.KW_polar_a||t.kind==Kind.KW_polar_r)
			{
			      //System.out.println(t.kind);
			      match();
			}
		else
			throw new SyntaxException(t,"Syntax Error");
		}
	LHS Lhs() throws SyntaxException{
		Token firstToken=t; 
		Token name; 
		Index index = null;
		//System.out.println("----------------------Lhs-------------------------");
		if(t.kind==Kind.IDENTIFIER){
			name=t;
			match();
			if(t.kind==Kind.LSQUARE)
			{
				match();
				index = LhsSelector();
				if(t.kind==Kind.RSQUARE)
					match();
				else
					throw new SyntaxException(t,"Syntax Error");
			}
		}
		else
			throw new SyntaxException(t,"Syntax Error");
		
		return new LHS(firstToken,name,index);

	}
    Index LhsSelector()throws SyntaxException{
    	//System.out.println("----------------------LhsSelector-------------------------");
    	Index i;
    	if(t.kind==Kind.LSQUARE){
    		match();
    		if(t.kind==Kind.KW_x){
    			i= XySelector();
    		}
    		else if(t.kind==Kind.KW_r){
    		    i= RaSelector();	
    		}
    		else
    			throw new SyntaxException(t,"Syntax Error");
    		
    		if(t.kind==Kind.RSQUARE)
    			match();
    		else
    			throw new SyntaxException(t,"Syntax Error");
    		
    		return i;
    	}
    	else
			throw new SyntaxException(t,"Syntax Error");
    }
    Index XySelector()throws SyntaxException{
    	//System.out.println("----------------------XySelector-------------------------");
    	Token firstToken=t; 
    	Expression e0;
    	Expression e1;
    	if(t.kind==Kind.KW_x)
			{e0 = new Expression_PredefinedName(firstToken,t.kind);match();}
    	else
    		throw new SyntaxException(t,"Syntax Error");
    	if(t.kind==Kind.COMMA)
			match();
    	else
    		throw new SyntaxException(t,"Syntax Error");
    	if(t.kind==Kind.KW_y)
			{e1=new Expression_PredefinedName(firstToken,t.kind);match();}
    	else
    		throw new SyntaxException(t,"Syntax Error");
    	return new Index(firstToken, e0, e1);
    }
    Index RaSelector()throws SyntaxException{
    	System.out.println("----------------------RaSelector-------------------------");
    	Token firstToken=t; 
    	Expression e0;
    	Expression e1;
    	System.out.println(t.kind+"-------------");
    	if(t.kind==Kind.KW_r)
			{e0 = new Expression_PredefinedName(firstToken,t.kind);match();System.out.println(t.kind+"-------------");}
    	else
    		throw new SyntaxException(t,"Syntax Error");
    	if(t.kind==Kind.COMMA)
			{match();System.out.println(t.kind+"-------------");}
    	else
    		throw new SyntaxException(t,"Syntax Error");
    	if(t.kind==Kind.KW_a)
			{e1=new Expression_PredefinedName(firstToken,t.kind);match();}
    	else
    		throw new SyntaxException(t,"Syntax Error");
    	return new Index(firstToken,e0,e1);
    }
    void VarType()throws SyntaxException{
    	//System.out.println("----------------------VarType-------------------------");
    	if(t.kind==Kind.KW_int||t.kind==Kind.KW_boolean)
			match();
    	else
    		throw new SyntaxException(t,"Syntax Error");
    	
    }
    Source Source()throws SyntaxException{
    	Token firstToken=t;
    	Expression paramNum;
    	//System.out.println("----------------------Source-------------------------");
    	if(t.kind==Kind.STRING_LITERAL||t.kind==Kind.IDENTIFIER){
    		{
    			if(t.kind==Kind.STRING_LITERAL)
    				{ String str=t.getText();match();return new Source_StringLiteral(firstToken, str);}
    			else
    			{ Token p=t;match();return new Source_Ident(firstToken, p);}
    			
    		 }
    	}
    	else if(t.kind==Kind.OP_AT){
    		match();
    		/*  finish later ------------------------------------------------------------------------------- */
    		 paramNum=expression();
    		 return new Source_CommandLineParam(firstToken, paramNum);
    	}
    	else
    		throw new SyntaxException(t,"Syntax Error");
    	
    	//System.out.println("Exitting source");
    }
    void SourceSinkType() throws SyntaxException{
    	//System.out.println("----------------------SourceSinkType-------------------------");
    	if(t.kind==Kind.KW_url||t.kind==Kind.KW_file)
    		match();
    	else 
    		throw new SyntaxException(t,"Syntax Error");
		
    }
	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		//TODO implement this.
		//System.out.println("----------------------expression-------------------------");
		Token firstToken=t; 
		Expression condition; 
		Expression trueExpression;
		Expression falseExpression;
		condition=OrExpression();
		if(t.kind==Kind.OP_Q){
			match();
			trueExpression=expression();
			if(t.kind==Kind.OP_COLON)
				match();
			else
				throw new SyntaxException(t,"Syntax Error");
			
			falseExpression=expression();
			return new Expression_Conditional(firstToken, condition, trueExpression, falseExpression);
		}
		return condition;
	}


	@SuppressWarnings("unused")
	private void match()throws SyntaxException{
		//System.out.println("match------------"+t.getText());
		t=scanner.nextToken();
		//System.out.println(t.getText());
			}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
