package com.fy.sparam.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fy.sparam.core.SearchContext.ISearchable;

public class SearchExpressionsCompiler {
	
	public static final <PT extends AbsParameter<PT, SCT, RT>, SCT, RT>
	CompileResult<PT, SCT, RT> compile(String expressions, String...symbols) {
		SearchExpressionsCompiler compiler = getInstance();
		return compiler.handleCompile(expressions, symbols);
	}
	
	public static SearchExpressionsCompiler getInstance() {
		return new SearchExpressionsCompiler();
	}
	
	protected <PT extends AbsParameter<PT, SCT, RT>, SCT, RT> 
	CompileResult<PT, SCT, RT> handleCompile(String expressionsStr, String[] symbols) {
		Pattern pattern = Pattern.compile("(AND|OR)?[\\s(]*\\{[^\\s]+\\}\\:[^\\s]+");
		Matcher matcher = pattern.matcher(expressionsStr);
		List<SearchExpression> expressions = new LinkedList<SearchExpression>();
		while(matcher.find()) {
			String expressionStr = expressionsStr.substring(matcher.start(), matcher.end());
			SearchExpression expression = this.compileSingleExpression(expressionStr, symbols);
			expressions.add(expression);
		}
		if(expressions.isEmpty()) {
			return null;
		}
		return new CompileResult<PT, SCT, RT>(expressions);
	}
	
	private SearchExpression compileSingleExpression(String expressionStr, String[] symbols) {
		String handlingExpressionStr = expressionStr;
		SearchExpression expression = new SearchExpression();
		// 获取连接关系
		boolean isAnd = true;
		if(handlingExpressionStr.startsWith("OR")) {
			isAnd = false;
		}
		expression.setAnd(isAnd);
		handlingExpressionStr = handlingExpressionStr.replaceAll("AND|OR", "").trim();
		// 处理前面括号
		while(handlingExpressionStr.startsWith("(")) {
			expression.increaseDsCount();
			handlingExpressionStr = handlingExpressionStr.substring(1, handlingExpressionStr.length()).trim();
		}
		// 获取搜索字段和搜索方法
		String[] contents = handlingExpressionStr.split(":");
		// 处理搜索字段
		String searchFieldPath = contents[0];
		searchFieldPath = searchFieldPath.replaceAll("\\{|\\}", "");
		for(String symbol : symbols) {
			if(searchFieldPath.contains(symbol)) {
				expression.addSymbol(symbol);
				searchFieldPath = searchFieldPath.replace(symbol, "");
			}
		}
		expression.setSearchFieldPath(searchFieldPath);
		handlingExpressionStr = handlingExpressionStr.replace(searchFieldPath, "").trim();
		// 处理操作
		String optExpression = contents[1];
		int paramsDsIndex = optExpression.indexOf("(");
		int paramsDeIndex = optExpression.indexOf(")", paramsDsIndex);
		if(paramsDsIndex != -1 && paramsDeIndex != -1) { /* eq(1)) */
			String searchMethod = optExpression.substring(0, paramsDsIndex);
			String searchParams = optExpression.substring(paramsDsIndex + 1, paramsDeIndex);
			expression.setSearchMethod(searchMethod);
			expression.setSearchParams(searchParams);
			optExpression = optExpression.substring(0, paramsDeIndex + 1);
		} else if(paramsDeIndex != -1) { /* isNull) */
			String searchMethod = optExpression.substring(0, paramsDeIndex);
			expression.setSearchMethod(searchMethod);
			optExpression = optExpression.substring(0, paramsDeIndex);
		} else { /* isNull */
			expression.setSearchMethod(optExpression);
		}
		handlingExpressionStr = handlingExpressionStr.replace(optExpression, "").trim();
		// 处理后面的括号
		while(handlingExpressionStr.endsWith(")")) {
			expression.increaseDeCount();
			handlingExpressionStr = handlingExpressionStr.substring(0, handlingExpressionStr.length() - 1);
		}
		return expression;
	}
	
	public static void main(String[] args) throws Exception {
		CompileResult<?, ?, ?> result = SearchExpressionsCompiler.compile(
				"{$TO.parentBtnID}:isNull AND ({$FROM.some}:isNotNull OR {$TO.bar}:eq(123) AND ({$TO.bar}:eq(123)))",
				"$TO.", "$FROM.");
		Collection<SearchExpression> exprssions = result.getExpressions();
		System.out.println(exprssions);
	}
	
	/**
	 * 
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static class CompileResult<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {
		
		/**
		 * 
		 * 
		 */
		private final Collection<SearchExpression> expressions;
		
		public CompileResult(Collection<SearchExpression> expressions) {
			this.expressions = expressions;
		}
		
		public Collection<SearchExpression> getExpressions() throws Exception {
			return Collections.unmodifiableCollection(this.expressions);
		}
		
		/**
		 * 
		 * @param searcher
		 * @throws Exception
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		public void doSearch(AbsSearcher<?,?,?,?> searcher, SearchExpression expression) throws Exception {
			if(expression == null) {
				return;
			}
			searcher.belongParameter.paramContext.getCurrentSearchContext().setSkipFirstRelation(false);
			SearchHandler handler = SearchHandler.valueOf(expression.getSearchMethod());
			if(expression.isAnd()) {
				searcher.belongParameter.above().and(null);
			} else {
				searcher.belongParameter.above().or(null);
			}
			for(int i = 0; i < expression.getDsCount(); i ++) {
				searcher.ds(null);
			}
			handler.getInstance().doSearch(searcher, expression);
			for(int i = 0; i < expression.getDeCount(); i ++) {
				searcher.de();
			}
		}
	}
	
	public static final class SearchExpression {
		
		private boolean isAnd;
		
		private String searchFieldPath;
		
		private String searchMethod;
		
		private String searchParams;
		
		private int dsCount;
		
		private int deCount;
		
		private Set<String> symbols = new HashSet<String>();
		
		public boolean isAnd() {
			return isAnd;
		}
		
		public void setAnd(boolean isAnd) {
			this.isAnd = isAnd;
		}

		public String getSearchFieldPath() {
			return searchFieldPath;
		}

		public void setSearchFieldPath(String searchFieldPath) {
			this.searchFieldPath = searchFieldPath;
		}

		public String getSearchMethod() {
			return searchMethod;
		}

		public void setSearchMethod(String searchMethod) {
			this.searchMethod = searchMethod;
		}

		public String getSearchParams() {
			return searchParams;
		}

		public void setSearchParams(String searchParams) {
			this.searchParams = searchParams;
		}

		public void addSymbol(String symbol) {
			symbols.add(symbol);
		}
		
		public boolean containsSymbol(String symbol) {
			return symbols.contains(symbol);
		}
		
		public int getDsCount() {
			return dsCount;
		}
		
		public void increaseDsCount() {
			dsCount ++;
		}
		
		public int getDeCount() {
			return deCount;
		}
		
		public void increaseDeCount() {
			deCount ++;
		}
		
		@Override
		public String toString() {
			return "[" + dsCount + "{" 
					+ searchFieldPath + ":" + searchMethod 
					+ "(" + searchParams + ")}" + deCount +"]";
		}
	}
	
	private interface ISearchHandler {
		
		<T> void doSearch(ISearchable<T> searcher, SearchExpression paramsStr) throws Exception;
	}
	
	private enum SearchHandler {
		eq(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				T arg = searcher.getTransformer().strToTarget(paramsStr);
				searcher.eq(arg);
			}
		}),
		notEq(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				T arg = searcher.getTransformer().strToTarget(paramsStr);
				searcher.eq(arg);
			}
		}),
		in(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				String[] elements = paramsStr.split(",");
				List<T> args = new ArrayList<T>(elements.length);
				for(String element : elements) {
					T arg = searcher.getTransformer().strToTarget(element);
					args.add(arg);
				}
				searcher.in(args);
			}
		}),
		notIn(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				String[] elements = paramsStr.split(",");
				List<T> args = new ArrayList<T>(elements.length);
				for(String element : elements) {
					T arg = searcher.getTransformer().strToTarget(element);
					args.add(arg);
				}
				searcher.notIn(args);
			}
		}),
		between(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				String[] elements = paramsStr.split(",");
				T from = searcher.getTransformer().strToTarget(elements[0]);
				T to = searcher.getTransformer().strToTarget(elements[1]);
				searcher.between(from, to);
			}
		}),
		lessThan(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				T arg = searcher.getTransformer().strToTarget(paramsStr);
				searcher.lessThan(arg);
			}
		}),
		notLessThan(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				T arg = searcher.getTransformer().strToTarget(paramsStr);
				searcher.notLessThan(arg);
			}
		}),
		greaterThan(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				T arg = searcher.getTransformer().strToTarget(paramsStr);
				searcher.greaterThan(arg);
			}
		}),
		notGreaterThan(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				String paramsStr = expression.getSearchParams();
				T arg = searcher.getTransformer().strToTarget(paramsStr);
				searcher.notGreaterThan(arg);
			}
		}),
		like(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				searcher.like(expression.getSearchParams());
			}
		}),
		notLike(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				searcher.notLike(expression.getSearchParams());
			}
		}),
		isNull(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				searcher.isNull();
			}
		}),
		isNotNull(new ISearchHandler() {
			@Override
			public <T> void doSearch(ISearchable<T> searcher, SearchExpression expression) throws Exception {
				searcher.isNotNull();
			}
		});
		
		private final ISearchHandler instance;
		
		private SearchHandler(ISearchHandler instance) {
			this.instance = instance;
		}
		
		public ISearchHandler getInstance() {
			return instance;
		}
	}
}