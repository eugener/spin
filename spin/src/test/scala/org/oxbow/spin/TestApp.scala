package org.oxbow.spin

import com.vaadin.data.util.BeanContainer
import scala.reflect.BeanProperty
import java.util.UUID

class TestApp extends com.vaadin.Application {

    val action = new Action {
        caption="Click Me"
        tooltip="Tooltip" 
        def perform( source: AnyRef ) = getMainWindow.addComponent(new Label("Thank you for clicking " + source.getClass ))
    }
    
    private final val actions = List(
         ActionSeq( "Menu 1" ),
         ActionSeq( "Menu 2" ),
         ActionSeq( "Menu 3",
            ActionSeq( "Submenu",
		        action, 
		        action
		    ),
		    action
        ),
        ActionSeq( "Menu 4" ),
        ActionSeq( "Menu 5" )
    )
    
    private final val menubar = ActionContainerFactory.menuBar( actions )

    override def init() = {
        
        val button = new Button
        
//        action.enabled = false
        action.caption = "My New Action"

        button.add( action )
        
        val model = new BeanContainer[String, Person]( classOf[Person] )
        model.setBeanIdProperty("id")
       
        for( i<-1 until 10 ) {
        	model.addBean( Person("Ashim", "Mandal", 32 )) 
        }
        
        val table = new Table("", model)
        table.setSelectable( true );
        table.addActionHandler( ActionContainerFactory.contextMenu(List(action,action)))
            
        val window = new Window("Spin Test Application")
        window.addComponent(menubar);
        window.addComponent(button)
        window.addComponent( ActionContainerFactory.toolbar(List(action,action)) )
        window.addComponent(table)
        setMainWindow(window)
        
    }
    
    case class Person (
        @BeanProperty val firstName: String,
        @BeanProperty val lastName: String,
        @BeanProperty val age: Int, 
        @BeanProperty val id: String = UUID.randomUUID.toString ) 
    
}
