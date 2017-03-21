/*
 * Copyright 2008-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nominanuda.zen.maven;



/**
 * usable for running integration-tests with maven failsafe
 * mark ITs with a @org.junit.experimental.categories.Category(FailSafeTest.class)
 * and configure maven according to these settings
 * {@code
 *   <properties>
 *    <skipTests>true</skipTests>
 *  </properties>
 *  ...
 *  <build>
 *    ...
 *    <plugins>
 *      ...
 *      <plugin>
 *        <artifactId>maven-surefire-plugin</artifactId>
 *        <configuration>
 *          <skipTests>false</skipTests>
 *          <excludedGroups>com.nominanuda.zen.maven.FailSafeTest</excludedGroups>
 *        </configuration>
 *      </plugin>
 *      <plugin>
 *        <groupId>org.apache.maven.plugins</groupId>
 *        <artifactId>maven-failsafe-plugin</artifactId>
 *        <configuration>
 *          <skipTests>${skipTests}</skipTests>
 *          <includes>
 *            <include>**< NO SPACE HERE :) />/*.java</include>
 *          </includes>
 *          <groups>com.nominanuda.zen.maven.FailSafeTest</groups>
 *        </configuration>
 *        <executions>
 *          <execution>
 *            <goals>
 *              <goal>integration-test</goal>
 *              <goal>verify</goal>
 *            </goals>
 *          </execution>
 *        </executions>
 *      </plugin>
 *      ...
 *    </plugins>
 *    ...
 *  </build>
 *
 * }
 * 
 * if you want to include them in mvn verify or install -DskipTests=false
 * 
 *
 */
public interface FailSafeTest {

}
