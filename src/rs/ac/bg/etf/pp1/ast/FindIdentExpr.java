// generated with ast extension for cup
// version 0.8
// 21/7/2023 19:37:6


package rs.ac.bg.etf.pp1.ast;

public class FindIdentExpr implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private FindIdent FindIdent;
    private Expr Expr;

    public FindIdentExpr (FindIdent FindIdent, Expr Expr) {
        this.FindIdent=FindIdent;
        if(FindIdent!=null) FindIdent.setParent(this);
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
    }

    public FindIdent getFindIdent() {
        return FindIdent;
    }

    public void setFindIdent(FindIdent FindIdent) {
        this.FindIdent=FindIdent;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FindIdent!=null) FindIdent.accept(visitor);
        if(Expr!=null) Expr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FindIdent!=null) FindIdent.traverseTopDown(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FindIdent!=null) FindIdent.traverseBottomUp(visitor);
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FindIdentExpr(\n");

        if(FindIdent!=null)
            buffer.append(FindIdent.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FindIdentExpr]");
        return buffer.toString();
    }
}
