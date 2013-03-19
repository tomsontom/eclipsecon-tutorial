package vc.parts;

import javax.annotation.PostConstruct;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;

public class CoolPart {

	@PostConstruct
	public void pc(ComponentContainer cc){
		cc.addComponent(new Button("This component was contributed on the fly"));
	}
	
}
