package org.geotools.tutorial.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotools.api.feature.type.Name;
import org.geotools.api.filter.capability.FunctionName;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.filter.expression.Function;
import org.geotools.api.filter.expression.Literal;
import org.geotools.feature.NameImpl;
import org.geotools.filter.FunctionFactory;

public class ExampleFunctionFactory implements FunctionFactory {//生产SnapFunction的工厂

    public List<FunctionName> getFunctionNames() {
        List<FunctionName> functionList = new ArrayList<>();
        functionList.add(SnapFunction.NAME);
        return Collections.unmodifiableList(functionList);
    }

    public Function function(String name, List<Expression> args, Literal fallback) {
        return function(new NameImpl(name), args, fallback);
    }

    public Function function(Name name, List<Expression> args, Literal fallback) {
        if (SnapFunction.NAME.getFunctionName().equals(name)) {
            return new SnapFunction(args, fallback);
        }
        return null; // we do not implement that function
    }

    public Function getSnapFunction( List<Expression> args, Literal fallback){
        return new SnapFunction(args, fallback);
    }
}
