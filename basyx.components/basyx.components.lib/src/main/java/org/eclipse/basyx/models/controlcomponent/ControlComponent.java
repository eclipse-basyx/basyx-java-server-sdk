/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.models.controlcomponent;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;



/**
 * BaSys 4.0 control component interface. This is a VAB object that cannot be serialized.
 * 
 * @author kuhn
 *
 */
public abstract class ControlComponent extends HashMap<String, Object> {

	// Miscellaneous String constants
	public static final String STATUS = "STATUS";
	public static final String OPERATIONS = "OPERATIONS";

	// String constants for operations
	public static final String OPERATION_CLEAR = "CLEAR";
	public static final String OPERATION_STOP = "STOP";
	public static final String OPERATION_ABORT = "ABORT";
	public static final String OPERATION_RESET = "RESET";
	public static final String OPERATION_START = "START";
	public static final String OPERATION_MANUAL = "MANUAL";
	public static final String OPERATION_AUTO = "AUTO";
	public static final String OPERATION_PRIORITY = "PRIO";
	public static final String OPERATION_OCCUPY = "OCCUPY";
	public static final String OPERATION_FREE = "FREE";

	// String constants for setting execution state
	public static final String CMD = "cmd";

	// String constants for status
	public static final String ERROR_STATE = "ER";
	public static final String WORK_STATE = "WORKST";
	public static final String OP_MODE = "OPMODE";
	public static final String EX_STATE = "EXST";
	public static final String EX_MODE = "EXMODE";
	public static final String OCCUPIER = "OCCUPIER";
	public static final String OCCUPATION_STATE = "OCCST";

	/**
	 * The status map implements the service/ substructure of the control component structure. It also 
	 * indicates variable changes via callbacks of the outer class.
	 * 
	 * @author kuhn
	 *
	 */
	class StatusMap extends HashMap<String, Object> {
		/**
		 * Version number of serialized instances
		 */
		private static final long serialVersionUID = 1L;
		
		
		
		/**
		 * Constructor
		 */
		public StatusMap() {
			// Populate control component "status" sub structure 
			put(OCCUPATION_STATE, OccupationState.FREE.getValue()); // Occupation state: FREE
			put(OCCUPIER, "");                      		// Occupier: none
			put(EX_MODE, ExecutionMode.AUTO.getValue()); // Execution mode: AUTO
			put(EX_STATE, ExecutionState.IDLE.getValue()); // Execution state: IDLE
			put(OP_MODE, "");                        		// Component specific operation mode (e.g. active service)
			put(WORK_STATE, "");                     		// Component specific work state
			put(ERROR_STATE, "");                    		// Component error state
		}
		
		
		/**
		 * Update an value
		 * 
		 * @return Added value
		 */
		@Override
		public Object put(String key, Object parValue) {
			// Value to be put in map
			Object value = parValue;
			// - Eventually we have to change the value to be put into the variable
			switch(key) {
				case EX_STATE: value = filterExecutionState(value.toString()); break;
				case OP_MODE:  value = filterOperationMode(value.toString()); break;
			}
			
			// Invoke base implementation
			Object result = super.put(key, value);
			
			// Indicate value change
			for (ControlComponentChangeListener listener: listeners) listener.onVariableChange(key, value);
			
			// Indicate specific changes to callback operations of control component
			switch(key) {
				case OCCUPATION_STATE: for (ControlComponentChangeListener listener: listeners) listener.onNewOccupationState(OccupationState.byValue((int) value)); break;
				case OCCUPIER:         for (ControlComponentChangeListener listener: listeners) listener.onNewOccupier(value.toString()); break;
				case EX_MODE:          for (ControlComponentChangeListener listener: listeners) listener.onChangedExecutionMode(ExecutionMode.byValue((int) value)); break;
				case EX_STATE:         for (ControlComponentChangeListener listener: listeners) listener.onChangedExecutionState(ExecutionState.byValue(value.toString())); break;
				case OP_MODE:          for (ControlComponentChangeListener listener: listeners) listener.onChangedOperationMode(value.toString()); break;
				case WORK_STATE:       for (ControlComponentChangeListener listener: listeners) listener.onChangedWorkState(value.toString()); break;
				case ERROR_STATE:      for (ControlComponentChangeListener listener: listeners) listener.onChangedErrorState(value.toString()); break;
			}
						
			// Return result
			return result;
		}
	}
	
	
	
	
	/**
	 * Version number of serialized instances
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Operations map
	 */
	protected Map<String, Function<?, ?>> operations = new HashMap<>();
	
	
	/**
	 * Saved occupier ID in case of local occupier overwrite
	 */
	protected String savedOccupierID = null;
	
	
	/**
	 * Status map
	 */
	protected Map<String, Object> status = null;

	
	/**
	 * Changed control component state listeners
	 */
	protected Collection<ControlComponentChangeListener> listeners = new LinkedList<>();



	
	/**
	 * Constructor
	 */
	public ControlComponent() {
		// Add control component output signals to map
		// - "status" sub structure
		status = new StatusMap();
		put(STATUS, status);

		// Input signals
		// - Command / stores last command
		put(CMD, "");                           // No command
		
		// Operations
		// - Add "operations" sub structure
		put(OPERATIONS, operations);
		// - Populate service operations
		operations.put(OPERATION_FREE, (Function<Object[], Void> & Serializable) (v) -> {
			freeControlComponent((String) v[0]);
			return null;
		});
		operations.put(OPERATION_OCCUPY, (Function<Object[], Void> & Serializable) (v) -> {
			occupyControlComponent((String) v[0]);
			return null;
		});
		operations.put(OPERATION_PRIORITY, (Function<Object[], Void> & Serializable) (v) -> {
			priorityOccupation((String) v[0]);
			return null;
		});
		operations.put(OPERATION_AUTO, (Function<Object, Void> & Serializable) (v) -> {
			this.setExecutionMode(ExecutionMode.AUTO);
			return null;
		});
		operations.put(OPERATION_MANUAL, (Function<Object, Void> & Serializable) (v) -> {
			this.setExecutionMode(ExecutionMode.MANUAL);
			return null;
		});
		operations.put(OPERATION_START, (Function<Object, Void> & Serializable) (v) -> {
			this.changeExecutionState(ExecutionOrder.START.getValue());
			return null;
		});
		operations.put(OPERATION_RESET, (Function<Object, Void> & Serializable) (v) -> {
			this.changeExecutionState(ExecutionOrder.RESET.getValue());
			return null;
		});
		operations.put(OPERATION_ABORT, (Function<Object, Void> & Serializable) (v) -> {
			this.changeExecutionState(ExecutionOrder.ABORT.getValue());
			return null;
		});
		operations.put(OPERATION_STOP, (Function<Object, Void> & Serializable) (v) -> {
			this.changeExecutionState(ExecutionOrder.STOP.getValue());
			return null;
		});
		operations.put(OPERATION_CLEAR, (Function<Object, Void> & Serializable) (v) -> {
			this.changeExecutionState(ExecutionOrder.CLEAR.getValue());
			return null;
		});
	}

	
	/**
	 * Optionally filter a set execution state. This function is always invoked when an execution state changes
	 */
	protected String filterExecutionState(String exState) {
		// Do nothing here
		return exState;
	}

	
	/**
	 * Optionally filter a set operation mode. This function is always invoked when an operation mode changes
	 */
	protected String filterOperationMode(String opMode) {
		// Do nothing here
		return opMode;
	}

	
	
	/**
	 * Add ControlComponentChangeListener
	 */
	public void addControlComponentChangeListener(ControlComponentChangeListener listener) {
		listeners.add(listener);
	}
	
	
	/**
	 * Remove ControlComponentChangeListener
	 */
	public void removeControlComponentChangeListener(ControlComponentChangeListener listener) {
		listeners.remove(listener);
	}

	
	/**
	 * Update an value
	 * 
	 * @return Added value
	 */
	@Override
	public Object put(String key, Object value) {
		// Invoke base implementation
		Object result = super.put(key, value);
		
		// Indicate value change
		for (ControlComponentChangeListener listener: listeners) listener.onVariableChange(key, value);
		
		// Process variable changes
		switch(key) {
			case CMD: 			   changeExecutionState(value.toString()); break;
		}
					
		// Return result
		return result;
	}
	
	
	/**
	 * Helper method - free this control component
	 */
	private void freeControlComponent(String senderId) {
		// Update occupier if sender is occupier
		if (senderId.equals(this.getOccupierID())) {
			// Get occupier from last occupier and reset last occupier
			// Component is free if last occupier is empty, occupied otherwise
			if (this.getOccupierID().isEmpty()) this.setOccupationState(OccupationState.FREE); else this.setOccupationState(OccupationState.OCCUPIED);
		}
	}

	
	/**
	 * Helper method - occupy this control component if it is free
	 */
	private void occupyControlComponent(String occupier) {
		// Update occupier if component is FREE
		if (this.getOccupationState().equals(OccupationState.FREE)) {this.setOccupierID(occupier); this.setOccupationState(OccupationState.OCCUPIED);}
	}

	
	/**
	 * Helper method - priority occupation of this component
	 */
	private void priorityOccupation(String occupier) {
		// Occupy component if component is FREE or OCCUPIED
		if ((this.getOccupationState().equals(OccupationState.FREE)) || (this.getOccupationState().equals(OccupationState.OCCUPIED))) {
			this.setOccupierID(occupier); 
			this.setOccupationState(OccupationState.PRIORITY);
		}
	}
		
	/**
	 * Change execution state based on execution order
	 */
	private void changeExecutionState(String orderString) {
		// Do not react on empty values
		if (orderString.isEmpty()) return;

		// Get execution order based on order string
		ExecutionOrder order = ExecutionOrder.byValue(orderString);		

		// Check if execution order leads to valid state in current state
		switch(ExecutionState.byValue(getExecutionState())) {
			case IDLE:
				// Process expected orders
				if (order.equals(ExecutionOrder.START)) {this.setExecutionState(ExecutionState.STARTING.getValue()); break;}
				if (order.equals(ExecutionOrder.STOP))  {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT)) {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case STARTING:
				if (order.equals(ExecutionOrder.STOP))  {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT)) {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case EXECUTE:
				// Process expected orders
				if (order.equals(ExecutionOrder.COMPLETE)) {this.setExecutionState(ExecutionState.COMPLETING.getValue()); break;}
				if (order.equals(ExecutionOrder.HOLD))     {this.setExecutionState(ExecutionState.HOLDING.getValue()); break;}
				if (order.equals(ExecutionOrder.SUSPEND))  {this.setExecutionState(ExecutionState.SUSPENDING.getValue()); break;}
				if (order.equals(ExecutionOrder.STOP))     {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case COMPLETING:
				if (order.equals(ExecutionOrder.STOP))     {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case COMPLETE:
				if (order.equals(ExecutionOrder.RESET)) {this.setExecutionState(ExecutionState.RESETTING.getValue()); break;}
				if (order.equals(ExecutionOrder.STOP))  {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT)) {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case RESETTING:
				if (order.equals(ExecutionOrder.STOP))     {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case HOLDING:
				if (order.equals(ExecutionOrder.STOP))     {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case HELD:
				if (order.equals(ExecutionOrder.UNHOLD)) {this.setExecutionState(ExecutionState.UNHOLDING.getValue()); break;}
				if (order.equals(ExecutionOrder.STOP))   {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))  {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());
				
			case UNHOLDING:
				if (order.equals(ExecutionOrder.STOP))     {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case SUSPENDING:
				if (order.equals(ExecutionOrder.STOP))     {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case SUSPENDED:
				if (order.equals(ExecutionOrder.UNSUSPEND)) {this.setExecutionState(ExecutionState.UNSUSPENDING.getValue()); break;}
				if (order.equals(ExecutionOrder.STOP))      {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))     {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case UNSUSPENDING:
				if (order.equals(ExecutionOrder.STOP))     {this.setExecutionState(ExecutionState.STOPPING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case STOPPING:
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case STOPPED:
				if (order.equals(ExecutionOrder.RESET)) {this.setExecutionState(ExecutionState.RESETTING.getValue()); break;}
				if (order.equals(ExecutionOrder.ABORT)) {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case ABORTED:
				if (order.equals(ExecutionOrder.CLEAR)) {this.setExecutionState(ExecutionState.CLEARING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			case CLEARING:
				if (order.equals(ExecutionOrder.ABORT))    {this.setExecutionState(ExecutionState.ABORTING.getValue()); break;}
				// Unexpected order in this state
				throw new RuntimeException("Unexpected command "+orderString+" in state "+getExecutionState());

			// Received order in unexpected state
			default:
				// Indicate error
				throw new RuntimeException("Unexpected order "+orderString+" in state "+getExecutionState());
		}
	}
	
	
	/**
	 * Finish current execution state (execute 'SC' order). This only works in transition states
	 */
	public void finishState() {
		// Check if state complete message leads to valid state in current state
		switch(ExecutionState.byValue(getExecutionState())) {
			// Process state changes
			case STARTING:     this.setExecutionState(ExecutionState.EXECUTE.getValue()); break;
			case EXECUTE:      this.setExecutionState(ExecutionState.COMPLETING.getValue()); break;
			case COMPLETING:   this.setExecutionState(ExecutionState.COMPLETE.getValue()); break;
			case RESETTING:    this.setExecutionState(ExecutionState.IDLE.getValue()); break;
			case HOLDING:      this.setExecutionState(ExecutionState.HELD.getValue()); break;
			case UNHOLDING:    this.setExecutionState(ExecutionState.EXECUTE.getValue()); break;
			case SUSPENDING:   this.setExecutionState(ExecutionState.SUSPENDED.getValue()); break;
			case UNSUSPENDING: this.setExecutionState(ExecutionState.EXECUTE.getValue()); break;
			case STOPPING:     this.setExecutionState(ExecutionState.STOPPED.getValue()); break;
			case STOPPED:      this.setExecutionState(ExecutionState.IDLE.getValue()); break;
			case ABORTING:     this.setExecutionState(ExecutionState.ABORTED.getValue()); break;
			case CLEARING:     this.setExecutionState(ExecutionState.STOPPED.getValue()); break;

			// Received order in unexpected state
			default:
				// Indicate error
				throw new RuntimeException("Unexpected state complete order in state "+getExecutionState());
		}
	}
	
	/**
	 * Get occupation state
	 */
	public OccupationState getOccupationState() {
		// Return occupation state
		return OccupationState.byValue((Integer) status.get(OCCUPATION_STATE));
	}
	
	
	/**
	 * Set occupation state
	 */
	public void setOccupationState(OccupationState occSt) {
		// Update occupation state
		status.put(OCCUPATION_STATE, occSt.getValue());
	}
	
	
	/**
	 * Get occupier ID
	 */
	public String getOccupierID() {
		// If occupier is not set, a null pointer Exception will be thrown when invoking toString(). Return an empty string in this case (=no occupier)
		try {
			return status.get(OCCUPIER).toString();
		} catch (NullPointerException e) {
			return "";
		}
	}
	
	
	/**
	 * Set occupier ID
	 */
	public void setOccupierID(String occId) {
		status.put(OCCUPIER, occId);
	}
	
	/**
	 * Get execution mode
	 */
	public ExecutionMode getExecutionMode() {
		// Return execution mode
		return ExecutionMode.byValue((Integer) status.get(EX_MODE));
	}
	
	
	/**
	 * Set execution mode
	 */
	public void setExecutionMode(ExecutionMode exMode) {
		// Return execution mode
		status.put(EX_MODE, exMode.getValue());
	}
	

	/**
	 * Get execution state
	 */
	public String getExecutionState() {
		// If member is not set, a null pointer Exception will be thrown when invoking toString(). Return an empty string in this case (=no occupier)
		try {
			return status.get(EX_STATE).toString();
		} catch (NullPointerException e) {
			return "";
		}
	}
	
	
	/**
	 * Set execution state
	 */
	public void setExecutionState(String newSt) {
		// System.out.println("Comp: change to:"+newSt);
		// Change execution state
		status.put(EX_STATE, newSt);
	}
	
	
	/**
	 * Get operation mode
	 */
	public String getOperationMode() {
		// If member is not set, a null pointer Exception will be thrown when invoking toString(). Return an empty string in this case (=no occupier)
		try {
			return status.get(OP_MODE).toString();
		} catch (NullPointerException e) {
			return "";
		}
	}
	
	
	/**
	 * Set operation mode
	 */
	public void setOperationMode(String opMode) {
		// Change operation mode
		status.put(OP_MODE, opMode);
	}
	
	
	/**
	 * Get work state
	 */
	public String getWorkState() {
		// If member is not set, a null pointer Exception will be thrown when invoking toString(). Return an empty string in this case (=no occupier)
		try {
			return status.get(WORK_STATE).toString();
		} catch (NullPointerException e) {
			return "";
		}
	}
	

	/**
	 * Set work state
	 */
	public void setWorkState(String workState) {
		// Change work state
		status.put(WORK_STATE, workState);
	}
	

	/**
	 * Get error state
	 */
	public String getErrorState() {
		// If member is not set, a null pointer Exception will be thrown when invoking toString(). Return an empty string in this case (=no occupier)
		try {
			return status.get(ERROR_STATE).toString();
		} catch (NullPointerException e) {
			return "";
		}
	}
	
	
	/**
	 * Set error state
	 */
	public void setErrorState(String errorState) {
		// Change error state
		status.put(ERROR_STATE, errorState);
	}


	/**
	 * Get last command
	 */
	public String getCommand() {
		// If member is not set, a null pointer Exception will be thrown when invoking toString(). Return an empty string in this case (=no occupier)
		try {return get(CMD).toString();} catch (NullPointerException e) {return "";}
	}
	
	
	/**
	 * Set command
	 */
	public void setCommand(String cmd) {
		// Change last command
		put(CMD, cmd);		
	}
}


