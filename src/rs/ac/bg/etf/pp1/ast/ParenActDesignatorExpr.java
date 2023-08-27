// generated with ast extension for cup
// version 0.8
// 21/7/2023 19:37:6


package rs.ac.bg.etf.pp1.ast;

public class ParenActDesignatorExpr extends DesignatorExpr {

    private ActParamList ActParamList;

    public ParenActDesignatorExpr (ActParamList ActParamList) {
        this.ActParamList=ActParamList;
        if(ActParamList!=null) ActParamList.setParent(this);
    }

    public ActParamList getActParamList() {
        return ActParamList;
    }

    public void setActParamList(ActParamList ActParamList) {
        this.ActParamList=ActParamList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParamList!=null) ActParamList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParamList!=null) ActParamList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParamList!=null) ActParamList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ParenActDesignatorExpr(\n");

        if(ActParamList!=null)
            buffer.append(ActParamList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ParenActDesignatorExpr]");
        return buffer.toString();
    }
}
