package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.ac.bg.etf.pp1.CounterVisitor.FormParamCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPc;

	boolean matrixing;
	int findLabel1 = -1;
	int findLabel2 = -1;
	int findLabel3 = -1;
	int findAnyLabel1 = -1;
	int findAnyLabel2 = -1;
	int stayInWhile = -1;
	int foreachLabel = -1;
	boolean inforeach = false;

	List<Integer> condFactList;
	List<Integer> condTermList;
	Stack<Integer> ifStack = new Stack<Integer>();
	Stack<List<Integer>> whileStack = new Stack<List<Integer>>();
	Stack<Integer> whileStart = new Stack<Integer>();
	List<Integer> continueForEach;
	
	public CodeGenerator(){
		{
			Tab.chrObj.setAdr(Code.pc);
			Code.put(Code.enter);
			Code.put(1);
			Code.put(1);
			Code.put(Code.load_n);
			Code.put(Code.exit);
			Code.put(Code.return_);
		}
		{
			Tab.ordObj.setAdr(Code.pc);
			Code.put(Code.enter);
			Code.put(1);
			Code.put(1);
			Code.put(Code.load_n);
			Code.put(Code.exit);
			Code.put(Code.return_);
		}
		{
			Tab.lenObj.setAdr(Code.pc);
			Code.put(Code.enter);
			Code.put(1);
			Code.put(1);
			Code.put(Code.load_n);
			Code.put(Code.arraylength);
			Code.put(Code.exit);
			Code.put(Code.return_);
		}
	}

	public int getMainPc() {
		return mainPc;
	}

	public int depth(ExprBrackets exprBrackets) {
		if (exprBrackets instanceof NoExprBrList)
			return 0;
		else
			return depth(((ExprBrList) exprBrackets).getExprBrackets()) + 1;
	}

	public void visit(Term term) {
		if (term.getParent() instanceof NegativeExpr) {
			Code.put(Code.neg);
		}
	}

	public void visit(FindAnyStmt anyStmt) {
		Designator designator1 = anyStmt.getDesignator();
		Designator designator2 = anyStmt.getDesignator1();

		Code.loadConst(0);
		Code.store(designator1.obj);
		Code.loadConst(0);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		findAnyLabel1 = Code.pc;
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.load(designator2.obj);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		if (designator2.obj.getType().getElemType() == Tab.intType
				|| designator2.obj.getType().getElemType() == Compiler.boolType) {
			Code.put(Code.aload);
		} else if (designator2.obj.getType().getElemType() == Tab.charType) {
			Code.put(Code.baload);
		}
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup_x1);
		Code.putFalseJump(Code.eq, 0);
		findAnyLabel2 = Code.pc - 2;
		Code.loadConst(1);
		Code.store(designator1.obj);
		Code.fixup(findAnyLabel2);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup);
		Code.put(Code.arraylength);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.put(Code.dup_x1);
		Code.putFalseJump(Code.eq, findAnyLabel1);
		Code.put(Code.pop);
		Code.put(Code.pop);
		Code.put(Code.pop);
		Code.put(Code.pop);
	}

	public void visit(FindIdent ident) {
		if (ident.getParent() instanceof FindIdentExpr) {
			Code.loadConst(0);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			findLabel1 = Code.pc;
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.put(Code.dup2);
			if (ident.obj.getType() == Tab.intType
					|| ident.obj.getType() == Compiler.boolType) {
				Code.put(Code.aload);
			} else if (ident.obj.getType() == Tab.charType) {
				Code.put(Code.baload);
			}
			Code.store(ident.obj);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.put(Code.dup);
			Code.load(ident.obj);
			Code.putFalseJump(Code.eq, 0);
			findLabel3 = Code.pc - 2;
		} else if (ident.getParent() instanceof ForEachStmt) {
			Code.loadConst(0);
			foreachLabel = Code.pc;
			Code.put(Code.dup2);
			if (ident.obj.getType() == Tab.intType
					|| ident.obj.getType() == Compiler.boolType) {
				Code.put(Code.aload);
			} else if (ident.obj.getType() == Tab.charType) {
				Code.put(Code.baload);
			}
			Code.store(ident.obj);
		}
	}

	public void visit(ForEachStmt eachStmt) {
		for (int patchAdr : continueForEach) {
			Code.fixup(patchAdr);
		}
		Code.loadConst(1);
		Code.put(Code.add);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup);
		Code.put(Code.arraylength);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup_x1);
		Code.putFalseJump(Code.eq, foreachLabel);

		for (int patchAdr : whileStack.peek()) {
			Code.fixup(patchAdr);
		}
		whileStack.pop();

		Code.put(Code.pop);
		Code.put(Code.pop);
	}

	public void visit(FindIdentExpr findIdentExpr) {
		Code.store(findIdentExpr.getFindIdent().obj);
		Code.fixup(findLabel3);
	}

	public void visit(FindStmt findStmt) {
		Designator designator1 = findStmt.getDesignator();
		Designator designator2 = findStmt.getDesignator1();

		Code.load(designator1.obj);
		Code.loadConst(0);
		Code.putFalseJump(Code.eq, 0);
		findLabel2 = Code.pc - 2;

		Code.load(designator2.obj);
		Code.put(Code.arraylength);
		Code.put(Code.newarray);
		if (findStmt.getExpr().struct == Tab.intType
				|| findStmt.getExpr().struct == Compiler.boolType) {
			Code.loadConst(1);
		} else if (findStmt.getExpr().struct == Tab.charType) {
			Code.loadConst(0);
		}
		Code.store(designator1.obj);

		Code.fixup(findLabel2);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup);
		Code.put(Code.arraylength);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup);
		Code.load(designator1.obj);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.load(findStmt.getFindIdentExpr().getFindIdent().obj);
		if (findStmt.getExpr().struct == Tab.intType
				|| findStmt.getExpr().struct == Compiler.boolType) {
			Code.put(Code.astore);
		} else if (findStmt.getExpr().struct == Tab.charType) {
			Code.put(Code.bastore);
		}
		Code.loadConst(1);
		Code.put(Code.add);
		Code.put(Code.dup);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.putFalseJump(Code.eq, findLabel1);
		Code.put(Code.pop);
		Code.put(Code.pop);
		Code.put(Code.pop);
		Code.put(Code.pop);
	}

	public void visit(PrintNoNumStmt printNoNumStmt) {
		if (printNoNumStmt.getExpr().struct == Tab.intType) {
			Code.loadConst(5);
			Code.put(Code.print);
		} else if (printNoNumStmt.getExpr().struct == Compiler.boolType) {
			Code.loadConst(5);
			Code.put(Code.print);
		} else {
			Code.loadConst(1);
			Code.put(Code.bprint);
		}
	}

	public void visit(PrintNumStmt printNumStmt) {
		if (printNumStmt.getExpr().struct == Tab.intType) {
			Code.loadConst(printNumStmt.getN2());
			Code.put(Code.print);
		} else if (printNumStmt.getExpr().struct == Compiler.boolType) {
			Code.loadConst(printNumStmt.getN2());
			Code.put(Code.print);
		} else {
			Code.loadConst(printNumStmt.getN2());
			Code.put(Code.bprint);
		}
	}

	public void visit(NumberFactor numberFactor) {
		Obj con = Tab.insert(Obj.Con, "$", numberFactor.struct);
		con.setLevel(0);
		con.setAdr(numberFactor.getNumber());

		Code.load(con);
	}

	public void visit(TrueFactor trueFactor) {
		Obj con = Tab.insert(Obj.Con, "$", trueFactor.struct);
		con.setLevel(0);
		con.setAdr(1);

		Code.load(con);
	}

	public void visit(FalseFactor falseFactor) {
		Obj con = Tab.insert(Obj.Con, "$", falseFactor.struct);
		con.setLevel(0);
		con.setAdr(0);

		Code.load(con);
	}

	public void visit(CharFactor charFactor) {
		Obj con = Tab.insert(Obj.Con, "$", charFactor.struct);
		con.setLevel(0);
		con.setAdr((int) charFactor.getP1());

		Code.load(con);
	}

	public void visit(MethodTypeName methodTypeName) {
		if ("main".equalsIgnoreCase(methodTypeName.getMethName())) {
			mainPc = Code.pc;
		}

		methodTypeName.obj.setAdr(Code.pc);

		SyntaxNode methodNode = methodTypeName.getParent();

		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);

		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);

		Code.put(Code.enter);
		Code.put(fpCnt.getCount());
		Code.put(fpCnt.getCount() + varCnt.getCount());
	}

	public void visit(MethodDecl methodDecl) {
		if (!(methodDecl.getMethodTypeName().getMethodType() instanceof VoidType)) {
			Code.put(Code.trap);
			Code.loadConst(-5);
		}
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(DesignatorStatement designatorStatement) {

		DesignatorExpr designatorExpr = designatorStatement.getDesignatorExpr();
		if (designatorExpr instanceof AssingopDesignatorExpr) {
			Expr expr = ((AssingopDesignatorExpr) designatorExpr).getExpr();
			if (expr instanceof PositiveExpr) {
				Term term = ((PositiveExpr) expr).getTerm();
				AddopTermList addopTermList = ((PositiveExpr) expr)
						.getAddopTermList();
				if (addopTermList instanceof NoAddopTerms) {
					Factor factor = term.getFactor();
					if (factor instanceof NewTwoExprFactor) {
						return;
					}
				}
			}
		}

		int designatorDepth = depth(designatorStatement.getDesignator()
				.getExprBrackets());
		if (designatorStatement.getDesignatorExpr() instanceof AssingopDesignatorExpr) {
			if (designatorDepth == 0) {
				Code.store(designatorStatement.getDesignator().obj);
			} else if (designatorDepth == 1) {
				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType) {
					Code.put(Code.astore);
				} else {
					Code.put(Code.bastore);
				}
			} else {
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.astore);
				} else {
					Code.put(Code.bastore);
				}
			}
		} else if (designatorStatement.getDesignatorExpr() instanceof ParenDesignatorExpr) {
			Obj functionObj = designatorStatement.getDesignator().obj;
			int offset = functionObj.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
			if (designatorStatement.getDesignator().obj.getType() != Tab.noType) {
				Code.put(Code.pop);
			}
		} else if (designatorStatement.getDesignatorExpr() instanceof IncDesignatorExpr) {
			if (designatorDepth == 0) {
				Code.load(designatorStatement.getDesignator().obj);
				Code.loadConst(1);
				Code.put(Code.add);
				Code.store(designatorStatement.getDesignator().obj);
			} else if (designatorDepth == 1) {
				Code.put(Code.dup);
				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
				Code.loadConst(1);
				Code.put(Code.add);

				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType) {
					Code.put(Code.astore);
				} else {
					Code.put(Code.bastore);
				}
			} else {
				Code.put(Code.dup2);
				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}

				Code.loadConst(1);
				Code.put(Code.add);

				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.astore);
				} else {
					Code.put(Code.bastore);
				}
			}
		} else if (designatorStatement.getDesignatorExpr() instanceof DecDesignatorExpr) {
			if (designatorDepth == 0) {
				Code.load(designatorStatement.getDesignator().obj);
				Code.loadConst(1);
				Code.put(Code.sub);
				Code.store(designatorStatement.getDesignator().obj);
			} else if (designatorDepth == 1) {
				Code.put(Code.dup);
				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
				Code.loadConst(1);
				Code.put(Code.sub);

				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType) {
					Code.put(Code.astore);
				} else {
					Code.put(Code.bastore);
				}
			} else {
				Code.put(Code.dup2);
				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}

				Code.loadConst(1);
				Code.put(Code.sub);

				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.load(designatorStatement.getDesignator().obj);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designatorStatement.getDesignator().obj.getType()
						.getElemType() == Tab.intType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType() == Compiler.boolType
						|| designatorStatement.getDesignator().obj.getType()
								.getElemType().getKind() == Struct.Array) {
					Code.put(Code.astore);
				} else {
					Code.put(Code.bastore);
				}
			}
		} else if (designatorStatement.getDesignatorExpr() instanceof ParenActDesignatorExpr) {
			Obj functionObj = designatorStatement.getDesignator().obj;
			int offset = functionObj.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
			if (designatorStatement.getDesignator().obj.getType() != Tab.noType) {
				Code.put(Code.pop);
			}
		}
	}

	public void visit(Designator designator) {
		SyntaxNode parent = designator.getParent();
		int designatorDepth = depth(designator.getExprBrackets());
		if (DesignatorStatement.class != parent.getClass()) {
			if (designatorDepth == 0 && ReadStmt.class != parent.getClass()) {
				Code.load(designator.obj);
			} else if (designatorDepth == 1) {
				Code.load(designator.obj);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (ReadStmt.class != parent.getClass()) {
					if (designator.obj.getType().getElemType() == Tab.intType
							|| designator.obj.getType().getElemType() == Compiler.boolType) {
						Code.put(Code.aload);
					} else {
						Code.put(Code.baload);
					}
				}
			} else if(designatorDepth==2){
				Code.load(designator.obj);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				if (designator.obj.getType().getElemType() == Tab.intType
						|| designator.obj.getType().getElemType() == Compiler.boolType
						|| designator.obj.getType().getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				if (designator.obj.getType().getElemType() == Tab.intType
						|| designator.obj.getType().getElemType() == Compiler.boolType
						|| designator.obj.getType().getElemType().getKind() == Struct.Array) {
					Code.put(Code.aload);
				} else {
					Code.put(Code.baload);
				}
			}
		}
	}

	public void visit(ConstDeclVal constDeclVal) {
		Obj conVal = Tab.find(constDeclVal.getConstName());
		Code.store(conVal);
	}

	public void visit(FuncCallNoParam funcCallNoParam) {
		Obj functionObj = funcCallNoParam.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);

		Code.put2(offset);
	}

	public void visit(FuncCallWIthParam funcCallWIthParam) {
		Obj functionObj = funcCallWIthParam.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);

		Code.put2(offset);
	}

	public void visit(ReturnExprStmt returnExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(ReturnStmt returnNoExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(AddopTerm addopTerm) {
		if (addopTerm.getAddop() instanceof PlusOp) {
			Code.put(Code.add);
		} else {
			Code.put(Code.sub);
		}
	}

	public void visit(MulopFactor mulopFactor) {
		if (mulopFactor.getMulop() instanceof MultiplyOp) {
			Code.put(Code.mul);
		} else if (mulopFactor.getMulop() instanceof DivideOp) {
			Code.put(Code.div);
		} else {
			Code.put(Code.rem);
		}
	}

	public void visit(ReadStmt readStmt) {
		Obj desObj = readStmt.getDesignator().obj;
		Struct struct = desObj.getType();
		int designatorDepth = depth(readStmt.getDesignator().getExprBrackets());
		if(designatorDepth == 1){
			struct = struct.getElemType();
		}
		if (struct == Tab.intType) {
			Code.put(Code.read);
		} else if (struct == Compiler.boolType) {
			Code.put(Code.read);
		} else {
			Code.put(Code.bread);
		}
		
		if(designatorDepth == 0){
			Code.store(desObj);
		} else {
			if (struct == Tab.intType) {
				Code.put(Code.astore);
			} else if (struct == Compiler.boolType) {
				Code.put(Code.astore);
			} else {
				Code.put(Code.bastore);
			}
		}
	}

	public void visit(NewOneExprFactor exprFactor) {
		Code.put(Code.newarray);
		if (exprFactor.getExpr().struct == Tab.intType
				|| exprFactor.getExpr().struct == Compiler.boolType) {
			Code.loadConst(1);
		} else if (exprFactor.getExpr().struct == Tab.charType) {
			Code.loadConst(0);
		}
	}

	public void visit(NewTwoExprFactor exprFactor) {
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup);
		Code.put(Code.newarray);
		Code.loadConst(1);
		SyntaxNode parent = exprFactor;
		while (!(parent instanceof DesignatorStatement)) {
			parent = parent.getParent();
		}
		Designator designator = ((DesignatorStatement) parent).getDesignator();
		Code.store(designator.obj);
		Code.loadConst(0);
		int label = Code.pc;
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup);
		Code.put(Code.newarray);
		if (exprFactor.getType().struct == Tab.intType
				|| exprFactor.getType().struct == Compiler.boolType) {
			Code.loadConst(1);
		} else if (exprFactor.getType().struct == Tab.charType) {
			Code.loadConst(0);
		}
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup2);
		Code.load(designator.obj);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		if (exprFactor.getExpr().struct == Tab.intType
				|| exprFactor.getExpr().struct == Compiler.boolType) {
			Code.put(Code.astore);
		} else if (exprFactor.getExpr().struct == Tab.charType) {
			Code.put(Code.bastore);
		}
		Code.put(Code.pop);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.put(Code.dup2);
		Code.putFalseJump(Code.eq, label);
		Code.put(Code.pop);
		Code.put(Code.pop);
		Code.put(Code.pop);
		matrixing = true;
	}
	
	public void visit(CondFactExpr condFactExpr){
		Code.loadConst(1);
		Code.putFalseJump(Code.eq, 0);
		condFactList.add(Code.pc - 2);
	}

	public void visit(CondFactExprRelop condFactExprRelop) {
		if (condFactExprRelop.getRelop() instanceof IsEqualOp) {
			Code.putFalseJump(Code.eq, 0);
		} else if (condFactExprRelop.getRelop() instanceof NeqOp) {
			Code.putFalseJump(Code.ne, 0);
		} else if (condFactExprRelop.getRelop() instanceof GreaterOp) {
			Code.putFalseJump(Code.gt, 0);
		} else if (condFactExprRelop.getRelop() instanceof GequalOp) {
			Code.putFalseJump(Code.ge, 0);
		} else if (condFactExprRelop.getRelop() instanceof LessOp) {
			Code.putFalseJump(Code.lt, 0);
		} else if (condFactExprRelop.getRelop() instanceof LequalOp) {
			Code.putFalseJump(Code.le, 0);
		}
		condFactList.add(Code.pc - 2);
	}

	public void visit(ConditionBegin conditionBegin) {
		condTermList = new ArrayList<Integer>();
	}

	public void visit(CondTermBegin condTermBegin) {
		condFactList = new ArrayList<Integer>();
	}

	public void visit(CondTerm condTerm) {
		Code.putJump(0);
		condTermList.add(Code.pc - 2);

		for (int patchAdr : condFactList) {
			Code.fixup(patchAdr);
		}
	}

	public void visit(Condition condition) {
		Code.putJump(0);
		if (condition.getParent() instanceof WhileStmt) {
			whileStack.push(new ArrayList<Integer>());
			whileStack.peek().add(Code.pc - 2);
		} else {
			ifStack.push(Code.pc - 2);
		}

		for (int patchAdr : condTermList) {
			Code.fixup(patchAdr);
		}
	}

	public void visit(While stmt) {
		whileStart.push(Code.pc);
		inforeach = false;
	}

	public void visit(Foreach foreach) {
		whileStart.push(Code.pc);
		whileStack.push(new ArrayList<Integer>());
		continueForEach = new ArrayList<Integer>();
		inforeach = true;
	}

	public void visit(BreakStmt stmt) {
		Code.putJump(0);
		whileStack.peek().add(Code.pc - 2);
	}

	public void visit(ContinueStmt stmt) {
		if (inforeach) {
			Code.putJump(0);
			continueForEach.add(Code.pc - 2);
		} else {
			Code.putJump(whileStart.peek());
		}
	}

	public void visit(WhileStmt stmt) {
		Code.putJump(whileStart.pop());

		for (int patchAdr : whileStack.peek()) {
			Code.fixup(patchAdr);
		}
		whileStack.pop();
	}

	public void visit(IfStmt ifStmt) {
		Code.fixup(ifStack.pop());
	}

	public void visit(Else elseword) {
		Code.putJump(0);
		Code.fixup(ifStack.pop());
		ifStack.push(Code.pc - 2);
	}

	public void visit(IfElseStmt ifElseStmt) {
		Code.fixup(ifStack.pop());
	}

}
