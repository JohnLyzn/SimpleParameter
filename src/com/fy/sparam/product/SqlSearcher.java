package com.fy.sparam.product;

import java.util.Collection;

import com.fy.sparam.core.AbsSearcher;
import com.fy.sparam.core.SearchContext.ISearchable;

public class SqlSearcher<T> extends AbsSearcher<SqlParameter, SqlPiece, SqlResult, T> {

	@Override
	protected void onEq(T value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onEq(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotEq(T value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotEq(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onIn(Collection<T> values) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onIn(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotIn(Collection<T> values) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotIn(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBetween(T from, T to) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBetween(ISearchable<?> from, ISearchable<?> to) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onLessThan(T value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onLessThan(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotLessThan(T value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotLessThan(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onGreaterThan(T value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onGreaterThan(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotGreaterThan(T value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotGreaterThan(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onLike(String value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onLike(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotLike(String value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotLike(ISearchable<?> searchField) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onIsNull() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onIsNotNull() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onInChildQuery(SqlParameter childQuery) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNotInChildQuery(SqlParameter childQuery) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onAnd() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onOr() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDelimiterStart(Object... params) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDelimiterEnd(Object... params) throws Exception {
		// TODO Auto-generated method stub
		
	}

	

}
