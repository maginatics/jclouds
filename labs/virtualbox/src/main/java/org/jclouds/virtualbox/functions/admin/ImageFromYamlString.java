/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.virtualbox.functions.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.virtualbox.domain.YamlImage;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * @author Andrea Turli
 */
@Singleton
public class ImageFromYamlString implements Function<String, Map<Image, YamlImage>> {

	/**
	 * Type-safe config class for YAML
	 * 
	 */
	public static class Config {
		public List<YamlImage> images;
	}

	@Override
	public Map<Image, YamlImage> apply(String source) {
	  
	  checkNotNull(source, "yaml descriptor");
	  checkState(!source.equals(""),"yaml descriptor is empty");

		Constructor constructor = new Constructor(Config.class);

		TypeDescription imageDesc = new TypeDescription(YamlImage.class);
		imageDesc.putListPropertyType("images", String.class);
		constructor.addTypeDescription(imageDesc);

		Yaml yaml = new Yaml(constructor);
		Config config = (Config) yaml.load(source);
		checkState(config != null, "missing config: class");
		checkState(config.images != null, "missing images: collection");

		Map<Image, YamlImage> backingMap = Maps.newLinkedHashMap();
		for (YamlImage yamlImage : config.images) {
			backingMap.put(YamlImage.toImage.apply(yamlImage), yamlImage);
		}
		return backingMap;
	}

}