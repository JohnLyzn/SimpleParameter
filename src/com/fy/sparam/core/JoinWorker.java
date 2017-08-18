package com.fy.sparam.core;

/**
 * 关联关系连接器
 * 
 * @param <PT>　搜索参数类类型
 * 
 * @author linjie
 * @since 1.0.1
 */
public final class JoinWorker<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {

	/**
	 * 连接时使用的类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static enum JoinType {
		
		/**
		 * 内连接类型
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		INNER_JOIN,
		
		/**
		 * 左外连接类型
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		LEFT_JOIN,
		
		/**
		 * 右外连接类型
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		RIGHT_JOIN;
	}
	
	/**
	 * 关联的关联字段之间的关系类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static enum RelationType {
		
		/**
		 * 连接条件使用等于
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		EQ,
		
		/**
		 * 连接条件使用不等于
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		NOT_EQ,
		
		/**
		 * 连接条件使用在集合中
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		IN,
		
		/**
		 * 连接条件使用不在集合中
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		NOT_IN;
	}
	
	boolean hasJoin;
	JoinType mappedJoinType;
	RelationType mappedRelationType;
	ParameterField<PT, SCT, RT> mappedField;
	ParameterField<PT, SCT, RT> mappedFromField;
	PT mappedParam;
	PT mappedFromParam;

	SearchContext<PT, SCT, RT> usingSearchContext;
	
	/**
	 * 
	 * @param paramType
	 * @param param
	 * @param joinType
	 * @param relationType
	 * @param from
	 * @param to
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	static <PT extends AbsParameter<PT, SCT, RT>, SCT, RT> JoinWorker<PT, SCT, RT> build(PT fromParam, PT toParam,
			JoinType joinType, RelationType relationType,
			ParameterField<PT, SCT, RT> fromField, ParameterField<PT, SCT, RT> toField) {
		JoinWorker<PT, SCT, RT> joinWorker = new JoinWorker<PT, SCT, RT>();
		joinWorker.mappedFromParam = fromParam;
		joinWorker.mappedParam = toParam;
		joinWorker.mappedJoinType = joinType;
		joinWorker.mappedRelationType = relationType;
		joinWorker.mappedFromField = fromField;
		joinWorker.mappedField = toField;
		return joinWorker;
	}
	
	/**
	 * 
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void doJoinWork() throws Exception {
		if(! this.hasJoin) {
			// 调用实际关联信息添加实现
			this.mappedFromParam.onJoin(this.mappedParam,
					this.mappedJoinType, this.mappedRelationType,
					this.mappedFromField, this.mappedField);
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void rollBackJoinWork() throws Exception {
		// 字段被搜索不能回滚
	}
	
	/**
	 * 
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void reset() throws Exception {
		
	}
	
	/**
	 * 不允许直接new
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private JoinWorker() {};
}
