package com.davidlong.http.expression;

import com.davidlong.http.exception.RuntimeExecuteException;
import com.davidlong.http.exception.RuntimeResolveException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ContextExpressionParser {
    private SpelExpressionParser parser;
    private EvaluationContext evaluationContext;

    public ContextExpressionParser(SpelExpressionParser parser,EvaluationContext evaluationContext) {
        this.parser=parser;
        this.evaluationContext=evaluationContext;
    }

    private Expression parseExpression(String str){
        return parser.parseExpression(str,new ParserContext() {
            public String getExpressionPrefix() {
                return "${";
            }

            public String getExpressionSuffix() {
                return "}";
            }

            public boolean isTemplate() {
                return true;
            }
        });
    }

    public Object parse(String str){
        return parseExpression(str).getValue(evaluationContext);
    }

    public <T> T parse(String str,Class<T> expectedResultType){
        return parseExpression(str).getValue(evaluationContext,expectedResultType);
    }
}
