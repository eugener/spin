package org.oxbow.spin
import com.vaadin.ui.Window
import com.vaadin.ui.Button
import com.vaadin.ui.Label
import com.vaadin.ui.Button.ClickListener

class TestApp extends com.vaadin.Application {

    private var window: Window = null

    override def init() = {
        
        window = new Window("Spin Test Application")
        setMainWindow(window)
        val button = new Button("Click Me")
        button.addListener(new ClickListener {
            def buttonClick( event: Button#ClickEvent ) {
                window.addComponent(new Label("Thank you for clicking"))
            }
        })
        window.addComponent(button)
        
    }
    
}