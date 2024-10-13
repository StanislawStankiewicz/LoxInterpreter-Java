package com.stahu.lox;

import com.stahu.lox.model.Token;

import java.util.List;

interface Expr {
    interface Visitor<R> {
        R visitBinaryExpr(Binary expression);
        R visitCallExpr(Call expression);
        R visitGroupingExpr(Grouping expression);
        R visitLiteralExpr(Literal expression);
        R visitLogicalExpr(Logical expression);
        R visitUnaryExpr(Unary expression);
        R visitVariableExpr(Variable expression);
        R visitAssignExpr(Assign expression);
    }

    <R> R accept(Visitor<R> visitor);

    record Binary(Expr left, Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    record Grouping(Expr expr) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    record Literal(Object value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    record Logical(Expr left, Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    record Unary(Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    record Variable(Token name) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    record Assign(Token name, Expr value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }
}
