// generated with ast extension for cup
// version 0.8
// 21/7/2023 19:37:6


package rs.ac.bg.etf.pp1.ast;

public class VarDeclVals extends VarDeclValList {

    private VarDeclValList VarDeclValList;
    private VarDeclVal VarDeclVal;

    public VarDeclVals (VarDeclValList VarDeclValList, VarDeclVal VarDeclVal) {
        this.VarDeclValList=VarDeclValList;
        if(VarDeclValList!=null) VarDeclValList.setParent(this);
        this.VarDeclVal=VarDeclVal;
        if(VarDeclVal!=null) VarDeclVal.setParent(this);
    }

    public VarDeclValList getVarDeclValList() {
        return VarDeclValList;
    }

    public void setVarDeclValList(VarDeclValList VarDeclValList) {
        this.VarDeclValList=VarDeclValList;
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
        if(VarDeclValList!=null) VarDeclValList.accept(visitor);
        if(VarDeclVal!=null) VarDeclVal.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclValList!=null) VarDeclValList.traverseTopDown(visitor);
        if(VarDeclVal!=null) VarDeclVal.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclValList!=null) VarDeclValList.traverseBottomUp(visitor);
        if(VarDeclVal!=null) VarDeclVal.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclVals(\n");

        if(VarDeclValList!=null)
            buffer.append(VarDeclValList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclVal!=null)
            buffer.append(VarDeclVal.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclVals]");
        return buffer.toString();
    }
}
