package com.anno.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Arrays;

/**
 * 切面类
 * @Aspect： 告诉Spring当前类是一个切面类
 *
 */
@Aspect
public class LogOfAspects {

    //抽取公共的切入点表达式
    //1、本类引用
    //2、其他的切面引用
    @Pointcut("execution(public int com.anno.aop.MathCalculator.*(..))")
    public void pointCut()
    {};

    @Before("pointCut()")
    public void logStart(JoinPoint joinPoint)
    {
        Object[] args = joinPoint.getArgs();
        System.out.println(joinPoint.getSignature().getName()+"运行.......@Before:参数列表是"+ Arrays.asList(args));
    }

    @After("com.anno.aop.LogOfAspects.pointCut()")
    public void logEnd(JoinPoint joinPoint)
    {
        System.out.println(joinPoint.getSignature().getName()+" 结束........@After");
    }

    //注意:JoinPoint一定要出现在参数表的第一位
    @AfterReturning(value = "pointCut()",returning = "result")
    public void logReturn(JoinPoint joinPoint,Object result)
    {
        System.out.println(joinPoint.getSignature().getName()+" 正常返回.......... @AfterReturning:运行结果:"+result);
    }

    @AfterThrowing(value = "pointCut()",throwing = "exception")
    public void logException(JoinPoint joinPoint,Exception exception)
    {
        System.out.println(joinPoint.getSignature().getName()+" 异常.............异常信息: "+exception);
    }

}
