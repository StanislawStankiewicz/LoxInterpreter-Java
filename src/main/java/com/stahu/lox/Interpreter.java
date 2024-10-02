package com.stahu.lox;

import com.stahu.lox.error.RuntimeError;
import com.stahu.lox.model.Token;
import com.stahu.lox.model.TokenType;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expression) {
        Object left = evaluate(expression.left());
        Object right = evaluate(expression.right());

        switch (expression.operator().type()) {
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expression.operator(), left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expression.operator(), left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expression.operator(), left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expression.operator(), left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expression.operator(), left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String sLeft && right instanceof String sRight) {
                    return sLeft + sRight;
                }
                if (left instanceof Double && right instanceof String) {
                    Object temp = left;
                    left = right;
                    right = temp;
                }
                if (left instanceof String sLeft && right instanceof Double dRight) {
                    double sum = 0;
                    for (char c : sLeft.toCharArray()) {
                        sum += c;
                    }
                    return sum + dRight;
                }
                throw new RuntimeError(expression.operator(), "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expression.operator(), left, right);
                if ((double)right == (double) 0) {
                    throw new RuntimeError(expression.operator(), "Division by zero.");
                }
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expression.operator(), left, right);
                return (double)left * (double)right;
            default:
                // Unreachable.
                return null;
        }
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expression) {
        return evaluate(expression.expr());
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expression) {
        return expression.value();
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left());

        if (expr.operator().type() == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right());
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expression) {
        Object right = evaluate(expression.right());

        return switch (expression.operator().type()) {
            case BANG -> !isTruthy(right);
            case MINUS -> {
                checkNumberOperand(expression.operator(), right);
                yield -(double) right;}
            default ->
                // Unreachable.
                    null;
        };
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name());
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean bool) return bool;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements(), new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression());
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition()))) {
            execute(stmt.thenBranch());
        } else if (stmt.elseBranch() != null) {
            execute(stmt.elseBranch());
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression());
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer() != null) {
            value = evaluate(stmt.initializer());
        }
        environment.define(stmt.name().lexeme(), value);
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value());
        environment.assign(expr.name(), value);
        return value;
    }
}