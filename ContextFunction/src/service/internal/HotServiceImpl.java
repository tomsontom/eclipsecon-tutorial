package service.internal;

import service.ICoolService;

public class HotServiceImpl implements ICoolService {

	@Override
	public void coolAction() {
		System.err.println("This is not a cool but a hot action");
	}

}
