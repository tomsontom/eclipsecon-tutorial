package service.internal;

import javax.annotation.PostConstruct;

import service.ICoolService;

public class FreezingService implements ICoolService {

	@Override
	public void coolAction() {
		System.err.println("Freezing action");
	}
	
	@PostConstruct
	public void pc(){
		System.err.println("PostConstruct");
	}

}
