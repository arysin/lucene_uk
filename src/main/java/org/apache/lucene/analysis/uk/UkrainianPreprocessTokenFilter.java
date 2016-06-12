package org.apache.lucene.analysis.uk;

import java.io.IOException;
import java.util.function.IntPredicate;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
* A TokenFilter which replaces Unicode apostrophes with a straight one 
* and removes stress character.
*/
public class UkrainianPreprocessTokenFilter extends TokenFilter {
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	
	public UkrainianPreprocessTokenFilter(TokenStream input) {
		super(input);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (!input.incrementToken())
			return false;

		if (termAtt.chars().anyMatch(new UnicodeApostrophePredicate())) {
			String transformed = termAtt.toString().replace('\u2019', '\'').replace('\u02BC', '\'');
			termAtt.setEmpty().append(transformed);
		}
		if (termAtt.chars().anyMatch(new StressCharPredicate())) {
			String transformed = termAtt.toString().replace("\u0301", "");
			termAtt.setEmpty().append(transformed);
		}

		return true;
	}

	private final static class UnicodeApostrophePredicate implements IntPredicate {
		@Override
		public boolean test(int value) {
			return value == '\u2019' || value == '\u02BC';
		}
	}

	private final static class StressCharPredicate implements IntPredicate {
		@Override
		public boolean test(int value) {
			return value == '\u0301';
		}
	}
}