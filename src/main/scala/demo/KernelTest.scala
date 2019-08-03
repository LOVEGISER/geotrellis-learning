import geotrellis.raster._
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.spark._
import geotrellis.vector._
import scala.util._
import geotrellis.raster.density.KernelStamper
import geotrellis.raster.mapalgebra.local.LocalTileBinaryOp
import geotrellis.raster.mapalgebra.focal.Kernel
import geotrellis.raster.render._
import geotrellis.spark.tiling._
import geotrellis.spark._
import geotrellis.spark.stitch.TileLayoutStitcher


//https://blog.csdn.net/qq_32432081/article/details/80835199

object KernelTest {
  def helloSentence = "Hello GeoTrellis"
  val tl = TileLayout(7, 4, 100, 100)
  val extent = Extent(-109, 37, -102, 41) // Extent of Colorado
  val ld = LayoutDefinition(extent, tl)
  def dataBasePath = "/Users/wangleigis163.com/Documents/alex/dev/code/private/cloud-map/data-server/"




  def main(args: Array[String]): Unit = {
    //1：定义随机坐标点
    val pts = (for (i <- 1 to 1000) yield randomPointFeature(extent)).toList
    val kernelWidth: Int = 9
    /* 2：定义高斯和 deviation 1.5, amplitude 25 */
    val kern: Kernel = Kernel.gaussian(kernelWidth, 1.5, 25)
    //3：基于坐标数据做核计算
    val kde: Tile = pts.kernelDensity(kern, RasterExtent(extent, 700, 400))
    //4：定义渲染方式
    val colorMap = ColorMap(
      (0 to kde.findMinMax._2 by 4).toArray,
      ColorRamps.HeatmapBlueToYellowToRedSpectrum
    )
    //5：将计算的结果写入到本地文件
    kde.renderPng(colorMap).write(dataBasePath+"kerneldata/result.png")

    val keyfeatures: Map[SpatialKey, List[PointFeature[Double]]] =
      pts
        .flatMap(ptfToSpatialKey)
        .groupBy(_._1)
        .map { case (sk, v) => (sk, v.unzip._2) }
    val keytiles = keyfeatures.map { case (sk, pfs) =>
      (sk, pfs.kernelDensity(
        kern,
        RasterExtent(ld.mapTransform(sk), tl.tileDimensions._1, tl.tileDimensions._2)
      ))
    }
    val aa=ld.layoutRows    //4
    val dd = ld.layoutCols  //7
    val bb =tl.tileRows     //100
    val cc =tl.tileCols     //100

    for(i<- 0 until 7){
      println(i)
    }
    val tileList =
      for {
        r <- 0 until ld.layoutRows   //4
        c <- 0 until ld.layoutCols    //7
      } yield {
        val k = SpatialKey(c,r)
        (k, keytiles.getOrElse(k, IntArrayTile.empty(tl.tileCols, tl.tileRows)))
      }

    val stitched = TileLayoutStitcher.stitch(tileList)._1
    stitched.renderPng(colorMap).write(dataBasePath+"kerneldata/result.png")
  }

  /**
    * convert the list of points into a collection of (SpatialKey, List[PointFeature[Double]])
    * @param ptf
    * @tparam D
    * @return
    */
  def ptfToSpatialKey[D](ptf: PointFeature[D]): Iterator[(SpatialKey, PointFeature[D])] = {
    val ptextent = ptfToExtent(ptf)
    val gridBounds = ld.mapTransform(ptextent)   //gridBounds的格式为：（col，row）

    for {
      (c, r) <- gridBounds.coordsIter
      if r < tl.totalRows
      if c < tl.totalCols
    } yield (SpatialKey(c,r), ptf)
  }

  def ptfToExtent[D](p: PointFeature[D]) = pointFeatureToExtent(9, ld, p)

  /**
    * generate random points
    * @param extent
    * @return
    */
  def randomPointFeature(extent: Extent): PointFeature[Double] = {
    def randInRange (low: Double, high: Double): Double = {
      val x = Random.nextDouble
      low * (1-x) + high * x
    }
    Feature(Point(randInRange(extent.xmin, extent.xmax),      // the geometry
      randInRange(extent.ymin, extent.ymax)),
      Random.nextInt % 16 + 16)                         // the weight (attribute)
  }

  /**
    * to generate the extent of the kernel centered at a given point
    * @param kwidth
    * @param ld
    * @param ptf
    * @tparam D
    * @return
    */
  def pointFeatureToExtent[D](kwidth: Double, ld: LayoutDefinition, ptf: PointFeature[D]): Extent = {
    val p = ptf.geom

    Extent(p.x - kwidth * ld.cellwidth / 2,
      p.y - kwidth * ld.cellheight / 2,
      p.x + kwidth * ld.cellwidth / 2,
      p.y + kwidth * ld.cellheight / 2)
  }

    def stampPointFeature(
                           tile: MutableArrayTile,
                           tup: (SpatialKey, PointFeature[Double]),
                            kern:Kernel
                         ): MutableArrayTile = {
      val (spatialKey, pointFeature) = tup
      val tileExtent = ld.mapTransform(spatialKey)
      val re = RasterExtent(tileExtent, tile)
      val result = tile.copy.asInstanceOf[MutableArrayTile]

      KernelStamper(result, kern)
        .stampKernelDouble(re.mapToGrid(pointFeature.geom), pointFeature.data)

      result
    }


}
