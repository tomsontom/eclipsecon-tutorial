package javafx.e4.app.jemmy;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import junit.framework.Assert;

import org.jemmy.fx.NodeWrap;
import org.jemmy.fx.SceneDock;
import org.jemmy.lookup.Lookup;
import org.jemmy.lookup.LookupCriteria;
import org.junit.BeforeClass;
import org.junit.Test;

public class SampleTestCase {
	protected static SceneDock scene;
	
	@BeforeClass
	public static void startApp() throws InterruptedException {
		try {
			scene = new SceneDock();
		} catch(Throwable t ) {
			t.printStackTrace();
		}
	}
	
	@Test
	public void testWindowTitle() {
		Assert.assertTrue(scene.control().getWindow() instanceof Stage);
		Stage s = (Stage) scene.control().getWindow();
		Assert.assertEquals("Todolist", s.getTitle());
	}
	
	@Test
	public void testCreateNew() {
		Lookup<Node> lookup = scene.wrap().asParent().lookup(new LookupCriteria<Node>() {
			
			@Override
			public boolean check(Node node) {
				if( node instanceof Button ) {
					return "add-todo-button".equals(node.getId());
				}
				return false;
			}
		});
		lookup.wrap().mouse().click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
