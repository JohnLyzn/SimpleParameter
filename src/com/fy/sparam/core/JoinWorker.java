package com.fy.sparam.core;

import java.util.Collection;

import com.fy.sparam.util.StringUtils;

/**
 * 关联处理器器
 * 
 * @param <PT>　搜索参数类类型
 * @param <SCT>　搜索内容类类型
 * @param <RT>　搜索结果类类型
 * 
 * @author linjie
 * @since 1.0.2
 */
public final class JoinWorker<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {

	/**
	 * 关联时使用的类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static enum JoinType {
		
		/**
		 * 内关联类型
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		INNER_JOIN,
		
		/**
		 * 左外关联类型
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		LEFT_JOIN,
		
		/**
		 * 右外关联类型
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		RIGHT_JOIN;
	}
	
	/**
	 * 关联的关联字段之间的关系类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static enum RelationType {
		
		/**
		 * 关联关系使用等于
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		EQ,
		
		/**
		 * 关联关系使用不等于
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		NOT_EQ,
		
		/**
		 * 关联关系使用在集合中
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		IN,
		
		/**
		 * 关联关系使用不在集合中
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		NOT_IN;
	}
	// 是否完成关联操作处理的标志
	boolean hasJoin;
	// 关联的核心数据
	PT mappedFromParam;
	PT mappedParam;
	ParameterField<PT, SCT, RT> mappedFromField;
	ParameterField<PT, SCT, RT> mappedField;
	JoinType mappedJoinType;
	RelationType mappedRelationType;
	
	@Override
	public String toString() {
		return StringUtils.concat("from ", mappedFromField, " to ", mappedField);
	}
	
	/**
	 * 构建一个关联处理
	 * 
	 * @param fromParam 关联起点搜索参数
	 * @param toParam 关联终点搜索参数
	 * @param joinType 关联类型
	 * @param relationType 关联关系类型
	 * @param fromField 关联起点字段
	 * @param toField 关联终点字段
	 * @return 返回生成的关联处理器
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final static <PT extends AbsParameter<PT, SCT, RT>, SCT, RT> JoinWorker<PT, SCT, RT> build(
			PT fromParam, PT toParam,
			JoinType joinType, RelationType relationType,
			ParameterField<PT, SCT, RT> fromField, ParameterField<PT, SCT, RT> toField) {
		JoinWorker<PT, SCT, RT> joinWorker = new JoinWorker<PT, SCT, RT>();
		joinWorker.mappedFromParam = fromParam;
		joinWorker.mappedParam = toParam;
		joinWorker.mappedJoinType = joinType;
		joinWorker.mappedRelationType = relationType;
		joinWorker.mappedFromField = fromField;
		joinWorker.mappedField = toField;
		
		joinWorker.mappedFromField.isMappedFromField = true;
		return joinWorker;
	}
	
	/**
	 * 进行关联处理操作, 回调搜索参数中的实现
	 * 
	 * @throws Exception 处理失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void doJoinWork() throws Exception {
		if(! this.hasJoin) {
			// 如果跳着调用保证关联链中前面的搜索参数会进行关联
			JoinWorker<PT, SCT, RT> fromParamJoinWorker = this.mappedFromParam.usingJoinWorker;
			if(fromParamJoinWorker != null) {
				fromParamJoinWorker.doJoinWork();
			}
			// 设置为已经关联
			this.hasJoin = true;
			// 调用实际关联信息添加实现, 一定要是关联终点搜索参数的方法, 这样才能保证搜索内容属于该搜索参数, 方便回滚
			this.mappedParam.onJoin(this.mappedFromParam,
					this.mappedJoinType, this.mappedRelationType,
					this.mappedFromField, this.mappedField);
		}
	}
	
	/**
	 * 进行回滚关联处理操作
	 * 
	 * @param isReset 是否强制清除掉搜索参数字段使用的搜索器产生的搜索内容, 这样不会因为字段被搜索或输出而退出
	 * @param isNeedjudgeReachable 是否需要判断所有可达的搜索参数字段, 主要为内部调用不用重复判断
	 * @throws Exception 回滚失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void cancelJoinWork(boolean isReset, boolean isNeedjudgeReachable) throws Exception {
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
				if(! isReset) {
					if(paramField.isOutput || paramField.isSearched) {
						// 如果有非搜索参数类型字段输出或者被搜索了, 直接退出
						return;
					}
				} else {
					// 直接重置字段
					paramField.reset();
				}
			}
			// 清理掉对应的关联时添加的搜索内容
			SearchContext<PT, SCT, RT> searchContext = this.mappedParam.paramContext.getCurrentSearchContext();
			searchContext.removeSearchEntryBySource(this.mappedParam);
			// 还原关联标志
			this.hasJoin = false;
			// 对关联来源的搜索参数进行此操作(可能是一个隔代字段设置输出导致关联)
			if(this.mappedFromParam.usingJoinWorker != null) {
				this.mappedFromParam.usingJoinWorker.cancelJoinWork(false, false);
			}
		}
	}
	
	/**
	 * 反转关联方向, 起点变终点
	 * 
	 * @author linjie
	 * @since 1.0.2
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
	 * @since 1.0.2
	 */
	private JoinWorker() {};
}
