package com.anno.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

//判断是否linux系统
public class LinuxCondition implements Condition {
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetdata) {
        Environment e = conditionContext.getEnvironment();
        if( e.getProperty("os.name").equals("linux"))
        {
            return true;
        }
        return false;
    }
}
