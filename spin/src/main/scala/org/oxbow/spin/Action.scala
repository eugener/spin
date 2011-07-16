package org.oxbow.spin

import com.vaadin.ui.MenuBar.Command
import com.vaadin.ui.AbstractComponent
import com.vaadin.event.{ Action => VaadinAction }
import com.vaadin.ui.AbstractOrderedLayout

/**
 * Command which can be attached to buttons and menu items
 * Any change to action properties is immediately reflected on attached components
 */
trait Action extends Serializable {

   private[Action] object Attribute extends Enumeration {
	   type Attribute = Value
	   val Enabled, Caption, Icon, Tooltip = Value
   }

   import Attribute._   
   
   private[spin] type AttrEntry = Tuple2[Attribute, Any]
   
   def execute(source: AnyRef): Unit = if (enabled) perform(source)
   protected def perform(source: AnyRef): Unit

   //    override def toString = "Action ['%s', enabled:%s]".format(caption, enabled.toString)

   private lazy val components = scala.collection.mutable.ListBuffer[ComponentProxy]()
   private lazy val props = scala.collection.mutable.Map[Attribute, Any]()

   private def setProp(p: AttrEntry): Unit = {
      if ( props.get(p._1) != p._2) {
         props += p
         propertyChange(p)
      }
   }
   
   def caption: String = props.getOrElse(Caption, "").asInstanceOf[String]
   def caption_=(caption: String) = setProp( Caption -> caption)

   def enabled: Boolean = props.getOrElse(Enabled, true).asInstanceOf[Boolean]
   def enabled_=(enabled: Boolean) = setProp( Enabled -> enabled )

   def icon: Option[ThemeResource] = props.getOrElse(Icon, None).asInstanceOf[Option[ThemeResource]]
   def icon_=(icon: Option[ThemeResource]) = setProp( Icon -> icon )

   def tooltip: String = props.getOrElse(Tooltip, "").asInstanceOf[String]
   def tooltip_=(tooltip: String) = setProp( Tooltip -> tooltip)

   protected[spin] def attachTo(cmpt: AbstractComponent, toolbar:Boolean = false ) = Option(cmpt).foreach(components += setup(_, toolbar))
   protected[spin] def attachTo(menuItem: MenuItem) = Option(menuItem).foreach(components += setup(_))

   private def setup(c: AnyRef, toolbar: Boolean = false ): ComponentProxy = {
      ComponentProxy(c).enabled(enabled).icon(icon).tooltip(tooltip).action(this)
      .caption( if (( !toolbar || icon.isEmpty )) caption else "" )
   }

   private def propertyChange( prop: AttrEntry ) = components.foreach( _.setProp(prop))

   protected[spin] def asMenuCommand: Command = new Command {
      def menuSelected(selectedItem: MenuBar#MenuItem) = execute(selectedItem)
   }
   
   protected[spin] def asButtonListener: ButtonClickListener = 
      new ButtonClickListener { def buttonClick(event: ButtonClickEvent) = execute(event.getSource) }
   
   private[Action] case class ComponentProxy(val target: Any) {
	
	   def setProp(p: AttrEntry): Unit = p._1 match {
	      case Caption => caption(p._2.asInstanceOf[String])
	      case Enabled => enabled(p._2.asInstanceOf[Boolean])
	      case Icon    => icon(p._2.asInstanceOf[Option[ThemeResource]])
	      case Tooltip => tooltip(p._2.asInstanceOf[String])
	   }
	
	   def caption(caption: String): ComponentProxy = target match {
	      case c: AbstractComponent => c.setCaption(caption); this
	      case m: MenuItem => m.setText(caption); this
	   }
	
	   def enabled(enabled: Boolean): ComponentProxy = target match {
	      case c: AbstractComponent => c.setEnabled(enabled); this
	      case m: MenuItem => m.setEnabled(enabled); this
	   }
	
	   def icon(icon: Option[ThemeResource]): ComponentProxy = target match {
	      case c: AbstractComponent => icon.foreach(c.setIcon); this
	      case m: MenuItem => icon.foreach(m.setIcon); this
	   }
	
	   def tooltip(tooltip: String): ComponentProxy = target match {
	      case c: AbstractComponent => c.setDescription(tooltip); this
	      case m: MenuItem => m.setDescription(tooltip); this
	   }
	
	   def action(a: Action): ComponentProxy = target match {
	      case b: Button   => b.addListener( a.asButtonListener ); this
	      case m: MenuItem => m.setCommand( a.asMenuCommand ); this
	   }
	
	}   
	   
}


/**
 * Represents Action sequence which may act like Action itself. 
 * This is helpful for creating action trees, which than can be transformed into buttons, menus, toolbars.
 * 
 */
object ActionSeq {

   def apply(title: String, actions: Action*): ActionSeq = new ActionSeq(title, actions.toSeq)
   def apply(actions: Action*): ActionSeq = new ActionSeq("", actions.toList)

   def apply(title: String, actions: List[Action]): ActionSeq = new ActionSeq(title, actions)
   def apply(actions: List[Action]): ActionSeq = new ActionSeq("", actions)

}

class ActionSeq(override val caption: String, val actions: Seq[Action]) extends Action with Seq[Action] {

   final def perform(source: AnyRef): Unit = {}
   protected[spin] override def asMenuCommand = null    // menu groups should not execute actions
   protected[spin] override def asButtonListener = null // groups attached to buttons should not execute actions
   
   def apply(idx: Int): Action = actions(idx)
   def length = actions.length
   def iterator = actions.iterator
   
}

object ActionContainerFactory {

   private case class ContextAction(val action: Action) extends VaadinAction(action.caption, action.icon.orNull)

   /**
    * Creates menu bar from sequence of actions
    */
   def menuBar(actions: Seq[Action]): MenuBar = {

      type MenuParent = { def addItem(s: String, c: com.vaadin.ui.MenuBar.Command): MenuItem }
      def createChild(parent: MenuParent): MenuItem = parent.addItem("", null)

      def process(action: Action, item: MenuItem): Unit = {
         action.attachTo(item)
         action match {
            case seq: Seq[Action] => seq.foreach(process(_, createChild(item)))
            case _ => // do nothing
         }
      }

      val menuBar = new MenuBar
      actions.foreach { process(_, createChild(menuBar)) }
      menuBar

   }

   /**
    * Creates context menu from sequence of actions
    */
   def contextMenu(actions: Seq[Action]): ActionHandler = {
      new ActionHandler {

         def getActions(target: AnyRef, sender: AnyRef) = actions.map(ContextAction).toArray

         def handleAction(action: VaadinAction, sender: AnyRef, target: AnyRef) = action match {
            case a: ContextAction => a.action.execute(sender)
            case _ =>
         }

      }

   }

   /**
    * Creates tool bar from sequence of actions. 
    * Currently only top level actions
    * TODO: use drop-down buttons for action trees
    */
   def toolbar(actions: Seq[Action], horizontal: Boolean = true): AbstractOrderedLayout = {
      //TODO: has to work with action trees and use drop-down buttons
      val layout = if (horizontal) new HorizontalLayout else new VerticalLayout
      layout.setSpacing(true)
      actions.foreach { a =>
         val button = new Button
         a.attachTo(button, true)
         layout.addComponent(button)
      }
      layout
   }
}

