package org.geotools.tutorial.function;


import org.geotools.api.filter.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import org.geotools.api.filter.expression.Function;

public class FuncApp {
    public static void main(String[] args) {
        ExampleFunctionFactory factory1=new ExampleFunctionFactory();
        List<Expression> list=new ArrayList<>();//这个Expresssion和Literal是什么作用？
        list.add(null);
        list.add(null);
        Function func= factory1.getSnapFunction(list,null);//生产一个SnapFunction
        System.out.println(func.toString());
    }
}
