package de.perdian.commons.fx.execution;

public interface GuiJob {

    void execute(GuiProgressController progressController) throws Exception;

}
