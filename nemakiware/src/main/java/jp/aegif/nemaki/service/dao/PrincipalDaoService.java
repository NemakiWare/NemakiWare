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
package jp.aegif.nemaki.service.dao;

import java.util.List;

import jp.aegif.nemaki.model.Group;
import jp.aegif.nemaki.model.User;

/**
 * DAO Service interface.
 */
public interface PrincipalDaoService {
	User getUser(String nodeId);

	/**
	 * Get a user by userId(It's supposed to be unique)
	 * @param userId
	 * @return
	 */
	User getUserById(String userId);

	/**
	 * Get a users
	 * @return
	 */
	List<User> getUsers();

	/**
	 * Get Admin user
	 * @return
	 */
	User getAdmin();

	/**
	 * Get a group
	 * @param nodeId
	 * @return
	 */
	Group getGroup(String nodeId);

	/**
	 * Get a group by groupId(It's supposed to be unique)
	 * @param groupId
	 * @return
	 */
	Group getGroupById(String groupId);

	/**
	 * Get all the groups
	 * @return
	 */
	List<Group> getGroups();

	/**
	 * Create a user
	 * @param user
	 * @return newly created user
	 */
	User createUser(User user);

	/**
	 * Create a group
	 * @param group
	 * @return newly created group
	 */
	Group createGroup(Group group);

	/**
	 * Update a user
	 * @param user
	 * @return updated user
	 */
	User updateUser(User user);

	/**
	 * Update a group
	 * @param group
	 * @return updated group
	 */
	Group updateGroup(Group group);

	/**
	 * Delete a user / group
	 * @param clazz
	 * @param id
	 */
	void delete(Class<?> clazz, String nodeId);
}