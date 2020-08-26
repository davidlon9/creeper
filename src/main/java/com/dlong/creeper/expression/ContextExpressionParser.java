package com.dlong.creeper.expression;

import com.dlong.creeper.resolver.util.WrapUtil;
import org.apache.log4j.Logger;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.CompositeStringExpression;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ContextExpressionParser {
    private static Logger logger=Logger.getLogger(ContextExpressionParser.class);
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
        Expression expression = parseExpression(str);
        if(expression instanceof CompositeStringExpression){
            String value = getCompositeStringValue((CompositeStringExpression) expression);
            return ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(value), String.class);
        }
        return expression.getValue(evaluationContext);
    }

    public <T> T parse(String str,Class<T> expectedResultType){
        Expression expression = parseExpression(str);
        if(expression instanceof CompositeStringExpression){
            String value = getCompositeStringValue((CompositeStringExpression) expression);
            return ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(value), expectedResultType);
        }
        return expression.getValue(evaluationContext,expectedResultType);
    }

    private String getCompositeStringValue(CompositeStringExpression expression) {
        CompositeStringExpression cse= expression;
        StringBuilder sb = new StringBuilder();
        Expression[] expressions = cse.getExpressions();
        for (Expression e : expressions) {
            if (e instanceof SpelExpression) {
                SpelExpression spelExpression = (SpelExpression) e;
                String value = spelExpression.getValue(evaluationContext, String.class);
                if("".equals(value) || value == null){
                    logger.warn("expression "+ WrapUtil.enDoubleQuote(spelExpression.getExpressionString())+" parsed value is null or empty, please make sure ContextParamStore contains params which names is in expression string.");
                }else{
                    sb.append(value);
                }
            }else{
                String value = e.getValue(evaluationContext, String.class);
                if (value != null) {
                    sb.append(value);
                }
            }
        }
        return sb.toString();
    }
}