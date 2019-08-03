package demo

import java.net.URI

import akka.http.scaladsl.model.Uri.Path
import geotrellis.spark.io.cassandra.{CassandraAttributeStore, CassandraInstance, CassandraLayerReader, CassandraLayerWriter}
import geotrellis.spark.io.file.{FileAttributeStore, FileLayerReader, FileLayerWriter}
import geotrellis.spark.io.hadoop.{HadoopAttributeStore, HadoopLayerReader, HadoopLayerWriter}
import geotrellis.spark.io.hbase.{HBaseAttributeStore, HBaseInstance, HBaseLayerReader, HBaseLayerWriter}
import geotrellis.spark.io.{AttributeStore, LayerReader, LayerWriter, ValueReader, cassandra}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext

object Backend {
  def main(args: Array[String]): Unit = {
  /*  //1 ： s3 数据存储和读取
     val uri = new URI("s3://bucket/catalog")
      val store = AttributeStore(uri)
      val reader = LayerReader(uri)
      val writer = LayerWriter(uri)
      val values = ValueReader(uri)


    // 2:文件系统数据存储和读取
    val catalogPath: String = ""
    val flieStore: AttributeStore = FileAttributeStore(catalogPath)
    val reader = FileLayerReader(flieStore)
    val writer = FileLayerWriter(flieStore)
    val rootPath: Path = ""
    // 3:HDFS数据存储和读取
    val config: Configuration
    val store1: AttributeStore = HadoopAttributeStore(rootPath, config)
    val reader = HadoopLayerReader(store1)
    val writer = HadoopLayerWriter(rootPath, store1)

    // 4:Cassandra数据存储和读取
    val instance: CassandraInstance = ...
    val keyspace: String = ...
    val attrTable: String = ...
    val dataTable: String =
    implicit val sc: SparkContext = ...
    val store: AttributeStore = CassandraAttributeStore(instance, keyspace, attrTable)
    val reader = CassandraLayerReader(store) /* Needs the implicit SparkContext */
    val writer = cassandra.CassandraLayerWriter(store, instance, keyspace, dataTable)

    // 5:HBASE数据存储和读取
    val instance: HBaseInstance = ...
    val attrTable: String = ...
    val dataTable: String = ...
    implicit val sc: SparkContext = ...
    val store: AttributeStore = HBaseAttributeStore(instance, attrTable)
    val reader = HBaseLayerReader(store) /* Needs the implicit SparkContext */
    val writer = HBaseLayerWriter(store, dataTable)*/


  }
}
