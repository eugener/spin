package org.oxbow.spin

class TestApp extends com.vaadin.Application {

    private var window: Window = null
    private val menubar = new MenuBar();

    override def init() = {
        
        window = new Window("Spin Test Application")
        
        val file1 = menubar.addItem("File 1", null)
        val file2 = menubar.addItem("File 2", null)
        val file3 = menubar.addItem("File 3", null)
        val menuItem = file3.addItem("New", null)

        val action = new Action{
            caption="Click Me"
            tooltip="Tooltip" 
            def perform( source: AnyRef ) = window.addComponent(new Label("Thank you for clicking " + source.getClass ))
        }
        
        new ActionGroup(
            new ActionGroup(
		        action, 
		        action
		    ),
		    action
        )
        
        val button = new Button
        button.addActon( action )
        menuItem.addActon( action )
        
        
//        action.enabled = false
        action.caption = "My New Action"
        
        window.addComponent(menubar);
        window.addComponent(button)
        setMainWindow(window)
        
    }
    
}