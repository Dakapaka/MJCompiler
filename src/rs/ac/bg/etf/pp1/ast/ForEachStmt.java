// generated with ast extension for cup
// version 0.8
// 21/7/2023 19:37:6


package rs.ac.bg.etf.pp1.ast;

public class ForEachStmt extends Statement {

    private Designator Designator;
    private Foreach Foreach;
    private FindIdent FindIdent;
    private Statement Statement;

    public ForEachStmt (Designator Designator, Foreach Foreach, FindIdent FindIdent, Statement Statement) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.Foreach=Foreach;
        if(Foreach!=null) Foreach.setParent(this);
        this.FindIdent=FindIdent;
        if(FindIdent!=null) FindIdent.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public Foreach getForeach() {
        return Foreach;
    }

    public void setForeach(Foreach Foreach) {
        this.Foreach=Foreach;
    }

    public FindIdent getFindIdent() {
        return FindIdent;
    }

    public void setFindIdent(FindIdent FindIdent) {
        this.FindIdent=FindIdent;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(Foreach!=null) Foreach.accept(visitor);
        if(FindIdent!=null) FindIdent.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(Foreach!=null) Foreach.traverseTopDown(visitor);
        if(FindIdent!=null) FindIdent.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(Foreach!=null) Foreach.traverseBottomUp(visitor);
        if(FindIdent!=null) FindIdent.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ForEachStmt(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Foreach!=null)
            buffer.append(Foreach.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FindIdent!=null)
            buffer.append(FindIdent.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ForEachStmt]");
        return buffer.toString();
    }
}
