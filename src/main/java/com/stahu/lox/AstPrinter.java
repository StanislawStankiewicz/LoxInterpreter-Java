package com.stahu.lox;

public class AstPrinter implements Expression.Visitor<String>{
    String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        return parenthesize(expression.operator().lexeme(), expression.left(), expression.right());
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        return parenthesize("group", expression.expression());
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        if (expression.value() == null) return "nil";
        return expression.value().toString();
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        return parenthesize(expression.operator().lexeme(), expression.right());
    }

    private String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder();

        for (Expression expression : expressions) {
            builder.append(expression.accept(this));
            builder.append(" ");
        }
        builder.append(name);

        return builder.toString();
        }
}
