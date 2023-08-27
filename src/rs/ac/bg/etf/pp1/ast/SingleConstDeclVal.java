// generated with ast extension for cup
// version 0.8
// 21/7/2023 19:37:6


package rs.ac.bg.etf.pp1.ast;

public class SingleConstDeclVal extends ConstDeclValList {

    private ConstDeclVal ConstDeclVal;

    public SingleConstDeclVal (ConstDeclVal ConstDeclVal) {
        this.ConstDeclVal=ConstDeclVal;
        if(ConstDeclVal!=null) ConstDeclVal.setParent(this);
    }

    public ConstDeclVal getConstDeclVal() {
        return ConstDeclVal;
    }

    public void setConstDeclVal(ConstDeclVal ConstDeclVal) {
        this.ConstDeclVal=ConstDeclVal;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstDeclVal!=null) ConstDeclVal.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstDeclVal!=null) ConstDeclVal.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstDeclVal!=null) ConstDeclVal.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleConstDeclVal(\n");

        if(ConstDeclVal!=null)
            buffer.append(ConstDeclVal.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleConstDeclVal]");
        return buffer.toString();
    }
}
