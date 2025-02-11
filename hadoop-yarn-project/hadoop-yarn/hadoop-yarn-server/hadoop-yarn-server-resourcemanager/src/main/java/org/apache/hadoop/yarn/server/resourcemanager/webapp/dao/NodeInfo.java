/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.api.records.OpportunisticContainersStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNodeReport;

import com.google.common.annotations.VisibleForTesting;

@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeInfo {

  protected String rack;
  protected NodeState state;
  private String id;
  protected String nodeHostName;
  protected String nodeHTTPAddress;
  private long lastHealthUpdate;
  protected String version;
  protected String healthReport;
  protected int numContainers;
  protected long usedMemoryMB;
  protected long availMemoryMB;
  protected long usedVirtualCores;
  protected long availableVirtualCores;
  private float memUtilization;
  private float cpuUtilization;
  private int numRunningOpportContainers;
  private long usedMemoryOpportGB;
  private long usedVirtualCoresOpport;
  private int numQueuedContainers;
  protected ArrayList<String> nodeLabels = new ArrayList<String>();
  protected ResourceUtilizationInfo resourceUtilization;
  protected ResourceInfo usedResource;
  protected ResourceInfo availableResource;

  public NodeInfo() {
  } // JAXB needs this

  public NodeInfo(RMNode ni, ResourceScheduler sched) {
    NodeId id = ni.getNodeID();
    SchedulerNodeReport report = sched.getNodeReport(id);
    this.numContainers = 0;
    this.usedMemoryMB = 0;
    this.availMemoryMB = 0;
    if (report != null) {
      this.numContainers = report.getNumContainers();
      this.usedMemoryMB = report.getUsedResource().getMemorySize();
      this.availMemoryMB = report.getAvailableResource().getMemorySize();
      this.usedVirtualCores = report.getUsedResource().getVirtualCores();
      this.availableVirtualCores =
          report.getAvailableResource().getVirtualCores();
      this.usedResource = new ResourceInfo(report.getUsedResource());
      this.availableResource = new ResourceInfo(report.getAvailableResource());
      Resource totalPhysical = ni.getPhysicalResource();
      long nodeMem;
      long nodeCores;
      if (totalPhysical == null) {
        nodeMem =
            this.usedMemoryMB + this.availMemoryMB;
        // If we don't know the number of physical cores, assume 1. Not
        // accurate but better than nothing.
        nodeCores = 1;
      } else {
        nodeMem = totalPhysical.getMemorySize();
        nodeCores = totalPhysical.getVirtualCores();
      }
      this.memUtilization = nodeMem <= 0 ? 0
          : (float)report.getUtilization().getPhysicalMemory() * 100F / nodeMem;
      this.cpuUtilization =
          (float)report.getUtilization().getCPU() * 100F / nodeCores;
    }
    this.id = id.toString();
    this.rack = ni.getRackName();
    this.nodeHostName = ni.getHostName();
    this.state = ni.getState();
    this.nodeHTTPAddress = ni.getHttpAddress();
    this.lastHealthUpdate = ni.getLastHealthReportTime();
    this.healthReport = String.valueOf(ni.getHealthReport());
    this.version = ni.getNodeManagerVersion();

    // Status of opportunistic containers.
    this.numRunningOpportContainers = 0;
    this.usedMemoryOpportGB = 0;
    this.usedVirtualCoresOpport = 0;
    this.numQueuedContainers = 0;
    OpportunisticContainersStatus opportStatus =
        ni.getOpportunisticContainersStatus();
    if (opportStatus != null) {
      this.numRunningOpportContainers =
          opportStatus.getRunningOpportContainers();
      this.usedMemoryOpportGB = opportStatus.getOpportMemoryUsed();
      this.usedVirtualCoresOpport = opportStatus.getOpportCoresUsed();
      this.numQueuedContainers = opportStatus.getQueuedOpportContainers();
    }

    // add labels
    Set<String> labelSet = ni.getNodeLabels();
    if (labelSet != null) {
      nodeLabels.addAll(labelSet);
      Collections.sort(nodeLabels);
    }

    // update node and containers resource utilization
    this.resourceUtilization = new ResourceUtilizationInfo(ni);
  }

  public String getRack() {
    return this.rack;
  }

  public String getState() {
    return String.valueOf(this.state);
  }

  public String getNodeId() {
    return this.id;
  }

  public String getNodeHTTPAddress() {
    return this.nodeHTTPAddress;
  }

  public void setNodeHTTPAddress(String nodeHTTPAddress) {
    this.nodeHTTPAddress = nodeHTTPAddress;
  }

  public long getLastHealthUpdate() {
    return this.lastHealthUpdate;
  }

  public String getVersion() {
    return this.version;
  }

  public String getHealthReport() {
    return this.healthReport;
  }

  public int getNumContainers() {
    return this.numContainers;
  }

  public long getUsedMemory() {
    return this.usedMemoryMB;
  }

  public long getAvailableMemory() {
    return this.availMemoryMB;
  }

  public long getUsedVirtualCores() {
    return this.usedVirtualCores;
  }

  public long getAvailableVirtualCores() {
    return this.availableVirtualCores;
  }

  public int getNumRunningOpportContainers() {
    return numRunningOpportContainers;
  }

  public long getUsedMemoryOpportGB() {
    return usedMemoryOpportGB;
  }

  public long getUsedVirtualCoresOpport() {
    return usedVirtualCoresOpport;
  }

  public int getNumQueuedContainers() {
    return numQueuedContainers;
  }

  public ArrayList<String> getNodeLabels() {
    return this.nodeLabels;
  }

  public ResourceInfo getUsedResource() {
    return usedResource;
  }

  public void setUsedResource(ResourceInfo used) {
    this.usedResource = used;
  }

  public ResourceInfo getAvailableResource() {
    return availableResource;
  }

  public void setAvailableResource(ResourceInfo avail) {
    this.availableResource = avail;
  }

  public ResourceUtilizationInfo getResourceUtilization() {
    return this.resourceUtilization;
  }

  public float getMemUtilization() {
    return memUtilization;
  }

  public void setMemUtilization(float util) {
    this.memUtilization = util;
  }

  public float getVcoreUtilization() {
    return cpuUtilization;
  }

  public void setVcoreUtilization(float util) {
    this.cpuUtilization = util;
  }

  @VisibleForTesting
  public void setId(String id) {
    this.id = id;
  }

  @VisibleForTesting
  public void setLastHealthUpdate(long lastHealthUpdate) {
    this.lastHealthUpdate = lastHealthUpdate;
  }

}
