package com.macys.mst.Atlas.utilmethods;

import org.jbehave.core.model.ExamplesTable;

public interface PickingUtil {
	
	/**
	 * Complete the pick by work_id transaction
	 * @param example
	 */
	public void completePickByWorkId(ExamplesTable example) ;
	
	/**
	 * Complete the exception pick transaction
	 * @param example
	 */
	public void completeExceptionPick(ExamplesTable example);
	
	/**
	 * Complete pick to tote transaction 
	 * @param example
	 */
	public void completePickToTote(ExamplesTable example);
	
	/**
	 * Complete Beauty pick cart transaction
	 * @param example
	 */
	public void completeBeautyPickCart(ExamplesTable example);
}
