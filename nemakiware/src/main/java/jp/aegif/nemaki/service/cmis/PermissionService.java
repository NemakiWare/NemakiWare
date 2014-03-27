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
package jp.aegif.nemaki.service.cmis;

import java.util.List;

import jp.aegif.nemaki.model.Acl;
import jp.aegif.nemaki.model.Content;

import org.apache.chemistry.opencmis.commons.server.CallContext;

/**
 * Permission Service interface.
 */
public interface PermissionService {

	public static final String CMIS_READ_PERMISSION = "cmis:read";
	public static final String CMIS_WRITE_PERMISSION = "cmis:write";
	public static final String CMIS_ALL_PERMISSION = "cmis:all";

	/**
	 * permissionDenied Exception check 
	 * @param content TODO
	 * @return
	 */
	public Boolean checkPermission(CallContext context, String key, Acl acl, String baseObjectType, Content content);
	
	/**
	 * 
	 * @param callContext
	 * @param contents
	 * @return
	 */
	public <T> List<T> getFiltered(CallContext callContext,List<T>contents);
}
