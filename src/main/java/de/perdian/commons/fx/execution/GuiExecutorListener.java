package de.perdian.commons.fx.execution;

public interface GuiExecutorListener {

    void onExecutionStarting(GuiJob job);
    void onExecutionCompleted(GuiJob job);

}
