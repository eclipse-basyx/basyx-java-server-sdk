/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.processengine.stubs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Coilcar implements ICoilcar {
	
	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Coilcar.class);
	
	private int currentPosition = 0;
	private int currentLifterPosition = 0;
	
	
	@Override
	public int moveTo(int position) {
		logger.debug("#submodel# invoke service +MoveTo+ with parameter: %d \n\n", position);
		Double steps[] =  generateCurve(currentPosition,  position);
		for(Double step : steps) {
			logger.debug(step == null ? "null" : step.toString());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		currentPosition = position;
		return currentPosition;
	}

	@Override
	public int liftTo(int position) {
		logger.debug("#submodel# Call service LiftTo with Parameter: %d \n\n", position);
		Double steps[] =  generateCurve(currentLifterPosition,  position);
		for(Double step : steps) {
			logger.debug(step == null ? "null" : step.toString());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		currentLifterPosition = position;
		return currentLifterPosition;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public int getCurrentLifterPosition() {
		return currentLifterPosition;
	}
	
	
	private Double[] generateCurve(double current, double goal){
		Double stepList[] = new Double[20];
		double delta = (goal-current)/20;
		for(int i= 0; i< 20; i++) {
			stepList[i]= current+delta*(i+1);
		}
		return stepList;
	}
	
}
