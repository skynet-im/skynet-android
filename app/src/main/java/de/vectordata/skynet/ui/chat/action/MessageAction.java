package de.vectordata.skynet.ui.chat.action;

public enum MessageAction {
    NONE(false),
    EDIT(true),
    QUOTE(false);

    private boolean clearOnExit;

    /**
     * Defines a new message action
     *
     * @param clearOnExit Whether the input field should be cleared when exiting the action
     */
    MessageAction(boolean clearOnExit) {
        this.clearOnExit = clearOnExit;
    }

    public boolean isClearOnExit() {
        return clearOnExit;
    }
}

