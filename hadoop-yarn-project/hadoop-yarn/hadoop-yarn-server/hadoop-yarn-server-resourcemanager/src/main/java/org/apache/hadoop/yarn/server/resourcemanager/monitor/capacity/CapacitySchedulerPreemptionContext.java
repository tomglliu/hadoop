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

package org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity;

import org.apache.hadoop.classification.InterfaceStability.Unstable;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity.ProportionalCapacityPreemptionPolicy.IntraQueuePreemptionOrderPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

interface CapacitySchedulerPreemptionContext {
  CapacityScheduler getScheduler();

  TempQueuePerPartition getQueueByPartition(String queueName,
      String partition);

  Collection<TempQueuePerPartition> getQueuePartitions(String queueName);

  ResourceCalculator getResourceCalculator();

  RMContext getRMContext();

  boolean isObserveOnly();

  Set<ContainerId> getKillableContainers();

  double getMaxIgnoreOverCapacity();

  double getNaturalTerminationFactor();

  Set<String> getLeafQueueNames();

  Set<String> getAllPartitions();

  int getClusterMaxApplicationPriority();

  Resource getPartitionResource(String partition);

  LinkedHashSet<String> getUnderServedQueuesPerPartition(String partition);

  void addPartitionToUnderServedQueues(String queueName, String partition);

  float getMinimumThresholdForIntraQueuePreemption();

  float getMaxAllowableLimitForIntraQueuePreemption();

  @Unstable
  IntraQueuePreemptionOrderPolicy getIntraQueuePreemptionOrderPolicy();

  boolean getCrossQueuePreemptionConservativeDRF();

  boolean getInQueuePreemptionConservativeDRF();
}
