package com.fruit.web.plugin.realm;

import com.fruit.web.model.Permission;
import com.fruit.web.model.Role;
import com.fruit.web.model.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import java.util.List;

public class ShiroDbRealm extends AuthorizingRealm {

	// @Override
	// public void setCacheManager(CacheManager cacheManager) {
	// super.setCacheManager(cacheManager);
	// // ShiroCache.setCacheManager(cacheManager);
	// }

	/**
	 * 身份证认证
	 * 
	 * @param authenticationToken
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
		String username = usernamePasswordToken.getUsername();

		User user = User.dao.getUser(username);
		SimpleAuthenticationInfo authenticationInfo = null;
		if (user != null) {
			authenticationInfo = new SimpleAuthenticationInfo(user, user.getPass(), getName());
			// authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(name
			// + user.get("salt")));
		}

		return authenticationInfo;
	}

	/**
	 * 权限认证
	 * 
	 * @param principals
	 * @return
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		User user = (User) principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		if (user == null) {
			return info;
		}
		int id = user.getId();
		authRole(id, info);
		return info;
	}

	/**
	 * 角色授权
	 * 
	 * @param id
	 * @param info
	 */
	protected void authRole(int id, SimpleAuthorizationInfo info) {
		List<Role> roleList = Role.dao.getRoleByUid(id);
		if (roleList != null && roleList.size() > 0) {
			for (Role role : roleList) {
				info.addRole(role.getRoleKey());
				authUrl(role.getId(), info);
			}
		}
	}

	/**
	 * 资源授权
	 * 
	 * @param id
	 * @param info
	 */
	protected void authUrl(int id, SimpleAuthorizationInfo info) {
		List<Permission> permissions = Permission.dao.getPermissionByRoleId(id);
		if (permissions != null && permissions.size() > 0) {
			for (Permission permission : permissions) {
				info.addStringPermission(permission.getPermissionKey());//设置角色可以访问的地址或操作
			}
		}
	}

	public void clearCacheAuth(Object principal) {
		SimplePrincipalCollection info = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthenticationInfo(info);
	}

	public void clearAllCacheAuth() {
		Cache<Object, AuthenticationInfo> cache = getAuthenticationCache();
		if (null != cache) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}
}