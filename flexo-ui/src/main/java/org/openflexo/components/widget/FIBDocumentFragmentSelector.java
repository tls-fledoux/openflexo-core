/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Flexo-ui, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.components.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.listener.FIBSelectionListener;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.fib.view.widget.FIBCustomWidget;
import org.openflexo.foundation.doc.FlexoDocument;
import org.openflexo.foundation.doc.FlexoDocumentElement;
import org.openflexo.foundation.doc.FlexoDocumentFragment;
import org.openflexo.foundation.doc.FlexoDocumentFragment.FragmentConsistencyException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.rm.Resource;

/**
 * Widget allowing to select an {@link FlexoDocumentFragment} inside a {@link FlexoDocument}<br>
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public abstract class FIBDocumentFragmentSelector<F extends FlexoDocumentFragment<D, TA>, D extends FlexoDocument<D, TA>, TA extends TechnologyAdapter>
		extends FIBFlexoObjectSelector<F> {
	static final Logger logger = Logger.getLogger(FIBDocumentFragmentSelector.class.getPackage().getName());

	private D document;

	public FIBDocumentFragmentSelector(F editedObject) {
		super(editedObject);
	}

	@Override
	public abstract Resource getFIBResource();

	@Override
	public abstract Class<F> getRepresentedType();

	public D getDocument() {
		return document;
	}

	public void setDocument(D document) {
		if ((document == null && this.document != null) || (document != null && !document.equals(this.document))) {
			D oldValue = this.document;
			this.document = document;
			getPropertyChangeSupport().firePropertyChange("document", oldValue, document);
		}
	}

	public String renderedString(D editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	@Override
	public void updateCustomPanel(F editedObject) {
		// TODO Auto-generated method stub
		super.updateCustomPanel(editedObject);
	}

	private void updateWith(List<FlexoDocumentElement<D, TA>> elements) {
		F newFragment = null;
		if (elements.size() == 0) {
			newFragment = null;
		} else if (elements.size() == 1) {
			FlexoDocumentElement<D, TA> startElement = elements.get(0);
			try {
				newFragment = (F) document.getFragment(startElement, startElement);
			} catch (FragmentConsistencyException e) {
				e.printStackTrace();
			}
		} else {
			FlexoDocumentElement<D, TA> startElement = elements.get(0);
			FlexoDocumentElement<D, TA> endElement = elements.get(elements.size() - 1);
			try {
				newFragment = (F) document.getFragment(startElement, endElement);
			} catch (FragmentConsistencyException e) {
				e.printStackTrace();
			}

		}
		System.out.println("fragment=" + newFragment);
		setEditedObject(newFragment);
	}

	@Override
	protected FragmentSelectorDetailsPanel makeCustomPanel(F editedObject) {
		return new FragmentSelectorDetailsPanel(editedObject);
	}

	@Override
	protected FragmentSelectorFIBController makeCustomFIBController(FIBComponent fibComponent) {
		return new FragmentSelectorFIBController(fibComponent, this);
	}

	@Override
	protected FragmentSelectorFIBController getController() {
		return (FragmentSelectorFIBController) super.getController();
	}

	@Override
	public FragmentSelectorDetailsPanel getCustomPanel() {
		return (FragmentSelectorDetailsPanel) super.getCustomPanel();
	}

	public static class FragmentSelectorFIBController extends SelectorFIBController {
		public FragmentSelectorFIBController(final FIBComponent component, final FIBDocumentFragmentSelector selector) {
			super(component, selector);
			addSelectionListener(new FIBSelectionListener() {
				@Override
				public void selectionChanged(List<Object> selection) {
					System.out.println("Selection change: " + selection);
					List<FlexoDocumentElement<?, ?>> elements = new ArrayList<>();
					for (Object o : selection) {
						if (o instanceof FlexoDocumentElement) {
							elements.add((FlexoDocumentElement<?, ?>) o);
						}
					}
					selector.updateWith(elements);
				}
			});
		}

	}

	public class FragmentSelectorDetailsPanel extends SelectorDetailsPanel {

		private FIBCustomWidget<?, ?> docEditorWidget = null;

		protected FragmentSelectorDetailsPanel(F anObject) {
			super(anObject);
		}

		public FIBCustomWidget<?, ?> getDocEditorWidget() {
			if (docEditorWidget == null) {
				docEditorWidget = retrieveDocEditorWidget();
			}
			return docEditorWidget;
		}

		private FIBCustomWidget<?, ?> retrieveDocEditorWidget() {
			List<FIBComponent> listComponent = getFIBComponent().getAllSubComponents();
			for (FIBComponent c : listComponent) {
				if (c instanceof FIBCustom) {
					return (FIBCustomWidget) getController().viewForComponent(c);
				}
			}
			return null;
		}

		@Override
		public FragmentSelectorFIBController getController() {
			return (FragmentSelectorFIBController) super.getController();
		}

		@Override
		protected void selectValue(F value) {
			FIBBrowserWidget browserWidget = getFIBBrowserWidget();
			if (browserWidget != null) {
				// Force reselect value because tree may have been recomputed
				browserWidget.setSelected(value);
			}
		}

		@Override
		public void update() {
			super.update();
			System.out.println("tiens, ca serait bien de representer le fragment " + getSelectedValue());
			selectFragmentInDocumentEditor(getSelectedValue());
		}

	}

	protected void selectFragmentInDocumentEditor(F fragment) {

	}
}
