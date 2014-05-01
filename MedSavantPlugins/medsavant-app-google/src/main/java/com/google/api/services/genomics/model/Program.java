/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * Modify at your own risk.
 */

package com.google.api.services.genomics.model;

/**
 * Model definition for Program.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Genomics API. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 */
@SuppressWarnings("javadoc")
public final class Program extends com.google.api.client.json.GenericJson {

  /**
   * (CL) Command line.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String commandLine;

  /**
   * (ID) Program record identifier.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String id;

  /**
   * (PN) Program name.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * (PP) Previous program id.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String prevProgramId;

  /**
   * (VN) Program version.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String version;

  /**
   * (CL) Command line.
   * @return value or {@code null} for none
   */
  public java.lang.String getCommandLine() {
    return commandLine;
  }

  /**
   * (CL) Command line.
   * @param commandLine commandLine or {@code null} for none
   */
  public Program setCommandLine(java.lang.String commandLine) {
    this.commandLine = commandLine;
    return this;
  }

  /**
   * (ID) Program record identifier.
   * @return value or {@code null} for none
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * (ID) Program record identifier.
   * @param id id or {@code null} for none
   */
  public Program setId(java.lang.String id) {
    this.id = id;
    return this;
  }

  /**
   * (PN) Program name.
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * (PN) Program name.
   * @param name name or {@code null} for none
   */
  public Program setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * (PP) Previous program id.
   * @return value or {@code null} for none
   */
  public java.lang.String getPrevProgramId() {
    return prevProgramId;
  }

  /**
   * (PP) Previous program id.
   * @param prevProgramId prevProgramId or {@code null} for none
   */
  public Program setPrevProgramId(java.lang.String prevProgramId) {
    this.prevProgramId = prevProgramId;
    return this;
  }

  /**
   * (VN) Program version.
   * @return value or {@code null} for none
   */
  public java.lang.String getVersion() {
    return version;
  }

  /**
   * (VN) Program version.
   * @param version version or {@code null} for none
   */
  public Program setVersion(java.lang.String version) {
    this.version = version;
    return this;
  }

  @Override
  public Program set(String fieldName, Object value) {
    return (Program) super.set(fieldName, value);
  }

  @Override
  public Program clone() {
    return (Program) super.clone();
  }

}