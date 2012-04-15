package logic;

import java.util.Vector;

import logic.GestureModel;
import logic.ProcessingUnit;

public class ProcessingUnitWrapper
{
	private ProcessingUnit punit;
	
	public ProcessingUnitWrapper(ProcessingUnit punit)
	{
		this.punit = punit;
	}
	
	public Vector<GestureModel> getGestureModels()
	{
		return punit.classifier.getGestureModels();
	}
}
