/*******************************************************************************
 * Copyright (c) 2013 aegif.
 *
 * This file is part of NemakiWare.
 *
 * NemakiWare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NemakiWare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with NemakiWare.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     linzhixing(https://github.com/linzhixing) - initial API and implementation
 ******************************************************************************/
package jp.aegif.nemaki.service.dao.impl;

import java.util.List;

import jp.aegif.nemaki.model.Group;
import jp.aegif.nemaki.model.User;
import jp.aegif.nemaki.service.dao.PrincipalDaoService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Dao Service for Principal(User/Group) Implementation for CouchDB
 *
 * @author linzhixing
 */
@Component
public class PrincipalDaoServiceImpl implements PrincipalDaoService {

	private static final Log log = LogFactory
			.getLog(PrincipalDaoServiceImpl.class);

	private final CacheManager cacheManager;
	private PrincipalDaoService nonCachedPrincipalDaoService;

	/**
	 *
	 */

	public PrincipalDaoServiceImpl() {
		cacheManager = CacheManager.newInstance();
		Cache userCache = new Cache("userCache", 10000, false, false, 60 * 60,
				60 * 60);
		Cache usersCache = new Cache("usersCache", 1, false, false, 60 * 60,
				60 * 60);
		Cache groupCache = new Cache("groupCache", 10000, false, false,
				60 * 60, 60 * 60);
		Cache groupsCache = new Cache("groupsCache", 1, false, false, 60 * 60,
				60 * 60);
		cacheManager.addCache(userCache);
		cacheManager.addCache(usersCache);
		cacheManager.addCache(groupCache);
		cacheManager.addCache(groupsCache);
	}

	@Override
	public User createUser(User user) {
		User created = nonCachedPrincipalDaoService.createUser(user);

		Cache userCache = cacheManager.getCache("userCache");
		userCache.put(new Element(created.getUserId(), created));
		Cache usersCache = cacheManager.getCache("usersCache");
		usersCache.removeAll();
		return created;
	}

	@Override
	public Group createGroup(Group group) {
		Group created = nonCachedPrincipalDaoService.createGroup(group);

		Cache groupCache = cacheManager.getCache("groupCache");
		groupCache.put(new Element(created.getGroupId(), created));
		Cache groupsCache = cacheManager.getCache("groupsCache");
		groupsCache.removeAll();
		return created;
	}

	@Override
	public User updateUser(User user) {
		User u = nonCachedPrincipalDaoService.updateUser(user);

		Cache userCache = cacheManager.getCache("userCache");
		userCache.put(new Element(u.getUserId(), u));
		Cache usersCache = cacheManager.getCache("usersCache");
		usersCache.removeAll();

		return u;
	}

	@Override
	public Group updateGroup(Group group) {
		Group g = nonCachedPrincipalDaoService.updateGroup(group);

		Cache groupCache = cacheManager.getCache("groupCache");
		groupCache.put(new Element(g.getGroupId(), g));

		Cache groupsCache = cacheManager.getCache("groupsCache");
		groupsCache.removeAll();

		return g;
	}

	@Override
	public void delete(Class<?> clazz, String nodeId) {
		if (clazz.equals(User.class)) {
			User exising = getUser(nodeId);

			nonCachedPrincipalDaoService.delete(null, nodeId);

			Cache userCache = cacheManager.getCache("userCache");
			userCache.remove(exising.getUserId());
			Cache usersCache = cacheManager.getCache("usersCache");
			usersCache.removeAll();
		} else if(clazz.equals(Group.class)){
			Group exising = getGroup(nodeId);

			nonCachedPrincipalDaoService.delete(null, nodeId);

			Cache groupCache = cacheManager.getCache("groupCache");
			groupCache.remove(exising.getGroupId());
			Cache groupsCache = cacheManager.getCache("groupsCache");
			groupsCache.removeAll();
		}
	}

	@Override
	public User getUser(String nodeId) {
		Cache userCache = cacheManager.getCache("userCache");

		User user = nonCachedPrincipalDaoService.getUser(nodeId);
		if (user != null) {
			userCache.put(new Element(user.getUserId(), user));
		}

		return user;
	}

	@Override
	public User getAdmin() {
		User admin = nonCachedPrincipalDaoService.getAdmin();
		return admin;
	}

	/**
	 *
	 */
	@Override
	public User getUserById(String userId) {

		Cache userCache = cacheManager.getCache("userCache");
		Element e = userCache.get(userId);

		if (e != null) {
			return (User) e.getObjectValue();
		}

		User user = nonCachedPrincipalDaoService.getUserById(userId);
		if (user != null) {
			userCache.put(new Element(userId, user));
		}

		return user;
	}

	/**
	 *
	 */
	@Override
	public List<User> getUsers() {
		Cache usersCache = cacheManager.getCache("usersCache");
		Element userEl = usersCache.get("users");

		if (userEl != null) {
			return (List<User>) userEl.getObjectValue();
		}

		List<User> users = nonCachedPrincipalDaoService.getUsers();

		usersCache.put(new Element("users", users));

		return users;

	}

	@Override
	public Group getGroup(String nodeId) {
		Cache groupCache = cacheManager.getCache("groupCache");

		Group group = nonCachedPrincipalDaoService.getGroup(nodeId);
		if (group != null) {
			groupCache.put(new Element(group.getGroupId(), group));
		}

		return group;
	}

	/**
	 *
	 */
	@Override
	public Group getGroupById(String groupId) {
		Cache groupCache = cacheManager.getCache("groupCache");
		Element groupEl = groupCache.get(groupId);

		if (groupEl != null) {
			return (Group) groupEl.getObjectValue();
		}

		Group group = nonCachedPrincipalDaoService.getGroupById(groupId);
		if (group != null) {
			groupCache.put(new Element(groupId, group));
		}

		return group;
	}

	/**
	 *
	 */
	@Override
	public List<Group> getGroups() {
		Cache groupsCache = cacheManager.getCache("groupsCache");
		Element groupEl = groupsCache.get("groups");

		if (groupEl != null) {
			return (List<Group>) groupEl.getObjectValue();
		}

		List<Group> groups = nonCachedPrincipalDaoService.getGroups();

		groupsCache.put(new Element("groups", groups));

		return groups;
	}

	public void setNonCachedPrincipalDaoService(
			PrincipalDaoService nonCachedPrincipalDaoService) {
		this.nonCachedPrincipalDaoService = nonCachedPrincipalDaoService;
	}
}
