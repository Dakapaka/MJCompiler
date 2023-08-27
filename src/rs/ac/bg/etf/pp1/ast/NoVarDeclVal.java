// generated with ast extension for cup
// version 0.8
// 21/7/2023 19:37:6


package rs.ac.bg.etf.pp1.ast;

public class NoVarDeclVal extends VarDeclValList {

    private VarDeclVal VarDeclVal;

    public NoVarDeclVal (VarDeclVal VarDeclVal) {
        this.VarDeclVal=VarDeclVal;
        if(VarDeclVal!=null) VarDeclVal.setParent(this);
    }

    public VarDeclVal getVarDeclVal() {
        return VarDeclVal;
    }

    public void setVarDeclVal(VarDeclVal VarDeclVal) {
        this.VarDeclVal=VarDeclVal;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclVal!=null) VarDeclVal.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclVal!=null) VarDeclVal.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclVal!=null) VarDeclVal.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NoVarDeclVal(\n");

        if(VarDeclVal!=null)
            buffer.append(VarDeclVal.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NoVarDeclVal]");
        return buffer.toString();
    }
}
