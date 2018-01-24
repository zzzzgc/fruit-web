package com.fruit.web.model;

import com.fruit.web.model.base.BaseParam;

import java.util.Date;

/**
 * @author ZGC
 * @date Created in 16:34 2018/1/23
 */
@SuppressWarnings("serial")
public class Param extends BaseParam<Param> {
	
	public static final Param dao = new Param().dao();

	/**
	 * 根据节点id获取存入的参数
	 * @param nodeId 节点id
	 * @return 参数
	 */
	public String getNodeParam(Integer nodeId){
		Param param = dao.findById(nodeId);
		return param.getParam();
	}

	/**
	 * 常用的获取秘钥的方式
	 * @param key 约定key
	 * @return key 对应的参数
	 */
	public String getParam(String key){
		String sql = "SELECT p.param FROM a_param AS p WHERE p.`key` = ?";
		return dao.findFirst(sql,key).getParam();
	}

	/**
	 * @param key 键
	 * @return id 对应的id
	 */
	private Integer getSupIdBykey(String key){
		String sql = "SELECT p.id FROM a_param AS p WHERE p.`key` = ?";
		return dao.findFirst(sql,key).getId();
	}

	/**
	 * @param paramName paramName 参数名
	 * @return id 对应的id
	 */
	private Integer getSupIdByParamName(String paramName){
		String sql = "SELECT p.id FROM a_param AS p WHERE p.paramName = ?";
		return dao.findFirst(sql,paramName).getId();
	}

	/**
	 * 添加Param
	 * @param supParamName 父节点名
	 * @param paramName 参数名
	 * @param param 参数
	 * @param key 键
	 * @param describe 描述信息
	 */
	public void createParam(String supParamName,String paramName,String param,String key,String describe){
		createParam(getSupIdByParamName(supParamName), paramName, param, key, describe);
	}

	/**
	 * 添加目录
	 * @param paramName 参数名
	 */
	public void createFolder(String paramName){
		createFolder(paramName,null);
	}

	/**
	 * 添加目录
	 * @param paramName 目录名
	 * @param describe 描述信息
	 */
	public void createFolder(String paramName,String describe){
		createParam(0,paramName,null,null,describe);
	}

	/**
	 * 添加Param(主)
	 * @param beforeNodeId 父节点id
	 * @param paramName 参数名
	 * @param param 参数
	 * @param key 键
	 * @param describe 描述信息
	 */
	public void createParam(Integer beforeNodeId,String paramName,String param,String key,String describe){
		Param saveParam = new Param();
		saveParam.setBeforeNodeId(beforeNodeId);
		saveParam.setParamName(paramName);
		saveParam.setParam(param);
		saveParam.setKey(key);
		saveParam.setDescribe(describe);
		saveParam.setCreateTime(new Date());
		saveParam.setUpdateTime(new Date());
		saveParam.save();
	}

	public void setParam(String key,String param){
		setParam(getSupIdBykey(key),param);
	}

	/**
	 * 修改内容
	 * @param nodeId 节点id
	 * @param param 参数
	 */
	private void setParam(Integer nodeId,String param){
		dao.findById(nodeId).set("param",param).update();
	}

}
