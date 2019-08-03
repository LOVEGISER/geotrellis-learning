package demo

import java.io.File


import geotrellis.raster._
import geotrellis.raster.io.geotiff.MultibandGeoTiff
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.raster.mapalgebra.focal.Square
import geotrellis.raster.render.{ColorMap, ColorRamp, RGB}
import geotrellis.spark._
import geotrellis.spark.etl.Etl
import geotrellis.spark.io._
import geotrellis.spark.io.file.{FileAttributeStore, FileLayerReader}
import geotrellis.spark.util.SparkUtils
import geotrellis.vector.ProjectedExtent
import org.apache.spark.SparkConf


object Main {
  def helloSentence = "Hello GeoTrellis"
  def dataBasePath = "/Users/wangleigis163.com/Documents/alex/dev/code/private/cloud-map/data-server/"

  def helloRaster(): Unit = {
    val nd = NODATA    //-2147483648

    val input = Array[Int](
      nd, 7, 1, 1, 3, 5, 9, 8, 2,
      9, 1, 1, 2, 2, 2, 4, 3, 5,
      3, 8, 1, 3, 3, 3, 1, 2, 2,
      2, 4, 7, 1, nd, 1, 8, 4, 3)

    //将数组转化为4*9矩阵
    val iat = IntArrayTile(input, 9, 4)

    //用一个n*n的窗口对矩阵做卷积，设中心值为平均值
    //Square(i) => n = 2 * i + 1
    val focalNeighborhood = Square(1)
    println(focalNeighborhood)
    val meanTile = iat.focalMean(focalNeighborhood)

    for (i <- 0 to 3) {
      for (j <- 0 to 8) {
        print(meanTile.getDouble(j, i) + " ")
      }
      println()
    }
  }

  def redTiff(path:String): Unit = {
    val tiffPath: String = dataBasePath+path
    //读取单波段image
    //val geoTiff: SinglebandGeoTiff = GeoTiffReader.readSingleband(tiffPath)
    //读取多波段image
    val geoTiff: MultibandGeoTiff = GeoTiffReader.readMultiband(tiffPath)
  }
//https://github.com/geotrellis/spark-etl
  // spark-shell --conf spark.serializer=org.apache.spark.serializer.KryoSerializer --class demo.Main  --jars  geotrellis-sbt-template-assembly-0.2.0.jar --master local[4]
  //  cd geotrellis
  //  ./sbt
  //  sbt> project spark-etl
  //  sbt> assembly
  def createTile(): Unit = {
    var args = Array[String](
      "--input",
      dataBasePath+"config/input.json",
      "--output",
      dataBasePath+"config/output.json",
      "--backend-profiles",
      dataBasePath+"config/backend-profiles.json"
    );
    //Logger.getLogger("org").setLevel(Level.ERROR)
    System.out.println(args)
    implicit val sc = SparkUtils.createSparkContext("ETL", new SparkConf(true).setMaster("local[*]"))
    try
      Etl.ingest[ProjectedExtent, SpatialKey, Tile](args)
    finally {
      sc.stop
    }
  }
//https://www.jianshu.com/p/1eda79747648
  def renderTile(): Unit = {
     val zoomId = 11
    implicit val sc = SparkUtils.createSparkContext("ReadLayer", new SparkConf(true).setMaster("local[*]"))
      //1:要读取的瓦片数据路径
     val path = dataBasePath+"/110000BJ_L5_TM_1990/title"  //图层文件根目录
     val store = FileAttributeStore(path)
     val reader = FileLayerReader(path)
     val layerId = LayerId("etlTest1", zoomId)  //设置图层名称和zoom
     //2：读取图层数据
     val layers: TileLayerRDD[SpatialKey] = reader.read[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](layerId)
     //3：定义色带，非必须
     //   val colorMap1 = ColorMap(Map(
     //    0 -> RGB(0,0,0),
     //    1 -> RGB(255,255,255)
     //  ))
     val colorRamp = ColorRamp(RGB(0,0,0), RGB(255,255,255))
       .stops(100)
       .setAlphaGradient(0xFF, 0xAA)
     // 4: 定义输出路径，如果没有则创建
     val outputPath = dataBasePath+"/110000BJ_L5_TM_1990/render/" + zoomId  //图片输出路径
     val zoomDir: File = new File(outputPath)
     if (!zoomDir.exists()) {
         zoomDir.mkdirs()
      }
     //5：将瓦片图层数据渲染成jpg并安装规则写入磁盘
     layers.foreach(layer => {
       val key = layer._1
       val tile = layer._2
       val layerPath = outputPath + "/" + key.row + "_" + key.col + ".jpg"
       System.out.println(layerPath)
       tile.renderJpg(colorRamp).write(layerPath)  //调用渲染方法，colorRamp为非必须参数
     })
     sc.stop
  }




  def main(args: Array[String]): Unit = {
    helloRaster()
    createTile()
    redTiff("110000BJ_L5_TM_1990/110000BJ_L5_TM_1990.TIF")
    renderTile()
  }
}
