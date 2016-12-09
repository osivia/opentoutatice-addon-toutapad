/*
 * (C) Copyright 2016 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package fr.toutatice.addons.toutapad.ecm.services;

/**
 * @author Loïc Billon
 *
 */
public class EtherpadObject {

	private String groupId;
	
	private String padId;
	
	
	public EtherpadObject(String groupId, String  padId) {
		this.groupId = groupId;
		this.padId = padId;
	}
	

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the padId
	 */
	public String getPadId() {
		return padId;
	}

	/**
	 * @param padId the padId to set
	 */
	public void setPadId(String padId) {
		this.padId = padId;
	}
	
	
	
	public String getGroupAndPadId() {
		return groupId + '$' + padId;
	}
}
