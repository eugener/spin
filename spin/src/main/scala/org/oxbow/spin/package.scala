package org.oxbow

package object spin {

    type ActionHandler = com.vaadin.event.Action.Handler 
    type Alignment = com.vaadin.ui.Alignment
    type BeanItem[A] = com.vaadin.data.util.BeanItem[A]
    type BeanItemContainer[A] = com.vaadin.data.util.BeanItemContainer[A]
    type Button = com.vaadin.ui.Button
    type ButtonClickEvent = com.vaadin.ui.Button#ClickEvent
    type ButtonClickListener = com.vaadin.ui.Button.ClickListener
    type CheckBox = com.vaadin.ui.CheckBox
    type Component = com.vaadin.ui.Component
    type ComponentAttachEvent = com.vaadin.ui.ComponentContainer.ComponentAttachEvent
    type ComponentDetachEvent = com.vaadin.ui.ComponentContainer.ComponentDetachEvent
    type ComponentEvent = com.vaadin.ui.Component.Event
    type ComponentErrorEvent = com.vaadin.ui.AbstractComponent.ComponentErrorEvent
    type CustomComponent = com.vaadin.ui.CustomComponent
    type Form = com.vaadin.ui.Form
    type HierarchicalContainer = com.vaadin.data.util.HierarchicalContainer
    type HorizontalLayout = com.vaadin.ui.HorizontalLayout
    type HorizontalSplitPanel = com.vaadin.ui.HorizontalSplitPanel
    type IntegerValidator = com.vaadin.data.validator.IntegerValidator
    type Item = com.vaadin.data.Item
    type ItemClickEvent = com.vaadin.event.ItemClickEvent
    type Label = com.vaadin.ui.Label
    type LayoutClickEvent = com.vaadin.event.LayoutEvents.LayoutClickEvent
    type MenuBar = com.vaadin.ui.MenuBar
    type MenuItem = com.vaadin.ui.MenuBar#MenuItem
    type Panel = com.vaadin.ui.Panel
    type Property = com.vaadin.data.Property
    type ReadOnlyStatusChangeEvent = com.vaadin.data.Property.ReadOnlyStatusChangeEvent
    type RepaintRequestEvent = com.vaadin.terminal.Paintable.RepaintRequestEvent
    type Resource = com.vaadin.terminal.Resource
    type SplitterClickEvent = com.vaadin.ui.AbstractSplitPanel#SplitterClickEvent
    type TextArea = com.vaadin.ui.TextArea
    type Table= com.vaadin.ui.Table
    type TextField = com.vaadin.ui.TextField
    type ThemeResource = com.vaadin.terminal.ThemeResource
    type TreeCollapseEvent = com.vaadin.ui.Tree.CollapseEvent
    type TreeCollapseListener = com.vaadin.ui.Tree.CollapseListener
    type TreeExpandEvent = com.vaadin.ui.Tree.ExpandEvent
    type TreeExpandListener = com.vaadin.ui.Tree.ExpandListener
    type VaadinApplication = com.vaadin.Application
    type ValueChangeEvent = com.vaadin.data.Property.ValueChangeEvent
    type VerticalLayout = com.vaadin.ui.VerticalLayout
    type VerticalSplitPanel = com.vaadin.ui.VerticalSplitPanel
    type Window = com.vaadin.ui.Window
    type WindowCloseEvent = Window#CloseEvent
    
    implicit def button2x( b: Button ) = new {
	    implicit def add( action: Action ) = Option(action).foreach( _.attachTo(b))
	}
    
    implicit def menuItem2x( m: MenuItem ) = new {
	    implicit def add( action: Action ) = Option(action).foreach( _.attachTo(m))
	}
}