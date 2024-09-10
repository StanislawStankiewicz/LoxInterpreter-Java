package com.stahu.lox;

public class AstPrinter implements Expr.Visitor<String>{
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expr.Binary expression) {
        return parenthesize(expression.operator().lexeme(), expression.left(), expression.right());
    }

    @Override
    public String visitGroupingExpression(Expr.Grouping expression) {
        return parenthesize("group", expression.expr());
    }

    @Override
    public String visitLiteralExpression(Expr.Literal expression) {
        if (expression.value() == null) return "nil";
        return expression.value().toString();
    }

    @Override
    public String visitUnaryExpression(Expr.Unary expression) {
        return parenthesize(expression.operator().lexeme(), expression.right());
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
