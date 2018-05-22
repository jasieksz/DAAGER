package model.domain

import org.joda.time.DateTime

case class NodeHealth(
  NodeState: String,
  ClusterState: String,
  ClusterSafe: Boolean,
  MigrationQueueSize: Int,
  ClusterSize: Int,
  timestamp: DateTime = DateTime.now,
  nodeId: String
)
