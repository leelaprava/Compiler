package cop5556fa17;

import java.net.MalformedURLException;
import java.net.URL;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;



import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {
	
        SymbolTable symboltable=new SymbolTable();
		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
	
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(                             // >>>>>>>>>>>>>>>>>>>>>>>>>.>>>>>>>>>>>>>>>>>
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Declaration_Variable"+"11");
		if(declaration_Variable.e!=null){
			declaration_Variable.e.Type=(Type)declaration_Variable.e.visit(this, null);
		}
		if(symboltable.lookupType(declaration_Variable.name)==null){
			System.out.println("declare variable");
			symboltable.insert(declaration_Variable.name, declaration_Variable);
			declaration_Variable.Type=TypeUtils.getType(declaration_Variable.firstToken);
			if(declaration_Variable.e!=null){
				if(declaration_Variable.Type==declaration_Variable.e.Type){
					return declaration_Variable.Type;
				}
				else
					throw new SemanticException(declaration_Variable.firstToken, "declaration variable");
			}
			else
				return declaration_Variable.Type; 
		}
		throw new SemanticException(declaration_Variable.firstToken,"declaration variable");
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Expression_Binary"+"22");
		System.out.println("-----------66666666666666");
		if(expression_Binary.e0!=null){
			expression_Binary.e0.Type=(Type)expression_Binary.e0.visit(this, null);
		}
		if(expression_Binary.e1!=null){
			expression_Binary.e1.Type=(Type)expression_Binary.e1.visit(this, null);
		}
		System.out.println(expression_Binary.e0.Type+"55555555555555555"+expression_Binary.e1.Type+"55"+expression_Binary.Type);
		 
			 if(expression_Binary.op==Kind.OP_EQ||expression_Binary.op==Kind.OP_NEQ){
				 expression_Binary.Type=Type.BOOLEAN;
			 }
			 else if(expression_Binary.op==Kind.OP_GT||expression_Binary.op==Kind.OP_GE||expression_Binary.op==Kind.OP_LT||expression_Binary.op==Kind.OP_LE&&(expression_Binary.e0.Type==Type.INTEGER)){
				 expression_Binary.Type=Type.BOOLEAN;
			 }
			 else if((expression_Binary.op==Kind.OP_AND||expression_Binary.op==Kind.OP_OR)&&(expression_Binary.e0.Type==Type.INTEGER||expression_Binary.e0.Type==Type.BOOLEAN)){
				 expression_Binary.Type=expression_Binary.e0.Type;
			 }
			 else if(expression_Binary.op==Kind.OP_DIV||expression_Binary.op==Kind.OP_MINUS||expression_Binary.op==Kind.OP_MOD||expression_Binary.op==Kind.OP_PLUS||expression_Binary.op==Kind.OP_POWER||expression_Binary.op==Kind.OP_TIMES&&(expression_Binary.e0.Type==Type.INTEGER)){
				expression_Binary.Type=Type.INTEGER; 
			 }
			 else{
				 System.out.println("------------00000000000");
				 expression_Binary.Type=null;
			 }
			
			 if(expression_Binary.e0.Type==expression_Binary.e1.Type&&(expression_Binary.Type!=null)){
		 return expression_Binary.Type;}
			 else
				 throw new SemanticException(expression_Binary.firstToken, "expression binary");
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {                  // -------------------------------------- llater
		// TODO Auto-generated method stub
		System.out.println("Expression_Unary"+"33");
		if(expression_Unary.e!=null){
			expression_Unary.e.Type=(Type)expression_Unary.e.visit(this, null);
		}
		Type t=expression_Unary.e.Type;
		if(expression_Unary.op==Kind.OP_EXCL&&(t==Type.BOOLEAN||t==Type.INTEGER))
			expression_Unary.Type=t;
		else if(expression_Unary.op==Kind.OP_PLUS||expression_Unary.op==Kind.OP_MINUS&&(t==Type.INTEGER))
			expression_Unary.Type=Type.INTEGER;
		else 
			expression_Unary.Type=null;
		if(expression_Unary.Type!=null)
			return expression_Unary.Type;
		else
			throw new SemanticException(expression_Unary.firstToken,"Expression_Unary");
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("visitIndex"+"44");
		if(index.e0!=null){
			index.e0.Type=(Type)index.e0.visit(this, null);
		}
		if(index.e1!=null){
			index.e1.Type=(Type)index.e1.visit(this, null);
		}
		System.out.println("----------*******"+index.e0.Type+"-----------&&"+index.e1.Type);
		if(index.e0.Type==Type.INTEGER&&index.e1.Type==Type.INTEGER){
			
			index.setCartesian(!(index.e0.firstToken.kind==Kind.KW_r&&index.e1.firstToken.kind==Kind.KW_a));
			return index.Type;
		}
		throw new SemanticException(index.firstToken,"Index");
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("PixelSelector"+"55");
		Type t=symboltable.lookupType(expression_PixelSelector.name);
		if(expression_PixelSelector.index!=null)
			expression_PixelSelector.index.visit(this, null);
		if(t==Type.IMAGE){
			expression_PixelSelector.Type=Type.INTEGER;
		}
		else if(expression_PixelSelector.index==null){
			expression_PixelSelector.Type=t;
		}
		else {
			expression_PixelSelector.Type=null;
		}
		if(expression_PixelSelector.Type!=null)
			return expression_PixelSelector.Type;
		throw new SemanticException(expression_PixelSelector.firstToken,"Expression_PixelSelector");
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		System.out.println("Expression_Conditional"+"66");
		// TODO Auto-generated method stub
		if(expression_Conditional.condition != null){
			expression_Conditional.condition.Type = (Type) expression_Conditional.condition.visit(this, null);
		}
		
		if(expression_Conditional.trueExpression != null){
			expression_Conditional.trueExpression.Type = (Type) expression_Conditional.trueExpression.visit(this, null);
		}
		
		if(expression_Conditional.condition != null){
			expression_Conditional.falseExpression.Type = (Type) expression_Conditional.falseExpression.visit(this, null);
		}
		
		if(expression_Conditional.condition.Type==Type.BOOLEAN&&expression_Conditional.trueExpression.Type==expression_Conditional.falseExpression.Type)
		{
			expression_Conditional.Type=expression_Conditional.trueExpression.Type;
			return expression_Conditional.Type;
		}
		throw new SemanticException(expression_Conditional.firstToken,"Expression_Conditional");
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Declaration_Image"+"77");
		if(declaration_Image.source!=null){
			declaration_Image.source.Type=(Type)declaration_Image.source.visit(this, null);
		}
		if(declaration_Image.xSize!=null){
			declaration_Image.xSize.Type=(Type)declaration_Image.xSize.visit(this, null);
		}
		if(declaration_Image.ySize!=null){
			declaration_Image.ySize.Type=(Type)declaration_Image.ySize.visit(this, null);
		}
		if(symboltable.lookupType(declaration_Image.name)==null){
			System.out.println("dec im");
			symboltable.insert(declaration_Image.name, declaration_Image);
			
			declaration_Image.Type=Type.IMAGE;
			if(declaration_Image.xSize!=null){
				if(declaration_Image.ySize!=null&&(declaration_Image.xSize.Type==Type.INTEGER&&declaration_Image.ySize.Type==Type.INTEGER))
					return declaration_Image.Type;
				else
					throw new SemanticException(declaration_Image.firstToken, "declaration_Image");
			}else{
				if(declaration_Image.ySize==null)
					return declaration_Image.Type;
				else
					throw new SemanticException(declaration_Image.firstToken, "declaration_Image");
			}
		}
		else
			throw new SemanticException(declaration_Image.firstToken, "declaration image");
	}

	@Override
	public Object visitSource_StringLiteral(         // ------------------------------------------------------
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Source_StringLiteral"+"88"); 
				try{
					URL fileOrURL= new java.net.URL(source_StringLiteral.fileOrUrl);
					source_StringLiteral.Type=Type.URL;
					return source_StringLiteral.Type;
				}
				catch(MalformedURLException m){
					source_StringLiteral.Type=Type.FILE;
					return source_StringLiteral.Type;
				}
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("-------------------------"+source_CommandLineParam.paramNum);
//		if(source_CommandLineParam.paramNum!=null){
//			source_CommandLineParam.paramNum.Type=(Type)source_CommandLineParam.paramNum.visit(this, null);
//		}
//		source_CommandLineParam.Type=source_CommandLineParam.paramNum.Type;
		System.out.println("----------------------------"+source_CommandLineParam.Type);
		source_CommandLineParam.Type=null;
		if((Type)source_CommandLineParam.paramNum.visit(this, null)==Type.INTEGER)
			return source_CommandLineParam.Type;
		else
			throw new SemanticException(source_CommandLineParam.firstToken,"Source_CommandLineParam");
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Source_Ident"+"99");
		if(symboltable.lookupType(source_Ident.name)!=null)
		source_Ident.Type=symboltable.lookupType(source_Ident.name);
		else 
			throw new SemanticException(source_Ident.firstToken, "source ident");
	//System.out.println("-------"+source_Ident.Type);
		if(source_Ident.Type == Type.FILE || source_Ident.Type == Type.URL)
			return source_Ident.Type;
		else
			throw new SemanticException(source_Ident.firstToken, "SOurce_Ident ");
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//declaration_SourceSink.visit(this, arg);
		System.out.println("Declaration_SourceSink"+"100");
		if(declaration_SourceSink.source!=null){
			declaration_SourceSink.source.Type=(Type)declaration_SourceSink.source.visit(this, null);
		}
		if(symboltable.lookupType(declaration_SourceSink.name)==null){
			System.out.println("dec sour");
			symboltable.insert(declaration_SourceSink.name, declaration_SourceSink);
			declaration_SourceSink.Type=TypeUtils.getType(declaration_SourceSink.firstToken);
			if(declaration_SourceSink.source.Type==declaration_SourceSink.Type||declaration_SourceSink.source.Type==null)
				return declaration_SourceSink.Type;
			else
				throw new SemanticException(declaration_SourceSink.firstToken, "declaration_SourceSink");
		}
		throw new SemanticException(declaration_SourceSink.firstToken, "declaration_SourceSink");
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		System.out.println("Expression_IntLit"+"101");
		expression_IntLit.Type=Type.INTEGER;
		return expression_IntLit.Type;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Expression_FunctionAppWithExprArg"+"102");
		if(expression_FunctionAppWithExprArg.arg!=null){
			expression_FunctionAppWithExprArg.arg.Type=(Type)expression_FunctionAppWithExprArg.arg.visit(this, null);
		}
		if(expression_FunctionAppWithExprArg.arg.Type!=Type.INTEGER)
		throw new SemanticException(expression_FunctionAppWithExprArg.firstToken,"Expression_FunctionAppWithExprArg");
		else{
			expression_FunctionAppWithExprArg.Type=Type.INTEGER;
		}
		return expression_FunctionAppWithExprArg.Type;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		System.out.println("Expression_FunctionAppWithIndexArg"+"103");
		expression_FunctionAppWithIndexArg.Type=Type.INTEGER;
		return expression_FunctionAppWithIndexArg.Type;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		System.out.println("Expression_PredefinedName"+"104");
		expression_PredefinedName.Type=Type.INTEGER;
		return expression_PredefinedName.Type;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//// if(st_out.sink!=null)
		// st.out.aink.type=type st.out.sink.visit(this,null)
		System.out.println("Statement_Out"+"105");
		if(statement_Out.sink!=null){
			statement_Out.sink.Type=(Type)statement_Out.sink.visit(this, arg);
		}
		System.out.println("visit out");
	
		statement_Out.setDec(symboltable.lookupDec(statement_Out.name));
		System.out.println("visit out");
		Declaration dec =symboltable.lookupDec(statement_Out.name);
		if(dec!=null){
			Type t=symboltable.lookupType(statement_Out.name); 
			if(((t==Type.INTEGER||t==Type.BOOLEAN)&&statement_Out.sink.Type==Type.SCREEN)||(t==Type.IMAGE&&(statement_Out.sink.Type==Type.FILE||statement_Out.sink.Type==Type.SCREEN))){
				return statement_Out.Type;
			}
		}
		
		throw new SemanticException(statement_Out.firstToken,"Statement_Out");
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Statement_In"+"106");
		if(statement_In.source!=null){
			statement_In.source.Type=(Type)statement_In.source.visit(this, arg);
		}
		System.out.println("visit in");
		Declaration dec=symboltable.lookupDec(statement_In.name);
		statement_In.setDec(dec);
		//if(dec!=null&&symboltable.lookupType(statement_In.name)==statement_In.source.Type){
			return statement_In.Type;
		//}
		//throw new SemanticException(statement_In.firstToken,"Statement_In");
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Statement_Assign"+"107");
		if(statement_Assign.lhs!=null){
			statement_Assign.lhs.Type=(Type)statement_Assign.lhs.visit(this, null);
			System.out.println("---------"+statement_Assign.lhs.Type+"))))))))))");
		}
		if(statement_Assign.e!=null){
			statement_Assign.e.Type=(Type)statement_Assign.e.visit(this, null);
			System.out.println("---------"+statement_Assign.e.Type+"((((((((((((((");
		}
		
		if(statement_Assign.lhs.Type==statement_Assign.e.Type||(statement_Assign.lhs.Type==Type.IMAGE&&statement_Assign.e.Type==Type.INTEGER)){
			statement_Assign.setCartesian(statement_Assign.lhs.isCartesian);
			System.out.println("---------"+statement_Assign.Type+"aaaaa");
			return statement_Assign.Type;
		}
		throw new SemanticException(statement_Assign.firstToken,"Statement_Assign");
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("LHS"+"108");
		if(symboltable.lookupDec(lhs.name)!=null)
		lhs.declaration=symboltable.lookupDec(lhs.name);
		else
			throw new SemanticException(lhs.firstToken, "lhs");
		lhs.Type=lhs.declaration.Type;
		if(lhs.index!=null)
		lhs.isCartesian=lhs.index.isCartesian();
		System.out.println("+++++++++++++"+lhs.Type+"+++++++++++++++++");
		return lhs.Type;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		System.out.println("Sink_SCREEN"+"109");
		sink_SCREEN.Type=Type.SCREEN;
		return sink_SCREEN.Type;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Sink_Ident"+"110");
		sink_Ident.Type=symboltable.lookupType(sink_Ident.name);
		if(sink_Ident.Type==Type.FILE)
			return sink_Ident.Type;
		else
			throw new SemanticException(sink_Ident.firstToken,"Sink_Ident");
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Expression_BooleanLit"+"111");
		expression_BooleanLit.Type=Type.BOOLEAN;
		return expression_BooleanLit.Type;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Expression_Ident"+"112");
		expression_Ident.Type=symboltable.lookupType(expression_Ident.name);
		return expression_Ident.Type;
		//throw new UnsupportedOperationException();
	}

}
