package com.stahu.lox;

import com.stahu.lox.model.Token;

interface Expression {
    interface Visitor<R> {
        R visitBinaryExpression(Binary expression);
        R visitGroupingExpression(Grouping expression);
        R visitLiteralExpression(Literal expression);
        R visitUnaryExpression(Unary expression);
    }

    <R> R accept(Visitor<R> visitor);

    record Binary(Expression left, Token operator, Expression right) implements Expression {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }
    }

    record Grouping(Expression expression) implements Expression {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }
    }

    record Literal(Object value) implements Expression {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }
    }

    record Unary(Token operator, Expression right) implements Expression {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }
    }
}
