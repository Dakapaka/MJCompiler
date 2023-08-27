// generated with ast extension for cup
// version 0.8
// 21/7/2023 19:37:6


package rs.ac.bg.etf.pp1.ast;

public class ConstNumber extends ConstVal {

    private Integer numberVal;

    public ConstNumber (Integer numberVal) {
        this.numberVal=numberVal;
    }

    public Integer getNumberVal() {
        return numberVal;
    }

    public void setNumberVal(Integer numberVal) {
        this.numberVal=numberVal;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstNumber(\n");

        buffer.append(" "+tab+numberVal);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstNumber]");
        return buffer.toString();
    }
}
