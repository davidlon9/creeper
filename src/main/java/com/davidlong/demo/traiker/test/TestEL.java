package com.davidlong.demo.traiker.test;

import com.davidlong.creeper.execution.context.ContextRootObject;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TestEL {
    public static void main(String[] args) {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE,null));
//        Map<String, Object> map = new HashMap<>();
//        map.put("A","B");

        StandardEvaluationContext context = new StandardEvaluationContext(new ContextRootObject(null));
//        context.setVariables(map);

        Object value2 = spelExpressionParser.parseExpression("#{time.now()}", ParserContext.TEMPLATE_EXPRESSION).getValue(context);
        System.out.println(value2);
    }
}
