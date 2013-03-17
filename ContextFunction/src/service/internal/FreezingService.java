package service.internal;

import service.ICoolService;

public class FreezingService implements ICoolService {

	@Override
	public void coolAction() {
		System.err.println("Freezing action");

	}

}
