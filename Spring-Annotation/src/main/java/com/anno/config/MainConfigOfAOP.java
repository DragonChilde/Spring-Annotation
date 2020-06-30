package com.anno.config;

import com.anno.aop.LogOfAspects;
import com.anno.aop.MathCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@Configuration
public class MainConfigOfAOP {

    @Bean
    public MathCalculator mathCalculator()
    {
        return new MathCalculator();
    }

    @Bean
    public LogOfAspects logOfAspects()
    {
        return new LogOfAspects();
    }
}
