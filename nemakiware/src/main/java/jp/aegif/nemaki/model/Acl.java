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
 *     linzhixing - initial API and implementation
 ******************************************************************************/
package jp.aegif.nemaki.model;

import java.util.ArrayList;
import java.util.List;

//This class need to be calculated for the path of inherited ACEs
public class Acl {
	private List<Ace> inheritedAces;
	private List<Ace> localAces;
	
	public Acl(){
		inheritedAces = new ArrayList<Ace>();
		localAces = new ArrayList<Ace>();
	}
	
	public List<Ace> getInheritedAces() {
		return inheritedAces;
	}
	public void setInheritedAces(List<Ace> inheritedAces) {
		this.inheritedAces = inheritedAces;
	}
	public List<Ace> getLocalAces() {
		return localAces;
	}
	public void setLocalAces(List<Ace> localAces) {
		this.localAces = localAces;
	}
	
	public List<Ace> getAllAces(){
		List<Ace> merged = inheritedAces;
		merged.addAll(localAces);
		return merged;
	}
	
	public List<Ace>getPropagatingAces(){
		List<Ace> merged = inheritedAces;
		for(Ace ace : localAces){
			if(!ace.isObjectOnly()) merged.add(ace);
		}
		return merged;
	}
}
