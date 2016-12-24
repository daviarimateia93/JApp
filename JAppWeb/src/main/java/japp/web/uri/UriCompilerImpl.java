package japp.web.uri;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import japp.util.JAppRuntimeException;
import japp.util.SingletonFactory;
import japp.util.Singletonable;

public class UriCompilerImpl implements Singletonable, UriCompiler {
	
	public static final String PATTERN_VARIABLE = "\\{(.+?)\\}";
	
	public static synchronized UriCompilerImpl getInstance() {
		return SingletonFactory.getInstance(UriCompilerImpl.class);
	}
	
	protected UriCompilerImpl() {
		
	}
	
	public String removeVariables(final String uriPattern) {
		return uriPattern.replaceAll(PATTERN_VARIABLE, "");
	}
	
	@Override
	public UriCompilation compile(final String uriPattern, final String uriValue) {
		try {
			final Pattern pattern = Pattern.compile(PATTERN_VARIABLE);
			final Matcher patternMatcher = pattern.matcher(uriPattern);
			final Pattern compiledPattern = Pattern.compile("^" + uriPattern.replaceAll(pattern.toString(), "([a-zA-Z0-9\\\\\\%\\\\\\-\\\\\\.\\\\\\_\\\\\\~\\\\\\:\\\\\\/\\\\\\?\\\\\\#\\\\\\[\\\\\\]\\\\\\@\\\\\\!\\\\\\$\\\\\\&\\\\\\'\\\\\\(\\\\\\)\\\\\\*\\\\\\+\\\\\\,\\\\\\;\\\\\\=]*?)") + "$", Pattern.CASE_INSENSITIVE);
			final Matcher compiledPatternMatcher = compiledPattern.matcher(uriValue);
			final Float score = (float) uriPattern.replaceAll(PATTERN_VARIABLE, "").length();
			final Boolean valid = compiledPatternMatcher.find();
			final Map<String, String> variables = new HashMap<>();
			
			if (valid) {
				int compiledPatternMatcherGroup = 1;
				
				while (patternMatcher.find()) {
					if (patternMatcher.groupCount() > 0) {
						variables.put(patternMatcher.group(1), compiledPatternMatcher.group(compiledPatternMatcherGroup++));
					}
					
					// it was bugging, i was always getting group 1
					// now we are going to find once, and do it by a counter
					// compiledPatternMatcher.find();
				}
			}
			
			return new UriCompilation(score, valid, uriPattern, compiledPattern.toString(), variables);
		} catch (final RuntimeException exception) {
			throw new JAppRuntimeException(String.format("Was not possible to compile %s - %s", uriPattern, uriValue), exception);
		}
	}
}
