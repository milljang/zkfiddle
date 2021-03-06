package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleSourceEventQueue;
import org.zkoss.fiddle.util.FiddleConfig;
import org.zkoss.fiddle.util.GAUtil;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.api.Window;

public class ViewResultComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = 7445220094351755044L;

	private Textbox directUrl;

	private Iframe content;

	private Window viewEditor;

	private Label directDesc;
	private Button openNewWindow;


	public void doAfterCompose(final Component comp) throws Exception {
		super.doAfterCompose(comp);

		FiddleSourceEventQueue.lookup().subscribeShowResult(
				new FiddleEventListener<ShowResultEvent>(ShowResultEvent.class, viewEditor) {
			public void onFiddleEvent(ShowResultEvent event) throws Exception {
				ShowResultEvent evt = (ShowResultEvent) event;
				FiddleSandbox inst = evt.getSandbox();
				viewEditor.setTitle("Running Sandbox : " + inst.getName() + " @ ZK " + inst.getZKVersion() );


				if(evt.getCase().getVersion() != 0){
					String tokenpath = evt.getCase().getCaseUrl( inst.getZKVersion());
					directUrl.setText( FiddleConfig.getHostName() + "/direct/" + tokenpath	+ "?run=" + inst.getHash());
					openNewWindow.setHref( FiddleConfig.getHostName() + "/direct/" + tokenpath	+ "?run=" + inst.getHash());
					setDirectVisible(true);
				}else{
					setDirectVisible(false);
				}

				content.setSrc(inst.getSrc(evt.getCase()));
				viewEditor.doModal();
			}
		});
	}

	private void setDirectVisible(boolean show){

		directDesc.setVisible(show);
		openNewWindow.setVisible(show);
		directUrl.setVisible(show);

	}

	public void onClick$openNewWindow(Event ev){
		GAUtil.logAction(FiddleConstant.GA_CATEGORY_SOURCE, "go-direct", openNewWindow.getHref());
	}

	public void onClick$closeWindow(ForwardEvent e) {
		viewEditor.setVisible(false);
		content.setSrc("/img/viewresult-loading.gif");
	}
}
