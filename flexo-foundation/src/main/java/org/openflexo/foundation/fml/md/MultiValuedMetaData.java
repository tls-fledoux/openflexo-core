/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.foundation.fml.md;

import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Finder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * A {@link MultiValuedMetaData} represent metadata with some values
 * 
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(MultiValuedMetaData.MultiValuedMetaDataImpl.class)
@XMLElement
public interface MultiValuedMetaData extends FMLMetaData {

	@PropertyIdentifier(type = MetaDataKeyValue.class, cardinality = Cardinality.LIST)
	public static final String KEY_VALUES_KEY = "keyValues";

	/**
	 * Return list of key-vaues declared for this meta-data
	 * 
	 * @return
	 */
	@Getter(value = KEY_VALUES_KEY, cardinality = Cardinality.LIST, inverse = MetaDataKeyValue.OWNING_METADATA_KEY)
	public List<MetaDataKeyValue<?>> getKeyValues();

	@Adder(KEY_VALUES_KEY)
	public void addToKeyValues(MetaDataKeyValue<?> metaData);

	@Remover(KEY_VALUES_KEY)
	public void removeFromKeyValues(MetaDataKeyValue<?> metaData);

	@Finder(collection = KEY_VALUES_KEY, attribute = MetaDataKeyValue.KEY_KEY)
	public <T> MetaDataKeyValue<T> getKeyValue(String key);

	public boolean hasKeyValue(String key);

	public <T> T getValue(String key, Class<T> type);

	public <T> void setValue(String key, T value, Class<T> type);

	public static abstract class MultiValuedMetaDataImpl extends FMLMetaDataImpl implements MultiValuedMetaData {

		@Override
		public boolean hasKeyValue(String key) {
			return getKeyValue(key) != null;
		}

		@Override
		public <T> T getValue(String key, Class<T> type) {
			if (getKeyValue(key) != null) {
				return ((MetaDataKeyValue<T>) getKeyValue(key)).getValue(type);
			}
			return null;

		}

		@Override
		public <T> void setValue(String key, T value, Class<T> type) {
			if (value != null) {
				if (getKeyValue(key) != null) {
					((MetaDataKeyValue<T>) getKeyValue(key)).setValue(value, type);
				}
				else {
					MetaDataKeyValue<T> newKV = getFMLModelFactory().newMetaDataKeyValue(key, value, type);
					addToKeyValues(newKV);
				}
			}
			else {
				if (getKeyValue(key) != null) {
					removeFromKeyValues(getKeyValue(key));
				}
			}
		}

	}

}
