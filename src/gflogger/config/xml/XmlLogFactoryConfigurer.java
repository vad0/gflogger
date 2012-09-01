/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gflogger.config.xml;

import gflogger.LogFactory;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author Harald Wendel
 * @author Vladimir Dolzhenko, vladimir.dolzhenko@gmail.com
 */
public final class XmlLogFactoryConfigurer {

	public static void configure(InputStream in) throws Exception {
		new XmlLogFactoryConfigurer(in);
	}

	public static void configure(final String xmlFileName) throws Exception {
		configure(XmlLogFactoryConfigurer.class.getResourceAsStream(xmlFileName));
	}

	public static void configure() throws Exception {
		configure(System.getProperty("gflogger.configuration", "/gflogger.xml"));
	}

	private XmlLogFactoryConfigurer(final InputStream in) throws Exception {
		try {
			final SAXParserFactory factory = SAXParserFactory.newInstance();

			factory.setNamespaceAware(true);
			factory.setValidating(true);

			final SAXParser saxParser = factory.newSAXParser();
			saxParser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
	                "http://www.w3.org/2001/XMLSchema");
			saxParser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaSource",
				getClass().getResourceAsStream("/gflogger.xsd"));

			final Configuration configuration = new Configuration();
			saxParser.parse(in, configuration);

			LogFactory.init(configuration.getLoggerViews());
		} finally {
			in.close();
		}
	}
}
