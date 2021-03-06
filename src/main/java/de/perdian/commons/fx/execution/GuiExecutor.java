package de.perdian.commons.fx.execution;

public interface GuiExecutor {

    void execute(GuiJob job);

    boolean addProgressListener(GuiProgressListener progressListener);
    boolean removeProgressListener(GuiProgressListener progressListener);

    boolean addExecutorListener(GuiExecutorListener executorListener);
    boolean removeExecutorListener(GuiExecutorListener executorListener);

}
