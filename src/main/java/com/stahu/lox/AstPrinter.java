package com.stahu.lox;

public class AstPrinter implements Expr.Visitor<String>{
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expression) {
        return parenthesize(expression.operator().lexeme(), expression.left(), expression.right());
    }

    @Override
    public String visitCallExpr(Expr.Call expression) {
        return "";
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expression) {
        return parenthesize("group", expression.expr());
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expression) {
        if (expression.value() == null) return "nil";
        return expression.value().toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expression) {
        return "";
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expression) {
        return parenthesize(expression.operator().lexeme(), expression.right());
    }

    @Override
    public String visitVariableExpr(Expr.Variable expression) {
        return "";
    }

    @Override
    public String visitAssignExpr(Expr.Assign expression) {
        return "";
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");

        // Append the operator name first (this is usually for unary operations)
        builder.append(name);

        // Iterate through the expressions to form the infix notation
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }

        builder.append(")");

        return builder.toString();
    }
}
