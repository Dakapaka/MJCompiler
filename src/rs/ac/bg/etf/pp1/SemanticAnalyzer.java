package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticAnalyzer extends VisitorAdaptor {

	Struct varType = null;
	Obj currentMethod = null;
	int constValue = 0;
	Struct methodType = null;
	boolean errorDetected = false;
	boolean returnFound = false;
	String currentDesignator = null;
	int whileDepth = 0;
	int forEachDepth = 0;
	int nVars;

	public HashMap<String, Integer> funcParamLens = new HashMap<String, Integer>();
	public HashMap<String, Boolean> methods = new HashMap<String, Boolean>();
	public HashMap<String, HashMap<Integer, Integer>> funcParamTypes = new HashMap<String, HashMap<Integer, Integer>>();
	public HashMap<String, Integer> arrayDimension = new HashMap<String, Integer>();

	Logger log = Logger.getLogger(getClass());

	public int depth(ExprBrackets exprBrackets) {
		if (exprBrackets instanceof NoExprBrList)
			return 0;
		else
			return depth(((ExprBrList) exprBrackets).getExprBrackets()) + 1;
	}

	public int paramLength(ActParamList actParamList) {
		if (!(actParamList.getParent() instanceof ActParamExprs)) {
			return 1;
		}
		return paramLength((ActParamExprs) actParamList.getParent()) + 1;
	}

	public int getOrderParam(ActParamList actParamList) {
		return paramLength(actParamList);
	}

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}

	public void visit(ExprBr exprBr) {
		if (exprBr.getExpr().struct.getKind() != Struct.Int) {
			report_error("Indeksiranje se moze vrsiti samo intom", exprBr);
		}
	}

	public void visit(ProgName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
		Tab.openScope();
	}

	public void visit(Program program) {
		if (methods.get("main") == null) {
			report_error("Ne postoji funkcija main", null);
		}
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}

	public void visit(Foreach foreach) {
		forEachDepth++;
	}

	public void visit(VarDeclVal varDeclVal) {
		report_info("Deklarisana promenljiva " + varDeclVal.getVarName(),
				varDeclVal);
		if (varDeclVal.getArray() instanceof NoBrackets) {
			Tab.insert(Obj.Var, varDeclVal.getVarName(), varType);
			arrayDimension.put(varDeclVal.getVarName(), 0);
		} else if (varDeclVal.getArray() instanceof SingleBrackets) {
			Tab.insert(Obj.Var, varDeclVal.getVarName(), new Struct(
					Struct.Array, varType));
			arrayDimension.put(varDeclVal.getVarName(), 1);
		} else if (varDeclVal.getArray() instanceof TwoBrackets) {
			Tab.insert(Obj.Var, varDeclVal.getVarName(), new Struct(
					Struct.Array, new Struct(Struct.Array, varType)));
			arrayDimension.put(varDeclVal.getVarName(), 2);
		}
	}

	public void visit(FormalParamDecl formalParamDecl) {

		if (funcParamLens.get(currentMethod.getName()) != 0) {
			funcParamLens.put(currentMethod.getName(),
					funcParamLens.get(currentMethod.getName()) + 1);
			funcParamTypes.get(currentMethod.getName()).put(
					funcParamLens.get(currentMethod.getName()),
					formalParamDecl.getType().struct.getKind());
		} else {
			funcParamLens.put(currentMethod.getName(), 1);
			funcParamTypes.get(currentMethod.getName()).put(1,
					formalParamDecl.getType().struct.getKind());
		}

		report_info("Deklarisan parametar " + formalParamDecl.getName(),
				formalParamDecl);
		if (formalParamDecl.getArray() instanceof NoBrackets) {
			Tab.insert(Obj.Var, formalParamDecl.getName(), varType);
			arrayDimension.put(formalParamDecl.getName(), 0);
		} else if (formalParamDecl.getArray() instanceof SingleBrackets) {
			Tab.insert(Obj.Var, formalParamDecl.getName(), new Struct(
					Struct.Array, varType));
			arrayDimension.put(formalParamDecl.getName(), 1);
		} else if (formalParamDecl.getArray() instanceof TwoBrackets) {
			Tab.insert(Obj.Var, formalParamDecl.getName(), new Struct(
					Struct.Array, new Struct(Struct.Array, varType)));
			arrayDimension.put(formalParamDecl.getName(), 2);
		}
	}

	public void visit(ConstDeclVal constDeclVal) {
		Obj obj = Tab.find(constDeclVal.getConstName());
		if (obj != Tab.noObj) {
			report_error("Greska na liniji " + constDeclVal.getLine()
					+ " : konstanta " + constDeclVal.getConstName()
					+ " je vec deklarisana! ", null);
		} else {
			report_info("Deklarisana konstanta " + constDeclVal.getConstName(),
					constDeclVal);
			Obj conNode = Tab.insert(Obj.Con, constDeclVal.getConstName(),
					varType);
			conNode.setAdr(constValue);
		}
	}

	public void visit(FindIdent ident) {
		ident.obj = Tab.find(ident.getName());
	}

	public void visit(ConstNumber constNum) {
		constValue = constNum.getNumberVal();
	}

	public void visit(ConstTrue constTrue) {
		constValue = 1;
	}

	public void visit(ConstFalse constFalse) {
		constValue = 0;
	}

	public void visit(ConstChar constChar) {
		constValue = constChar.getCharVal();
	}

	public void visit(MethodTypeName methodTypeName) {
		methods.put(methodTypeName.getMethName(), true);
		funcParamTypes.put(methodTypeName.getMethName(),
				new HashMap<Integer, Integer>());
		funcParamLens.put(methodTypeName.getMethName(), 0);
		currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethName(),
				methodType);
		methodTypeName.obj = currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodTypeName.getMethName(),
				methodTypeName);
	}

	public void visit(AnyType anyType) {
		methodType = anyType.getType().struct;
	}

	public void visit(VoidType voidType) {
		methodType = Tab.noType;
	}

	public void visit(MethodDecl methodDecl) {
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();

		currentMethod = null;
	}

	public void visit(ReturnExprStmt returnExpr) {
		returnFound = true;
		Struct currMethType = currentMethod.getType();
		if (!currMethType.compatibleWith(returnExpr.getExpr().struct)) {
			report_error(
					"Greska na liniji "
							+ returnExpr.getLine()
							+ " : "
							+ "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
							+ currentMethod.getName(), null);
		}
	}

	public void visit(ReturnStmt returnStmt) {
		returnFound = true;
		Struct currMethType = currentMethod.getType();
		if (!(currMethType == Tab.noType)) {
			report_error(
					"Greska na liniji "
							+ returnStmt.getLine()
							+ " : "
							+ "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
							+ currentMethod.getName(), null);
		}
	}

	public void visit(Term term) {
		term.struct = term.getFactor().struct;

		if (term.getMulopFactorList().struct != Tab.nullType)
			if (term.getMulopFactorList().struct.getKind() != Struct.Int
					|| term.getFactor().struct.getKind() != Struct.Int)
				report_error(
						"Greska na liniji "
								+ term.getLine()
								+ ": "
								+ "Moguce je vrsiti aritmeticke operacije samo na intovima",
						term);
	}

	public void visit(MulopFactor mulopFactor) {
		mulopFactor.struct = mulopFactor.getFactor().struct;

		if (mulopFactor.struct.getKind() != Struct.Int) {
			report_error(
					"Greska na liniji "
							+ mulopFactor.getLine()
							+ ": "
							+ "Moguce je vrsiti aritmeticke operacije samo na intovima",
					mulopFactor);
		}
	}

	public void visit(NoMulopFactors noMulopFactors) {
		noMulopFactors.struct = Tab.nullType;
	}

	public void visit(AddopTerms addopTerms) {
		addopTerms.struct = addopTerms.getAddopTerm().struct;
		if ((addopTerms.getAddopTerm().struct.getKind() != Struct.Int)
				|| (addopTerms.getAddopTermList().struct != Tab.nullType && addopTerms
						.getAddopTermList().struct.getKind() != Struct.Int)) {
			report_error(
					"Greska na liniji "
							+ addopTerms.getLine()
							+ ": "
							+ "Moguce je vrsiti aritmeticke operacije samo na intovima",
					addopTerms);
		}
	}

	public void visit(NoAddopTerms noAddopTerms) {
		noAddopTerms.struct = Tab.nullType;
	}

	public void visit(PositiveExpr positiveExpr) {
		positiveExpr.struct = positiveExpr.getTerm().struct;
		if (positiveExpr.getAddopTermList().struct != Tab.nullType)
			if (positiveExpr.getAddopTermList().struct.getKind() != Struct.Int
					|| positiveExpr.getTerm().struct.getKind() != Struct.Int)
				report_error(
						"Greska na liniji "
								+ positiveExpr.getLine()
								+ ": "
								+ "Moguce je vrsiti aritmeticke operacije samo na intovima",
						positiveExpr);
	}

	public void visit(AddopTerm addopTerm) {
		addopTerm.struct = addopTerm.getTerm().struct;

		if (addopTerm.struct.getKind() != Struct.Int) {
			report_error(
					"Greska na liniji "
							+ addopTerm.getLine()
							+ ": "
							+ "Moguce je vrsiti aritmeticke operacije samo na intovima",
					addopTerm);
		}
	}

	public void visit(NegativeExpr negativeExpr) {
		negativeExpr.struct = negativeExpr.getTerm().struct;
		if (negativeExpr.getAddopTermList().struct != Tab.nullType)
			if (negativeExpr.getAddopTermList().struct.getKind() != Struct.Int)
				report_error(
						"Greska na liniji "
								+ negativeExpr.getLine()
								+ ": "
								+ "Moguce je vrsiti aritmeticke operacije samo na intovima",
						negativeExpr);
		if (negativeExpr.getTerm().struct.getKind() != Struct.Int)
			report_error(
					"Greska na liniji "
							+ negativeExpr.getLine()
							+ ": "
							+ "Moguce je vrsiti aritmeticke operacije samo na intovima",
					negativeExpr);
	}

	public void visit(AssingopDesignatorExpr assingopDesignatorExpr) {
		Designator designator = ((DesignatorStatement) assingopDesignatorExpr
				.getParent()).getDesignator();

		Struct designatorStruct = designator.obj.getType();
		int designatorDepth = depth(designator.getExprBrackets());

		while (designatorDepth > 0
				&& designatorStruct.getKind() == Struct.Array) {
			designatorStruct = designatorStruct.getElemType();
			designatorDepth--;
		}

		Struct exprStruct = assingopDesignatorExpr.getExpr().struct;

		if (exprStruct.assignableTo(designatorStruct))
			return;

		report_error("Nije moguca dodela vrednosti na liniji "
				+ assingopDesignatorExpr.getLine(), null);
	}

	public void visit(IncDesignatorExpr incDesignatorExpr) {
		Designator designator = ((DesignatorStatement) incDesignatorExpr
				.getParent()).getDesignator();

		Struct designatorStruct = designator.obj.getType();

		int designatorDepth = depth(designator.getExprBrackets());

		while (designatorDepth > 0
				&& designatorStruct.getKind() == Struct.Array) {
			designatorStruct = designatorStruct.getElemType();
			designatorDepth--;
		}

		if (designatorStruct == Tab.intType) {
			return;
		}

		report_error("Nije moguce inkrementiranje na liniji "
				+ incDesignatorExpr.getLine(), incDesignatorExpr);
	}

	public void visit(DecDesignatorExpr decDesignatorExpr) {
		Designator designator = ((DesignatorStatement) decDesignatorExpr
				.getParent()).getDesignator();

		Struct designatorStruct = designator.obj.getType();

		int designatorDepth = depth(designator.getExprBrackets());

		while (designatorDepth > 0
				&& designatorStruct.getKind() == Struct.Array) {
			designatorStruct = designatorStruct.getElemType();
			designatorDepth--;
		}

		if (designatorStruct == Tab.intType) {
			return;
		}

		report_error("Nije moguce dekrementiranje na liniji "
				+ decDesignatorExpr.getLine(), decDesignatorExpr);
	}

	public void visit(ParenDesignatorExpr parenDesignatorExpr) {
		Designator designator = ((DesignatorStatement) parenDesignatorExpr
				.getParent()).getDesignator();
		int designatorKind = designator.obj.getKind();

		if (designatorKind != Obj.Meth) {
			report_error("Greska na liniji " + designator.getLine() + ": "
					+ designator.getName() + " nije funkcija!", null);
		} else {
			report_info("Pronadjen poziv funkcije " + designator.getName()
					+ " na liniji " + designator.getLine(), null);
		}
	}

	public void visit(ParenActDesignatorExpr parenActDesignatorExpr) {
		Designator designator = ((DesignatorStatement) parenActDesignatorExpr
				.getParent()).getDesignator();
		int designatorKind = designator.obj.getKind();

		if (designatorKind != Obj.Meth) {
			report_error("Greska na liniji " + designator.getLine() + ": "
					+ designator.getName() + " nije funkcija!", null);
		} else {
			report_info("Pronadjen poziv funkcije " + designator.getName()
					+ " na liniji " + designator.getLine(), null);
		}
	}

	public void visit(MulopFactors mulopFactors) {
		mulopFactors.struct = mulopFactors.getMulopFactor().struct;
		if ((mulopFactors.getMulopFactor().struct.getKind() != Struct.Int)
				|| (mulopFactors.getMulopFactorList().struct != Tab.nullType && mulopFactors
						.getMulopFactorList().struct.getKind() != Struct.Int)) {
			report_error(
					"Greska na liniji "
							+ mulopFactors.getLine()
							+ ": "
							+ "Moguce je vrsiti aritmeticke operacije samo na intovima",
					mulopFactors);
		}
	}

	public void visit(ActParamExprs actParamExprs) {
		actParamExprs.struct = actParamExprs.getExpr().struct;

		if (actParamExprs.getExpr().struct.getKind() != funcParamTypes.get(
				currentDesignator).get(getOrderParam(actParamExprs))) {
			report_error("Greska na liniji " + actParamExprs.getLine() + ": "
					+ " tip parametara nije tacan!", null);
		}
	}

	public void visit(SingleActParamExpr singleActParamExpr) {
		singleActParamExpr.struct = singleActParamExpr.getExpr().struct;

		int paramLen = paramLength(singleActParamExpr);

		if (paramLen != funcParamLens.get(currentDesignator)) {
			report_error("Greska na liniji " + singleActParamExpr.getLine()
					+ ": " + " broj parametara nije tacan!", null);
			return;
		}

		if (singleActParamExpr.getExpr().struct.getKind() != funcParamTypes
				.get(currentDesignator).get(getOrderParam(singleActParamExpr))) {
			report_error("Greska na liniji " + singleActParamExpr.getLine()
					+ ": " + " tip parametara nije tacan!", null);
		}
	}

	public void visit(Designator designator) {
		if (methods.get(designator.getName()) != null
				&& methods.get(designator.getName()) == true) {
			currentDesignator = designator.getName();
		}
		Obj obj = Tab.find(designator.getName());
		designator.obj = obj;
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designator.getLine() + " : ime "
					+ designator.getName() + " nije deklarisano! ", null);
			return;
		}

		int designatorDepth = depth(designator.getExprBrackets());

		Struct designatorStruct = designator.obj.getType();

		while (designatorDepth > 0) {
			if (designatorStruct.getKind() != Struct.Array) {
				report_error("Pogresno indeksiranje", designator);
				return;
			}
			designatorStruct = designatorStruct.getElemType();
			designatorDepth--;
		}
	}

	public void visit(PrintNumStmt printNumStmt) {
		printNumStmt.struct = printNumStmt.getExpr().struct;
		if (printNumStmt.getExpr().struct.getKind() != Struct.Int
				&& printNumStmt.getExpr().struct.getKind() != Struct.Char
				&& printNumStmt.getExpr().struct.getKind() != Struct.Bool) {
			report_error("Greska na liniji " + printNumStmt.getLine() + ": "
					+ "parametar nije odgovarajuceg tipa!", null);
		}
	}

	public void visit(PrintNoNumStmt printNoNumStmt) {
		printNoNumStmt.struct = printNoNumStmt.getExpr().struct;
		if (printNoNumStmt.getExpr().struct.getKind() != Struct.Int
				&& printNoNumStmt.getExpr().struct.getKind() != Struct.Char
				&& printNoNumStmt.getExpr().struct.getKind() != Struct.Bool) {
			report_error("Greska na liniji " + printNoNumStmt.getLine() + ": "
					+ "parametar nije odgovarajuceg tipa!", null);
		}
	}

	public void visit(ReadStmt readStmt) {
		readStmt.struct = readStmt.getDesignator().obj.getType();
		Designator designator = readStmt.getDesignator();
		Obj param = Tab.find(designator.getName());
		if (param.getKind() != Struct.Int && param.getKind() != Struct.Char
				&& param.getKind() != Struct.Bool
				&& param.getKind() != Struct.Array) {
			report_error("Greska na liniji " + readStmt.getLine() + ": "
					+ "parametar nije odgovarajuceg tipa!", null);
		}
	}

	public void visit(FindAnyStmt anyStmt) {
		Designator designator1 = anyStmt.getDesignator();
		Designator designator2 = anyStmt.getDesignator1();

		if (designator2.obj.getType().getElemType() != Tab.charType
				&& designator2.obj.getType().getElemType() != Tab.intType
				&& designator2.obj.getType().getElemType() != Compiler.boolType) {
			report_error("Greska na liniji " + anyStmt.getLine() + ": "
					+ "niz nije ugradjenog tipa!", null);
		}

		if (!designator1.obj.getType().assignableTo(Compiler.boolType)) {
			report_error("Greska na liniji " + anyStmt.getLine() + ": "
					+ "tipovi nisu kompatibilni!", null);
		}
	}

	public void visit(ForEachStmt eachStmt) {
		forEachDepth--;
		Designator designator = eachStmt.getDesignator();

		if (designator.obj.getType().getElemType() != Tab.charType
				&& designator.obj.getType().getElemType() != Tab.intType
				&& designator.obj.getType().getElemType() != Compiler.boolType) {
			report_error("Greska na liniji " + eachStmt.getLine() + ": "
					+ "niz nije ugradjenog tipa!", null);
		}

	}

	public void visit(FindStmt findStmt) {
		Designator designator1 = findStmt.getDesignator();
		Designator designator2 = findStmt.getDesignator1();

		if (designator1.obj.getType().assignableTo(designator2.obj.getType())) {
			if (arrayDimension.get(designator1.getName()) != 1
					&& arrayDimension.get(designator2.getName()) != 1) {
				report_error("Greska na liniji " + findStmt.getLine() + ": "
						+ "tipovi nisu kompatibilni!", null);
			}
		} else {
			report_error("Greska na liniji " + findStmt.getLine() + ": "
					+ "tipovi nisu kompatibilni!", null);
		}
	}

	public void visit(WhileStmt whileStmt) {
		whileDepth--;
	}

	public void visit(BreakStmt breakStmt) {
		if (whileDepth == 0 && forEachDepth == 0) {
			report_error("Greska na liniji " + breakStmt.getLine() + ": "
					+ "break ne moze biti pozvan van while-a ili foreach-a!",
					null);
		}
	}

	public void visit(ContinueStmt continueStmt) {
		if (whileDepth == 0 && forEachDepth == 0) {
			report_error(
					"Greska na liniji "
							+ continueStmt.getLine()
							+ ": "
							+ "continue ne moze biti pozvan van while-a ili foreach-a!!",
					null);
		}
	}

	public void visit(While whileEnter) {
		whileDepth++;
	}

	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName()
					+ " u tabeli simbola! ", null);
			type.struct = Tab.noType;
			varType = type.struct;
		} else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
				varType = type.struct;
			} else {
				report_error("Greska: Ime " + type.getTypeName()
						+ " ne predstavlja tip!", type);
				type.struct = Tab.noType;
				varType = type.struct;
			}
		}
	}

	public void visit(NumberFactor numFactor) {
		numFactor.struct = Tab.intType;
	}

	public void visit(CharFactor charFactor) {
		charFactor.struct = Tab.charType;
	}

	public void visit(TrueFactor trueFactor) {
		trueFactor.struct = Compiler.boolType;
	}

	public void visit(FalseFactor falseFactor) {
		falseFactor.struct = Compiler.boolType;
	}

	public void visit(ExprFactor exprFactor) {
		exprFactor.struct = exprFactor.getExpr().struct;
	}

	public void visit(DesignatorFactor designatorFactor) {
		designatorFactor.struct = designatorFactor.getDesignator().obj
				.getType();

		int designatorDepth = depth(designatorFactor.getDesignator()
				.getExprBrackets());

		while (designatorDepth > 0
				&& designatorFactor.struct.getKind() == Struct.Array) {
			designatorFactor.struct = designatorFactor.struct.getElemType();
			designatorDepth--;
		}
	}

	public void visit(FuncCallNoParam funcCall) {
		Obj func = funcCall.getDesignator().obj;
		if (Obj.Meth == func.getKind()) {
			report_info("Pronadjen poziv funkcije " + func.getName()
					+ " na liniji " + funcCall.getLine(), null);
			funcCall.struct = func.getType();
		} else {
			report_error("Greska na liniji " + funcCall.getLine() + " : ime "
					+ func.getName() + " nije funkcija!", null);
			funcCall.struct = Tab.noType;
		}
	}

	public void visit(FuncCallWIthParam funcCall) {
		Obj func = funcCall.getDesignator().obj;
		if (Obj.Meth == func.getKind()) {
			report_info("Pronadjen poziv funkcije " + func.getName()
					+ " na liniji " + funcCall.getLine(), null);
			funcCall.struct = func.getType();
		} else {
			report_error("Greska na liniji " + funcCall.getLine() + " : ime "
					+ func.getName() + " nije funkcija!", null);
			funcCall.struct = Tab.noType;
		}
	}

	public void visit(NewOneExprFactor newOneExprFactor) {
		newOneExprFactor.struct = new Struct(Struct.Array,
				newOneExprFactor.getType().struct);
		if (newOneExprFactor.getExpr().struct.getKind() != Struct.Int) {
			report_error("Greska na liniji " + newOneExprFactor.getLine()
					+ ": " + " nije int!", null);
		}
	}

	public void visit(NewTwoExprFactor newTwoExprFactor) {
		newTwoExprFactor.struct = new Struct(Struct.Array, new Struct(
				Struct.Array, newTwoExprFactor.getType().struct));
		if (newTwoExprFactor.getExpr1().struct.getKind() != Struct.Int
				|| newTwoExprFactor.getExpr().struct.getKind() != Struct.Int) {
			report_error("Greska na liniji " + newTwoExprFactor.getLine()
					+ ": " + " nije int!", null);
		}
	}

	public void visit(CondFactExpr condFactExpr) {
		if (condFactExpr.getExpr().struct.getKind() != Struct.Bool) {
			report_error("Greska na liniji " + condFactExpr.getLine() + ": "
					+ "nije logicki izraz!", null);
		}
	}

	public void visit(CondFactExprRelop condFactExprRelop) {
		Struct exprStruct1 = condFactExprRelop.getExpr().struct;
		Struct exprStruct2 = condFactExprRelop.getExpr1().struct;

		if (!exprStruct1.assignableTo(exprStruct2)) {
			report_error("Greska na liniji " + condFactExprRelop.getLine()
					+ ": " + "tipovi nisu kompatibilni!", null);
		}

		if ((exprStruct1.getKind() == Struct.Array || exprStruct2.getKind() == Struct.Array)
				&& (!(condFactExprRelop.getRelop() instanceof IsEqualOp) && !(condFactExprRelop
						.getRelop() instanceof NeqOp))) {
			report_error("Greska na liniji " + condFactExprRelop.getLine()
					+ ": " + "nizovi mogu da se porede samo sa == ili != !",
					null);
		}
	}

	public boolean passed() {
		return !errorDetected;
	}

}
