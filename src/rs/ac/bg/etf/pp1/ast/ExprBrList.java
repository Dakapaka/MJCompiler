// generated with ast extension for cup
// version 0.8
// 21/7/2023 19:37:6


package rs.ac.bg.etf.pp1.ast;

public class ExprBrList extends ExprBrackets {

    private ExprBrackets ExprBrackets;
    private ExprBr ExprBr;

    public ExprBrList (ExprBrackets ExprBrackets, ExprBr ExprBr) {
        this.ExprBrackets=ExprBrackets;
        if(ExprBrackets!=null) ExprBrackets.setParent(this);
        this.ExprBr=ExprBr;
        if(ExprBr!=null) ExprBr.setParent(this);
    }

    public ExprBrackets getExprBrackets() {
        return ExprBrackets;
    }

    public void setExprBrackets(ExprBrackets ExprBrackets) {
        this.ExprBrackets=ExprBrackets;
    }

    public ExprBr getExprBr() {
        return ExprBr;
    }

    public void setExprBr(ExprBr ExprBr) {
        this.ExprBr=ExprBr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprBrackets!=null) ExprBrackets.accept(visitor);
        if(ExprBr!=null) ExprBr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprBrackets!=null) ExprBrackets.traverseTopDown(visitor);
        if(ExprBr!=null) ExprBr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprBrackets!=null) ExprBrackets.traverseBottomUp(visitor);
        if(ExprBr!=null) ExprBr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ExprBrList(\n");

        if(ExprBrackets!=null)
            buffer.append(ExprBrackets.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprBr!=null)
            buffer.append(ExprBr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ExprBrList]");
        return buffer.toString();
    }
}
