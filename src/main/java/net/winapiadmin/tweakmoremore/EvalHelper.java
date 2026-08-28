package net.winapiadmin.tweakmoremore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public final class EvalHelper {
    private static final Map<String, Expression> CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> LAST_BAD = new ConcurrentHashMap<>();

    private EvalHelper() {}

    public static Expression compile(String formula, String... variables) { return CACHE.computeIfAbsent(formula, f -> new ExpressionBuilder(f).variables(variables).build()); }

    public static Expression compile(String formula, Function[] functions, String... variables) {
        String key = formula + "|" + functions.length;
        return CACHE.computeIfAbsent(key, f -> {
            ExpressionBuilder b = new ExpressionBuilder(formula);
            for (Function fn : functions)
                b.functions(fn);
            b.variables(variables);
            return b.build();
        });
    }

    public static double evaluateDouble(String key, String formula, Map<String, Double> variables, double fallback) {
        try {
            Expression expr = compile(formula, variables.keySet().toArray(new String[0]));
            for (Map.Entry<String, Double> e : variables.entrySet()) {
                expr.setVariable(e.getKey(), e.getValue());
            }
            double result = expr.evaluate();
            if (!Double.isFinite(result))
                throw new RuntimeException();
            return result;
        } catch (Exception e) {
            String last = LAST_BAD.get(key);
            if (!formula.equals(last)) {
                Main.LOGGER.warn("Invalid formula for '{}': '{}'. Reverting to default.", key, formula);
                LAST_BAD.put(key, formula);
            }
            return fallback;
        }
    }

    public static int evaluateInt(String key, String formula, Map<String, Double> variables, int fallback) { return (int)Math.round(evaluateDouble(key, formula, variables, fallback)); }
}
