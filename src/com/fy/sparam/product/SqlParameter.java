package com.fy.sparam.product;

import com.fy.sparam.core.AbsParameter;
import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;
import com.fy.sparam.core.ParameterField;
import com.fy.sparam.init.anno.AnnotationInitializor;

/**
 * 
 * 
 * @author linjie
 * @since 1.0.1
 */
public class SqlParameter extends AbsParameter<SqlParameter, SqlPiece, SqlResult> {

	/**
	 * 最小的页码
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static final int MIN_PAGE = 1;
	
	/**
	 * 最大的搜索数量
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static final int MAX_COUNT = 500;
	
	/**
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private static AnnotationInitializor<SqlParameter, SqlPiece, SqlResult> annoInitializor
		= new AnnotationInitializor<SqlParameter, SqlPiece, SqlResult>(BaseConfig.PARAM_TRANSLATORS);

	public void test() {
		for(ParameterField<SqlParameter, SqlPiece, SqlResult> pf : this.getParameterContext().getAllParameterFields()) {
			System.out.println(pf.getDbTableAliasLocateFieldName());
		}
	}
	
	@Override
	public void setCount(int count) {
		if(count < 0 || count > MAX_COUNT) {
			count = MAX_COUNT;
		}
		super.setCount(count);
	}
	
	@Override
	public void setPage(int page) {
		if(page < MIN_PAGE) {
			page = MIN_PAGE;
		}
		super.setPage(page);
	}
	
	/**
	 * 
	 * @param paramClazz
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static <SPT extends SqlParameter> SPT getParameter(Class<SPT> paramClazz) throws Exception {
		SPT param = paramClazz.newInstance();
		param.init(annoInitializor, SqlSearcher.class, SqlParameter.class, null, null);
		return param;
	}
	
	@Override
	protected void onJoin(SqlParameter joinParam, JoinType joinType, RelationType relationType,
			ParameterField<SqlParameter, SqlPiece, SqlResult> from,
			ParameterField<SqlParameter, SqlPiece, SqlResult> to) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBuild(Object... args) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
