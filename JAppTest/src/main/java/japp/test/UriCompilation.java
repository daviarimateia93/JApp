package japp.test;

import java.util.Map;

public class UriCompilation {
    private final Boolean greedy;
    private final Boolean matched;
    private final String pattern;
    private final String compiledPattern;
    private final Map<String, String> variables;

    public UriCompilation(final Boolean greedy, final Boolean matched, final String pattern,
            final String compiledPattern, final Map<String, String> variables) {
        this.greedy = greedy;
        this.matched = matched;
        this.pattern = pattern;
        this.compiledPattern = compiledPattern;
        this.variables = variables;
    }

    public Boolean isGreedy() {
        return greedy;
    }

    public Boolean isMatched() {
        return matched;
    }

    public String getPattern() {
        return pattern;
    }

    public String getCompiledPattern() {
        return compiledPattern;
    }

    public Map<String, String> getVariables() {
        return variables;
    }
}
