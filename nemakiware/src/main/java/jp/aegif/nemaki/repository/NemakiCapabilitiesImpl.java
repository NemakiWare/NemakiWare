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
package jp.aegif.nemaki.repository;

import java.util.HashSet;
import java.util.Set;

import org.apache.chemistry.opencmis.commons.enums.CapabilityAcl;
import org.apache.chemistry.opencmis.commons.enums.CapabilityChanges;
import org.apache.chemistry.opencmis.commons.enums.CapabilityContentStreamUpdates;
import org.apache.chemistry.opencmis.commons.enums.CapabilityJoin;
import org.apache.chemistry.opencmis.commons.enums.CapabilityOrderBy;
import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;
import org.apache.chemistry.opencmis.commons.enums.CapabilityRenditions;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CreatablePropertyTypesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.NewTypeSettableAttributesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryCapabilitiesImpl;

public class NemakiCapabilitiesImpl extends RepositoryCapabilitiesImpl {

	private static final long serialVersionUID = -7037495456587139344L;

	public void setup() {
		// Navigation Capabilities
		setSupportsGetDescendants(true);
		setSupportsGetFolderTree(true);
		setOrderByCapability(CapabilityOrderBy.NONE);
		
		// Object Capabilities
		setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.ANYTIME);
		setCapabilityChanges(CapabilityChanges.NONE);
		setCapabilityRendition(CapabilityRenditions.NONE);

		// Filling Capabilities
		setSupportsMultifiling(false);
		setSupportsUnfiling(false);
		setSupportsVersionSpecificFiling(false);

		// Versioning Capabilities
		setIsPwcUpdatable(true);
		setIsPwcSearchable(false);
		setAllVersionsSearchable(false);

		// Query Capabilities
		setCapabilityQuery(CapabilityQuery.BOTHCOMBINED);
		setCapabilityJoin(CapabilityJoin.NONE);

		// Changes Capabilities
		setCapabilityChanges(CapabilityChanges.OBJECTIDSONLY);
		
		// ACL Capabilities
		setCapabilityAcl(CapabilityAcl.MANAGE);
		
		//Type Mutability
		CreatablePropertyTypesImpl creatablePropertyTypes = new CreatablePropertyTypesImpl();
		Set<PropertyType> propertyTypes = new HashSet<PropertyType>();
		propertyTypes.add(PropertyType.BOOLEAN);
		propertyTypes.add(PropertyType.DATETIME);
		propertyTypes.add(PropertyType.DECIMAL);
		propertyTypes.add(PropertyType.HTML);
		propertyTypes.add(PropertyType.ID);
		propertyTypes.add(PropertyType.INTEGER);
		propertyTypes.add(PropertyType.STRING);
		propertyTypes.add(PropertyType.URI);
		creatablePropertyTypes.setCanCreate(propertyTypes);
		setCreatablePropertyTypes(creatablePropertyTypes);
		
		NewTypeSettableAttributesImpl newTypeSetableAttributes = new NewTypeSettableAttributesImpl();
		newTypeSetableAttributes.setCanSetControllableAcl(false);
		newTypeSetableAttributes.setCanSetControllablePolicy(false);
		newTypeSetableAttributes.setCanSetCreatable(true);
		newTypeSetableAttributes.setCanSetDescription(true);
		newTypeSetableAttributes.setCanSetDisplayName(true);
		newTypeSetableAttributes.setCanSetFileable(false);
		newTypeSetableAttributes.setCanSetFulltextIndexed(true);
		newTypeSetableAttributes.setCanSetId(true);
		newTypeSetableAttributes.setCanSetIncludedInSupertypeQuery(true);
		newTypeSetableAttributes.setCanSetLocalName(true);
		newTypeSetableAttributes.setCanSetLocalNamespace(true);
		newTypeSetableAttributes.setCanSetQueryable(true);
		newTypeSetableAttributes.setCanSetQueryName(true);
		setNewTypeSettableAttributes(newTypeSetableAttributes);
	}
}
