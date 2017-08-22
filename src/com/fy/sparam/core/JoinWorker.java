package com.fy.sparam.core;

import java.util.Collection;

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
	final static <PT extends AbsParameter<PT, SCT, RT>, SCT, RT> JoinWorker<PT, SCT, RT> build(PT fromParam, PT toParam,
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
	final void doJoinWork() throws Exception {
		if(! this.hasJoin) {
			// 如果跳着调用保证连接链中前面的搜索参数会进行连接
			JoinWorker<PT, SCT, RT> fromParamJoinWorker = this.mappedFromParam.usingJoinWorker;
			if(fromParamJoinWorker != null) {
				fromParamJoinWorker.doJoinWork();
			}
			// 设置为已经连接
			this.hasJoin = true;
			// 调用实际关联信息添加实现, 一定要是关联终点搜索参数的方法, 这样才能保证搜索内容属于该搜索参数, 方便回滚
			this.mappedParam.onJoin(this.mappedFromParam,
					this.mappedJoinType, this.mappedRelationType,
					this.mappedFromField, this.mappedField);
		}
	}
	
	/**
	 * 
	 * @param isNeedjudgeReachable
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final void cancelJoinWork(boolean isNeedjudgeReachable) throws Exception {
		if(this.hasJoin) {
			// 遍历一遍搜索参数字段没有输出且没有被搜索, 则需要重置关联信息
			Collection<ParameterField<PT, SCT, RT>> judgeParamFields = null;
			if(isNeedjudgeReachable) {
				// 需要遍历搜索中的搜索参数及其后代搜索参数所包含的所有搜索参数字段
				judgeParamFields = this.mappedParam.paramContext.getReachableParameterFieldsWithStartParam(this.mappedParam);
			} else {
				// 只需要遍历当前关联终点搜索参数自身包含的搜索参数字段(不包括继承的), 主要在关联来源尝试进行回滚时减少重复的获取
				judgeParamFields = this.mappedParam.myParameterFields.values();
			}
			for(ParameterField<PT, SCT, RT> paramField : judgeParamFields) {
				if(paramField.isOutput || paramField.isSearched) {
					// 如果有非搜索参数类型字段输出或者被搜索了, 直接退出
					return;
				}
			}
			// 清理掉对应的关联时添加的搜索内容
			SearchContext<PT, SCT, RT> searchContext = this.mappedParam.paramContext.getCurrentSearchContext();
			searchContext.removeSearchEntryBySource(this.mappedParam);
			// 还原关联标志
			this.hasJoin = false;
			// 对关联来源的搜索参数进行此操作(可能是一个隔代字段设置输出导致连接)
			if(this.mappedFromParam.usingJoinWorker != null) {
				this.mappedFromParam.usingJoinWorker.cancelJoinWork(false);
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final void reverseJoinRelation() {
		// 原来的被关联字段和关联字段 变为 当前的关联字段和被关联字段(交换位置)
		ParameterField<PT, SCT, RT> tmpMappedFromField = this.mappedFromField;
		PT tmpMappedFromParam = this.mappedFromParam;
		this.mappedFromParam = this.mappedParam;
		this.mappedParam = tmpMappedFromParam;
		this.mappedFromField = this.mappedField;
		this.mappedField = tmpMappedFromField;
		this.mappedFromField.isMappedFromField = true;
		this.mappedField.isMappedFromField = false;
	}
	
	/**
	 * 不允许直接new
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private JoinWorker() {};
}
