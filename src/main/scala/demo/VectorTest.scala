package demo

import geotrellis.raster._
import geotrellis.raster.density.KernelStamper
import geotrellis.raster.mapalgebra.focal.Kernel
import geotrellis.raster.render._
import geotrellis.spark._
import geotrellis.spark.stitch.TileLayoutStitcher
import geotrellis.spark.tiling._
import geotrellis.vector._
import geotrellis.vector.io.json.JsonFeatureCollectionMap

import scala.util._


//https://blog.csdn.net/qq_32432081/article/details/80835199

object VectorTest {
  def helloSentence = "Hello GeoTrellis"
  val tl = TileLayout(7, 4, 100, 100)
  val extent = Extent(-109, 37, -102, 41) // Extent of Colorado
  val ld = LayoutDefinition(extent, tl)
  def dataBasePath = "/Users/wangleigis163.com/Documents/alex/dev/code/private/cloud-map/data-server/"




  def main(args: Array[String]): Unit = {
    //1：定义polygon并获取其多边形面积
    var polygon = Polygon((10.0, 10.0), (10.0, 20.0), (30.0, 30.0), (10.0, 10.0))
    System.out.println("polygon area:%s".format(polygon.area))
    //2:定义一个嗲
    var point = PointFeature(Point(0,0), "钟楼")
    //3：
    val fc: String = """{
                       |  "type": "FeatureCollection",
                       |  "features": [
                       |    {
                       |      "type": "Feature",
                       |      "geometry": { "type": "Point", "coordinates": [1.0, 2.0] },
                       |      "properties": { "someProp": 14 },
                       |      "id": "target_12a53e"
                       |    }, {
                       |      "type": "Feature",
                       |      "geometry": { "type": "Point", "coordinates": [2.0, 7.0] },
                       |      "properties": { "someProp": 5 },
                       |      "id": "target_32a63e"
                       |    }
                       |  ]
                       |}""".stripMargin
    //val collection = fc.parseGeoJson[JsonFeatureCollectionMap]
  }


}
