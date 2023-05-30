/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.aasregistry.service.api;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.model.ServiceDescription;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ServiceDescription.ProfilesEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BasyxDescriptionApiDelegate implements DescriptionApiDelegate {

	private ServiceDescription description;

	@Autowired
	public void setValues(@Value("${description.profiles}") String[] profiles) {
		description = new ServiceDescription();
		List<ProfilesEnum> profilesList = new ArrayList<>();
		for (String eachProfile : profiles) {
			ProfilesEnum value = getProfile(eachProfile);
			profilesList.add(value);
		}
		description.setProfiles(profilesList);
	}

	private ProfilesEnum getProfile(String eachProfile) {
		for (ProfilesEnum b : ProfilesEnum.values()) {
			if (b.getValue().equals(eachProfile)) {
				return b;
			}
		}
		throw new ProfileNotFoundException(eachProfile);
	}

	@Override
	public ResponseEntity<ServiceDescription> getDescription() {
		return new ResponseEntity<>(description, HttpStatus.OK);
	}

	public static class ProfileNotFoundException extends IllegalArgumentException {

		private static final long serialVersionUID = 1L;

		public ProfileNotFoundException(String profile) {
			super("No profile found with name: " + profile);
		}
	}
}