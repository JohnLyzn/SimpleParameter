package com.fy.sparam.product;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fy.sparam.core.SearchContext.ISearchable;

/**
 * 搜索参数辅助类
 * <br/> 标记额外获取/更新/删除哪些内容的辅助构建器类
 * <br/> 根据搜索参数的条件允许一条语句额外获取/批量更新/批量删除.
 * 
 * @author linjie
 * @since 4.5.0
 */
public class SqlParamMarker {

	/**
	 * 标记搜索参数中哪些搜索参数字段需要更新
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private Map<ISearchable<?>, Object> updateContents;
	
	/**
	 * 标记搜索参数中哪些表的记录需要删除
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private List<SqlParameter> deleteContents;
	
	/**
	 * 标记搜索参数中可以找到的搜索字段进行内容更新
	 * <br/> 会自动设置该更新的搜索字段输出.
	 * <br/> 与{@link IBaseDao #updateByParam(SqlParameter, SqlParamMarker)}配合使用.
	 * 
	 * @param paramField 要更新的搜索参数字段
	 * @param setValue 需要设置的实际的值, <strong>注意是传入存储到数据库实际的值</strong>
	 * @return 当前类的实例, 形成链式引用
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public final SqlParamMarker markUpdate(ISearchable<?> paramField, Object setValue) throws Exception {
		if(updateContents == null) {
			updateContents = new HashMap<ISearchable<?>, Object>();
		}
		paramField.setOutput(true);
		updateContents.put(paramField, setValue);
		return this;
	}
	
	/**
	 * 标记搜索参数中可以找到的搜索参数进行删除对应表的记录
	 * <br/> 会自动设置该删除搜索参数中的所有搜索字段输出.
	 * <br/> 默认删除入口搜索参数对应的表记录.
	 * <br/> 与{@link IBaseDao #deleteByParam(SqlParameter, SqlParamMarker)}配合使用.
	 * 
	 * @param param 要删除记录的表对应的搜索参数
	 * @return 当前类的实例, 形成链式引用
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public final SqlParamMarker markDelete(SqlParameter param) throws Exception {
		if(deleteContents == null) {
			deleteContents = new LinkedList<SqlParameter>();
		}
		param.setAllMyFieldOutput(true);
		deleteContents.add(param);
		return this;
	}
	
	/**
	 * 重用时进行数据的重置
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public final void reset() {
		if(this.updateContents != null) {
			this.updateContents.clear();
		}
		if(this.deleteContents != null) {
			this.deleteContents.clear();
		}
	}
	
	/**
	 * 获取需要更新的搜索字段标记内容
	 * 
	 * @return 需要更新的搜索参数的字段标记内容
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public final Map<ISearchable<?>, Object> getUpdateContents() {
		return updateContents;
	}
	
	/**
	 * 获取需要删除的搜索参数标记内容
	 * @return 需要删除的搜索参数标记内容
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public final List<SqlParameter> getDeleteContents() {
		return deleteContents;
	}
}
