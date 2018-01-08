package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


import cop5556fa17.Scanner.Kind;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
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
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		//cw = new ClassWriter(0);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");
		mv.visitIntInsn(SIPUSH, 256);
		mv.visitVarInsn(ISTORE, 9);
		mv.visitIntInsn(SIPUSH, 256);
		mv.visitVarInsn(ISTORE, 10);
		mv.visitLdcInsn(new Integer(16777215));
		mv.visitVarInsn(ISTORE, 11);
		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			//System.out.println("+++++++++++"+node+"------\n");
			node.visit(this, arg);
			
		}
		//System.out.println("555555555555");
		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("x", "I", null, mainStart, mainEnd, 1);
		mv.visitLocalVariable("y", "I", null, mainStart, mainEnd, 2);
		mv.visitLocalVariable("X", "I", null, mainStart, mainEnd, 3);
		mv.visitLocalVariable("Y", "I", null, mainStart, mainEnd, 4);
		mv.visitLocalVariable("r", "I", null, mainStart, mainEnd, 5);
		mv.visitLocalVariable("a", "I", null, mainStart, mainEnd, 6);
		mv.visitLocalVariable("R", "I", null, mainStart, mainEnd, 7);
		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 8);
		
		mv.visitLocalVariable("DEF_X", "I", null, mainStart, mainEnd, 9);
		
		mv.visitLocalVariable("DEF_Y", "I", null,  mainStart, mainEnd, 10);
		
		mv.visitLocalVariable("Z", "I", null,  mainStart, mainEnd, 11);
		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction of main method
		mv.visitEnd();
		
		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		// TODO 
		
		FieldVisitor fv;
	
		//cw.visitField(ACC_STATIC, className, declaration_Variable.name, "I");
		if(declaration_Variable.Type==Type.INTEGER)
		 fv=cw.visitField(ACC_STATIC,declaration_Variable.name ,  "I",null, null);
		else
		  fv=cw.visitField(ACC_STATIC,declaration_Variable.name ,  "Z",null, null);	
		fv.visitEnd();
//		System.out.println("putstatic -----------1");
//		System.out.println("-------"+className+"----"+declaration_Variable.name);
//		System.out.println("-------"+className+"----"+declaration_Variable.e);
		if(declaration_Variable.e==null) {
			
		}else
		{
			declaration_Variable.e.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC,className,declaration_Variable.name,declaration_Variable.Type==Type.BOOLEAN?"Z":"I");
			}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		// TODO 
		// Visit to load expressions onto stack
		
			
			expression_Binary.e0.visit(this, null);
			
		
			expression_Binary.e1.visit(this, null);
			
		
		if(expression_Binary.op==Kind.OP_MINUS) {
			
			 mv.visitInsn(ISUB);
			 
		}
		else if(expression_Binary.op==Kind.OP_DIV) {
			
			mv.visitInsn(IDIV);
			
		}
		else if(expression_Binary.op==Kind.OP_PLUS) {
			
			mv.visitInsn(IADD);
			
		}
		else if(expression_Binary.op==Kind.OP_TIMES) {
			
			mv.visitInsn(IMUL);
			
		}
		else if(expression_Binary.op==Kind.OP_MOD) {
			
			mv.visitInsn(IREM);
			
		}
		else if(expression_Binary.op==Kind.OP_POWER) {
			//System.out.println("-----------------------------------------yet to be determined----------------------");
			//mv.visitInsn(IDIV);
		}
		else if(expression_Binary.op==Kind.OP_AND) {
			
			mv.visitInsn(IAND);
			
		}
		else if(expression_Binary.op==Kind.OP_OR) {
			
			mv.visitInsn(IOR);
			
		}
		else if(expression_Binary.op==Kind.OP_EQ) {
			
			 Label EQ = new Label();
			 Label NEQ = new Label();
			 Label END=new Label();
			 mv.visitJumpInsn(IF_ICMPEQ, EQ);
			 mv.visitJumpInsn(GOTO, NEQ);
			 mv.visitLabel(EQ);
			 mv.visitInsn(ICONST_1);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(NEQ);
			 mv.visitInsn(ICONST_0);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(END);
		}
		else if(expression_Binary.op==Kind.OP_NEQ) {
			
			 Label EQ = new Label();
			 Label NEQ = new Label();
			 Label END=new Label();
			 mv.visitJumpInsn(IF_ICMPNE, EQ);
			 mv.visitJumpInsn(GOTO, NEQ);
			 mv.visitLabel(EQ);
			 mv.visitInsn(ICONST_1);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(NEQ);
			 mv.visitInsn(ICONST_0);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(END);
			 
		}
		else if(expression_Binary.op == Kind.OP_GT ){
			
			 Label GT = new Label();
			 Label NG = new Label();
			 Label END=new Label();
			 mv.visitJumpInsn(IF_ICMPGT, GT);
			 mv.visitJumpInsn(GOTO, NG);
			 mv.visitLabel(GT);
			 mv.visitInsn(ICONST_1);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(NG);
			 mv.visitInsn(ICONST_0);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(END);
			 
		 }
		else if( expression_Binary.op == Kind.OP_GE ){
			 
			 Label GE = new Label();
			 Label NG = new Label();
			 Label END=new Label();
			 mv.visitJumpInsn(IF_ICMPGE, GE);
			 mv.visitJumpInsn(GOTO, NG);
			 mv.visitLabel(GE);
			 mv.visitInsn(ICONST_1);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(NG);
			 mv.visitInsn(ICONST_0);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(END);
			 
		 }
		else if(expression_Binary.op == Kind.OP_LT ){
			
			 Label LT = new Label();
			 Label NL = new Label();
			 Label END=new Label();
			 mv.visitJumpInsn(IF_ICMPLT, LT);
			 mv.visitJumpInsn(GOTO, NL);
			 mv.visitLabel(LT);
			 mv.visitInsn(ICONST_1);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(NL);
			 mv.visitInsn(ICONST_0);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(END);
		 }
		else if(expression_Binary.op == Kind.OP_LE ){
			
			 Label LE = new Label();
			 Label NL = new Label();
			 Label END=new Label();
			 mv.visitJumpInsn(IF_ICMPLE, LE);
			 mv.visitJumpInsn(GOTO, NL);
			 mv.visitLabel(LE);
			 mv.visitInsn(ICONST_1);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(NL);
			 mv.visitInsn(ICONST_0);
			 mv.visitJumpInsn(GOTO, END);
			 mv.visitLabel(END);			 
		 }

		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.getType());
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		// TODO 
		//throw new UnsupportedOperationException();
		expression_Unary.e.visit(this, null);
		if(expression_Unary.op==Kind.OP_PLUS)
		{     
			//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.getType());
		return null;}
		else if (expression_Unary.op==Kind.OP_MINUS) {
			mv.visitInsn(INEG);
		}
		else if(expression_Unary.op==Kind.OP_EXCL) {
			if(expression_Unary.e.getType()==Type.BOOLEAN) {
				
				 Label FL = new Label();
				 Label TR = new Label();
				 Label END= new Label();
				 mv.visitJumpInsn(IFEQ, FL);
				 mv.visitJumpInsn(GOTO, TR);
				 mv.visitLabel(FL);
				 mv.visitInsn(ICONST_1);
				 mv.visitJumpInsn(GOTO, END);
				 mv.visitLabel(TR);
				 mv.visitInsn(ICONST_0);
				 mv.visitJumpInsn(GOTO, END);
				 mv.visitLabel(END);
			}
			else if(expression_Unary.e.getType()==Type.INTEGER) {
			
		        mv.visitLdcInsn(Integer.MAX_VALUE);
				mv.visitInsn(IXOR);
				
			}
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.getType());
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		index.e0.visit(this, null);
		index.e1.visit(this, null);
		//System.out.println(index.isCartesian()+"--------here-------");
		if(index.isCartesian()) {
		//	System.out.println("is cartesian");
		}
		else {
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig,false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig,false);
		}
		//throw new UnsupportedOperationException();
        return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name, ImageSupport.ImageDesc);
		expression_PixelSelector.index.visit(this, null);
		//System.out.println("jjjjjjjjjjjj");
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className,"getPixel", ImageSupport.getPixelSig,false);
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO 
		
		expression_Conditional.condition.visit(this, null);
		//System.out.println(expression_Conditional.trueExpression+"ppppppppp"+expression_Conditional.falseExpression);
		Label TR=new Label();
		Label FL=new Label();
		Label END=new Label();
		mv.visitJumpInsn(IFEQ, FL);
		mv.visitJumpInsn(GOTO, TR);
		
		mv.visitLabel(TR);
		expression_Conditional.trueExpression.visit(this, null);
		mv.visitJumpInsn(GOTO, END);
		mv.visitLabel(FL);
		expression_Conditional.falseExpression.visit(this, null);
		mv.visitJumpInsn(GOTO, END);
		mv.visitLabel(END);
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.trueExpression.getType());
		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		FieldVisitor fv=cw.visitField(ACC_STATIC,declaration_Image.name , ImageSupport.ImageDesc,null, null);
		fv.visitEnd();
		if(declaration_Image.source!=null) {
			//System.out.println("Declaration Image");
			declaration_Image.source.visit(this, null);
			if(declaration_Image.xSize==null||declaration_Image.ySize==null) {
				//System.out.println("gdshkgdkhsgfk------");
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else {
				declaration_Image.xSize.visit(this, null);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
				declaration_Image.ySize.visit(this, null);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			
		}
		else {
			if(declaration_Image.xSize==null||declaration_Image.ySize==null) {
				
				mv.visitVarInsn(ILOAD, 9);
				mv.visitVarInsn(ILOAD,10);
			}
			else {
				declaration_Image.xSize.visit(this, null);
				//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
				declaration_Image.ySize.visit(this, null);
				//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig, false);
		 
		}
		//System.out.println("---"+declaration_Image.name);
		mv.visitFieldInsn(PUTSTATIC, className,declaration_Image.name ,ImageSupport.ImageDesc );
		return null;
	}
	
  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		// TODO HW6
		mv.visitLdcInsn(new String(source_StringLiteral.fileOrUrl));
		//throw new UnsupportedOperationException();
		return null;
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		//throw new UnsupportedOperationException();
		return null;
	}
     ///////////////  Check it out later
	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		// TODO HW6
		//mv.visitLdcInsn(source_Ident.name);
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name,"Ljava/lang/String;");
		//throw new UnsupportedOperationException();
		return null;
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		
		
		FieldVisitor fv=cw.visitField(ACC_STATIC,declaration_SourceSink.name ,  "Ljava/lang/String;",null, null);
		fv.visitEnd();
		if(declaration_SourceSink.source!=null) {
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.name,"Ljava/lang/String;" );
		}
		return null;
	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		// TODO 
		//throw new UnsupportedOperationException();
		//System.out.println("entered visitExpression_IntLit---__"+expression_IntLit.value);
		
		mv.visitLdcInsn(expression_IntLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		expression_FunctionAppWithExprArg.arg.visit(this, null);
		Kind ki=expression_FunctionAppWithExprArg.function;
		if(ki==Kind.KW_abs) {
        	mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "abs", RuntimeFunctions.absSig,false); 
         }
		else if(ki==Kind.KW_log) {
        	mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "log", RuntimeFunctions.logSig,false); 
         }
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		
         expression_FunctionAppWithIndexArg.arg.e0.visit(this, null);
         expression_FunctionAppWithIndexArg.arg.e1.visit(this, null);
         Kind ki=expression_FunctionAppWithIndexArg.function;
        // System.out.println(ki+"9999999999999991");
         if(ki==Kind.KW_cart_x) {
        	mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", "(II)I",false); 
         }
         else if(ki==Kind.KW_cart_y) {
        	 mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", "(II)I",false);
         }
         else if(ki==Kind.KW_polar_a) {  
        	 mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", "(II)I",false);
         }
         else if(ki==Kind.KW_polar_r) {
        	 mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", "(II)I",false); 
         }
//         else
//        	 System.out.println("this could be the problem"+"******************************************");
		return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
	
		
		Kind ki=expression_PredefinedName.kind;
		//System.out.println("66666666666666666666666666666666666"+ki);
		 if(ki==Kind.KW_x) {
			mv.visitVarInsn(ILOAD, 1);
		}
		else if(ki==Kind.KW_y) {
			mv.visitVarInsn(ILOAD, 2);
		}
		else if(ki==Kind.KW_X) {
			mv.visitVarInsn(ILOAD, 3);
		}
		else if(ki==Kind.KW_Y) {
			mv.visitVarInsn(ILOAD, 4);
		}
		else if(ki==Kind.KW_r) {
			mv.visitVarInsn(ILOAD, 5);
			//System.out.println("ILOAD 5");
		}
		else if(ki==Kind.KW_a) {
			mv.visitVarInsn(ILOAD, 6);
			//System.out.println("ILOAD 6");
		}
		else if(ki==Kind.KW_R) {
			mv.visitVarInsn(ILOAD, 7);
		}
		else if(ki==Kind.KW_A) {
				mv.visitVarInsn(ILOAD, 8);
			}
		else if(ki==Kind.KW_DEF_X) {
			mv.visitVarInsn(ILOAD, 9);
		}
		else if(ki==Kind.KW_DEF_Y) {
			mv.visitVarInsn(ILOAD, 10);
		}
		else if(ki==Kind.KW_Z) {
			mv.visitVarInsn(ILOAD, 11);
		}
		//throw new UnsupportedOperationException();
		return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		
		Type so_type=statement_Out.getDec().Type;
		if(so_type==Type.INTEGER) {
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name,"I" );	
			
			CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
			
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
			
		}else if(so_type==Type.BOOLEAN) {
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name,"Z");	
			
			CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
			
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
					
		}
		else if(so_type==Type.IMAGE) {
			//System.out.println(statement_Out.name+"----------<");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, ImageSupport.ImageDesc);

			
			CodeGenUtils.genLogTOS(GRADE, mv, Type.IMAGE);
			statement_Out.sink.visit(this, null);
			//System.out.println("stout");
			//mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, ImageSupport.ImageDesc);

			
		}
					
		return null;
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		//System.out.println("lllllllllllllllllllllllllll");
		
            //if(statement_In.source.Type==null) {
            	
			 if(statement_In.getDec().getType()==Type.INTEGER)  {
				 statement_In.source.visit(this, arg);
		    	  mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt","(Ljava/lang/String;)I",false);
				
				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "I");
				
				//System.out.println("putstatic ----------2");
				
		      }
		      else if(statement_In.getDec().getType()==Type.BOOLEAN) {	
		    	  statement_In.source.visit(this, arg);
		    	  mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				
		    	  mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "Z");
				}
		      else if(statement_In.getDec().getType()==Type.IMAGE) {
		    	 // System.out.println("IMaGe"+statement_In);
		    	  statement_In.source.visit(this, null);
		    	  
					if(((Declaration_Image)statement_In.getDec()).xSize==null||((Declaration_Image)statement_In.getDec()).ySize==null) {
						//System.out.println("entering this ----------->");
						mv.visitInsn(ACONST_NULL);
						mv.visitInsn(ACONST_NULL);
					}
					else {
						((Declaration_Image)statement_In.getDec()).xSize.visit(this, null);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
						((Declaration_Image)statement_In.getDec()).ySize.visit(this, null);
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
					}
					mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
					mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, ImageSupport.ImageDesc);
		      }
	//}
			 return null;
	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		//TODO  (see comment)
		//throw new UnsupportedOperationException();
		//System.out.println(statement_Assign.lhs.Type+"----->");
		if(statement_Assign.lhs.Type==Type.IMAGE) {
			//System.out.println("Entered statement assign image");
			//mv.visitFieldInsn(GETSTATIC, className, statement_Assign., desc);
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			//System.out.println(statement_Assign.lhs.name+"-----------n");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			mv.visitVarInsn(ISTORE,3);
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitVarInsn(ISTORE,4);
			
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 1);
			
			Label l7 = new Label();
			mv.visitJumpInsn(GOTO, l7);
			Label l8 = new Label();
			mv.visitLabel(l8);
	
	//	mv.visitFrame(Opcodes.F_FULL, 13, new Object[] {"[Ljava/lang/String;", Opcodes.INTEGER, Opcodes.TOP, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[] {});
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 2);
			//Label l9 = new Label();
			//mv.visitLabel(l9);
			Label l10 = new Label();
			mv.visitJumpInsn(GOTO, l10);
			Label l11 = new Label();
			mv.visitLabel(l11);
			//mv.visitLineNumber(16, l11);
/*---*/		//mv.visitFrame(Opcodes.F_FULL, 13, new Object[] {"[Ljava/lang/String;", Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[] {});
//			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//			mv.visitLdcInsn("2");
//			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
			mv.visitVarInsn(ILOAD,1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig,false);
			mv.visitVarInsn(ISTORE, 5);
			mv.visitVarInsn(ILOAD,1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig,false);
			mv.visitVarInsn(ISTORE, 6);
			statement_Assign.e.visit(this, null);
			statement_Assign.lhs.visit(this, null);
			//Label l12 = new Label();
			//mv.visitLabel(l12);
			//mv.visitLineNumber(14, l12);
			mv.visitIincInsn(2, 1);
			mv.visitLabel(l10);
/*---*/	//	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitVarInsn(ILOAD, 4);
			mv.visitJumpInsn(IF_ICMPLT, l11);
			//Label l13 = new Label();
			//mv.visitLabel(l13);
		
			mv.visitIincInsn(1, 1);
			mv.visitLabel(l7);
	/*---*///	mv.visitFrame(Opcodes.F_FULL, 13, new Object[] {"[Ljava/lang/String;", Opcodes.INTEGER, Opcodes.TOP, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[] {});
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 3);
			mv.visitJumpInsn(IF_ICMPLT, l8);
		}
		else {
		statement_Assign.e.visit(this, null);
		
		statement_Assign.lhs.visit(this, null);}
		
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		//TODO  (see comment)
		//throw new UnsupportedOperationException();
		//System.out.println("Lhs entered");
		//System.out.println(lhs.declaration.Type);
		if(lhs.declaration.Type==Type.BOOLEAN ) 
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
		
			
		else if(lhs.declaration.Type==Type.INTEGER) 
			{mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
			//System.out.println("putstatic ---------------3");
			}
		else if(lhs.declaration.Type==Type.IMAGE) {
			//System.out.println(lhs.name);
			mv.visitFieldInsn(GETSTATIC, className, lhs.name,ImageSupport.ImageDesc);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			//System.out.println("tyto");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig,false);
		}
			
		return null;
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		//TODO HW6
		
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeFrame", ImageSupport.makeFrameSig,false);
		mv.visitInsn(POP);
		//throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//TODO HW6
		//mv.visitLdcInsn(sink_Ident.name);
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, "Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig,false);
		//throw new UnsupportedOperationException();
          return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		//TODO
		//System.out.println("expre boolean lit------------------0"+expression_BooleanLit.value);
		mv.visitLdcInsn(expression_BooleanLit.value);
		
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		//TODO
		Type ei_type=expression_Ident.Type;
		//System.out.println("entered expression ident 999999999999999999"+expression_Ident.Type);
		if(ei_type==Type.BOOLEAN)
		mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name,"Z" );
		if(ei_type==Type.INTEGER)
		mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name,"I" );
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.getType());
		return null;
	}

}
