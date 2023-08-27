package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.io.FileOutputStream;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.ac.bg.etf.pp1.SemanticAnalyzer;
import rs.etf.pp1.mj.runtime.Code;

public class Compiler {
	
	public static final Struct boolType = new Struct(Struct.Bool);
	//public static Scope currentScope;

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws Exception {
		
		Logger log = Logger.getLogger(Compiler.class);
		
		Reader br = null;
		
		try {
			Tab.init();
			Scope universe = Tab.currentScope;
			universe.addToLocals(new Obj(Obj.Type, "bool", boolType));
			File sourceCode = new File("test/program.mj");
			log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			Yylex lexer = new Yylex(br);
			
			MJParser p = new MJParser(lexer);
	        Symbol s = p.parse();  //pocetak parsiranja
	        
	        Program prog = (Program)(s.value); 
			// ispis sintaksnog stabla
			log.info(prog.toString(""));
			log.info("===================================");

			// ispis prepoznatih programskih konstrukcija
			SemanticAnalyzer v = new SemanticAnalyzer();
			v.funcParamLens.put("ord", 1);
			v.funcParamLens.put("len", 1);
			v.funcParamLens.put("chr", 1);
			v.methods.put("ord", true);
			v.methods.put("len", true);
			v.methods.put("chr", true);
			HashMap<Integer, Integer> innerMapOrd = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> innerMapLen = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> innerMapChr = new HashMap<Integer, Integer>();
			innerMapOrd.put(1, Struct.Char);
			innerMapLen.put(1, Struct.Array);
			innerMapChr.put(1, Struct.Int);
			v.funcParamTypes.put("ord", innerMapOrd);
			v.funcParamTypes.put("len", innerMapLen);
			v.funcParamTypes.put("chr", innerMapChr);
			prog.traverseBottomUp(v);
			Tab.dump();
			
			if(!p.errorDetected && v.passed()){
				File objFile = new File("test/program.obj");
				if(objFile.exists()) objFile.delete();
				
				CodeGenerator codeGenerator = new CodeGenerator();
				prog.traverseBottomUp(codeGenerator);
				Code.dataSize = v.nVars;
				Code.mainPc = codeGenerator.getMainPc();
				Code.write(new FileOutputStream(objFile));
				log.info("Parsiranje uspjesno zavrseno!");
			}else{
				log.error("Parsiranje NIJE uspjesno zavrseno!");
			}
			
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}

	}
	
	
}
