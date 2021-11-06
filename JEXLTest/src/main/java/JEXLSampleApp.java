import org.apache.commons.jexl3.*;

import java.util.Arrays;

public class JEXLSampleApp {

    private static final JexlEngine jexl = new JexlBuilder().cache(512).strict(true).silent(false).create();

    public static void main(String[] args) {
       // Create an expression object
        String expression = "(size(RPF) >= 1) and (size(RPN) == size(OPN))";
        JexlExpression e = jexl.createExpression(expression);

        // populate the context
        JexlContext context = new MapContext();
        context.set("OPF", Arrays.asList("A", "B", "C", "D", "E", "F"));
        context.set("RPF", Arrays.asList("C", "F"));
        context.set("OPN", Arrays.asList("1", "2", "3"));
        context.set("RPN", Arrays.asList("1", "3"));

        // work it out
        Boolean result = (Boolean) e.evaluate(context);
        System.out.println("result=" + result);
    }
}