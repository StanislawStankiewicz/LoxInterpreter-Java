package com.stahu.lox;

import com.stahu.lox.error.RuntimeError;
import com.stahu.lox.model.Token;

class Interpreter implements Expression.Visitor<Object> {

    void interpret(Expression expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
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
    public Object visitGroupingExpression(Expression.Grouping expression) {
        return evaluate(expression.expression());
    }

    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value();
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
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
        if (object instanceof Boolean) return (boolean)object;
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

    private Object evaluate(Expression expr) {
        return expr.accept(this);
    }
}