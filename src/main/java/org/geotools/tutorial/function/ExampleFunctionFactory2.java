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
import org.geotools.filter.capability.FunctionNameImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

public class ExampleFunctionFactory2 implements FunctionFactory {
    private ArrayList<FunctionName> functionList;
    private static FunctionName FIRST = new FunctionNameImpl("first", "geometry");

    public synchronized List<FunctionName> getFunctionNames() {
        if (functionList == null) {
            functionList = new ArrayList<>();
            functionList.add(FIRST);
        }
        return Collections.unmodifiableList(functionList);
    }

    public Function function(String name, List<Expression> args, Literal fallback) {
        return function(new NameImpl(name), args, fallback);
    }

    public Function function(Name name, List<Expression> args, Literal fallback) {
        if (new NameImpl("first").equals(name)) {
            return new AbstractFunction(FIRST, args, fallback) {//生产函数
                public Geometry evaluate(Object object) {       //重写evaluate函数
                    Geometry geom = eval(object, 0, Geometry.class);
                    Coordinate coordinate = geom.getCoordinate();
                    return geom.getFactory().createPoint(coordinate);
                }
            };
        }
        return null; // we do not implement that function
    }
}
