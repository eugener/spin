package org.oxbow.spin
import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.MenuBar.Command

// TODO: Reference equality, hash code
trait Action { 

    import ActionProperty._
	    
    def perform( source: AnyRef ): Unit
    
//    override def toString = "Action ['%s', enabled:%s]".format(caption, enabled.toString)
    
    private val components = scala.collection.mutable.ListBuffer[ComponentProxy]()
    
    private val props = scala.collection.mutable.Map[ActionProperty,Any]()
    
    private def setProp( p: Tuple2[ActionProperty,Any] ): Unit = { props += p; propertyChange(p._1) }

    def caption: String = props.getOrElse(Caption, "").asInstanceOf[String]
    def caption_=( caption: String ) = setProp( (Caption, caption ))
    
    def enabled: Boolean = props.getOrElse(Enabled, true).asInstanceOf[Boolean]
    def enabled_=( enabled: Boolean ) = setProp( (Enabled, enabled ))

    def icon: Option[ThemeResource] = props.getOrElse(Icon, None).asInstanceOf[Option[ThemeResource]]
    def icon_=( icon: Option[ThemeResource] ) = setProp((Icon, icon))

    def tooltip: String = props.getOrElse(Tooltip, "").asInstanceOf[String]
    def tooltip_=( tooltip: String ) = setProp( (Tooltip, tooltip ))
    
    protected[spin] def associateWith( cmpt: AbstractComponent ) = Option(cmpt).foreach( components += setup(_) )
    protected[spin] def associateWith( menuItem: MenuItem )      = Option(menuItem).foreach( components += setup(_) ) 
    
    private def setup( c: AnyRef ): ComponentProxy = {
        ComponentProxy(c).caption(caption).enabled(enabled).icon(icon).tooltip(tooltip).action(this)
    }
    
    private def propertyChange( prop: ActionProperty ) = {
        prop match {
            case Enabled => components.foreach( _.enabled(enabled))
            case Caption => components.foreach( _.caption(caption))
            case Icon    => components.foreach( _.icon(icon))
            case Tooltip => components.foreach( _.tooltip(tooltip))
        }
    }
    
}

class ActionGroup( title: String, actions: List[Action] ) extends Action {
    
    caption = title
    
    def this( title: String, actions: Action* ) = this( title, actions.toList )
    def this( actions: Action* ) = this( "", actions.toList )
//    
//    def this( title: String )( a: Action* ) = this(title)( a.toList ) 
//    
    final def perform( source: AnyRef ): Unit = {}
    
}

private object ActionProperty extends Enumeration {
	type ActionProperty = Value
	val Enabled, Caption, Icon, Tooltip = Value
}

private case class ComponentProxy( private val c: Any ) {
    
    def caption( caption: String ): ComponentProxy = c match {
       case c: AbstractComponent => c.setCaption(caption); this
       case m: MenuItem => m.setText(caption); this
    }

    def enabled( enabled: Boolean ): ComponentProxy = c match {
       case c: AbstractComponent => c.setEnabled(enabled); this
       case m: MenuItem => m.setEnabled(enabled); this
    }

    def icon( icon: Option[ThemeResource] ): ComponentProxy = c match {
       case c: AbstractComponent => icon.foreach( c.setIcon ); this
       case m: MenuItem => icon.foreach( m.setIcon ); this
    }
    
    def tooltip( tooltip: String ): ComponentProxy = c match {
       case c: AbstractComponent => c.setDescription(tooltip); this
       case m: MenuItem => m.setDescription(tooltip); this
    }
    
    def action( a: Action ): ComponentProxy = c match {
       case b: Button => b.addListener( new ButtonClickListener {
	       def buttonClick(event: ButtonClickEvent) = a.perform( event.getSource )
	   }); this 
	   case m: MenuItem => {
		  m.setCommand( new Command { def menuSelected( selectedItem: MenuBar#MenuItem) = a.perform(selectedItem) }); this
	   }
    }
    
}

