package de.vectordata.skynet.ui.view;

public enum CheckableBehavior {

    /**
     * Long clicking on the item will open an action mode
     * and then select the item. Further items can be selected
     * by single clicking.
     */
    LONG_CLICK,

    /**
     * A single click on the item will toggle its checked state.
     * This does not open an action mode.
     */
    SINGLE_CLICK

}
