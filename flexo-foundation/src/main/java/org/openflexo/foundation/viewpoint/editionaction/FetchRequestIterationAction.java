/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.view.action.FlexoBehaviourAction;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.StringUtils;

@FIBPanel("Fib/VPM/FetchRequestIterationActionPanel.fib")
@ModelEntity
@ImplementationClass(FetchRequestIterationAction.FetchRequestIterationActionImpl.class)
@XMLElement
public interface FetchRequestIterationAction extends ControlStructureAction {

	@PropertyIdentifier(type = String.class)
	public static final String ITERATOR_NAME_KEY = "iteratorName";
	@PropertyIdentifier(type = FetchRequest.class)
	public static final String FETCH_REQUEST_KEY = "fetchRequest";

	@Getter(value = ITERATOR_NAME_KEY)
	@XMLAttribute
	public String getIteratorName();

	@Setter(ITERATOR_NAME_KEY)
	public void setIteratorName(String iteratorName);

	@Getter(value = FETCH_REQUEST_KEY)
	@XMLElement(context = "FetchRequest_")
	public FetchRequest<?, ?> getFetchRequest();

	@Setter(FETCH_REQUEST_KEY)
	public void setFetchRequest(FetchRequest<?, ?> fetchRequest);

	public static abstract class FetchRequestIterationActionImpl extends ControlStructureActionImpl implements FetchRequestIterationAction {

		private static final Logger logger = Logger.getLogger(FetchRequestIterationAction.class.getPackage().getName());

		private String iteratorName = "item";

		//private FetchRequest fetchRequest;

		public FetchRequestIterationActionImpl() {
			super();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append("for (" + getIteratorName() + " in (", context);
			out.append(getFetchRequest() != null ? getFetchRequest().getFMLRepresentation() : "Null fetch request", context);
			out.append(")) {", context);
			out.append(StringUtils.LINE_SEPARATOR, context);
			for (EditionAction action : getActions()) {
				out.append(action.getFMLRepresentation(context), context, 1);
				out.append(StringUtils.LINE_SEPARATOR, context);
			}

			out.append("}", context);
			return out.toString();
		}

		@Override
		public String getIteratorName() {
			return iteratorName;
		}

		@Override
		public void setIteratorName(String iteratorName) {
			this.iteratorName = iteratorName;
			rebuildInferedBindingModel();
		}

		/*@Override
		public FetchRequest<?, ?> getFetchRequest() {
			return fetchRequest;
		}*/

		@Override
		public void setFetchRequest(FetchRequest<?, ?> fetchRequest) {
			performSuperSetter(FETCH_REQUEST_KEY, fetchRequest);
			fetchRequest.setActionContainer(this);
			fetchRequest.setEmbeddingIteration(this);
			
			//this.fetchRequest = fetchRequest;
			/*
			// Big hack to prevent XMLCoDe to also append FetchRequest to the list of embedded actions
			// Should be removed either by the fixing of XMLCoDe or by the switch to PAMELA
			if (getActions().contains(fetchRequest)) {
				removeFromActions(fetchRequest);
			}
			*/
		}

		public Type getItemType() {
			if (getFetchRequest()!=null&&getFetchRequest().getFetchedType() != null) {
				return getFetchRequest().getFetchedType();
			}
			return Object.class;
		}

		@Override
		protected BindingModel buildInferedBindingModel() {
			BindingModel returned = super.buildInferedBindingModel();
			returned.addToBindingVariables(new BindingVariable(getIteratorName(), getItemType()) {
				@Override
				public Object getBindingValue(Object target, BindingEvaluationContext context) {
					logger.info("What should i return for " + getIteratorName() + " ? target " + target + " context=" + context);
					return super.getBindingValue(target, context);
				}

				@Override
				public Type getType() {
					return getItemType();
				}
			});
			return returned;
		}

		private List<?> fetchItems(FlexoBehaviourAction action) {
			if (getFetchRequest() != null) {
				System.out.println("Pour choper mes items, je lance " + getFetchRequest());
				return getFetchRequest().performAction(action);
			}
			return Collections.emptyList();
		}

		@Override
		public Object performAction(FlexoBehaviourAction action) {
			System.out.println("Perform FetchRequestIterationAction for " + getFetchRequest());
			List<?> items = fetchItems(action);
			System.out.println("Items=" + items);
			if (items != null) {
				for (Object item : items) {
					action.declareVariable(getIteratorName(), item);
					performBatchOfActions(getActions(), action);
				}
			}
			action.dereferenceVariable(getIteratorName());
			return null;
		}

		/*@Override
		public void addToActions(EditionAction<?, ?> action) {
			// Big hack to prevent XMLCoDe to also append FetchRequest to the list of embedded actions
			// Should be removed either by the fixing of XMLCoDe or by the switch to PAMELA
			if (getFetchRequest() != action) {
				super.addToActions(action);
			}
		}*/

		@Override
		public void addToActions(EditionAction<?, ?> action) {
			if (getFetchRequest() != action) {
				performSuperAdder(ACTIONS_KEY, action);
			}

		}

		@Override
		public String getStringRepresentation() {
			if (getFetchRequest() != null) {
				return getIteratorName() + " : " + getFetchRequest().getStringRepresentation();
			}
			return super.getStringRepresentation();
		}

	}
}
