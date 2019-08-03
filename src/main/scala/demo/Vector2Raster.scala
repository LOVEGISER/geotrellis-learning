package demo

import java.io.File
import java.nio.charset.Charset

import geotrellis.raster.{CellType, IntCellType, RasterExtent, Tile, TileLayout}
import geotrellis.raster.rasterize.Rasterizer
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.data.shapefile.shp.ShapefileHeader
import geotrellis.shapefile.ShapeFileReader
import geotrellis.spark.{Metadata, SpatialKey}
import geotrellis.spark.stitch.TileLayoutStitcher
import geotrellis.spark.tiling.LayoutDefinition
import geotrellis.vector.{Extent, Feature}
import org.apache.spark.{Partitioner, SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.opengis.feature.simple.SimpleFeature

import scala.collection.mutable

object Vector2Raster {
  def dataBasePath = "/Users/wangleigis163.com/Documents/alex/dev/code/private/cloud-map/data-server/"

  def main(args: Array[String]): Unit = {


    val conf = new SparkConf().setMaster("local").setAppName("Shp2Raster")
    val sc = new SparkContext(conf)

    val features = ShapeFileReader.readSimpleFeatures(dataBasePath+"chinamap/全国县.shp")
    val featureRDD :RDD[SimpleFeature]= sc.parallelize(features)
    val extent:Extent = Extent(80, 15, 140, 40)
    val tl = TileLayout(100, 72, 5, 5)
    val layout = LayoutDefinition(extent, tl)
  /*  val celltype:CellType=IntCellType
    val layerRDD: RDD[(SpatialKey, Tile)] with Metadata[LayoutDefinition] = featureRDD.
      rasterize(35, celltype, layout)
    val layerResult = layerRDD.collect();
    for(sublayer<-layerResult) {
      sublayer._2.renderPng(colorMap1).write("D:\\IdeaProjects\\ScalaDemo\\data\\test\\"+sublayer._1+".tif")
    }
    val stitched = TileLayoutStitcher.stitch(layerResult)._1
    stitched.renderPng(colorMap1).write("D:\\IdeaProjects\\ScalaDemo\\data\\test\\result.tif")*/


  }

//  def rasterizeFeature( geoms: RDD[(Geometry,Double)],
//                        cellType: CellType,
//                        layout: LayoutDefinition,
//                        options: Rasterizer.Options = Rasterizer.Options.DEFAULT,
//                        partitioner: Option[Partitioner] = None
//                      ): RDD[(SpatialKey, Tile)] with Metadata[LayoutDefinition] = {
//    val features = geoms.map({ g => Feature(g._1,g._2) })
//    fromFeature(features, cellType, layout, options, partitioner)

//  def getFeatures(path: String, attrName: String = "the_geom", charset: String = "UTF-8"): mutable.ListBuffer[Geometry] ={
//
//
//    val features = mutable.ListBuffer[Geometry]()
//    var polygon: Option[MultiPolygon] = null
//    val shpDataStore = new ShapefileDataStore(new File(path).toURI().toURL())
//    shpDataStore.setCharset(Charset.forName(charset))
//    val typeName = shpDataStore.getTypeNames()(0)
//    val featureSource = shpDataStore.getFeatureSource(typeName)
//    val result = featureSource.getFeatures()
//    val itertor = result.features()
//    while (itertor.hasNext()) {
//      val feature = itertor.next()
//      val p = feature.getProperties()
//      val it = p.iterator()
//
//      while (it.hasNext()) {
//        val pro = it.next()
//        if (pro.getName.getLocalPart.equals(attrName)) {
//          features += WKT.read(pro.getValue.toString) //get all geom from shp
//        }
//      }
//    }
//    itertor.close()
//    shpDataStore.dispose()
//    feature
//  }

}

