package com.stahu.lox;

import com.stahu.lox.model.Token;

import java.util.List;

interface Stmt {
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitIfStmt(If stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
        R visitBlockStmt(Block stmt);
    }

    <R> R accept(Visitor<R> visitor);

    record Expression(Expr expression) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    record Print(Expr expression) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    record Var(Token name, Expr initializer) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    record Block(List<Stmt> statements) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }
}