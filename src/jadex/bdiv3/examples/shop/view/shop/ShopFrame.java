/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.view.shop;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import jadex.bdiv3.examples.shop.shop.ShopCapa;
import jadex.bdiv3.runtime.ICapability;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;

/**
 * @author 罗佳欣
 *
 */
public class ShopFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShopFrame(final ICapability capa) {
		super("Shop - " + ((ShopCapa) capa.getPojoCapability()).getShopname());

		add(new ShopPanel(capa, this));

		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setSize(400, 450);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				capa.getAgent().getExternalAccess().killComponent();
			}
		});

		IResultListener<Void> dislis = new IResultListener<Void>() {
			public void exceptionOccurred(Exception exception) {
				dispose();
			}

			public void resultAvailable(Void result) {
			}
		};

		capa.getAgent().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>() {
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia) {
				ia.getComponentFeature(IMonitoringComponentFeature.class)
						.subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
						.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(
								new IntermediateDefaultResultListener<IMonitoringEvent>() {
					public void intermediateResultAvailable(IMonitoringEvent result) {
						setVisible(false);
						dispose();
					}
				}));
				return IFuture.DONE;
			}
		}).addResultListener(dislis);
	}

}
