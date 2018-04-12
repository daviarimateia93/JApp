package japp.web.uri;

import java.util.Collections;
import java.util.Map;

public class UriCompilation {
    private final Float score;
    private final Boolean valid;
    private final String pattern;
    private final String compiledPattern;
    private final Map<String, String> variables;

    public UriCompilation(final UriCompilation uriCompilation) {
        this(uriCompilation.getScore(), uriCompilation.isValid(), uriCompilation.getPattern(),
                uriCompilation.getCompiledPattern(), uriCompilation.getVariables());
    }

    public UriCompilation(final Float score, final Boolean valid, final String pattern, final String compiledPattern,
            final Map<String, String> variables) {
        this.score = score;
        this.valid = valid;
        this.pattern = pattern;
        this.compiledPattern = compiledPattern;
        this.variables = variables;
    }

    public Float getScore() {
        return score;
    }

    public Boolean isValid() {
        return valid;
    }

    public String getPattern() {
        return pattern;
    }

    public String getCompiledPattern() {
        return compiledPattern;
    }

    public Map<String, String> getVariables() {
        return variables != null ? Collections.unmodifiableMap(variables) : variables;
    }
}
